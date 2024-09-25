/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: JchUtil.java
 *  <p>
 *  Licensed under the Apache License Version 2.0;
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package org.konggradio.core.utils;

import cn.hutool.extra.ssh.JschUtil;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import io.micrometer.core.instrument.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class JchUtil {
	public static int BUFFER_SIZE = 1024;
	// 将堡垒机保护的内网8080端口映射到localhost，我们就可以通过访问http://localhost:8080/访问内网服务了
	public static boolean bindPort(Session session, String remoteHost, int remotePort, int localPort){
		return JschUtil.bindPort(session, remoteHost, remotePort, localPort);
	}
	public static String sshRunShell(String host, int port, String username, String password, String[] commands) {
		StringBuffer resp = new StringBuffer("");
		JSch jsch = new JSch();
		Session session = null;
		ChannelShell channelShell = null;
		try {
			session = jsch.getSession(username, host, port);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			session.setConfig(sshConfig);
			session.setPassword(password);
			session.connect();
			channelShell = (ChannelShell) session.openChannel("shell");
			channelShell.setPty(true);
			channelShell.connect();
			try (InputStream in = channelShell.getInputStream();
				 OutputStream out = channelShell.getOutputStream();
				 PrintWriter pw = new PrintWriter(out);) {
				for (String cmd : commands) {
					pw.println(cmd);
				}
				pw.println("exit");
				pw.flush();

				//日志返回输出
				byte[] buffer = new byte[BUFFER_SIZE];
				for (; ; ) {
					while (in.available() > 0) {
						int i = in.read(buffer, 0, BUFFER_SIZE);
						if (i < 0) {
							break;
						}
						String s = new String(buffer, 0, i);
						if (s != null && s.trim().length() > 0) {
							if(s.contains("# exit")){
								//continue;
							}
							if(s.contains("logout")){
								//continue;
							}
							resp.append(s);
						}

					}
					if (channelShell.isClosed()) {
						//System.out.println("SSH2 Exit: " + channelShell.getExitStatus());
						break;
					}
					try {
						TimeUnit.MILLISECONDS.sleep(10);
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (channelShell != null) {
				channelShell.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
		session.disconnect();
		return resp.toString();
	}

	public static String sessionRunShell(Session session, String[] commands) {
		ChannelShell channelShell = null;
		StringBuffer resp = new StringBuffer("");
		try {
			channelShell = (ChannelShell) session.openChannel("shell");
			channelShell.setPty(true);
			channelShell.connect();
			try (InputStream in = channelShell.getInputStream();
				 OutputStream out = channelShell.getOutputStream();
				 PrintWriter pw = new PrintWriter(out);) {
				for (String cmd : commands) {
					pw.println(cmd);
				}
				pw.println("exit");
				pw.flush();

				byte[] buffer = new byte[BUFFER_SIZE];
				for (; ; ) {
					while (in.available() > 0) {
						int i = in.read(buffer, 0, BUFFER_SIZE);
						if (i < 0) {
							break;
						}
						String s = new String(buffer, 0, i);
						if (s != null && s.trim().length() > 0) {
							resp.append(s);
						}
					}
					if (channelShell.isClosed()) {
						//System.out.println("SSH2 Exit: " + channelShell.getExitStatus());
						break;
					}
					try {
						TimeUnit.MILLISECONDS.sleep(100);
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (channelShell != null) {
				channelShell.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
		return resp.toString();
	}

	/**
	 * JSch，执行多条语句<
	 * pty 交互式命令
	 *
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 */

	public static Session getSshSession(String host, int port, String username, String password) {
		JSch jsch = new JSch();
		Session session = null;
		ChannelShell channelShell = null;
		try {
			session = jsch.getSession(username, host, port);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			session.setConfig(sshConfig);
			session.setPassword(password);
			session.connect();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (channelShell != null) {
				channelShell.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
		return session;
	}

	/**
	 * JSch，执行多条语句
	 * commond 一次命令
	 *
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @param commands
	 * @return
	 */
	public static String ssh2ShellExec(String host, int port, String username, String password, String[] commands) {
		JSch jsch = new JSch();
		Session session = null;
		ChannelExec channelExec = null;
		String result = "";
		try {
			session = jsch.getSession(username, host, port);
			Properties sshConfig = new Properties();
			sshConfig.put("StrictHostKeyChecking", "no");
			//sshConfig.put("PreferredAuthentications", "publickey,keyboard-interactive,password");//新环TDH 需要
			session.setConfig(sshConfig);
			session.setPassword(password);
			session.connect();
			channelExec = (ChannelExec) session.openChannel("exec");
			channelExec.setPty(false);
			String command = commands[0];
			for (int i = 1; i < commands.length; i++) {
				command += " && " + commands[i];
			}
			channelExec.setCommand(command);
			InputStream std = channelExec.getInputStream();
			InputStream error = channelExec.getErrStream();
			BufferedReader brStd = new BufferedReader(new InputStreamReader(std, StandardCharsets.UTF_8));
			BufferedReader brError = new BufferedReader(new InputStreamReader(error, StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = brStd.readLine()) != null) {
				if (StringUtils.isNotBlank(line)) {
					sb.append(line);
				}
			}

			while ((line = brError.readLine()) != null) {
				if (StringUtils.isNotBlank(line)) {
					sb.append(line);
				}
			}
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (channelExec != null) {
				channelExec.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
		return result;
	}

}
