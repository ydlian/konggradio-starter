/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: RestTemplateConfiguration.java
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


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.konggradio.core.cloud.header.KongGradioFeignAccountGetter;
import org.konggradio.core.cloud.props.KongGradioFeignHeadersProperties;
import org.konggradio.core.tool.utils.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.commons.httpclient.OkHttpClientConnectionPoolFactory;
import org.springframework.cloud.commons.httpclient.OkHttpClientFactory;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Http RestTemplateHeaderInterceptor 配置
 *
 * @author ydlian
 */
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@ConditionalOnClass(okhttp3.OkHttpClient.class)
@EnableConfigurationProperties(KongGradioFeignHeadersProperties.class)
public class RestTemplateConfiguration {
	private final ObjectMapper objectMapper;

	/**
	 * dev, test 环境打印出BODY
	 *
	 * @return HttpLoggingInterceptor
	 */
	@Bean("httpLoggingInterceptor")
	@Profile({"dev", "test", "qa", "pre"})
	public HttpLoggingInterceptor testLoggingInterceptor() {
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new OkHttpSlf4jLogger());
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		return interceptor;
	}

	/**
	 * ontest 环境 打印 请求头
	 *
	 * @return HttpLoggingInterceptor
	 */
	@Bean("httpLoggingInterceptor")
	@Profile("ontest")
	public HttpLoggingInterceptor onTestLoggingInterceptor() {
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new OkHttpSlf4jLogger());
		interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
		return interceptor;
	}

	/**
	 * prod 环境只打印请求url
	 *
	 * @return HttpLoggingInterceptor
	 */
	@Bean("httpLoggingInterceptor")
	@Profile({"prod", "prod-gray", "prod-gqt", "prod-saas", "prod-ggj"})
	public HttpLoggingInterceptor prodLoggingInterceptor() {
		HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new OkHttpSlf4jLogger());
		interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
		return interceptor;
	}

	/**
	 * okhttp3 链接池配置
	 *
	 * @param connectionPoolFactory 链接池配置
	 * @param httpClientProperties  httpClient配置
	 * @return okhttp3.ConnectionPool
	 */
	@Bean
	@ConditionalOnMissingBean(okhttp3.ConnectionPool.class)
	public okhttp3.ConnectionPool httpClientConnectionPool(
		FeignHttpClientProperties httpClientProperties,
		OkHttpClientConnectionPoolFactory connectionPoolFactory) {
		Integer maxTotalConnections = httpClientProperties.getMaxConnections();
		Long timeToLive = httpClientProperties.getTimeToLive();
		TimeUnit ttlUnit = httpClientProperties.getTimeToLiveUnit();
		return connectionPoolFactory.create(maxTotalConnections, timeToLive, ttlUnit);
	}

	/**
	 * 配置OkHttpClient
	 *
	 * @param httpClientFactory    httpClient 工厂
	 * @param connectionPool       链接池配置
	 * @param httpClientProperties httpClient配置
	 * @param interceptor          拦截器
	 * @return OkHttpClient
	 */
	@Bean
	@ConditionalOnMissingBean(okhttp3.OkHttpClient.class)
	public okhttp3.OkHttpClient httpClient(
		OkHttpClientFactory httpClientFactory,
		okhttp3.ConnectionPool connectionPool,
		FeignHttpClientProperties httpClientProperties,
		HttpLoggingInterceptor interceptor) {
		Boolean followRedirects = httpClientProperties.isFollowRedirects();
		Integer connectTimeout = httpClientProperties.getConnectionTimeout();
		return httpClientFactory.createBuilder(httpClientProperties.isDisableSslValidation())
			.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
			.writeTimeout(30, TimeUnit.SECONDS)
			.readTimeout(30, TimeUnit.SECONDS)
			.followRedirects(followRedirects)
			.connectionPool(connectionPool)
			.addInterceptor(interceptor)
			.build();
	}

	@Bean
	public RestTemplateHeaderInterceptor requestHeaderInterceptor(
		@Autowired(required = false) @Nullable KongGradioFeignAccountGetter accountGetter,
		KongGradioFeignHeadersProperties properties) {
		return new RestTemplateHeaderInterceptor(accountGetter, properties);
	}

	/**
	 * 普通的 RestTemplate，不透传请求头，一般只做外部 http 调用
	 *
	 * @param httpClient OkHttpClient
	 * @return RestTemplate
	 */
	@Bean
	@ConditionalOnMissingBean(RestTemplate.class)
	public RestTemplate restTemplate(okhttp3.OkHttpClient httpClient) {
		RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory(httpClient));
		configMessageConverters(restTemplate.getMessageConverters());
		return restTemplate;
	}

	/**
	 * 支持负载均衡的 LbRestTemplate
	 *
	 * @param httpClient  OkHttpClient
	 * @param interceptor RestTemplateHeaderInterceptor
	 * @return LbRestTemplate
	 */
	@Bean
	@LoadBalanced
	@ConditionalOnMissingBean(LbRestTemplate.class)
	public LbRestTemplate lbRestTemplate(okhttp3.OkHttpClient httpClient, RestTemplateHeaderInterceptor interceptor) {
		LbRestTemplate lbRestTemplate = new LbRestTemplate(new OkHttp3ClientHttpRequestFactory(httpClient));
		lbRestTemplate.setInterceptors(Collections.singletonList(interceptor));
		configMessageConverters(lbRestTemplate.getMessageConverters());
		return lbRestTemplate;
	}

	private void configMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.removeIf(x -> x instanceof StringHttpMessageConverter || x instanceof MappingJackson2HttpMessageConverter);
		converters.add(new StringHttpMessageConverter(Charsets.UTF_8));
		converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
	}
}
