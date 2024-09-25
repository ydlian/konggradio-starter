/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: Parser.java
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

package org.konggradio.core.deploy.ssh.cli;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.konggradio.core.deploy.ssh.core.SshUtil;
import org.konggradio.core.deploy.ssh.exec.CmdExecutor;
import org.konggradio.core.deploy.ssh.model.CICDInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class Parser {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Parser.class);

	static CommandOptions cmder = new CommandOptions();
	static CommandLineParser parser = new DefaultParser();


	/**
	 * 参数解析+执行命令
	 *
	 * @param args
	 * @param
	 * @throws InterruptedException
	 * @throws ParseException
	 * @author ops@KongGradio.com
	 * @date 2016年7月27日
	 */
	public static void parseAndExec(List<SshUtil> sshList, String args[]) {

		Validate.notNull(sshList);
		Validate.notNull(sshList.get(0));
		Validate.notNull(args);
		translateArgs(args);
		CICDInfo cicdInfo = sshList.get(0).getCicdInfo();
		//System.out.println("args[0]:" + args[0]);
		CommandLine cmd = null;
		try {
			//System.out.println(cmder.getOptions());
			cmd = parser.parse(cmder.getOptions(), args);
			//Gson gson = new Gson();
			//logger.info("{}",gson.toJson(cmd));
		} catch (ParseException e) {
			logger.error("命令解析异常！{}", e);
			return;
		}
		if (cmd.hasOption("h")) {
			for (String prompt : cicdInfo.getHelpList()) {
				System.out.println(prompt);
			}
		} else if (cmd.hasOption("init")) {
			// 这里显示简短的帮助信息
			String remoteFilePath = cicdInfo.getInitShellPath();
			String fileShort = "initFast.sh";
			String localFileName = cicdInfo.getWorkspace() + File.separator + fileShort;
			FileUtil.mkdir(cicdInfo.getWorkspace());
			List<String> artifactoryServers = cicdInfo.getArtifactoryServers();
			String serverUrl = cicdInfo.getArtifactoryServers().get(RandomUtil.randomInt(0, artifactoryServers.size()));

			if (!FileUtil.exist(localFileName)) {
				//https://oss-guard.s3.cn-north-1.jdcloud-oss.com/fast/init/index.html
				String url = serverUrl + "/fast/init/initFast.sh";
				logger.info("download:{},{}", url, cicdInfo.getWorkspace());
				HttpUtil.downloadFile(url, cicdInfo.getWorkspace());
				CmdExecutor.execute(sshList, "mkdir -p " + remoteFilePath);
				CmdExecutor.upload(sshList, localFileName, remoteFilePath);
				try {
					Thread.sleep(3000L);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			CmdExecutor.execute(sshList, "sh " + remoteFilePath + fileShort);

		} else if (cmd.hasOption("l") || cmd.hasOption("list")) {
			// 这里显示简短的帮助信息
			for (String prompt : cicdInfo.getAppList()) {
				System.out.println(prompt);
			}

		} else if (cmd.hasOption("k") || cmd.hasOption("pkg")) {
			String[] params = cmd.getOptionValues("k");
			if (params == null) {
				return;
			}
			String appNumber = params[0];
			CmdExecutor.pkg(sshList, appNumber, cicdInfo);
		} else if (cmd.hasOption("i") || cmd.hasOption("install")) {
			// 这里显示简短的帮助信息
			for (String prompt : cicdInfo.getInstallPrompts()) {
				System.out.println(prompt);
			}
			String[] params = cmd.getOptionValues("i");
			//System.out.println(params);
			if (params == null) {
				return;
			}
			String appNumber = params[0];
			String deployDest = "";
			if (params.length > 1) {
				deployDest = params[1];
			}

			//logger.info("deploy:{},{}", appNumber, deployDest);
			cicdInfo.updateDeployPathMap(appNumber, deployDest);
			CmdExecutor.deployApp(sshList, appNumber, cicdInfo);

		} else if (cmd.hasOption("c") || cmd.hasOption("clear")) {

			String[] params = cmd.getOptionValues("c");
			//System.out.println(params);
			if (params == null) {
				return;
			}
			String appNumber = params[0];
			CmdExecutor.clear(sshList, appNumber, cicdInfo);

		} else if (cmd.hasOption("g")) {
			String[] params = cmd.getOptionValues("g");
			String src = params[0];
			if (params.length == 1) {//省略目标参数
				CmdExecutor.download(sshList, src);
			} else {
				CmdExecutor.download(sshList, src, params[1]);
			}
		} else if (cmd.hasOption("p")) {
			String[] params = cmd.getOptionValues("p");
			String src = params[0];
			if (params.length == 1) {//省略目标参数
				CmdExecutor.upload(sshList, src);
			} else {
				CmdExecutor.upload(sshList, src, params[1]);
			}
		} else if (NumberUtil.isNumber(args[0])) {


		} else {
			//直接执行命令
			if (args[0].contains("exec")) {
				logger.info("执行命令:{}", StringUtils.substringAfter(args[0], "exec"));
				CmdExecutor.execute(sshList, StringUtils.substringAfter(args[0], "exec"));
				return;
			}
			System.out.println("不支持的命令!");
		}
	}

	private static void translateArgs(String args[]) {
		//get、put命令转换
		if (args[0].equals("get")) {
			args[0] = "-g";
		} else if (args[0].equals("put")) {
			args[0] = "-p";
		} else if (args[0].equals("init")) {
			args[0] = "-init";
		} else if (args[0].equals("pkg")) {
			args[0] = "-k";
		} else if (args[0].equals("h")) {
			args[0] = "-h";
		} else if (args[0].equals("l") || args[0].equals("list")) {
			args[0] = "-l";
		} else if (args[0].trim().equals("i") || args[0].trim().equals("install")) {
			System.out.println("准备安装应用...\n");
			args[0] = "-i";
		} else if (args[0].trim().equals("c") || args[0].trim().equals("clear")) {
			System.out.println("准备卸载应用...\n");
			args[0] = "-c";
		} else if (NumberUtil.isNumber(args[0])) {
			//args[0] = "-" + args[0];
		} else if (args[0].equals("app-")) {
			args[0] = "-" + args[0];
		} else if (args[0].equals("exit") || args[0].equals("quit")) {
			System.exit(1);
		} else {
			String cmdLine = args[0];
			if (!checkCmd(cmdLine)) {
				System.out.println("禁止使用高危命令:" + cmdLine + "\n");
				return;
			}
			args[0] = "exec " + args[0] + "";
		}
	}

	public static String getLocalInnerIp() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	private static boolean checkCmd(String cmdLine) {

		if (cmdLine.contains("rm ")) {
			StringBuffer sb = new StringBuffer("安全审计需要，命令将被记录:").append(cmdLine)
				.append("\n").append("IP:").append(getLocalInnerIp()).append("\n").append("Date:")
				.append(DateUtil.now());
			System.out.println(sb.toString() + "\n");
		}
		return true;
	}

}
