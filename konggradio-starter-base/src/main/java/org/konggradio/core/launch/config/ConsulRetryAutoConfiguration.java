/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ConsulRetryAutoConfiguration.java
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

import org.konggradio.core.launch.consul.KongGradioConsulRegistry;
import org.konggradio.core.launch.props.ConsulRetryProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryClient;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@ConditionalOnClass({
	ConsulDiscoveryClient.class,
	ConsulAutoRegistration.class,
	ConsulServiceRegistry.class
})
@Import({ConsulRetryProperties.class})
public class ConsulRetryAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean({KongGradioConsulRegistry.class})
	public KongGradioConsulRegistry consulRetryRegistry(
		ConsulAutoRegistration consulAutoRegistration,
		ConsulServiceRegistry consulServiceRegistry,
		DiscoveryClient discoveryClient,
		ConsulRetryProperties properties
	) {
		return new KongGradioConsulRegistry(
			consulAutoRegistration,
			consulServiceRegistry,
			discoveryClient,
			properties
		);
	}
}
