/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: LoadBalanceServiceImpl.java
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

package org.konggradio.mertrics.core.load.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import org.konggradio.core.launch.config.FrameworkContext;
import org.konggradio.core.tool.constant.SystemConstant;
import org.konggradio.core.tool.model.load.ServiceEntity;
import org.konggradio.core.tool.utils.RedisUtil;
import org.konggradio.mertrics.core.load.LoadBalanceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

@Service
public class LoadBalanceServiceImpl implements LoadBalanceService {
	@Resource
	RedisUtil redisUtil;


	private String makeLoadInsKey(String appName) {
		return LOAD_PRE + appName;
	}

	private String makeServerRegListKey() {
		return LOAD_SERVER_REG_LIST_KEY;
	}

	private String makeServerRegListKey(String appName) {
		return LOAD_SERVER_REG_LIST_KEY + SERVICE_CONNECTOR + appName;
	}

	@Override
	public Set<Object> addInstance(ServiceEntity instance) {
		String key = makeLoadInsKey(instance.getAppName());
		String value = instance.getHost() + SystemConstant.COLON + instance.getPort();
		redisUtil.sSet(key, value);
		return redisUtil.sGet(key);
	}

	@Override
	public Set<Object> registerInstance(ServiceEntity instance) {
		String key = makeServerRegListKey();
		String value = instance.getAppName() + SERVICE_CONNECTOR + instance.getHost() + SERVICE_CONNECTOR
			+ instance.getPort() + "," + instance.getAppName() + SERVICE_CONNECTOR + instance.getHost();
		redisUtil.sSet(key, value);
		redisUtil.set(key + SERVICE_CONNECTOR + instance.getAppName() + SERVICE_CONNECTOR + instance.getHost(), DateUtil.now());
		return redisUtil.sGet(key);
	}

	@Override
	public Set<Object> removeInstance(ServiceEntity instance) {
		String key = makeLoadInsKey(instance.getAppName());
		String value = instance.getHost() + SystemConstant.COLON + instance.getPort();
		redisUtil.setRemove(key, value);
		return redisUtil.sGet(key);
	}

	@Override
	public boolean clearInstance(String appName) {
		redisUtil.del(makeLoadInsKey(appName));
		return true;
	}

	@Override
	public Set<Object> listInstance(String appName) {
		return redisUtil.sGet(makeLoadInsKey(appName));
	}

	@Override
	public Set<Object> listRegisterInstance() {
		Set<Object> result = new HashSet<>();
		Set<Object> temp = redisUtil.sGet(makeServerRegListKey());
		for (Object e : temp) {
			String str = String.valueOf(e);
			if (str.contains(",")) {
				String[] arr = str.split(",");
				String key = makeServerRegListKey(arr[1]);
				result.add(str + "," + redisUtil.get(key));
			} else {
				result.add(str);
			}
		}
		return result;
	}

	@Override
	public boolean filter() {
		ServiceEntity instance = makeService();
		return filter(instance);
	}

	@Override
	public boolean filter(ServiceEntity instance) {
		Set<Object> instanceSets = listInstance(instance.getAppName());
		if (!ObjectUtil.isEmpty(instanceSets)) {
			String value = instance.getHost() + SystemConstant.COLON + instance.getPort();
			if (instanceSets.size() > SystemConstant.LOAD_INSTANCE_MIN_SIZE && !instanceSets.contains(value)) {

				return false;
			}
		}
		return true;
	}

	@Override
	public ServiceEntity makeService(String ip, Integer port, String appName) {
		String serviceId = ip + SystemConstant.COLON + port;
		String instanceId = appName + SERVICE_CONNECTOR + ip + SERVICE_CONNECTOR + port;
		return ServiceEntity.builder().appName(appName).host(ip).port(port).serviceId(serviceId).instanceId(instanceId).build();
	}

	@Override
	public ServiceEntity makeService() {
		FrameworkContext frameworkContext = FrameworkContext.getSysUserContext();
		String ip = frameworkContext.getIp();
		Integer port = frameworkContext.getPort();
		String appName = frameworkContext.getApplicationName();
		String serviceId = ip + SystemConstant.COLON + port;
		String instanceId = appName + SERVICE_CONNECTOR + ip + SERVICE_CONNECTOR + port;
		return ServiceEntity.builder().appName(appName).host(ip).port(port).serviceId(serviceId).instanceId(instanceId).build();
	}
}
