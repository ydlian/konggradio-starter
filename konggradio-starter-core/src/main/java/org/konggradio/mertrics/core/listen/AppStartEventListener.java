/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: AppStartEventListener.java
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
package org.konggradio.mertrics.core.listen;

import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.launch.service.LauncherCtlService;
import org.konggradio.core.tool.utils.RedisUtil;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 项目启动事件通知,启动后执行一次
 *
 * @author ydlian
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class AppStartEventListener {

	@Resource
	RedisUtil redisUtil;


	@Async
	@Order
	@EventListener(WebServerInitializedEvent.class)
	public void afterStart(WebServerInitializedEvent event) {
		Environment environment = event.getApplicationContext().getEnvironment();
		String appName = environment.getProperty("spring.application.name");
		String profile = StringUtils.arrayToCommaDelimitedString(environment.getActiveProfiles());
		log.info("---[{}] start succeed,profile:{},{}. ---", appName, profile, LauncherCtlService.update());
	}
}
