/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioConsulServiceRegistryConfiguration.java
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

package org.konggradio.core.launch.config;


import com.ecwid.consul.v1.ConsulClient;
import org.konggradio.core.launch.consul.KongGradioConsulServiceRegistry;
import org.konggradio.core.launch.server.ServerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.cloud.consul.ConditionalOnConsulEnabled;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.discovery.TtlScheduler;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnConsulEnabled
@AutoConfigureBefore({ConsulServiceRegistryAutoConfiguration.class})
public class KongGradioConsulServiceRegistryConfiguration {
	@Autowired(
		required = false
	)
	private TtlScheduler ttlScheduler;
	@Autowired
	private ServerInfo serverInfo;

	public KongGradioConsulServiceRegistryConfiguration() {
	}

	@Bean
	public ConsulServiceRegistry consulServiceRegistry(ConsulClient consulClient, ConsulDiscoveryProperties properties, HeartbeatProperties heartbeatProperties) {
		return new KongGradioConsulServiceRegistry(consulClient, properties, this.ttlScheduler, heartbeatProperties, this.serverInfo);
	}
}
