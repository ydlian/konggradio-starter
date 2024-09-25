/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongCloudSpringExtension.java
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

package org.konggradio.core.test;


import org.junit.jupiter.api.extension.ExtensionContext;
import org.konggradio.core.constant.NacosConstant;
import org.konggradio.core.constant.SentinelConstant;
import org.konggradio.core.constants.RegisterConstants;
import org.konggradio.core.constant.AppConstant;
import org.konggradio.core.launch.service.LauncherService;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 设置启动参数
 *
 * @author ydlian
 */
public class KongGradioSpringExtension extends SpringExtension {

	@Override
	public void beforeAll(@NonNull ExtensionContext context) throws Exception {
		super.beforeAll(context);
		setUpTestClass(context);
	}

	private void setUpTestClass(ExtensionContext context) {
		Class<?> clazz = context.getRequiredTestClass();
		KongGradioBootTest kongGradioBootTest = AnnotationUtils.getAnnotation(clazz, KongGradioBootTest.class);
		if (kongGradioBootTest == null) {
			throw new KongGradioBootTestException(String.format("%s must be @BootTest .", clazz));
		}
		String appName = kongGradioBootTest.appName();
		String profile = kongGradioBootTest.profile();
		boolean isLocalDev = RegisterConstants.isLocalDev();
		Properties props = System.getProperties();
		props.setProperty("konggradio.env", profile);
		props.setProperty("konggradio.name", appName);
		props.setProperty("konggradio.is-local", String.valueOf(isLocalDev));
		props.setProperty("konggradio.dev-mode", profile.equals(AppConstant.PROD_CODE) ? "false" : "true");
		props.setProperty("konggradio.service.version", AppConstant.APPLICATION_VERSION);
		props.setProperty("spring.application.name", appName);
		props.setProperty("spring.profiles.active", profile);
		props.setProperty("info.version", AppConstant.APPLICATION_VERSION);
		props.setProperty("info.desc", appName);
		props.setProperty("spring.cloud.nacos.discovery.server-addr", NacosConstant.NACOS_ADDR);
		props.setProperty("spring.cloud.nacos.config.server-addr", NacosConstant.NACOS_ADDR);
		props.setProperty("spring.cloud.nacos.config.prefix", NacosConstant.NACOS_CONFIG_PREFIX);
		props.setProperty("spring.cloud.nacos.config.file-extension", NacosConstant.NACOS_CONFIG_FORMAT);
		props.setProperty("spring.cloud.sentinel.transport.dashboard", SentinelConstant.SENTINEL_ADDR);
		props.setProperty("spring.main.allow-bean-definition-overriding", "true");
		// 加载自定义组件
		if (kongGradioBootTest.enableLoader()) {
			List<LauncherService> launcherList = new ArrayList<>();
			SpringApplicationBuilder builder = new SpringApplicationBuilder(clazz);
			ServiceLoader.load(LauncherService.class).forEach(launcherList::add);
			launcherList.stream().sorted(Comparator.comparing(LauncherService::getOrder)).collect(Collectors.toList())
				.forEach(launcherService -> launcherService.launcher(builder, appName, profile));
		}
		System.err.printf("---[junit.test]:[%s]---启动中，读取到的环境变量:[%s]%n", appName, profile);
	}

}
