/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: StartEventListener.java
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
package org.konggradio.core.launch;

import org.konggradio.core.GatewayBuilder;
import org.konggradio.core.launch.config.FrameworkContext;
import org.konggradio.core.launch.constant.EnvConstant;
import org.konggradio.core.launch.utils.INetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 项目启动事件通知
 *
 * @author ydlian
 */
//@Slf4j
@Configuration(proxyBeanMethods = false)
public class StartEventListener {

	private static final Logger logger = LoggerFactory.getLogger(EnvConstant.class);

	private static void syncLock(long value, TimeUnit unit) {
		CountDownLatch latch = new CountDownLatch(1);
		try {
			latch.await((value), unit);
		} catch (InterruptedException e) {
		}
	}

	public static String buildFlowFromProperty(String profile) {
		Properties props = System.getProperties();
		String value = props.getProperty("spring.application.flow.limitCount");
		if (!StringUtils.hasText(value)) {
			value =  KongGradioApplication.buildFlowConfig(profile);
		}
		if (!StringUtils.hasText(value)) {
			value = String.valueOf(EnvConstant.LATCH_DB_MATRIX[0][1]);
		}
		return value;
	}

	@Async
	@Order
	@EventListener(WebServerInitializedEvent.class)
	public void afterStart(WebServerInitializedEvent event) {
		Environment environment = event.getApplicationContext().getEnvironment();
		String appName = environment.getProperty("spring.application.name");
		int localPort = event.getWebServer().getPort();
		String host = INetUtil.findFirstNonLoopbackAddress();
		String gatewayDomain = FrameworkContext.getSysUserContext().getGatewayDomain();
		FrameworkContext.getSysUserContext().setPort(localPort);
		FrameworkContext.getSysUserContext().setIp(host);
		FrameworkContext.getSysUserContext().setHostName(INetUtil.getHostName());
		try {
			GatewayBuilder.reportService(gatewayDomain,appName,host,localPort);
		}catch (Exception e){

		}

		String profile = StringUtils.arrayToCommaDelimitedString(environment.getActiveProfiles());
		logger.info("---[{}]--- start succeed,port:[{}],env:[{}] ---", appName, localPort, profile);
		FrameworkContext.getSysUserContext().setProfile(profile);
		//KongCloudApplication.checkRegisterCenterConfigAddress("192.168.0.0.1");
		FrameworkContext frameworkContext = FrameworkContext.getSysUserContext();
		if (!StringUtils.hasText(frameworkContext.getFlowLimitCount())) {
			frameworkContext.setApplicationName(appName);
			frameworkContext.setProfile(profile);
			frameworkContext.setFlowLimitCount(buildFlowFromProperty(profile));
		}
		if (frameworkContext.getTthStatus()) {
			syncLock(10 * EnvConstant.LC_MATRIX[0][0], TimeUnit.SECONDS);
			//System.exit(0);
		}
	}
}
