/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: FastLauncher.java
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

package org.konggradio.core.deploy.ssh;


import com.google.common.util.concurrent.ListenableFuture;
import org.konggradio.core.deploy.framework.AbstractBaseSSHConfig;
import org.konggradio.core.deploy.ssh.cli.Parser;
import org.konggradio.core.deploy.ssh.core.SshUtil;
import org.konggradio.core.deploy.ssh.exec.CmdExecutor;
import org.konggradio.core.deploy.ssh.exec.HostsExecutor;
import org.konggradio.core.deploy.ssh.hosts.Assembler;
import org.konggradio.core.deploy.ssh.hosts.Fetcher;
import org.konggradio.core.deploy.ssh.hosts.SimpleFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class FastLauncher extends AbstractBaseSSHConfig {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(FastLauncher.class);

	private static boolean hostsReady;

	public static List<String> getCmdList() {
		return cmdList;
	}

	private static List<String> cmdList = new ArrayList<>();

	public static boolean isHostsReady() {
		return hostsReady;
	}

	public static void setHostsReady(boolean hostsReady) {
		FastLauncher.hostsReady = hostsReady;
	}

	public static Fetcher getFetcher() {
		return new SimpleFetcher();
	}

	public static void initCmd() {
		cmdList.add("get");
		cmdList.add("put");
		cmdList.add("install");
		cmdList.add("pkg");
		cmdList.add("clear");
	}

	/**
	 * 功能描述
	 *
	 * @param
	 * @param
	 * @throws Exception
	 * @author ops@KongGradio.com
	 * @date 2022年7月27日
	 */
	public static void init(String configUri) throws Exception {
		initCmd();
		useConfig(configUri);
		//初始化执行机
		CmdExecutor.init(
			prop.getInt("thread")
		);
	}


	public static void setCurrentJar(String value) {
		Properties environment = System.getProperties();
		environment.setProperty("KongGradio.current.jar.path", value);
	}

	public static String getCurrentJar() {
		Properties environment = System.getProperties();
		return environment.getProperty("KongGradio.current.jar.path");
	}

	public static boolean startWith(String str, List<String> list) {
		for (String s : list) {
			if (str.startsWith(s + " ")) return true;
		}
		return false;
	}

	public static void setRestartFlag(boolean value) {
		Properties environment = System.getProperties();
		environment.setProperty("KongGradio.current.restart", String.valueOf(value));
	}

	public static boolean getRestartFlag() {
		Properties environment = System.getProperties();
		return Boolean.parseBoolean(environment.getProperty("KongGradio.current.restart"));
	}

	public static void restartApplication() throws URISyntaxException, IOException {
		final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		final File currentJar = new File(getCurrentJar());

		/* is it a jar file? */
		if (!currentJar.getName().endsWith(".jar"))
			return;

		/* Build command: java -jar application.jar */
		final ArrayList<String> command = new ArrayList<String>();
		command.add(javaBin);
		command.add("-jar");
		command.add(currentJar.getPath());

		final ProcessBuilder builder = new ProcessBuilder(command);
		builder.start();
		System.exit(0);
	}

	public static void launch(String[] args) {
		initCmd();

		while (true) {
			Scanner in = new Scanner(System.in);  // 新建对象
			String str = in.nextLine();

			logger.info(">>{}", str);
			if (str.startsWith("reload")) {
				//prepare(SshConstant.CONFIG_FILE);
				System.exit(0);
			} else if (startWith(str, getCmdList())) {
				logger.info("startWith:{}", str);

				Parser.parseAndExec(HostsExecutor.getSsh(), str.split("( )+"));
			} else {
				Parser.parseAndExec(HostsExecutor.getSsh(), new String[]{str});
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void prepare(String configUri) throws Exception {
		//配置文件和参数解析初始化
		init(configUri);
		//装配清单中的ssh对象列表
		List<ListenableFuture<SshUtil>> futures = Assembler.assembly(
			getFetcher().getHosts(FastLauncher.prop),
			prop.getInt("thread"),
			prop.getInt("timeout")
		);
		logger.info("host.user:{}", FastLauncher.prop.get("host.user"));
		HostsExecutor.getResults(futures);
	}

}
