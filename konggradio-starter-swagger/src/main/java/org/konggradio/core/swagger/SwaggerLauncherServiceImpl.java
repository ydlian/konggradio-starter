/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: SwaggerLauncherServiceImpl.java
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
package org.konggradio.core.swagger;

import org.konggradio.core.constant.AppConstant;
import org.konggradio.core.launch.service.LauncherService;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.Ordered;

import java.util.Properties;

/**
 * 初始化Swagger配置
 *
 * @author ydlian
 */
public class SwaggerLauncherServiceImpl implements LauncherService {
	@Override
	public void launcher(SpringApplicationBuilder builder, String appName, String profile) {
		Properties props = System.getProperties();

		if (profile.equals(AppConstant.PROD_CODE)) {
			//props.setProperty("knife4j.production", "true");
		}
		props.setProperty("knife4j.basic.enable", "true");
		props.setProperty("knife4j.basic.username", AppConstant.APPLICATION_ROOT_USER_NAME);
		props.setProperty("knife4j.basic.password", "pass");
		props.setProperty("knife4j.enable", "true");
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}
