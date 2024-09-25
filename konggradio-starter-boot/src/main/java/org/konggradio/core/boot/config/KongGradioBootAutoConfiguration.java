/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioBootAutoConfiguration.java
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
package org.konggradio.core.boot.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.launch.props.kongProperties;
import org.konggradio.core.tool.constant.SystemConstant;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 配置类
 *
 * @author ydlian
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
	kongProperties.class
})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@AllArgsConstructor
public class KongGradioBootAutoConfiguration {

	private kongProperties kongProperties;

	@Bean
	public SystemConstant fileConst() {
		SystemConstant me = SystemConstant.me();

		//设定开发模式
		me.setDevMode(("dev".equals(kongProperties.getEnv())));

		//设定文件上传远程地址
		me.setDomain(kongProperties.get("upload-domain", "http://localhost:8888"));

		//设定文件上传是否为远程模式
		me.setRemoteMode(kongProperties.getBoolean("remote-mode", true));

		return me;
	}

}
