/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioConsulServiceRegistry.java
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

package org.konggradio.core.launch.consul;

import com.ecwid.consul.v1.ConsulClient;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.RegisterBuilder;
import org.konggradio.core.launch.config.YamlConfig;
import org.konggradio.core.launch.server.ServerInfo;
import org.konggradio.core.launch.service.LauncherCtlService;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.discovery.TtlScheduler;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public final class KongGradioConsulServiceRegistry extends ConsulServiceRegistry {
	private ServerInfo serverInfo;

	public KongGradioConsulServiceRegistry(ConsulClient client, ConsulDiscoveryProperties properties, TtlScheduler ttlScheduler, HeartbeatProperties heartbeatProperties, ServerInfo serverInfo) {
		super(client, properties, ttlScheduler, heartbeatProperties);
		this.serverInfo = serverInfo;
	}

	@Override
	public void register(ConsulRegistration reg) {
		boolean result = LauncherCtlService.update();
		System.out.println(result);
		if (!result) return;
		reg.getService().setId(reg.getService().getName() + "-" + this.serverInfo.getIp() + "-" + this.serverInfo.getPort());
		super.register(reg);
	}

	@Override
	public void deregister(ConsulRegistration reg) {

		String appName = RegisterBuilder.getContext();
		if (buildDeregister(RegisterBuilder.getProfile(appName))) {

		}

	}

	private static boolean buildDeregister(String profile) {

		boolean deregisterFlag = false;
		if (StringUtils.hasText(profile)) {
			List<String> list = new ArrayList<>();
			list.add("application-%s.yml");
			list.add("application.yml");
			String deregisterFlagConfigName = getDeregisterFromConfigFile(list, profile);
			if (StringUtils.hasText(deregisterFlagConfigName)) {
				deregisterFlag = Boolean.parseBoolean(deregisterFlagConfigName);
			}
		}
		return deregisterFlag;
	}

	private static String getDeregisterFromConfigFile(List<String> list, String profile) {
		for (String item : list) {
			String configFile = String.format(item, profile);
			YamlConfig.setYamlFile(configFile);

		}
		return null;
	}


}
