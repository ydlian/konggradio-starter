/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: RegistryConfiguration.java
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
package org.konggradio.core.secure.config;


import org.konggradio.core.secure.registry.SecureRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * secure注册默认配置
 *
 * @author ydlian
 */
@Order
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(SecureConfiguration.class)
public class RegistryConfiguration {

	@Bean
	@ConditionalOnMissingBean(SecureRegistry.class)
	public SecureRegistry secureRegistry() {
		return new SecureRegistry();
	}

}
