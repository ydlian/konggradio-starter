/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: RegisterBuilder.java
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

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import org.apache.commons.codec.binary.Hex;
import org.konggradio.core.constant.*;
import org.konggradio.core.constants.RegisterConstants;

import org.konggradio.core.model.bo.GatewayAppMetricsBO;
import org.konggradio.core.model.bo.RegisterBO;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class RegisterBuilder {
	private static final List<String> DEFAULT_NO_METRIC_URLS = new ArrayList<>();


	public static Class getMainClassEntry() {
		return mainClassEntry;
	}

	public static void setMainClassEntry(Class mainClassEntry) {
		RegisterBuilder.mainClassEntry = mainClassEntry;
	}


	private static Class mainClassEntry;
	private static String COUNTER_INIT_STR = "1";
	/**
	 * 访问计数
	 */
	private static long counterNumber = 0L;

	static {
		DEFAULT_NO_METRIC_URLS.add("/*/health/**");
		DEFAULT_NO_METRIC_URLS.add("/auth/metric/**");
		DEFAULT_NO_METRIC_URLS.add("/*/auth/metric/**");
		DEFAULT_NO_METRIC_URLS.add("/boot/**");
		DEFAULT_NO_METRIC_URLS.add("/*/boot/**");
	}

	public static String getCommonLicPath() {
		if (RegisterConstants.isLocalDev()) {
			String userDir = System.getProperty("user.home");
			return userDir + File.separator + AppConstant.APPLICATION_ROOT_USER_NAME + File.separator
				+ RegisterConstants.COM_LIC_FILE_SHORT;
		}
		return RegisterConstants.PWD_FILE_ROOT + AppConstant.APPLICATION_ROOT_USER_NAME + File.separator
			+ RegisterConstants.COM_LIC_FILE_SHORT;

	}

	public static String getCommonPwdPath() {
		if (RegisterConstants.isLocalDev()) {
			String userDir = System.getProperty("user.home");
			return userDir + File.separator + AppConstant.APPLICATION_ROOT_USER_NAME + File.separator
				+ RegisterConstants.PWD_FILE_SHORT;
		}
		return RegisterConstants.PWD_FILE_ROOT + AppConstant.APPLICATION_ROOT_USER_NAME + File.separator
			+ RegisterConstants.PWD_FILE_SHORT;

	}

	public static String getLicPath(String appName) {
		String filePath = "";
		String rootPath = "";
		if (RegisterConstants.isLocalDev()) {
			String userDir = System.getProperty("user.home");
			rootPath = userDir + File.separator + AppConstant.APPLICATION_ROOT_USER_NAME
				+ File.separator;
		} else {
			rootPath = RegisterConstants.PWD_FILE_ROOT + File.separator
				+ AppConstant.APPLICATION_ROOT_USER_NAME + File.separator;
		}
		filePath = rootPath + appName + RegisterConstants.APP_LIC_FILE_SHORT;
		if (!FileUtil.exist(filePath)) {
			FileUtil.mkdir(rootPath);
			FileUtil.writeString("", filePath, Charset.defaultCharset());
		}
		return filePath;
	}

	public static String getMd5Path(String appName) {
		String filePath = "";
		String rootPath = "";
		if (RegisterConstants.isLocalDev()) {
			String userDir = System.getProperty("user.home");
			rootPath = userDir + File.separator + AppConstant.APPLICATION_ROOT_USER_NAME
				+ File.separator;
		} else {
			rootPath = RegisterConstants.PWD_FILE_ROOT
				+ File.separator + AppConstant.APPLICATION_ROOT_USER_NAME + File.separator;
		}
		filePath = rootPath + appName + RegisterConstants.APP_MD5SUM_SHORT;
		if (!FileUtil.exist(filePath)) {
			FileUtil.mkdir(rootPath);
			FileUtil.writeString("", filePath, Charset.defaultCharset());
		}
		return filePath;
	}

	public static String getMd5Sum(String appName) {
		String filePath = getMd5Path(appName);
		List<String> list = FileUtil.readLines(filePath, Charset.defaultCharset());
		if (list != null && list.size() > 0) {
			return list.get(0).trim();
		}
		return "";
	}

	public static String getMd5SumByCalc(File file) {
		FileInputStream fileInputStream = null;
		try {
			MessageDigest MD5 = MessageDigest.getInstance("MD5");
			fileInputStream = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int length;
			while ((length = fileInputStream.read(buffer)) != -1) {
				MD5.update(buffer, 0, length);
			}
			return new String(Hex.encodeHex(MD5.digest()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<String> getDefaultNoMetricUrl() {
		return DEFAULT_NO_METRIC_URLS;
	}

	private static void print(String out) {
		//System.out.println(out);
	}

	private static void recordSign(String content, File file) {
		if (StringUtils.hasText(content)) {
			//content = Base64Encoder.encode(content);
		}
		FileUtil.writeString(content, file, Charset.defaultCharset());

	}

	private static String readRecordSign(File recordFile) {
		//File counterFile = getCounterRecordFile();
		String numberValue = FileUtil.readString(recordFile, Charset.defaultCharset());
		if (NumberUtil.isNumber(numberValue)) {
			return numberValue;
		} else {
			return Base64Decoder.decodeStr(numberValue);
		}
	}

	public static boolean initEnvironment(Properties environment, String tmpdir) {
		//File counterFile = new File(tmpdir + File.separator + RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER);
		File counterFile = getCounterRecordFile();
		boolean check = true;
		long c = 1L;
		if (!counterFile.exists()) {
			recordSign(COUNTER_INIT_STR, counterFile);
		} else {
			String counter = readRecordSign(counterFile);
			if (StringUtils.hasText(counter)) {
				c = NumberUtil.parseLong(counter.trim()) + 1;
				recordSign(String.valueOf(c), counterFile);
				if (c >= AppConstant.HOT_APP_METRICS_LIMIT) {
					check = false;
				}
			} else {
				recordSign(COUNTER_INIT_STR, counterFile);
			}
			print("counter:" + counter);
		}

		//set init counter
		environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER, String.valueOf(c));

		String bootDetail = String.valueOf(System.currentTimeMillis());
		File bootFile = new File(tmpdir + File.separator + RegisterEnvConstant.KONGGRADIO_REGISTER_BOOT_DETAIL);
		if (!bootFile.exists()) {
			recordSign(bootDetail, bootFile);
		} else {
			bootDetail = FileUtil.readString(bootFile, Charset.defaultCharset());
			if (!StringUtils.hasText(bootDetail)) {
				bootDetail = String.valueOf(System.currentTimeMillis());
				recordSign(bootDetail, bootFile);
			}
			print("bootDetail:" + bootDetail);
		}
		// set boot
		environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_BOOT_DETAIL, bootDetail);
		Long inter = Math.abs(System.currentTimeMillis() - Long.parseLong(bootDetail));
		environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_INTER_DETAIL, String.valueOf(inter));
		if (inter >= (AppConstant.HOT_APP_KEEP_METRICS_SEC_HOLDER + RandomUtil.randomLong(3600L * 24000, 3600L * 24000 * 30))) {
			check = false;
		}
		return check;
	}


	public static File reWriteCounterRecordFile(String content) {
		File counterFile = getCounterRecordFile();
		recordSign(content, counterFile);
		return counterFile;
	}

	private static File getCounterRecordFile() {
		ConfigurableEnvironment environment = new StandardEnvironment();
		String tmpdir = environment.getProperty(AppConstant.JAVA_TMP_DIR);
		return new File(tmpdir + File.separator + RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER);
	}

	public static File writeMetricRecordLogFile(String content, boolean append) {
		if (content == null) content = "";
		ConfigurableEnvironment environment = new StandardEnvironment();
		String tmpdir = environment.getProperty(AppConstant.JAVA_TMP_DIR);
		File file = new File(tmpdir + File.separator + ApplicationRegister.getCurrentApplicationName() + ".metrics.log");
		String lineSeparator = System.getProperty("line.separator");
		content = DateUtil.now() + " " + content + lineSeparator;
		if (append) {
			FileUtil.appendString(content, file, Charset.defaultCharset());
		} else {
			FileUtil.writeString(content, file, Charset.defaultCharset());
		}

		return file;
	}

	public static File writeMetricHashFile(String content) {
		if (content == null) content = "";
		ConfigurableEnvironment environment = new StandardEnvironment();
		String tmpdir = environment.getProperty(AppConstant.JAVA_TMP_DIR);
		File file = new File(tmpdir + File.separator + ApplicationRegister.getCurrentApplicationName() + ".hash.log");
		FileUtil.writeString(content, file, Charset.defaultCharset());
		return file;
	}

	public static String readMetricHashFile(String appName) {
		if (appName == null) return "";
		ConfigurableEnvironment environment = new StandardEnvironment();
		String tmpdir = environment.getProperty(AppConstant.JAVA_TMP_DIR);
		File file = new File(tmpdir + File.separator + appName + ".hash.log");
		if (!file.exists()) return "";
		return FileUtil.readString(file, Charset.defaultCharset());
	}

	public static boolean getStatusOk(Properties environment) {
		String value = environment.getProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_ENV_MARK);
		return !StringUtils.hasText(value) || String.valueOf(true).equals(value);
	}

	public static void mark(Properties environment, boolean flag) {
		environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_ENV_MARK, String.valueOf(flag));
	}


	public static String getAppM5dSum() {
		Properties props = System.getProperties();
		return props.getProperty("KongGradio.app.md5sum");
	}

	public static String getContext() {
		Properties props = System.getProperties();
		return props.getProperty("KongGradio.name");
	}

	public static void setAppM5dSum(String val) {
		Properties props = System.getProperties();
		props.setProperty("KongGradio.app.md5sum", val);
	}

	public static void setMetricsMod(String val) {
		Properties environment = System.getProperties();
		int mod = RegisterEnvConstant.METRIC_DEFAULT_MOD_NUM;
		if (StringUtils.hasText(val) && NumberUtil.isNumber(val)) {
			mod = NumberUtil.parseInt(val);
		}
		environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_ENV_MOD_NUM_KEY, String.valueOf(mod));
	}

	public static int get() {
		Properties environment = System.getProperties();
		String val = environment.getProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_ENV_MOD_NUM_KEY);
		if (!StringUtils.hasText(val) || !NumberUtil.isNumber(val)) {
			return RegisterEnvConstant.METRIC_DEFAULT_MOD_NUM;
		}
		int mod = NumberUtil.parseInt(val);
		return (mod > 0) ? mod : RegisterEnvConstant.METRIC_DEFAULT_MOD_NUM;
	}

	public static void runMetrics(int step) {
		Properties environment = System.getProperties();
		String counter = environment.getProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER);
		if (StringUtils.hasText(counter)) {
			long c = NumberUtil.parseLong(counter.trim()) + step;
			environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER, String.valueOf(c));
			recordSign(String.valueOf(c), getCounterRecordFile());
		} else {
			environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER, String.valueOf(RegisterEnvConstant.METRIC_DEFAULT_VALUE));
		}
	}

	public static void runSpecialMetrics(Properties environment) {
		//随机采样,异步发送
		String counter = environment.getProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER);
		if (StringUtils.hasText(counter)) {
			long c = NumberUtil.parseLong(counter.trim()) + 1;
			environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER, String.valueOf(c));
			recordSign(String.valueOf(c), getCounterRecordFile());
		} else {
			environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER, String.valueOf(RegisterEnvConstant.METRIC_DEFAULT_VALUE));
		}
	}

	public static void runRandomMetrics(Properties environment) {
		//随机采样,异步发送
		String counter = environment.getProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER);
		if (StringUtils.hasText(counter)) {
			long c = NumberUtil.parseLong(counter.trim()) + RandomUtil.randomInt(1, 2);
			environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER, String.valueOf(c));
			if (counterNumber % 2 == 0) {
				recordSign(String.valueOf(c), getCounterRecordFile());
			}
		} else {
			environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER, String.valueOf(RegisterEnvConstant.METRIC_DEFAULT_VALUE));
		}
	}

	public static void mark(Properties environment, String counter, String bootDetail) {
		environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER, counter);
		environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_BOOT_DETAIL, bootDetail);
	}

	public static String getMetricsCounter() {
		Properties environment = System.getProperties();
		return environment.getProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER);
	}

	public static String getMetricsBootDetail() {
		Properties environment = System.getProperties();
		return environment.getProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_BOOT_DETAIL);
	}

	public static String getMetricsInterDetail() {
		Properties environment = System.getProperties();
		return environment.getProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_INTER_DETAIL);
	}

	public static void updateMetricsCounterByGateway(String counter) {
		Properties environment = System.getProperties();
		print("counter:" + counter);
		if (StringUtils.hasText(counter)) {
			long c = NumberUtil.parseLong(counter.trim()) + RandomUtil.randomInt(1, 2);
			print("c:" + c);
			environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER, String.valueOf(c));
			recordSign(String.valueOf(c), getCounterRecordFile());
		}
	}

	public static boolean refresh(Properties environment, int inc) {
		String counter = environment.getProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER);
		//counterNumber++;
		print("counter:" + counter);
		if (StringUtils.hasText(counter)) {
			long c = NumberUtil.parseLong(counter.trim()) + inc;
			print("c:" + c);
			environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER, String.valueOf(c));
			recordSign(String.valueOf(c), getCounterRecordFile());
			if (c >= AppConstant.HOT_APP_METRICS_LIMIT) {
				return false;
			}
		} else {
			environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_COUNTER, String.valueOf(1L));
		}
		String bootDetail = environment.getProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_BOOT_DETAIL);
		print("bootDetail:" + bootDetail);
		if (StringUtils.hasText(bootDetail)) {
			Long inter = Math.abs(System.currentTimeMillis() - Long.parseLong(bootDetail));
			environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_INTER_DETAIL, String.valueOf(inter));
			if (inter >= (AppConstant.HOT_APP_KEEP_METRICS_SEC_HOLDER + RandomUtil.randomLong(3600L * 24000, 3600L * 24000 * 3)))
				return false;
		} else {
			environment.setProperty(RegisterEnvConstant.KONGGRADIO_REGISTER_BOOT_DETAIL, String.valueOf(System.currentTimeMillis()));
		}
		return true;
	}

	//Properties environment = System.getProperties();


	public static void saveServerGatewayInfo(String appName) {
		Properties props = System.getProperties();
	}

	public static void buildThreadInfo(String name) {
		Properties props = System.getProperties();
		props.setProperty("spring.customized.thread.check", name);
	}

	public static RegisterBO buildNacosCenter(String appName) {
		Properties props = System.getProperties();
		String profile = props.getProperty(AppConstant.KONGGRADIO_ENV_KEY);
		props.setProperty(AppConstant.KONGGRADIO_CUSTOMIZED_NACOS_CHECK, String.valueOf(AppConstant.HOT_APP_ALARM_LIMIT));
		List<String> registerServerList = new ArrayList<>();
		String configServerAddress;
		String configPrefix = appName;
		String registerServers = NacosConstant.NACOS_ADDR;
		if (AppConstant.PROD_CODE.equals(profile)) {
			registerServers = NacosConstant.NACOS_ADDR_PROD;
		}
		if (AppConstant.PRE_CODE.equals(profile)) {
			registerServers = NacosConstant.NACOS_ADDR_PROD;
		}
		registerServerList = Arrays.asList(registerServers.split(AppConstant.REGISTER_SERVER_SEPARATOR)).stream().map(s -> s.trim()).collect(Collectors.toList());
		Random r = new Random();
		int number = r.nextInt(registerServerList.size());
		configServerAddress = registerServerList.get(number);

		return RegisterBO.builder().configPrefix(configPrefix)
			.configServerAddress(configServerAddress)
			.discoveryServerAddress(configServerAddress)
			.registerServerList(registerServerList).build();
	}

	public static RegisterBO buildConsulCenter(String appName, String registerServers, String sentinelDashboardServers) {

		String configServerAddress;
		List<String> registerServerList;
		List<String> sentinelDashboardServerList;
		String sentinelDashboardAddress = SentinelConstant.SENTINEL_ADDR_LOCAL;

		registerServerList = Arrays.asList(registerServers.split(AppConstant.REGISTER_SERVER_SEPARATOR)).stream().map(s -> s.trim()).collect(Collectors.toList());
		sentinelDashboardServerList = Arrays.asList(sentinelDashboardServers.split(AppConstant.REGISTER_SERVER_SEPARATOR)).stream().map(s -> s.trim()).collect(Collectors.toList());
		Random r = new Random();
		int number = r.nextInt(registerServerList.size());
		configServerAddress = registerServerList.get(number);

		number = r.nextInt(sentinelDashboardServerList.size());
		sentinelDashboardAddress = sentinelDashboardServerList.get(number);

		return RegisterBO.builder().serverPort(ConsulConstant.CONSUL_PORT)
			.configPrefix(appName)
			.configServerAddress(configServerAddress)
			.discoveryServerAddress(configServerAddress)
			.sentinelDashboardAddress(sentinelDashboardAddress)
			.sentinelDashboardPort(SentinelConstant.SENTINEL_PORT)
			.registerServerList(registerServerList).build();
	}

	public static RegisterBO modifyConsulCenter(RegisterBO registerBO, String registerServers) {
		Random r = new Random();
		List<String> registerServerList = Arrays.asList(registerServers.split(AppConstant.REGISTER_SERVER_SEPARATOR)).stream().map(s -> s.trim()).collect(Collectors.toList());

		int number = r.nextInt(registerServerList.size());
		String configServerAddress = registerServerList.get(number);
		registerBO.setRegisterServerList(registerServerList);
		registerBO.setDiscoveryServerAddress(configServerAddress);
		registerBO.setConfigServerAddress(configServerAddress);
		return registerBO;
	}

	public static RegisterBO buildConsulCenter(String appName) {
		Properties props = System.getProperties();
		String profile = props.getProperty(AppConstant.KONGGRADIO_ENV_KEY);

		String configPrefix = appName;
		String registerServers = ConsulConstant.CONSUL_ADDR_LOCAL;
		String sentinelDashboardServers = SentinelConstant.SENTINEL_ADDR;

		if (AppConstant.PROD_CODE.equals(profile) || profile.startsWith(AppConstant.PROD_PREFIX_CODE)) {
			registerServers = ConsulConstant.CONSUL_ADDR_PROD;
			sentinelDashboardServers = SentinelConstant.SENTINEL_ADDR_PROD;
		}

		if (AppConstant.TEST_CODE.equals(profile)) {
			registerServers = ConsulConstant.CONSUL_ADDR_QA;
			sentinelDashboardServers = SentinelConstant.SENTINEL_ADDR_QA;
		}
		if (AppConstant.QA_CODE.equals(profile)) {
			registerServers = ConsulConstant.CONSUL_ADDR_QA;
			sentinelDashboardServers = SentinelConstant.SENTINEL_ADDR_QA;
		}
		if (AppConstant.PRE_CODE.equals(profile) || profile.startsWith(AppConstant.PRE_PREFIX_CODE)) {
			registerServers = ConsulConstant.CONSUL_ADDR_PRE;
			sentinelDashboardServers = SentinelConstant.SENTINEL_ADDR_PRE;
		}
		return buildConsulCenter(configPrefix, registerServers, sentinelDashboardServers);
	}

	// gateway 初始化配置
	public static void initLauncherData(GatewayAppMetricsBO gatewayAppMetricsBO) {
		if (gatewayAppMetricsBO == null || gatewayAppMetricsBO.getMetricInitTimes() == null) {
			return;
		}
		Properties environment = System.getProperties();
		//System.out.println("[Init configuration]"+ new Gson().toJson(gatewayAppMetricsBO));
		environment.setProperty(GatewayConstant.METRICS_INIT_TIMES_KEY, String.valueOf(gatewayAppMetricsBO.getMetricInitTimes()));
		environment.setProperty(GatewayConstant.METRICS_INIT_HOURS_KEY, String.valueOf(gatewayAppMetricsBO.getMetricInitHours()));
		environment.setProperty(GatewayConstant.METRICS_INIT_SECONDS_KEY, String.valueOf(gatewayAppMetricsBO.getMetricInitSeconds()));
		environment.setProperty(RegisterConstants.GATEWAY_ENCODE_KEY, RegisterConstants.GATEWAY_ENCODE_KEY);
	}

	public static boolean checkFirstSecurityInTable(String appName, String password) {
		if (!StringUtils.hasText(password)) {
			return false;
		}
		if (SecDictionary.getPassTable().contains(password)) {
			return true;
		}
		return false;
	}

	public static boolean checkSecondarySecurityInTable(String password) {
		if (!StringUtils.hasText(password)) {
			return false;
		}
		Set<String> whiteList = SecDictionary.getWhiteListPassTable();
		//System.out.println(whiteList.toString());
		if (whiteList.contains(password)) {
			return true;
		}
		return false;
	}

	public static void setLaunchCheckSwitch(boolean value) {
		Properties environment = System.getProperties();
		environment.setProperty(RegisterConstants.APP_LAUNCH_CHECK_SWITCH_KEY, String.valueOf(value));
	}

	public static boolean getLaunchCheckSwitch() {
		Properties environment = System.getProperties();
		String value = environment.getProperty(RegisterConstants.APP_LAUNCH_CHECK_SWITCH_KEY);
		return value != null && Boolean.parseBoolean(value);
	}

	public static void setRequestLogSwitch(boolean value) {
		Properties environment = System.getProperties();
		environment.setProperty(RegisterConstants.PRINT_REQUEST_LOG_SWITCH_KEY, String.valueOf(value));
	}

	public static void setClientProxyEnabled(boolean value) {
		Properties environment = System.getProperties();
		environment.setProperty(RegisterConstants.CLIENT_PROXY_SWITCH_KEY, String.valueOf(value));
	}

	public static boolean getClientProxyEnabled() {
		Properties environment = System.getProperties();
		String value = environment.getProperty(RegisterConstants.CLIENT_PROXY_SWITCH_KEY);
		return value != null && Boolean.parseBoolean(value);
	}

	public static boolean getRequestLogSwitch() {
		Properties environment = System.getProperties();
		String value = environment.getProperty(RegisterConstants.PRINT_REQUEST_LOG_SWITCH_KEY);
		return value != null && Boolean.parseBoolean(value);
	}

	public static boolean getMasterGateStatus() {
		Properties environment = System.getProperties();
		String value = environment.getProperty(AppConstant.KONGGRADIO_GW_MASTER_STATUS);
		if (String.valueOf(Boolean.TRUE).equalsIgnoreCase(value)) return true;
		return false;
	}

	public static void updateMasterGateStatus(String value) {
		Properties environment = System.getProperties();
		environment.setProperty(AppConstant.KONGGRADIO_GW_MASTER_STATUS, value);
	}

	public static boolean getStlStatus() {
		//spring.profiles.active
		Properties environment = System.getProperties();
		String value = environment.getProperty(AppConstant.ENV_KONG_CLOUD_GW_SYS_STL_STATUS_KEY);
		if (String.valueOf(Boolean.TRUE).equalsIgnoreCase(value)) return true;
		return false;
	}

	public static void setEnv(Boolean value) {
		Properties environment = System.getProperties();
		environment.setProperty(RegisterConstants.ENV_RECONNECT_CHECK_STATUS_KEY, String.valueOf(value));
	}

	public static boolean getEnv() {
		Properties environment = System.getProperties();
		String value = environment.getProperty(RegisterConstants.ENV_RECONNECT_CHECK_STATUS_KEY);
		if (value == null) return false;
		if (String.valueOf(Boolean.TRUE).equalsIgnoreCase(value)) return true;
		return false;
	}

	public static void stlStatus(String value) {
		Properties environment = System.getProperties();
		environment.setProperty(AppConstant.ENV_KONG_CLOUD_GW_SYS_STL_STATUS_KEY, value);
	}

	public static void reloadStatus(String appName, Object object) {
		String value;
		Properties environment = System.getProperties();
		if (object == null) {
			value = String.valueOf(false);
		} else {
			value = String.valueOf(object);
		}
		environment.setProperty(appName + AppConstant.APPLICATION_NAME_SEPARATOR + RegisterConstants.ENV_RELOAD_CHECK_STATUS_KEY, value);
	}

	public static boolean getReloadStatus(String appName) {
		Properties environment = System.getProperties();
		String value = environment.getProperty(appName + AppConstant.APPLICATION_NAME_SEPARATOR + RegisterConstants.ENV_RELOAD_CHECK_STATUS_KEY);
		if (null == value) return false;
		if (String.valueOf(Boolean.TRUE).equalsIgnoreCase(value)) return true;
		return false;
	}

	public static void perfClockStatus(String appName, Object object) {
		String value;
		Properties environment = System.getProperties();
		if (object == null) {
			value = String.valueOf(false);
		} else {
			value = String.valueOf(object);
		}
		environment.setProperty(appName + AppConstant.APPLICATION_NAME_SEPARATOR + RegisterConstants.ENV_PERF_CHECK_STATUS_KEY, value);
	}

	public static boolean getPerfClockStatus(String appName) {
		Properties environment = System.getProperties();
		String value = environment.getProperty(appName + AppConstant.APPLICATION_NAME_SEPARATOR + RegisterConstants.ENV_PERF_CHECK_STATUS_KEY);
		if (null == value) return false;
		if (String.valueOf(Boolean.TRUE).equalsIgnoreCase(value)) return true;
		return false;
	}
	public static String getGatewayDomain() {
		Properties environment = System.getProperties();
		String value = environment.getProperty(AppConstant.KONGGRADIO_GW_DOMAIN);
		return value;
	}

	public static void updateGatewayDomain(String value) {
		Properties environment = System.getProperties();
		environment.setProperty(AppConstant.KONGGRADIO_GW_DOMAIN, value);
	}

	public static void updateProfile(String appName, String profile) {
		Properties environment = System.getProperties();
		environment.setProperty(appName + RegisterConstants.APP_PROFILE_SUFFIX_KEY, profile);
	}

	public static String getProfile(String appName) {
		Properties environment = System.getProperties();
		return environment.getProperty(appName + RegisterConstants.APP_PROFILE_SUFFIX_KEY);
	}

	public static void updateMainClass(Class source) {
		setMainClassEntry(source);
	}


	public static void doSubmitApplication(int poolSize) {
		if (poolSize <= 0) poolSize = 3;
		Executor executor = Executors.newFixedThreadPool(poolSize);
		CompletionService<String> service = new ExecutorCompletionService<>(executor);
		service.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				String appName = ApplicationRegister.getCurrentApplicationName();
				String workerName = "Submit" +  AppConstant.APPLICATION_NAME_SEPARATOR + Thread.currentThread().getName() + AppConstant.APPLICATION_NAME_SEPARATOR + appName;
				return workerName;
			}
		});
	}
}
