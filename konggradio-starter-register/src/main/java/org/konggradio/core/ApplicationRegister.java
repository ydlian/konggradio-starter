/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ApplicationRegister.java
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

package org.konggradio.core;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import com.google.gson.Gson;
import org.konggradio.core.constant.GatewayConstant;
import org.konggradio.core.constants.RegisterConstants;

import org.konggradio.core.model.bo.ApplicationRegResultBO;
import org.springframework.util.StringUtils;

import java.util.Properties;

//应用注册
public class ApplicationRegister {
	private static String DOT = ".";
	public static int[][] LATCH_MATRIX = new int[][]{{100, 10000}, {4, 9}, {3000, 7500, 6500}, {51840, 17280, 8640},{200,400},{5,10}};

	public static final String APP_CANNOT_FOUND_GATEWAY = "No gateway found!";
	public static final String APP_ENV_ERROR = "Env Error!";
	public static final String APP_SYSTEM_ERROR = "System Error!";
	public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
	public static final String DEF_YYYYMMDDHHMMSS = "2022-10-10 00:00:00";
	public static final String DEF_TTH_YYYYMMDDHHMMSS = "2023-10-10 00:00:00";
	public static final String APP_RUN_LOCAL_MOD = "Application run in local mode: ";

	private static String makeLetTimesKey(String appName) {
		return GatewayConstant.APP_METRICS_LEFT_TIMES_KEY + DOT + appName;
	}

	private static String makeLetSecondsKey(String appName) {
		return GatewayConstant.APP_METRICS_LEFT_SECONDS_KEY + DOT + appName;
	}

