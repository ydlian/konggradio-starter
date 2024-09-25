/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: SshUtil.java
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

package org.konggradio.core.deploy.ssh.core;

import com.google.common.base.Strings;
import com.jcraft.jsch.*;
import org.konggradio.core.deploy.framework.util.IOUtil;
import org.konggradio.core.deploy.ssh.hosts.DeployKit;
import org.konggradio.core.deploy.ssh.model.CICDInfo;
import org.konggradio.core.deploy.ssh.model.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class SshUtil {

	private ChannelSftp channelSftp;
	private ChannelExec channelExec;
	private Session session = null;

	private CICDInfo cicdInfo = null;

	public DeployKit getDeployKit() {
		return deployKit;
	}

	public void setDeployKit(DeployKit deployKit) {
		this.deployKit = deployKit;
	}

	private DeployKit deployKit = null;

	private static int timeout = 6000;

	public static void setTimeout(int timeout) {
		SshUtil.timeout = timeout;
	}

	private boolean error = false;

	public boolean hasError() {
		return error;
	}

	private String result = "";

	public String result() {
		return result;
	}

	private Host host;

	public Host getHost() {
		return host;
	}

	public CICDInfo getCicdInfo() {
		return cicdInfo;
	}

	public void setCicdInfo(CICDInfo cicdInfo) {
		this.cicdInfo = cicdInfo;
	}

	private static final Logger logger = LoggerFactory.getLogger(SshUtil.class);


	public static SshUtil init(Host host) throws Exception {
		return new SshUtil(host);
	}


	public static SshUtil init(Host host, int timeout) throws Exception {
		SshUtil.setTimeout(timeout);
		return new SshUtil(host);
	}

	public static SshUtil init(Host host, int timeout, DeployKit deployKit) throws Exception {
		SshUtil.setTimeout(timeout);
		SshUtil util = new SshUtil(host);
		util.setDeployKit(deployKit);
		return util;
	}

	public SshUtil(Host host) {
		this.host = host;
		logger.info("尝试连接到主机 {} ...", host.getAddr());
		logger.info("username:{},password:{}", host.getUsername(), "******");
		JSch jsch = new JSch(); // 创建JSch对象
		boolean done = false;
		int count = 0;
		while (!done && count <= 3) {
			try {
				session = jsch.getSession(host.getUsername(), host.getAddr(),
					host.getPort()); // 根据用户名，主机ip，端口获取一个Session对象

				session.setPassword(host.getPassword()); // 设置密码
				Properties config = new Properties();
				config.put("StrictHostKeyChecking", "no");
				session.setConfig(config); // 为Session对象设置properties
				session.setTimeout(timeout); // 设置timeout时间
				session.connect(); // 通过Session建立链接
				done = true;
			} catch (JSchException e) {
				count ++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
					throw new RuntimeException(ex);
				}
			}

		}
		if(done) logger.info("主机 {} 连接成功!", host.getAddr());
	}

	public void download(String src, String dst) throws Exception {
		channelSftp = (ChannelSftp) session.openChannel("sftp");
		channelSftp.connect();
		channelSftp.get(src, dst);
		channelSftp.quit();
	}

	public void uploadBatch(String src, String dst) throws Exception {
		push(src, dst);
	}

	public void upload(String src, String dst) throws Exception {
		push(src, dst);
//		close();
	}

	private void push(String src, String dst) throws JSchException,
		SftpException, IOException {
		channelSftp = (ChannelSftp) session.openChannel("sftp");
		channelSftp.connect();
//		logger.info("文件传输准备:源文件-[{}]，目标文件-[{}]",src,dst);
		channelSftp.put(src, dst,
			new FileProgressMonitor(IOUtil.sizeOf(src)),
			ChannelSftp.OVERWRITE);
		channelSftp.quit();
	}


	public SshUtil exec(String cmd, String charset) throws JSchException, IOException {
		runCommand(cmd, charset);
		return this;
	}

	public SshUtil exec(String cmd) throws Exception {
		return exec(cmd, "UTF-8");
	}

	public SshUtil execOnce(String cmd, String charset) throws JSchException, IOException {
		runCommand(cmd, charset);
		close();
		return this;
	}

	public SshUtil execOnce(String cmd) throws Exception {
		return execOnce(cmd, "UTF-8");
	}


	private void runCommand(String cmd, String charset) throws JSchException,
		IOException {
		String errorMsg = "";
		channelExec = (ChannelExec) session.openChannel("exec");
		//channelExec = (ChannelExec) session.openChannel("");
		channelExec.setCommand(cmd);
		channelExec.setInputStream(null);
		channelExec.setErrStream(System.err);
		channelExec.connect();
		InputStream in = channelExec.getInputStream();
		//必须先声明，否则流一旦读完就无法重复读取
		InputStream stderr = channelExec.getErrStream();
		result = getResultFromStream(in, charset);
		errorMsg = getResultFromStream(stderr, charset);
		channelExec.disconnect();
		if (!Strings.isNullOrEmpty(errorMsg)) {
			//logger.error("Error command:\"{}\"",cmd.trim());
			error = true;
			result = errorMsg;
		}
	}

	private String getResultFromStream(InputStream in, String charset) throws IOException {
		String result = "";
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,
			Charset.forName(charset)));
		String buf = null;
		while ((buf = reader.readLine()) != null) {
			result += buf + "\n";
		}
		reader.close();
		return result;
	}

	public void close() {
		if (session != null || session.isConnected()) {
			session.disconnect();
		}
	}
}
