/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ReportConfiguration.java
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
package org.konggradio.core.statistic.config;

import com.bstek.ureport.UReportPropertyPlaceholderConfigurer;
import com.bstek.ureport.console.UReportServlet;
import com.bstek.ureport.provider.report.ReportProvider;
import org.konggradio.core.statistic.props.ReportDatabaseProperties;
import org.konggradio.core.statistic.props.ReportProperties;
import org.konggradio.core.statistic.provider.DatabaseProvider;
import org.konggradio.core.statistic.provider.ReportPlaceholderProvider;
import org.konggradio.core.statistic.service.IReportFileService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;

import javax.servlet.Servlet;

/**
 * UReport配置类
 *
 * @author ydlian
 */
@Order
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "report.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({ReportProperties.class, ReportDatabaseProperties.class})
@ImportResource("classpath:ureport-console-context.xml")
public class ReportConfiguration {

	@Bean
	public ServletRegistrationBean<Servlet> registrationBean() {
		return new ServletRegistrationBean<>(new UReportServlet(), "/ureport/*");
	}

	@Bean
	public UReportPropertyPlaceholderConfigurer uReportPropertyPlaceholderConfigurer(ReportProperties properties) {
		return new ReportPlaceholderProvider(properties);
	}

	@Bean
	@ConditionalOnMissingBean
	public ReportProvider reportProvider(ReportDatabaseProperties properties, IReportFileService service) {
		return new DatabaseProvider(properties, service);
	}

}
