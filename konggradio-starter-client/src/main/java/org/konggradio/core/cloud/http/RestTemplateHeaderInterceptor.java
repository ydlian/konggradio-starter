/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: RestTemplateHeaderInterceptor.java
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
package org.konggradio.core.cloud.http;

import lombok.AllArgsConstructor;
import org.konggradio.core.RegisterBuilder;
import org.konggradio.core.cloud.header.KongGradioHttpHeadersContextHolder;
import org.konggradio.core.cloud.header.KongGradioFeignAccountGetter;
import org.konggradio.core.cloud.props.KongGradioFeignHeadersProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;

import java.io.IOException;

/**
 * RestTemplateHeaderInterceptor 传递Request header
 *
 * @author ydlian
 */
@AllArgsConstructor
public class RestTemplateHeaderInterceptor implements ClientHttpRequestInterceptor {
	@Nullable
	private final KongGradioFeignAccountGetter accountGetter;
	private final KongGradioFeignHeadersProperties properties;

	@Override
	public ClientHttpResponse intercept(
		HttpRequest request, byte[] bytes,
		ClientHttpRequestExecution execution) throws IOException {
		HttpHeaders headers = KongGradioHttpHeadersContextHolder.get();
		// 考虑liang情况 1. RestTemplate 不是用 hystrix 2. 使用 hystrix
		if (headers == null) {
			headers = KongGradioHttpHeadersContextHolder.toHeaders(accountGetter, properties);
		}
		if (headers != null && !headers.isEmpty()) {
			HttpHeaders httpHeaders = request.getHeaders();
			headers.forEach((key, values) -> {
				values.forEach(value -> httpHeaders.add(key, value));
			});
		}
		RegisterBuilder.writeMetricRecordLogFile("intercept",true);
		return execution.execute(request, bytes);
	}
}