	private static String makeLetHoursKey(String appName) {
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

	private static String makeAppRegResultBoKey(String appName) {
		return RegisterConstants.APP_REG_RESULT_BO_KEY + DOT + appName;
	}

	private static String makeAppRegResultCacheKey(String appName) {
		return RegisterConstants.APP_REG_RESULT_BO_KEY + DOT + "register." + appName;
	}

	private static String makeAppTthCacheKey(String appName) {
		return RegisterConstants.APP_REG_RESULT_BO_KEY + DOT + "tth." + appName;
	}

	private static String makeAppLcTimesKey(String appName) {
		return RegisterConstants.APP_REG_RESULT_BO_KEY + DOT + "lc.tm." + appName;
	}

	public static boolean getEnvMarkStatus() {
		Properties environment = System.getProperties();
		return RegisterBuilder.getStatusOk(environment);
	}

	public static boolean setApplicationRegResultBo(ApplicationRegResultBO applicationRegInfo) {
		Properties environment = System.getProperties();
		String appName = applicationRegInfo.getAppName();
		Gson gson = new Gson();
		environment.setProperty(makeAppRegResultBoKey(appName), gson.toJson(applicationRegInfo));
		return true;
	}


	public static boolean getApplicationLcRegStatus() {
		String appName = getCurrentApplicationName();
		Properties environment = System.getProperties();
		String key = makeAppRegResultCacheKey(appName);
		String value = environment.getProperty(key);
		if (value == null) return true;
		if (value.equalsIgnoreCase("true")) return true;
		return false;
	}

	private static boolean checkCaller() {
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		if (stack != null && stack.length > 2) {
			StackTraceElement caller = stack[2];
			//System.out.println(new Gson().toJson(stack));
		}
		return true;
	}

	public static boolean updateApplicationLcRegStatus(String appName, Boolean status) {
		if (!checkCaller()) return false;
		Properties environment = System.getProperties();
		String key = makeAppRegResultCacheKey(appName);
		environment.setProperty(key, String.valueOf(status));
		return true;
	}


	public static boolean getApplicationStatus(String appName) {
		Properties environment = System.getProperties();
		String appRegResultVal = environment.getProperty(makeAppRegResultKey(appName));
		return appRegResultVal != null && appRegResultVal.equalsIgnoreCase("true");
	}

	public static void setGatewayActiveMode(String appName, boolean mode) {
		Properties environment = System.getProperties();
		environment.setProperty(RegisterConstants.GATEWAY_ACTIVE_MODE_PROP + RegisterConstants.DOT + appName, String.valueOf(mode));
	}

	public static boolean setCurrentApplicationName(String appName) {
		Properties environment = System.getProperties();
		environment.setProperty(RegisterConstants.APP_REG_NAME_KEY, appName);
		return true;
	}

	public static String getCurrentApplicationName() {
		Properties environment = System.getProperties();
		String value = environment.getProperty(RegisterConstants.APP_REG_NAME_KEY);
		if (!StringUtils.hasText(value)) {
			return "app";
		}
		return value;
	}

	public static void setGatewayAdminType(String appName, boolean type) {
		Properties environment = System.getProperties();
		environment.setProperty(RegisterConstants.GATEWAY_ADMIN_TYPE_PROP + RegisterConstants.DOT + appName, String.valueOf(type));

	}

	public static boolean getGatewayActiveMode(String appName) {

		Properties environment = System.getProperties();
		String value = environment.getProperty(RegisterConstants.GATEWAY_ACTIVE_MODE_PROP + RegisterConstants.DOT + appName);
		return Boolean.parseBoolean(value);
	}

	public static boolean getGatewayAdminType(String appName) {
		Properties environment = System.getProperties();
		String value = environment.getProperty(RegisterConstants.GATEWAY_ADMIN_TYPE_PROP + RegisterConstants.DOT + appName);
		return Boolean.parseBoolean(value);
	}

	public static ApplicationRegResultBO handleApplicationFirstTimeQueryGateway(String appName) {
		Properties environment = System.getProperties();
		Assert.notBlank(appName);
		String intTimes = String.valueOf(GatewayConstant.DEF_METRICS_INIT_TIMES);
		String intHours = String.valueOf(GatewayConstant.DEF_METRICS_INIT_HOURS);
		String intSeconds = String.valueOf(GatewayConstant.DEF_METRICS_INIT_SECONDS);

		String appRegTimeVal = environment.getProperty(makeAppRegTimeKey(appName));
		if (!StringUtils.hasText(appRegTimeVal)) {
			DateTime now = new DateTime();
			String nowVal = DateUtil.format(now, YYYYMMDDHHMMSS);
			//首次连接
			ApplicationRegResultBO applicationRegResultBO = ApplicationRegResultBO.builder()
				.appName(appName).lastConnectTime(nowVal)
				.leftMetresHours(Long.valueOf(intHours))
				.leftMetresSeconds(Long.valueOf(intSeconds))
				.leftMetresTimes(Long.valueOf(intTimes))
				.regResult(true).regTime(nowVal)
				.build();
			initApplicationWithData(appName, applicationRegResultBO);
			return applicationRegResultBO;
		} else {
			DateTime now = new DateTime();
			DateTime appRegTime = DateUtil.parse(appRegTimeVal, YYYYMMDDHHMMSS);
			long usedSeconds = DateUtil.between(now, appRegTime, DateUnit.SECOND, true);
			ApplicationRegResultBO applicationRegResultBO = updateApplicationRegInfoWhenConnect(appName, usedSeconds);
			return applicationRegResultBO;
		}

	}

	private static ApplicationRegResultBO updateApplicationRegInfoWhenConnect(String appName, long secondsUsed) {
		ApplicationRegResultBO old = getApplicationRegInfoWhenConnect(appName);
		Properties environment = System.getProperties();
		String intSecondsVal = environment.getProperty(GatewayConstant.METRICS_INIT_SECONDS_KEY);
		long intSeconds = NumberUtil.parseLong(intSecondsVal);

		DateTime now = new DateTime();
		String nowVal = DateUtil.format(now, YYYYMMDDHHMMSS);
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
		initApplicationWithData(appName, old);
		return old;
	}

	public static ApplicationRegResultBO initApplicationWithData(String appName, ApplicationRegResultBO applicationRegResultBO) {
		Properties environment = System.getProperties();
		environment.setProperty(makeLetHoursKey(appName), String.valueOf(applicationRegResultBO.getLeftMetresHours()));
		environment.setProperty(makeLetSecondsKey(appName), String.valueOf(applicationRegResultBO.getLeftMetresSeconds()));
		environment.setProperty(makeLetTimesKey(appName), String.valueOf(applicationRegResultBO.getLeftMetresTimes()));
		environment.setProperty(makeAppRegResultKey(appName), String.valueOf(applicationRegResultBO.isRegResult()));
		String lastConnectTimeVal = applicationRegResultBO.getLastConnectTime();
		environment.setProperty(makeAppLastConnectKey(appName), lastConnectTimeVal);
		String regTimeVal = applicationRegResultBO.getRegTime();
		environment.setProperty(makeAppRegTimeKey(appName), regTimeVal);
		return applicationRegResultBO;
	}

	public static ApplicationRegResultBO getApplicationRegInfoWhenConnect(String appName) {
		Properties environment = System.getProperties();
		String leftMetresHoursVal = environment.getProperty(makeLetHoursKey(appName));
		String leftMetresSecondsVal = environment.getProperty(makeLetSecondsKey(appName));
		String leftMetresTimesVal = environment.getProperty(makeLetTimesKey(appName));
		String appRegResultVal = environment.getProperty(makeAppRegResultKey(appName));
		String appLastConnectVal = environment.getProperty(makeAppLastConnectKey(appName));
		DateTime appLastConnectTime = DateUtil.parse(appLastConnectVal, YYYYMMDDHHMMSS);
		String appLastConnectTimeVal = DateUtil.format(appLastConnectTime, YYYYMMDDHHMMSS);
		String appRegTimeVal = environment.getProperty(makeAppRegTimeKey(appName));


		ApplicationRegResultBO applicationRegResultBO = ApplicationRegResultBO.builder()
			.appName(appName).lastConnectTime(appLastConnectTimeVal)
			.leftMetresHours(Long.valueOf(leftMetresHoursVal))
			.leftMetresSeconds(Long.valueOf(leftMetresSecondsVal))
			.leftMetresTimes(Long.valueOf(leftMetresTimesVal))
			.regResult(Boolean.parseBoolean(appRegResultVal)).regTime(appRegTimeVal)
			.build();
		return applicationRegResultBO;
	}
}
