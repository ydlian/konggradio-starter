/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioEnvironmentAware.java
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

package org.konggradio.core.launch.config;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.constant.AppConstant;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
@Slf4j
public class KongGradioEnvironmentAware implements EnvironmentAware {

	@Override
	public void setEnvironment(Environment environment) {
		Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources.get(environment);
		Binder binder = new Binder(sources);
		BindResult<Properties> bindResult = binder.bind("spring.profiles", Properties.class);
		if (bindResult != null) {
			Properties properties = bindResult.get();
			Properties props = System.getProperties();
			String env = properties.getProperty("active");
			log.info("-----register-cluster-----:{}", env);
			if (!StringUtils.isBlank(env)) {
				props.setProperty(AppConstant.KONGGRADIO_ENV_KEY, env);
			}
		} else {
			log.info("-----KongGradio have no register cluster config-----");
		}

	}
}
