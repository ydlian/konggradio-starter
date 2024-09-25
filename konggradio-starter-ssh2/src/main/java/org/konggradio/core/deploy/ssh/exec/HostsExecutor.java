/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: HostsExecutor.java
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

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.apache.commons.lang3.Validate;
import org.konggradio.core.deploy.ssh.FastLauncher;
import org.konggradio.core.deploy.ssh.core.SshUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *  负责与清单中的主机建立ssh连接，并调用Parser解析命令和执行
 *
 * @author  ops@KongGradio.com
 * @date    2016年8月1日
 */
public class HostsExecutor {
	/**
	* Logger for this class
	*/
	private static final Logger logger = LoggerFactory.getLogger(HostsExecutor.class);

	private static List<SshUtil> sshs;

	public static List<SshUtil> getSsh(){
		Validate.notNull(sshs);
		return sshs;
	}

	public static void getResults(List<ListenableFuture<SshUtil>> futures) {
		//主机建立连接按顺序返回连接结果
			ListenableFuture<List<SshUtil>> successfulQueries = Futures.successfulAsList(futures);
			Futures.addCallback(successfulQueries, new FutureCallback<List<SshUtil>>() {
				//只在futures执行完成时被调用一次
				@Override
				public void onSuccess(List<SshUtil> results) {
					//统计失败数
					int failure = sumFailures(results);
					logger.info("[Succeed]主机全部初始化完成，成功:{}，失败:{}",results.size()-failure,failure);
					sshs = results;
					if(failure == results.size()){
						System.out.println("######## [Warning] 主机全部初始化失败!    ##########");
						System.out.println("######## [INFO]    请检查配置，或修timeout后重试   ##########");
						System.out.println("######## [Exit]    系统即将退出！##########");
						System.exit(1);
					}
					FastLauncher.setHostsReady(true);
					System.out.println("########## 请输入命令... ##########");
				}

				private int sumFailures(List<SshUtil> results) {
					int failure=0;
					for (int i = 0; i < results.size(); i++) {
						if(results.get(i)==null){
							failure++;
						}
					}
					return failure;
				}

				@Override
				public void onFailure(Throwable t) {
					logger.error("执行失败",t);
				}
			});
	}
}
