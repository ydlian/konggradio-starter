/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioHttpHeadersCallable.java
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
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;

import java.util.concurrent.Callable;

/**
 * HttpHeaders hystrix Callable
 *
 * @param <V> 泛型标记
 * @author ydlian
 */
public class KongGradioHttpHeadersCallable<V> implements Callable<V> {
	private final Callable<V> delegate;
	@Nullable
	private HttpHeaders httpHeaders;

	public KongGradioHttpHeadersCallable(Callable<V> delegate,
										 @Nullable KongGradioFeignAccountGetter accountGetter,
										 KongGradioFeignHeadersProperties properties) {
		this.delegate = delegate;
		this.httpHeaders = KongGradioHttpHeadersContextHolder.toHeaders(accountGetter, properties);
	}

	@Override
	public V call() throws Exception {
		if (httpHeaders == null) {
			return delegate.call();
		}
		try {
			KongGradioHttpHeadersContextHolder.set(httpHeaders);
			return delegate.call();
		} finally {
			KongGradioHttpHeadersContextHolder.remove();
			httpHeaders.clear();
			httpHeaders = null;
		}
	}
}
