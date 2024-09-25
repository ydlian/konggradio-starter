/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: SocialConfiguration.java
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
package org.konggradio.core.social.config;

import com.xkcoding.http.HttpUtil;
import com.xkcoding.http.support.Http;
import com.xkcoding.http.support.httpclient.HttpClientImpl;
import org.konggradio.core.social.props.SocialProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SocialConfiguration
 *
 * @author ydlian
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SocialProperties.class)
public class SocialConfiguration {

	@Bean
	@ConditionalOnMissingBean(Http.class)
	public Http simpleHttp() {
		HttpClientImpl httpClient = new HttpClientImpl();
		HttpUtil.setHttp(httpClient);
		return httpClient;
	}

}
