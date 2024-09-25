/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioConsulRegistry.java
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

package org.konggradio.core.launch.consul;

import cn.hutool.core.thread.ThreadUtil;
import com.ecwid.consul.v1.agent.model.NewService;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.launch.props.ConsulRetryProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;


import java.util.List;

/**
 * @author
 * @date 2022/10/30
 * @since 1.8
 */
@Slf4j
public class KongGradioConsulRegistry implements CommandLineRunner {
	private ConsulAutoRegistration consulAutoRegistration;
	private ConsulServiceRegistry consulServiceRegistry;
	private DiscoveryClient discoveryClient;
	private ConsulRetryProperties properties;

	public KongGradioConsulRegistry(
		ConsulAutoRegistration consulAutoRegistration,
		ConsulServiceRegistry consulServiceRegistry,
		DiscoveryClient discoveryClient,
		ConsulRetryProperties properties
	) {
		//if (ConsulConfig.ignoreRegisterCenter()) return;
		this.consulAutoRegistration = consulAutoRegistration;
		this.consulServiceRegistry = consulServiceRegistry;
		this.discoveryClient = discoveryClient;
		this.properties = properties;
	}

	@Override
	public void run(String... args) {
		//if (ConsulConfig.ignoreRegisterCenter()) return;
		final NewService service = this.consulAutoRegistration.getService();
		ThreadUtil.newSingleExecutor().execute(
			() -> {
				while (true) {
					//log.info("consul service monitor starting:[{}]", service);

					try {
						Thread.sleep(this.properties.getInitialInterval());
					} catch (InterruptedException e) {
						// 当前线程异常退出
						log.error("consul The service has stopped re registering:[{}]", service);
						break;
					}
					if (!this.checkLive(service)) {
						try {
							this.registry();
							//log.info("consul service re registration succeeded:[{}]", service);
						} catch (Exception e) {
							//log.warn("The current consul registration of the service failed. Prepare for the next registration:[{}]", service);
						}
					}
				}
			}
		);
	}

	/**
	 * 服务注册
	 */
	private void registry() {
		this.consulServiceRegistry.register(this.consulAutoRegistration);
	}

	/**
	 * 检查服务状态
	 *
	 * @param service 服务
	 * @return 返回布尔值，正常true，异常false
	 */
	private boolean checkLive(NewService service) {

		boolean flag = false;
		try {

			List<ServiceInstance> instances = this.discoveryClient.getInstances(service.getName());
			for (ServiceInstance instance : instances) {
				if (instance.getInstanceId().equals(service.getId())) {
					flag = true;
					break;
				}
			}
		} catch (Exception e) {
		}
		if (!flag) {
			//log.debug("consul service heartbeat detection finished, and the detection result is:[{}]", flag ? "Succeed" : "Fail");
		}
		return flag;
	}
}
