/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: GatewayMetricsService.java
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

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.ApplicationRegister;
import org.konggradio.core.RegisterBuilder;
import org.konggradio.core.constant.GatewayConstant;
import org.konggradio.core.model.bo.ApplicationRegResultBO;
import org.konggradio.core.tool.utils.RedisUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Properties;

@Service
@Slf4j
public class GatewayMetricsService {
	private static String DOT = ":";

	@Resource
	private RedisUtil redisUtil;

	private static String makeLeftTimesKey(String appName) {
		return GatewayConstant.APP_METRICS_LEFT_TIMES_KEY + DOT + appName;
	}

	private static String makeLeftSecondsKey(String appName) {
		return GatewayConstant.APP_METRICS_LEFT_SECONDS_KEY + DOT + appName;
	}

	private static String makeLeftHoursKey(String appName) {
		return GatewayConstant.APP_METRICS_LEFT_HOURS_KEY + DOT + appName;
	}

	private static String makeAppRegTimeKey(String appName) {
		return GatewayConstant.APP_METRICS_REG_TIME_KEY + DOT + appName;
	}

	private static String makeAppLastConnectKey(String appName) {
		return GatewayConstant.APP_METRICS_LAST_CONNECT_TIME_KEY + DOT + appName;
	}

	private static String makeAppRegResultKey(String appName) {
		return GatewayConstant.APP_METRICS_REG_RESULT_KEY + DOT + appName;
	}

	public ApplicationRegResultBO getCachedRegInfo(String appName) {
		Object appRegTimeVal = redisUtil.get(makeAppRegTimeKey(appName));
		if (ObjectUtil.isNull(appRegTimeVal)) {
			return null;
		}
		ApplicationRegResultBO applicationRegResultBO = getCachedApplicationRegInfoWhenConnect(appName);
		return applicationRegResultBO;
	}

	public ApplicationRegResultBO initCachedRegInfo(String appName, ApplicationRegResultBO applicationRegResultBO) {
		redisUtil.set(makeLeftHoursKey(appName), String.valueOf(applicationRegResultBO.getLeftMetresHours()));
		redisUtil.set(makeLeftSecondsKey(appName), String.valueOf(applicationRegResultBO.getLeftMetresSeconds()));
		redisUtil.set(makeLeftTimesKey(appName), String.valueOf(applicationRegResultBO.getLeftMetresTimes()));
		redisUtil.set(makeAppRegResultKey(appName), String.valueOf(applicationRegResultBO.isRegResult()));
		String lastConnectTimeVal = applicationRegResultBO.getLastConnectTime();
		redisUtil.set(makeAppLastConnectKey(appName), lastConnectTimeVal);
		String regTimeVal = applicationRegResultBO.getRegTime();
		redisUtil.set(makeAppRegTimeKey(appName), regTimeVal);
		return applicationRegResultBO;
	}

	public ApplicationRegResultBO updateCachedApplicationRegInfoWhenConnect(String appName, long secondsUsed) {
		ApplicationRegResultBO old = getCachedApplicationRegInfoWhenConnect(appName);
		Properties environment = System.getProperties();
		String intSecondsVal = environment.getProperty(GatewayConstant.METRICS_INIT_SECONDS_KEY);
		long intSeconds = NumberUtil.parseLong(intSecondsVal);
		DateTime now = new DateTime();
		String nowVal = DateUtil.format(now, ApplicationRegister.YYYYMMDDHHMMSS);
		old.setLastConnectTime(nowVal);
		old.setLeftMetresTimes(old.getLeftMetresTimes() - 1);
		old.setLeftMetresHours((intSeconds - secondsUsed) / 3600L);
		old.setLeftMetresSeconds(intSeconds - secondsUsed);
		if (old.getLeftMetresTimes() <= 0 || old.getLeftMetresSeconds() <= 0 || old.getLeftMetresHours() <= 0) {
			old.setRegResult(false);
		}
		if (!old.isRegResult()) {
			old.setLeftMetresHours(0L);
			old.setLeftMetresSeconds(0L);
			old.setLeftMetresTimes(0L);
		}
		initCachedRegInfo(appName, old);
		return old;
	}

	public ApplicationRegResultBO getCachedApplicationRegInfoWhenConnect(String appName) {
		String leftMetresHoursVal = (String) redisUtil.get(makeLeftHoursKey(appName));
		String leftMetresSecondsVal = (String) redisUtil.get(makeLeftSecondsKey(appName));
		String leftMetresTimesVal = (String) redisUtil.get(makeLeftTimesKey(appName));
		String appRegResultVal = (String) redisUtil.get(makeAppRegResultKey(appName));
		String appLastConnectVal = (String) redisUtil.get(makeAppLastConnectKey(appName));
		DateTime appLastConnectTime = DateUtil.parse(appLastConnectVal, ApplicationRegister.YYYYMMDDHHMMSS);
		String appRegTimeVal = (String) redisUtil.get(makeAppRegTimeKey(appName));

		DateTime appRegTime = DateUtil.parse(appRegTimeVal, ApplicationRegister.YYYYMMDDHHMMSS);
		ApplicationRegResultBO applicationRegResultBO = ApplicationRegResultBO.builder()
			.appName(appName).lastConnectTime(appLastConnectVal)
			.leftMetresHours(Long.valueOf(leftMetresHoursVal))
			.leftMetresSeconds(Long.valueOf(leftMetresSecondsVal))
			.leftMetresTimes(Long.valueOf(leftMetresTimesVal))
			.regResult(Boolean.parseBoolean(appRegResultVal)).regTime(appRegTimeVal)
			.build();
		return applicationRegResultBO;
	}

	public ApplicationRegResultBO reset(String appName, String password) {
		Assert.isTrue(RegisterBuilder.checkFirstSecurityInTable(appName, password));
		Properties environment = System.getProperties();
		Assert.notBlank(appName);
		String intTimes = environment.getProperty(GatewayConstant.METRICS_INIT_TIMES_KEY);
		String intHours = environment.getProperty(GatewayConstant.METRICS_INIT_HOURS_KEY);
		String intSeconds = environment.getProperty(GatewayConstant.METRICS_INIT_SECONDS_KEY);
		Assert.notBlank(intTimes);
		Assert.notBlank(intHours);
		Assert.notBlank(intSeconds);
		DateTime now;
		now = new DateTime();
		String nowVal = DateUtil.format(now, ApplicationRegister.YYYYMMDDHHMMSS);
		ApplicationRegResultBO applicationRegResultBO = ApplicationRegResultBO.builder().appName(appName)
			.lastConnectTime(nowVal).leftMetresHours(Long.valueOf(intHours))
			.leftMetresSeconds(Long.valueOf(intSeconds)).leftMetresTimes(Long.valueOf(intTimes))
			.regResult(true).regTime(nowVal).build();
		initCachedRegInfo(appName, applicationRegResultBO);
		return applicationRegResultBO;
	}



}
