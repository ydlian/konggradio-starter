/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: CustomizedGatewayLoadBalancer.java
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

package org.konggradio.core.boot.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.utils.IpUtils;
import org.apache.commons.lang3.StringUtils;

import org.konggradio.core.launch.utils.INetUtil;
import org.konggradio.mertrics.MetricRegistry;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 负载均衡算法的默认实现是 {@link org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer}
 */
@Slf4j
public class CustomizedGatewayLoadBalancer implements ReactorServiceInstanceLoadBalancer {

	private final String serviceId;
	private final AtomicInteger position;
	private final LoadBalancerTypeEnum type;
	private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

	public CustomizedGatewayLoadBalancer(String serviceId,
                                         LoadBalancerTypeEnum type,
                                         ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider) {
		this(serviceId, new Random().nextInt(1000), type, serviceInstanceListSupplierProvider);
	}

	public CustomizedGatewayLoadBalancer(String serviceId,
                                         int seedPosition,
                                         LoadBalancerTypeEnum type,
                                         ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider) {
		this.serviceId = serviceId;
		this.position = new AtomicInteger(seedPosition);
		this.type = type;
		this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
	}

	@Override
	public Mono<Response<ServiceInstance>> choose(Request request) {
		ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable(NoopServiceInstanceListSupplier::new);
		return supplier.get(request).next().map(serviceInstances -> processInstanceResponse(request, supplier, serviceInstances));
	}

	private Response<ServiceInstance> processInstanceResponse(Request request,
															  ServiceInstanceListSupplier supplier,
															  List<ServiceInstance> serviceInstances) {
		Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(request, serviceInstances);
		if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
			((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
		}
		return serviceInstanceResponse;
	}

	private Response<ServiceInstance> getInstanceResponse(Request request, List<ServiceInstance> instances) {
		if (instances.isEmpty()) {
			if (log.isWarnEnabled()) {
				log.warn("No servers available for service: " + serviceId);
			}
			return new EmptyResponse();
		}
		LoadBalancerTypeEnum balancerTypeEnum = getByRequest(request);
		if (balancerTypeEnum == null) {
			balancerTypeEnum = type;
		}
		if (Objects.equals(balancerTypeEnum, LoadBalancerTypeEnum.ROUND_ROBIN)) {
			return this.getRoundRobinInstance(instances);
		} else if (Objects.equals(balancerTypeEnum, LoadBalancerTypeEnum.RANDOM)) {
			return this.getRandomInstance(instances);
		} else if (Objects.equals(balancerTypeEnum, LoadBalancerTypeEnum.DEV)) {
			return this.getDevelopmentInstance(instances);
		} else if (Objects.equals(balancerTypeEnum, LoadBalancerTypeEnum.GATEWAY)) {
			return this.getGatewayDevelopmentInstance(request, instances);
		} else if (Objects.equals(balancerTypeEnum, LoadBalancerTypeEnum.FIX_FIRST)) {
			return this.getFixFirstInstance(instances);
		}else if (Objects.equals(balancerTypeEnum, LoadBalancerTypeEnum.DENY)) {
			return new EmptyResponse();
		}
		return this.getRoundRobinInstance(instances);
	}

	private LoadBalancerTypeEnum getByRequest(Request request) {
		if (MetricRegistry.getApplicationLcTmStatus()) {
			return LoadBalancerTypeEnum.FIX_FIRST;
		}
		DefaultRequest<RequestDataContext> defaultRequest = Convert.convert(new TypeReference<DefaultRequest<RequestDataContext>>() {
		}, request);
		RequestDataContext context = defaultRequest.getContext();
		RequestData clientRequest = context.getClientRequest();
		HttpHeaders headers = clientRequest.getHeaders();
		List<String> strategyList = headers.get("x-lbs");
		if (strategyList == null || strategyList.isEmpty()) {
			return null;
		}
		String strategy = strategyList.get(0);
		return LoadBalancerTypeEnum.findByName(strategy);
	}

	/**
	 * 获取网关本机实例
	 *
	 * @param instances 实例
	 * @return {@link Response }<{@link ServiceInstance }>
	 * @author : lwq
	 * @date : 2022-12-15 14:22:13
	 */
	private Response<ServiceInstance> getGatewayDevelopmentInstance(Request request, List<ServiceInstance> instances) {

		//把request转为默认的DefaultRequest，从request中拿到请求的ip信息，再选择ip一样的微服务
		DefaultRequest<RequestDataContext> defaultRequest = Convert.convert(new TypeReference<DefaultRequest<RequestDataContext>>() {
		}, request);
		RequestDataContext context = defaultRequest.getContext();
		RequestData clientRequest = context.getClientRequest();
		HttpHeaders headers = clientRequest.getHeaders();
		String requestIp = INetUtil.getIpAddressFromHttpHeaders(headers);
		log.debug("客户端请求gateway的ip:{}", requestIp);

		//先取得和本地ip一样的服务，如果没有则按默认来取
		for (ServiceInstance instance : instances) {
			String currentServiceId = instance.getServiceId();
			String host = instance.getHost();
			log.debug("注册服务：{}，ip：{}", currentServiceId, host);
			if (StringUtils.isNotEmpty(host) && StringUtils.equals(requestIp, host)) {
				return new DefaultResponse(instance);
			}
		}
		return getRoundRobinInstance(instances);
	}

	private Response<ServiceInstance> getDevelopmentInstance(List<ServiceInstance> instances) {

		String hostIp = IpUtils.getLocalIp();
		for (ServiceInstance instance : instances) {
			String currentServiceId = instance.getServiceId();
			String host = instance.getHost();
			if (StringUtils.isNotEmpty(host) && StringUtils.equals(hostIp, host)) {
				return new DefaultResponse(instance);
			}
		}
		return getRoundRobinInstance(instances);
	}

	/**
	 * 使用随机算法
	 * 参考{link {@link org.springframework.cloud.loadbalancer.core.RandomLoadBalancer}}
	 *
	 * @param instances 实例
	 * @return {@link Response }<{@link ServiceInstance }>
	 * @author : lwq
	 * @date : 2022-12-15 13:32:11
	 */
	private Response<ServiceInstance> getRandomInstance(List<ServiceInstance> instances) {
		int index = ThreadLocalRandom.current().nextInt(instances.size());
		ServiceInstance instance = instances.get(index);
		return new DefaultResponse(instance);
	}

	/**
	 * 使用RoundRobin机制获取节点
	 *
	 * @param instances 实例
	 * @return {@link Response }<{@link ServiceInstance }>
	 * @author : lwq
	 * @date : 2022-12-15 13:28:31
	 */
	private Response<ServiceInstance> getRoundRobinInstance(List<ServiceInstance> instances) {
		// 每一次计数器都自动+1，实现轮询的效果
		int pos = this.position.incrementAndGet() & Integer.MAX_VALUE;
		ServiceInstance instance = instances.get(pos % instances.size());
		return new DefaultResponse(instance);
	}


	private Response<ServiceInstance> getFixFirstInstance(List<ServiceInstance> instances) {
		ServiceInstance resultIns = instances.get(0);
		String instanceId = instances.get(0).getInstanceId();

		for (int i = 0; i < instances.size(); i++) {
			ServiceInstance ins = instances.get(i);
			if (StrUtil.compareIgnoreCase(instanceId, ins.getInstanceId(), true) > 0) {
				instanceId = ins.getServiceId();
				resultIns = ins;
			}
		}
		return new DefaultResponse(resultIns);
	}
}
