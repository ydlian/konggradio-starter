/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioErrorMvcAutoConfiguration.java
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
import org.konggradio.core.log.error.KongGradioErrorAttributes;
import org.konggradio.core.log.error.KongGradioErrorController;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;

/**
 * 统一异常处理
 *
 * @author ydlian
 */
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@ConditionalOnWebApplication
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
public class KongGradioErrorMvcAutoConfiguration {

	private final ServerProperties serverProperties;

	@Bean
	@ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
	public DefaultErrorAttributes errorAttributes() {
		return new KongGradioErrorAttributes();
	}

	@Bean
	@ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
	public BasicErrorController basicErrorController(ErrorAttributes errorAttributes) {
		return new KongGradioErrorController(errorAttributes, serverProperties.getError());
	}

}
