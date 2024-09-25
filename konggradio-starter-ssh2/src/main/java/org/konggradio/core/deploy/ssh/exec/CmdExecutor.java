/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: CmdExecutor.java
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

package org.konggradio.core.deploy.ssh.exec;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.*;
import org.apache.commons.lang3.Validate;
import org.konggradio.core.deploy.framework.util.IOUtil;
import org.konggradio.core.deploy.ssh.core.SshUtil;
import org.konggradio.core.deploy.ssh.model.CICDInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class CmdExecutor {

	private static final Logger logger = LoggerFactory.getLogger(CmdExecutor.class);

	private static ListeningExecutorService pool;

	private static List<ListenableFuture<String>> futures;

	private static int thread = 5;
	private static boolean sequence = false;

	public static void init(int thread) {
		CmdExecutor.thread = thread;
	}

	public static void execute(List<SshUtil> sshList, String cmd) {
		Validate.notNull(sshList, "ssh清单不能为空！");
		pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(thread));
		futures = Lists.newArrayList();
		for (SshUtil ssh : sshList) {
			if (ssh != null) {
				ListenableFuture<String> future = pool.submit(new Callable<String>() {
					@Override
					public String call() throws Exception {
						return exec(cmd, ssh);
					}
				});
				futures.add(future);
			}
		}
		execFutures(futures);
	}

	public static String exec(String cmd, SshUtil ssh) {
		try {
			return "\n--------------    " + ssh.getHost().getAddr() + "    --------------\n"
				+ ssh.exec(cmd).result()
				+ "\n--------------  " + ssh.getHost().getAddr() + " done --------------\n";

		} catch (Exception e) {
			logger.error("主机:" + ssh.getHost().getAddr() + "命令执行失败!", e);
		}
		return "failure";
	}

	/**
	 * 执行futures
	 *
	 * @param
	 * @author ops@KongGradio.com
	 * @date 2022年8月1日
	 */
	private static void execFutures(List<ListenableFuture<String>> futures) {
		//命令按顺序返回结果
		if (sequence) {
			ListenableFuture<List<String>> successfulQueries = Futures.successfulAsList(futures);
			Futures.addCallback(successfulQueries, new FutureCallback<List<String>>() {
				@Override
				public void onSuccess(List<String> results) {
					for (String result : results) {
						System.out.println(result);
					}
				}

				@Override
				public void onFailure(Throwable t) {
					logger.error("执行失败", t);
				}
			});
		} else {
			for (ListenableFuture<String> future : futures) {
				Futures.addCallback(future, new FutureCallback<String>() {
					@Override
					public void onSuccess(String result) {
						System.out.println(result);
					}

					@Override
					public void onFailure(Throwable t) {
						logger.error("执行失败", t);
					}
				});
			}
		}
	}

	public static void download(List<SshUtil> sshList, String src) {
		String dest = ".";
		download(sshList, src, dest);
	}


	public static void deployApp(List<SshUtil> sshList, String applicationNumber, CICDInfo cicdInfo) {

		for (SshUtil ssh : sshList) {
			if (ssh != null) {
				ssh.getDeployKit().installApplication(Integer.valueOf(applicationNumber), ssh, cicdInfo);
			}
		}
	}

	public static void pkg(List<SshUtil> sshList, String applicationNumber, CICDInfo cicdInfo) {

		for (SshUtil ssh : sshList) {
			if (ssh != null) {
				ssh.getDeployKit().pkg(Integer.valueOf(applicationNumber), ssh, cicdInfo);
			}
		}
	}


	public static void clear(List<SshUtil> sshList, String applicationNumber, CICDInfo cicdInfo) {
		pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(thread));
		futures = Lists.newArrayList();
		for (SshUtil ssh : sshList) {
			if (ssh != null) {
				ListenableFuture<String> future = pool.submit(new Callable<String>() {
					String dest_by_host;

					@Override
					public String call() {
						try {
							dest_by_host = cicdInfo.getDeployBasePath();
							//IOUtil.ensureExists(dest_by_host);
							logger.info("清理应用：[{}]...", applicationNumber);
							ssh.getDeployKit().clear(Integer.valueOf(applicationNumber), ssh, cicdInfo);
							//ssh.download(src, dest_by_host);
						} catch (Exception e) {
							logger.error("主机:" + ssh.getHost().getAddr() + "命令执行失败!", e);
						}
						return "success";
					}
				});

				Futures.addCallback(future, new FutureCallback<String>() {
					@Override
					public void onSuccess(String result) {
						logger.info("主机{}应用清理完成!", ssh.getHost().getAddr());
					}

					@Override
					public void onFailure(Throwable t) {
						logger.error("执行失败", t);
					}
				});
			}
		}
	}

	/**
	 * 将文件下载到当前java虚拟机执行目录下的各自主机目录内
	 *
	 * @param src
	 * @param dest
	 * @throws
	 * @author ops@KongGradio.com
	 * @date 2022年7月17日
	 */
	public static void download(List<SshUtil> sshList, String src, String dest) {
		pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(thread));
		futures = Lists.newArrayList();
		for (SshUtil ssh : sshList) {
			if (ssh != null) {
				ListenableFuture<String> future = pool.submit(new Callable<String>() {
					String dest_by_host;

					@Override
					public String call() {
						try {
							dest_by_host = dest + File.separator + ssh.getHost().getAddr();
							IOUtil.ensureExists(dest_by_host);
							logger.info("下载文件[{}] 到 [{}]目录...", src, dest_by_host);
							ssh.download(src, dest_by_host);
						} catch (Exception e) {
							logger.error("主机:" + ssh.getHost().getAddr() + "命令执行失败!", e);
						}
						return "success";
					}
				});

				Futures.addCallback(future, new FutureCallback<String>() {
					@Override
					public void onSuccess(String result) {
						logger.info("主机{}文件传输完成!", ssh.getHost().getAddr());
					}

					@Override
					public void onFailure(Throwable t) {
						logger.error("执行失败", t);
					}
				});
			}
		}
	}

	public static void upload(List<SshUtil> sshList, String src) {
		String dest = "";
		upload(sshList, src, dest);
	}

	public static void upload(List<SshUtil> sshList, String src, String dest) {
		pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(thread));
		futures = Lists.newArrayList();
		for (SshUtil ssh : sshList) {
			if (ssh != null) {
				ListenableFuture<String> future = pool.submit(new Callable<String>() {
					@Override
					public String call() {
						try {
							//提取扩展名，合成目标URI
							String name = new File(src).getName();
							if (!dest.equals("")) {
								String dest_with_fileName = dest + "/" + name;
								logger.info("文件[{}]上传到 [{}]", src, dest_with_fileName);
								ssh.upload(src, dest_with_fileName);
							} else {
								logger.info("文件[{}]上传到home目录...", src, name);
								ssh.upload(src, name);
							}
						} catch (Exception e) {
							logger.error("主机:" + ssh.getHost().getAddr() + "命令执行失败!", e);
						}
						return "success";
					}
				});

				Futures.addCallback(future, new FutureCallback<Object>() {
					@Override
					public void onSuccess(Object result) {
						logger.info("主机{}文件传输完成!", ssh.getHost().getAddr());
					}

					@Override
					public void onFailure(Throwable t) {
						logger.error("执行失败", t);
					}
				});
			}
		}
	}
}
