/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: Assembler.java
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

package org.konggradio.core.deploy.ssh.hosts;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.konggradio.core.deploy.ssh.core.SshUtil;
import org.konggradio.core.deploy.ssh.model.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class Assembler {
	/**
	* Logger for this class
	*/
	private static final Logger logger = LoggerFactory.getLogger(Assembler.class);


	/**
	 * 装配主机
	 * @param hosts
	 * @param thread   线程数
	 * @param timeout  超时(秒)
	 * @return
	 * @author ops@KongGradio.com
	 * @date   2016年7月27日
	 */
	public static List<ListenableFuture<SshUtil>> assembly(List<Host> hosts, int thread, int timeout){
		ListeningExecutorService pool = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(thread));
		List<ListenableFuture<SshUtil>> futures = Lists.newArrayList();
		for (Host host : hosts) {
			ListenableFuture<SshUtil> future = pool.submit(new Callable<SshUtil>() {
			    @Override
			    public SshUtil call(){
			    	try {
						return SshUtil.init(host,timeout*1000);
					} catch (Exception e) {
						logger.error("主机{}初始化失败！原因:{}",host.getAddr(),e.getMessage());
				    	logger.error("",e);
					}
					return null;
			    }
			});
			futures.add(future);
		}
		return futures;
	}
}
