/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ApplicationConfigService.java
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

package org.konggradio.mertrics.core;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.ApplicationRegister;
import org.konggradio.core.RegisterBuilder;
import org.konggradio.core.launch.config.FrameworkContext;
import org.konggradio.core.tool.utils.RedisUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class ApplicationConfigService extends ApplicationRegister implements ApplicationContextAware {

	@Resource
	RedisUtil redisUtil;

	private ApplicationContext context;
	@Resource
	SecurityCheckService securityCheckService;

	public String getWorkerName() {
		return workerName;
	}

	private String workerName;

	public boolean isSkipSelect(String context) {
		return true;
	}

	public void checkReconnect() {
		boolean c = RandomUtil.randomInt(1, 100) < RegisterBuilder.get();
		RegisterBuilder.setEnv(c);
		if (c) {
			doReconnect();
		}

	}

	private void savePerf(Object object){
		Boolean perfLock = false;
		if (object != null) {
			perfLock = Boolean.parseBoolean(String.valueOf(object));
		}
		FrameworkContext.getSysUserContext().setPerfLockFlag(perfLock);
	}

	public Boolean checkPass(String password) {
		return securityCheckService.checkGateSecondaryPass(password);
	}


	private void doReconnect() {
		//
	}

	private void doReload() {
		((ConfigurableApplicationContext) (context)).close();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
}
