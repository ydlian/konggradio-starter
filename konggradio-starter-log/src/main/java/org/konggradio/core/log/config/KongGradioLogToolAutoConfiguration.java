/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioLogToolAutoConfiguration.java
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

package org.konggradio.core.log.config;

import lombok.AllArgsConstructor;
import org.konggradio.core.log.aspect.ApiLogAspect;
import org.konggradio.core.log.event.ApiLogListener;
import org.konggradio.core.log.event.UsualLogListener;
import org.konggradio.core.log.event.ErrorLogListener;
import org.konggradio.core.log.logger.KongGradioLogger;
import org.konggradio.core.launch.props.kongProperties;
import org.konggradio.core.launch.server.ServerInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 日志工具自动配置
 *
 * @author ydlian
 */
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@ConditionalOnWebApplication
public class KongGradioLogToolAutoConfiguration {


	private final ServerInfo serverInfo;
	private final kongProperties kongProperties;

	@Bean
	public ApiLogAspect apiLogAspect() {
		return new ApiLogAspect();
	}

	@Bean
	public KongGradioLogger kongCloudLogger() {
		return new KongGradioLogger();
	}

	@Bean
	public ApiLogListener apiLogListener() {
		return new ApiLogListener( serverInfo, kongProperties);
	}

	@Bean
	public ErrorLogListener errorEventListener() {
		return new ErrorLogListener(serverInfo, kongProperties);
	}

	@Bean
	public UsualLogListener kongCloudEventListener() {
		return new UsualLogListener( serverInfo, kongProperties);
	}

	public static boolean getDebugSwitch(){
		String txt = System.getProperty("spring.application.debug.switch");
		return Boolean.parseBoolean(txt);
	}

}
