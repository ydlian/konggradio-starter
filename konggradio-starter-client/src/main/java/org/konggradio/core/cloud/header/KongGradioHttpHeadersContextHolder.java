/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioHttpHeadersContextHolder.java
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
package org.konggradio.core.cloud.header;

import org.konggradio.core.cloud.props.KongGradioFeignHeadersProperties;
import org.konggradio.core.tool.utils.StringUtil;
import org.konggradio.core.tool.utils.WebUtil;
import org.springframework.core.NamedThreadLocal;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * HttpHeadersContext
 *
 * @author ydlian
 */
public class KongGradioHttpHeadersContextHolder {
	private static final ThreadLocal<HttpHeaders> HTTP_HEADERS_HOLDER = new NamedThreadLocal<>("Feign HttpHeaders");

	/**
	 * 请求和转发的ip
	 */
	private static final String[] ALLOW_HEADS = new String[]{
		"X-Real-IP", "x-forwarded-for", "authorization", "kongGradio-auth", "Authorization", "kongGradio-Auth",
		"x-app","x-stage","x-gw-version","x-client","x-ts","x-api","x-nonce","x-token","x-team-id","x-lang","x-app-id",
		"x-request-id","request-id", "access_token", "access-token"
	};

	static void set(HttpHeaders httpHeaders) {
		HTTP_HEADERS_HOLDER.set(httpHeaders);
	}

	@Nullable
	public static HttpHeaders get() {
		return HTTP_HEADERS_HOLDER.get();
	}

	static void remove() {
		HTTP_HEADERS_HOLDER.remove();
	}

	@Nullable
	public static HttpHeaders toHeaders(
		@Nullable KongGradioFeignAccountGetter accountGetter,
		KongGradioFeignHeadersProperties properties) {
		HttpServletRequest request = WebUtil.getRequest();
		if (request == null) {
			return null;
		}
		HttpHeaders headers = new HttpHeaders();
		String accountHeaderName = properties.getAccount();
		// 如果配置有 account 读取器
		if (accountGetter != null) {
			String xAccountHeader = accountGetter.get(request);
			if (StringUtil.isNotBlank(xAccountHeader)) {
				headers.add(accountHeaderName, xAccountHeader);
			}
		}
		List<String> allowHeadsList = new ArrayList<>(Arrays.asList(ALLOW_HEADS));
		// 如果有传递 account header 继续往下层传递
		allowHeadsList.add(accountHeaderName);
		// 传递请求头
		Enumeration<String> headerNames = request.getHeaderNames();
		if (headerNames != null) {
			List<String> allowed = properties.getAllowed();
			String pattern = properties.getPattern();
			while (headerNames.hasMoreElements()) {
				String key = headerNames.nextElement();
				// 只支持配置的 header
				if (allowHeadsList.contains(key) || allowed.contains(key) || PatternMatchUtils.simpleMatch(pattern, key)) {
					String values = request.getHeader(key);
					// header value 不为空的 传递
					if (StringUtil.isNotBlank(values)) {
						headers.add(key, values);
					}
				}

			}
		}
		return headers.isEmpty() ? null : headers;
	}
}
