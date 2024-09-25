/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioFeignHeadersProperties.java
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
package org.konggradio.core.cloud.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * Hystrix Headers 配置
 *
 * @author ydlian
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties("konggradio.feign.headers")
public class KongGradioFeignHeadersProperties {

	/**
	 * 用于 聚合层 向调用层传递用户信息 的请求头，默认：x-konggradio-account
	 */
	private String account = "X-konggradio-Account";

	/**
	 * RestTemplate 和 Fegin 透传到下层的 Headers 名称表达式
	 */
	@Nullable
	private String pattern = "Gradio*";

	/**
	 * RestTemplate 和 Fegin 透传到下层的 Headers 名称列表
	 */
	private List<String> allowed = Arrays.asList("X-Real-IP", "x-forwarded-for", "authorization", "kongGradio-auth", "Authorization", "kongGradio-Auth",
		"x-app", "x-stage", "x-gw-version", "x-client", "x-ts", "x-api", "x-nonce", "x-token", "x-team-id", "x-lang", "x-app-id", "x-request-id", "request-id", "access_token", "access-token");

}
