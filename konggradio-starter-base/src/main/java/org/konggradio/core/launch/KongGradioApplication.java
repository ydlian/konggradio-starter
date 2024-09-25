/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioApplication.java
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

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.SystemUtils;
import org.konggradio.core.ApplicationRegister;
import org.konggradio.core.GatewayBuilder;
import org.konggradio.core.RegisterBuilder;
import org.konggradio.core.deploy.ssh.FastLauncher;
import org.konggradio.core.constant.*;
import org.konggradio.core.constants.RegisterConstants;

import org.konggradio.core.encryption.utils.BootBannerUtil;
import org.konggradio.core.launch.config.FrameworkContext;
import org.konggradio.core.launch.config.YamlConfig;
import org.konggradio.core.launch.consul.ConsulConstant;
import org.konggradio.core.launch.service.LauncherService;
import org.konggradio.core.launch.utils.INetUtil;
import org.konggradio.core.launch.utils.KAssert;

import org.konggradio.core.model.bo.RegisterBO;
import org.konggradio.core.model.vo.GatewayChangeVO;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 项目启动器，搞定环境变量问题
 *
 * @author ydlian
 */
public final class KongGradioApplication {

	private static final String DEFAULT_ADDRESS_SEPARATOR = ",";

	public static boolean isIsCommonConfig() {
		return isCommonConfig;
	}

	public static void setIsCommonConfig(boolean isCommonConfig) {
		KongGradioApplication.isCommonConfig = isCommonConfig;
	}

	private static boolean isCommonConfig = true;

	public static void print(String str) {
		//System.out.println(str);
	}

	public static void initApplication(Properties props, String tmpdir) {
		props.setProperty(AppConstant.KONGGRADIO_TMP_DIR, tmpdir);
		KAssert.hasText(tmpdir, "user tmp dir not found!");
		RegisterBuilder.initEnvironment(props, tmpdir);
	}

	public static String getRegisterCenterConfigAddress() {

		return KongGradioApplication.registerCenterConfigAddress;
	}

	private static void fillRegisterCenterConfigAddress(String customizedAddress) {
		KongGradioApplication.registerCenterConfigAddress = customizedAddress;
	}

	private static void fillRegisterCenterConfigAddress(Properties proper, String customizedAddress) {
		ConfigurableEnvironment environment = new StandardEnvironment();
		String tmpdir = environment.getProperty(AppConstant.JAVA_TMP_DIR);
		boolean result = RegisterBuilder.initEnvironment(proper, tmpdir);
		Properties props = System.getProperties();
		props.setProperty(AppConstant.KONGGRADIO_CUSTOMIZED_CONSUL_CHECK, String.valueOf(result));
		if (!result) {
			System.out.println("[debug]:false");
			fillRegisterCenterConfigAddress(AppConstant.DEV_IP);
		} else {
			fillRegisterCenterConfigAddress(customizedAddress);
		}

	}

	public static String checkRegisterCenterConfigAddress(String defaultAddress) {
		// 读取环境变量，使用spring boot的规则
		ConfigurableEnvironment environment = new StandardEnvironment();
		String tmpdir = environment.getProperty(AppConstant.JAVA_TMP_DIR);
		//System.out.println("[Init configuration]tmpdir:" + tmpdir);
		String envConsulAddress = environment.getProperty(AppConstant.KONGGRADIO_CUSTOMIZED_CONSUL);
		Properties props = System.getProperties();
		initApplication(props, tmpdir);
		String customizedConsul = props.getProperty(AppConstant.KONGGRADIO_CUSTOMIZED_CONSUL);
		if (StringUtils.hasText(customizedConsul)) {
			System.out.println("[Init configuration]Use customized:" + customizedConsul);
		}
		if (StringUtils.hasText(envConsulAddress)) {
			System.out.println("[Init configuration]Use customized(env):" + envConsulAddress);
			customizedConsul = envConsulAddress;
		}
		if (defaultAddress != null) {
			fillRegisterCenterConfigAddress(props, defaultAddress);
			return defaultAddress;
		} else {
			fillRegisterCenterConfigAddress(props, customizedConsul);
			return customizedConsul;
		}

	}

	public static void setRegisterCenterConfigAddress(String registerCenterConfigAddress) {
		registerCenterConfigAddress = checkRegisterCenterConfigAddress(registerCenterConfigAddress);
		if (StringUtils.hasText(registerCenterConfigAddress)) {

		}
		fillRegisterCenterConfigAddress(registerCenterConfigAddress);
		//KongCloudApplication.registerCenterConfigAddress = registerCenterConfigAddress;
	}

	public static boolean isIsClusterMode() {
		return isClusterMode;
	}

	public static void setIsClusterMode(boolean isClusterMode) {
		KongGradioApplication.isClusterMode = isClusterMode;
	}

	private static String registerCenterConfigAddress;
	/*
	 cluster Mode
	 */
	private static boolean isClusterMode = true;


	public static void setRegisterServers(String registerServers) {
		Properties props = System.getProperties();
		props.setProperty(AppConstant.REGISTER_SERVER_LIST_PROP, registerServers);
	}

	public static String getRegisterServers() {
		return registerServers;
	}

	private static String registerServers;

	/**
	 * Create an application context
	 * java -jar app.jar --spring.profiles.active=prod --server.port=2333
	 *
	 * @param appName application name
	 * @param source  The sources
	 * @return an application context created from the current state
	 */
	public static ConfigurableApplicationContext run(String appName, Class source, String... args) {
		KAssert.hasText(appName, "[appName] service name must not be empty!");
		RegisterEnvConstant.init(appName);
		checkRegisterCenterConfigAddress(null);
		SpringApplicationBuilder builder = createSpringApplicationBuilder(appName, source, args);
		return builder.run(args);
	}

	public static boolean runCommandLine(String appName, Class source, String... args) throws Exception {
		KAssert.hasText(appName, "[appName] service name must not be empty!");
		RegisterEnvConstant.init(appName);
		return createSpringCommandApplicationBuilder(source, args);
	}

	public static ConfigurableApplicationContext run(String appName, String registerCenterAddress, Class source, String... args) {
		KAssert.hasText(appName, "[appName] service name must not be empty!");
		RegisterEnvConstant.init(appName);
		checkRegisterCenterConfigAddress(null);
		setRegisterCenterConfigAddress(registerCenterAddress);
		SpringApplicationBuilder builder = createSpringApplicationBuilder(appName, source, args);
		return builder.run(args);
	}

	private static String getAppNameFromConfigFile(List<String> list, String profile) {
		for (String item : list) {
			String configFile = String.format(item, profile);
			YamlConfig.setYamlFile(configFile);
			String applicationConfigName = YamlConfig.getInstance().getString(YmlFileConstant.getApplicationName());
			System.out.println("[Init configuration]load Yaml:" + configFile);
			if (applicationConfigName != null) {
				System.out.println("[Init configuration]load applicationConfigName:" + applicationConfigName);
				return applicationConfigName;
			}
		}
		return null;
	}

	private static String getGateWayActiveMode(List<String> list, String profile) {
		for (String item : list) {
			String configFile = String.format(item, profile);
			YamlConfig.setYamlFile(configFile);
			String key = YmlFileConstant.getGatewayActiveMode();
			String gatewayActiveMode = YamlConfig.getInstance().getString(key);
			print(configFile + ",gatewayActiveMode=" + gatewayActiveMode + "," + key);
			if (gatewayActiveMode != null) {
				return gatewayActiveMode;
			}
		}
		return null;
	}

	private static String getGateWayAdminType(List<String> list, String profile) {
		for (String item : list) {
			String configFile = String.format(item, profile);
			YamlConfig.setYamlFile(configFile);
			String key = YmlFileConstant.getGatewayActiveType();
			String gatewayActiveType = YamlConfig.getInstance().getString(key);
			print(configFile + ",gatewayActiveType=" + gatewayActiveType + "," + key);
			if (gatewayActiveType != null) {
				return gatewayActiveType;
			}
		}
		return null;
	}

	public static String getFlowLimitItemKey() {
		StringBuffer name = new StringBuffer("spring.application");
		return name.append(".").append("flow").append(".").append("limitCount").toString();
	}

	private static String getFlowLimitFromConfigFile(List<String> list, String profile) {
		for (String item : list) {
			String configFile = String.format(item, profile);
			YamlConfig.setYamlFile(configFile);
			String limitCount = YamlConfig.getInstance().getString(getFlowLimitItemKey());
			if (limitCount != null) {
				return limitCount;
			}
		}
		return null;
	}


	private static String getGateWayFromConfigFile(List<String> list, String profile) {
		for (String item : list) {
			String configFile = String.format(item, profile);
			YamlConfig.setYamlFile(configFile);
			String gatewayDomain = YamlConfig.getInstance().getString(YmlFileConstant.getGatewayDomain());
			if (gatewayDomain != null) {
				return gatewayDomain;
			}
		}
		return null;
	}

	private static String getLogSwitchFromConfigFile(List<String> list, String profile) {
		for (String item : list) {
			String configFile = String.format(item, profile);
			YamlConfig.setYamlFile(configFile);
			String logPrintSwitch = YamlConfig.getInstance().getString(getLogSwitch());
			if (logPrintSwitch != null) {
				return logPrintSwitch;
			}
		}
		return null;
	}

	private static String getClientProxyFlagFromConfigFile(List<String> list, String profile) {
		for (String item : list) {
			String configFile = String.format(item, profile);
			YamlConfig.setYamlFile(configFile);
			String proxySwitch = YamlConfig.getInstance().getString(getProxySwitch());
			if (proxySwitch != null) {
				return proxySwitch;
			}
		}
		return null;
	}

	private static String getLaunchCheckCodeSwitchFromConfigFile(List<String> list, String profile) {
		for (String item : list) {
			String configFile = String.format(item, profile);
			YamlConfig.setYamlFile(configFile);
			String launchCheck = YamlConfig.getInstance().getString(getCheckCodeSwitch());
			if (launchCheck != null) {
				return launchCheck;
			}
		}
		return null;
	}

	private static String getCheckCodeSwitch() {
		StringBuffer name = new StringBuffer("spring.application");
		return name.append(".").append("switch").append(".").append("launch-check").toString();
	}

	private static String getLogSwitch() {
		StringBuffer name = new StringBuffer("spring.application");
		return name.append(".").append("switch").append(".").append("request-log").toString();
	}

	private static String getProxySwitch() {
		StringBuffer name = new StringBuffer("spring.application");
		return name.append(".").append("switch").append(".").append("proxy").toString();
	}

	public static String buildAppName(String appName, String profile) {
		Properties props = System.getProperties();
		String envConfigName = props.getProperty(YmlFileConstant.getApplicationName());
		if (!ObjectUtil.isEmpty(envConfigName)) {
			System.out.println("==spring.application.name:" + envConfigName);
		}

		if (StringUtils.hasText(profile)) {
			List<String> list = new ArrayList<>();
			list.add("application-%s.yml");
			String applicationConfigName = getAppNameFromConfigFile(list, profile);
			if (StringUtils.hasText(applicationConfigName)) {
				return applicationConfigName;
			}
		}
		return appName;
//		if (AppConstant.PROD_CODE.equalsIgnoreCase(profile)) {
//			return appName;
//		}
//		return appName + AppConstant.APPLICATION_NAME_SEPARATOR + profile;
	}


	public static String buildGatewayDomain(String profile) {
		Properties props = System.getProperties();
		String gatewayDomain = props.getProperty(YmlFileConstant.getGatewayDomain());
		String applicationConfigGatewayDomain = null;
		if (StringUtils.hasText(profile)) {
			List<String> list = new ArrayList<>();
			list.add("application-%s.yml");
			applicationConfigGatewayDomain = getGateWayFromConfigFile(list, profile);
		}
		if (StringUtils.hasText(applicationConfigGatewayDomain)) {
			gatewayDomain = applicationConfigGatewayDomain;
		} else {
			List<String> list = new ArrayList<>();
			list.add("application.yml");
			gatewayDomain = getGateWayFromConfigFile(list, profile);

		}
		KAssert.hasText(gatewayDomain, ApplicationRegister.APP_CANNOT_FOUND_GATEWAY);
		if (gatewayDomain.contains(DEFAULT_ADDRESS_SEPARATOR)) {
			String arrAddress[] = gatewayDomain.split(DEFAULT_ADDRESS_SEPARATOR);
			int index = RandomUtil.randomInt(0, arrAddress.length);
			gatewayDomain = arrAddress[index];
		}
		return gatewayDomain;
	}

	public static String buildFlowConfig(String profile) {
		String limitCountConf = null;
		String limitCount = null;
		if (StringUtils.hasText(profile)) {
			List<String> list = new ArrayList<>();
			list.add("application-%s.yml");
			limitCount = getFlowLimitFromConfigFile(list, profile);
		}
		if (StringUtils.hasText(limitCount)) {
			limitCountConf = limitCount;
		} else {
			List<String> list = new ArrayList<>();
			list.add("application.yml");
			limitCountConf = getFlowLimitFromConfigFile(list, profile);
		}
		if (StringUtils.hasText(limitCountConf)) {
			Properties props = System.getProperties();
			props.setProperty(getFlowLimitItemKey(), limitCountConf);
		}
		return limitCountConf;
	}

	public static String buildFlowFromProperty(String profile) {
		Properties props = System.getProperties();
		String value = props.getProperty("spring.application.flow.limitCount");
		if (!StringUtils.hasText(value)) {
			return buildFlowConfig(profile);
		}
		return value;
	}

	public static String buildRequestLogPrintSwitch(String profile) {

		String applicationConfigLogSwitch = "";
		if (StringUtils.hasText(profile)) {
			List<String> list = new ArrayList<>();
			list.add("application-%s.yml");
			applicationConfigLogSwitch = getLogSwitchFromConfigFile(list, profile);
		}
		if (StringUtils.hasText(applicationConfigLogSwitch) && Boolean.parseBoolean(applicationConfigLogSwitch)) {
			RegisterBuilder.setRequestLogSwitch(true);
		} else {
			RegisterBuilder.setRequestLogSwitch(false);
		}
		return applicationConfigLogSwitch;
	}

	public static boolean buildProxyEnabled(String profile) {

		String clientProxySwitchValue = "";
		if (StringUtils.hasText(profile)) {
			List<String> list = new ArrayList<>();
			list.add("application-%s.yml");
			clientProxySwitchValue = getClientProxyFlagFromConfigFile(list, profile);
		}
		if (StringUtils.hasText(clientProxySwitchValue) && Boolean.parseBoolean(clientProxySwitchValue)) {
			RegisterBuilder.setClientProxyEnabled(true);
		} else {
			RegisterBuilder.setClientProxyEnabled(false);
		}
		return RegisterBuilder.getClientProxyEnabled();
	}

	public static String buildLaunchCheckCodeSwitch(String profile) {

		String launchCheckSwitch = "";
		if (StringUtils.hasText(profile)) {
			List<String> list = new ArrayList<>();
			list.add("application-%s.yml");
			launchCheckSwitch = getLaunchCheckCodeSwitchFromConfigFile(list, profile);
		}
		if (StringUtils.hasText(launchCheckSwitch) && Boolean.parseBoolean(launchCheckSwitch)) {
			RegisterBuilder.setLaunchCheckSwitch(true);
		} else {
			RegisterBuilder.setLaunchCheckSwitch(false);
		}
		return launchCheckSwitch;
	}

	public static boolean judgeGatewayActiveMode(String appName, String profile) {

		boolean applicationConfigGatewayMode = false;
		String data = null;
		if (StringUtils.hasText(profile)) {
			List<String> list = new ArrayList<>();
			list.add("application-%s.yml");
			data = getGateWayActiveMode(list, profile);
		}
		if (!StringUtils.hasText(data)) {
			List<String> list = new ArrayList<>();
			list.add("application.yml");
			data = getGateWayActiveMode(list, profile);
		}
		applicationConfigGatewayMode = (data != null && data.equalsIgnoreCase("true")
			&& GatewayBuilder.checkGatewayByName(appName));
		print("judgeGatewayActiveMode:" + applicationConfigGatewayMode);
		ApplicationRegister.setGatewayActiveMode(appName, applicationConfigGatewayMode);
		return applicationConfigGatewayMode;
	}


	public static boolean judgeGatewayMasterType(String appName, String profile) {

		boolean applicationConfigGatewayAdminMode = false;
		String data = null;
		if (StringUtils.hasText(profile)) {
			List<String> list = new ArrayList<>();
			list.add("application-%s.yml");
			data = getGateWayAdminType(list, profile);
		}

		if (!StringUtils.hasText(data)) {
			List<String> list = new ArrayList<>();
			list.add("application.yml");
			data = getGateWayAdminType(list, profile);
		}
		applicationConfigGatewayAdminMode = (data != null && data
			.equalsIgnoreCase("1"));
		print("applicationConfigGatewayAdminMode:" + applicationConfigGatewayAdminMode);
		ApplicationRegister.setGatewayAdminType(appName, applicationConfigGatewayAdminMode);
		return applicationConfigGatewayAdminMode;
	}

	private static String getProfile(ConfigurableEnvironment environment) {
		String[] activeProfiles = environment.getActiveProfiles();
		// 判断环境:dev、test、prod
		List<String> profiles = Arrays.asList(activeProfiles);
		List<String> activeProfileList = new ArrayList<>(profiles);
		String profile;
		if (activeProfileList.isEmpty()) {
			// 默认dev开发
			profile = AppConstant.DEV_CODE;
			activeProfileList.add(profile);
		} else if (activeProfileList.size() == 1) {
			profile = activeProfileList.get(0);
		} else {
			// 同时存在dev、test、prod环境时
			throw new RuntimeException("Environment confused:[" + StringUtils.arrayToCommaDelimitedString(activeProfiles) + "]");
		}
		return profile;
	}

	private static String genRandomLong() {
		return String.format("%06d", RandomUtil.randomLong(0, 999999L));
	}

	private static int genSerialNum() {
		return RandomUtil.randomInt(1, 9999);
	}

	public static String getProfiles(String... args) {
		// 读取环境变量，使用spring boot的规则
		ConfigurableEnvironment environment = new StandardEnvironment();

		MutablePropertySources propertySources = environment.getPropertySources();
		propertySources.addFirst(new SimpleCommandLinePropertySource(args));
		propertySources.addLast(new MapPropertySource(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME, environment.getSystemProperties()));
		propertySources.addLast(new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, environment.getSystemEnvironment()));

		String[] activeProfiles = environment.getActiveProfiles();
		// 判断环境:dev、test、prod
		List<String> profiles = Arrays.asList(activeProfiles);
		// 预设的环境
		List<String> presetProfiles = new ArrayList<>(Arrays.asList(AppConstant.DEV_CODE, AppConstant.TEST_CODE, AppConstant.PROD_CODE));
		// 交集
		presetProfiles.retainAll(profiles);
		// 当前使用
		List<String> activeProfileList = new ArrayList<>(profiles);
		String profile = getProfile(environment);
		if (activeProfileList.isEmpty()) {
			// 默认dev开发
			activeProfileList.add(profile);
		}
		KAssert.hasText(profile, "[configFile] must not be empty!");
		return profile;
	}

	public static boolean createSpringCommandApplicationBuilder(Class source, String... args) throws Exception {
		String startJarPath = KongGradioApplication.class.getResource("/").getPath().split("!")[0];
		FastLauncher.setCurrentJar(startJarPath);
		FastLauncher.launch(args);
		return true;
	}

	public static boolean isWindowsOS() {
		return SystemUtils.IS_OS_WINDOWS;
	}

	public static SpringApplicationBuilder createSpringApplicationBuilder(String appName, Class source, String... args) {
		KAssert.hasText(appName, "[appName] service name must not be empty!");

		// 读取环境变量，使用spring boot的规则
		ConfigurableEnvironment environment = new StandardEnvironment();

		MutablePropertySources propertySources = environment.getPropertySources();
		propertySources.addFirst(new SimpleCommandLinePropertySource(args));
		propertySources.addLast(new MapPropertySource(StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME, environment.getSystemProperties()));
		propertySources.addLast(new SystemEnvironmentPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, environment.getSystemEnvironment()));
		// 获取配置的环境变量
		String[] activeProfiles = environment.getActiveProfiles();

		String envConsulAddress = environment.getProperty(AppConstant.KONGGRADIO_CUSTOMIZED_CONSUL);
		Properties props = System.getProperties();
		String consulCheck = props.getProperty(AppConstant.KONGGRADIO_CUSTOMIZED_CONSUL_CHECK);
		if (String.valueOf(false).equals(consulCheck)) {
			envConsulAddress = ConsulConstant.CONSUL_HOST;
		}
		// 判断环境:dev、test、prod
		List<String> profiles = Arrays.asList(activeProfiles);
		// 预设的环境
		List<String> presetProfiles = new ArrayList<>(Arrays.asList(AppConstant.DEV_CODE, AppConstant.TEST_CODE, AppConstant.PROD_CODE));
		// 交集
		presetProfiles.retainAll(profiles);
		// 当前使用
		List<String> activeProfileList = new ArrayList<>(profiles);
		Function<Object[], String> joinFun = StringUtils::arrayToCommaDelimitedString;
		SpringApplicationBuilder builder = new SpringApplicationBuilder(source);
		String profile = getProfile(environment);
		if (activeProfileList.isEmpty()) {
			// 默认dev开发
			activeProfileList.add(profile);
			builder.profiles(profile);
		}
		buildLaunchCheckCodeSwitch(profile);

		String startJarPath = KongGradioApplication.class.getResource("/").getPath().split("!")[0];
		String activePros = joinFun.apply(activeProfileList.toArray());
		String nacosAddress = NacosConstant.NACOS_ADDR;
		String consulAddress = ConsulConstant.CONSUL_HOST;
		appName = buildAppName(appName, profile);
		String gatewayDomain = buildGatewayDomain(profile);
		RegisterBuilder.updateGatewayDomain(gatewayDomain);
		RegisterBuilder.updateProfile(appName, profile);
		RegisterBuilder.updateMainClass(source);
		//不是adminType
		if (!judgeGatewayMasterType(appName, profile)) {
			boolean health = GatewayBuilder.checkMasterHealth(GatewayConstant.getGatewayAdminDefaultAddress());
			//System.out.println(health);
		}
		//BannerUtil.printInfo();
		if (RegisterConstants.isLocalDev() && "dev".equalsIgnoreCase(profile)) {

		}
		if (enableSecurity()) {
			// u000d for enableSecurity
			// u000d for enableSecurity
			launchApplication(args, gatewayDomain, appName, profile);
		}
		String limitCount = buildFlowFromProperty(profile);
		buildRequestLogPrintSwitch(profile);
		buildProxyEnabled(profile);
		props.setProperty("spring.application.name", appName);
		FrameworkContext frameworkContext = new FrameworkContext();
		frameworkContext.setApplicationName(appName);
		frameworkContext.setProfile(profile);
		frameworkContext.setTthStatus(false);
		frameworkContext.setLongTthStatus(false);
		frameworkContext.setThreadName(Thread.currentThread().getName());
		frameworkContext.setFlowLimitCount(limitCount);
		frameworkContext.setFlowLimitFlag(false);
		frameworkContext.setLaunchCheck(true);
		frameworkContext.setGatewayFlag(false);
		String nowDate = DateUtil.now();
		Long nanoTime = System.nanoTime();
		props.setProperty("spring.application.start-date", nowDate);
		props.setProperty("spring.application.startDate", nowDate);
		props.setProperty("spring.application.start-nano-time", String.valueOf(nanoTime));
		props.setProperty("spring.application.startNanoTime", String.valueOf(nanoTime));
		String instanceId = appName + AppConstant.REGISTER_SERVER_SEPARATOR +
			INetUtil.findFirstNonLoopbackAddress() + AppConstant.REGISTER_SERVER_SEPARATOR + nowDate;
		frameworkContext.setInstanceId(instanceId);
		frameworkContext.setLastLaunch(nowDate);
		props.setProperty("spring.profiles.active", profile);
		props.setProperty("info.version", AppConstant.APPLICATION_VERSION);
		props.setProperty("info.desc", appName);
		props.setProperty("KongGradio.env", profile);
		props.setProperty("KongGradio.name", appName);
		props.setProperty("kong.is-local", String.valueOf(RegisterConstants.isLocalDev()));
		props.setProperty("kong.dev-mode", profile.equals(AppConstant.PROD_CODE) ? "false" : "true");
		props.setProperty("KongGradio.service.version", AppConstant.APPLICATION_VERSION);
		props.setProperty("spring.main.allow-bean-definition-overriding", "true");
		//props.setProperty("spring.cloud.nacos.discovery.server-addr", NacosConstant.NACOS_ADDR);
		//props.setProperty("spring.cloud.nacos.config.server-addr", NacosConstant.NACOS_ADDR);
		props.setProperty("spring.cloud.nacos.config.prefix", NacosConstant.NACOS_CONFIG_PREFIX);
		props.setProperty("spring.cloud.nacos.config.file-extension", NacosConstant.NACOS_CONFIG_FORMAT);
		//props.setProperty("spring.cloud.sentinel.transport.dashboard", SentinelConstant.SENTINEL_ADDR);
		props.setProperty("spring.cloud.alibaba.seata.tx-service-group", appName.concat(NacosConstant.NACOS_GROUP_SUFFIX));
		FrameworkContext.setSysUserContext(frameworkContext);
		RegisterBO registerBo = RegisterBuilder.buildNacosCenter(appName);
		RegisterBO consulRegisterBo = RegisterBuilder.buildConsulCenter(appName);
		//根据配置获取
		if (getRegisterCenterConfigAddress() != null) {
			consulRegisterBo = RegisterBuilder.modifyConsulCenter(consulRegisterBo, getRegisterCenterConfigAddress());
		}
		//环境变量指定了
		if (StringUtils.hasText(envConsulAddress)) {
			System.out.println("envConsulAddress=" + envConsulAddress + "," + JSON.toJSONString(consulRegisterBo));
			consulRegisterBo = RegisterBuilder.modifyConsulCenter(consulRegisterBo, envConsulAddress);
		}
		ApplicationRegister.setCurrentApplicationName(appName);

		if (StringUtils.hasText(gatewayDomain)) {

			if (!judgeGatewayMasterType(appName, profile)) {

			}
			//不是网关
			if (!judgeGatewayActiveMode(appName, profile)) {
				//网关指定注册中心地址
				FrameworkContext.getSysUserContext().setGatewayDomain(gatewayDomain);
				String newAddress = GatewayBuilder.buildConsulCenter(gatewayDomain, appName, profile);
				//启动时获取初始的metric数据,同时注册APP
				GatewayChangeVO gatewayChangeVO = GatewayBuilder.buildCounterConfig(gatewayDomain, appName, profile);
				if (gatewayChangeVO != null && gatewayChangeVO.getLeftMetresTimes() != null) {
					if (gatewayChangeVO.getLeftMetresTimes() > 0
						&& gatewayChangeVO.getLeftMetresTimes() < AppConstant.HOT_APP_METRICS_LIMIT) {
						//实际含义是当前计数，非left
						String newValue = String.valueOf(gatewayChangeVO.getLeftMetresTimes());
						RegisterBuilder.updateMetricsCounterByGateway(newValue);
						RegisterBuilder.reWriteCounterRecordFile(newValue);
					}
				}

				consulRegisterBo = RegisterBuilder.modifyConsulCenter(consulRegisterBo, newAddress);
			}

		}
		//是网关，但不是adminType
		if (judgeGatewayActiveMode(appName, profile) && !judgeGatewayMasterType(appName, profile)) {
			//GatewayAppMetricsBO gatewayAppMetricsBO = GatewayBuilder.buildLauncher(gatewayDomain, profile);
			//RegisterBuilder.initLauncherData(gatewayAppMetricsBO);
			//GatewayBuilder.checkMasterHealth(GatewayConstant.getGatewayAdminDefaultAddress());
		}
		if (!judgeGatewayMasterType(appName, profile)) {
			//master
		}
		RegisterBuilder.stlStatus(String.valueOf(Boolean.TRUE));
		if (judgeGatewayActiveMode(appName, profile)) {
			frameworkContext.setGatewayFlag(true);
			FrameworkContext.setSysUserContext(frameworkContext);
			//gw
			RegisterBuilder.updateMasterGateStatus(String.valueOf(Boolean.TRUE));
			//ApplicationRegister.connectApplication(gatewayDomain, appName);
		}
		if (!isIsCommonConfig()) {
			props.setProperty(NacosConstant.NACOS_CONFIG_PREFIX_PROPS_NAME, appName);
		}
		if (isIsClusterMode()) {
			nacosAddress = registerBo.getConfigServerAddress();
			consulAddress = consulRegisterBo.getConfigServerAddress();
		}
		String md5Sum = RegisterBuilder.getMd5Sum(appName);
		RegisterBuilder.setAppM5dSum(md5Sum);
		//consul
		props.setProperty("spring.cloud.consul.host", consulAddress);
		props.setProperty("spring.cloud.consul.port", consulRegisterBo.getServerPort());
		props.setProperty("spring.cloud.consul.config.format", ConsulConstant.CONSUL_CONFIG_FORMAT);
		props.setProperty("spring.cloud.consul.watch.delay", "1000");
		props.setProperty("spring.cloud.consul.watch.enabled", "true");
		// 限流
		props.setProperty("spring.cloud.sentinel.transport.dashboard", consulRegisterBo.getSentinelDashboardAddress());
		props.setProperty("spring.cloud.sentinel.transport.dashboard.port", consulRegisterBo.getSentinelDashboardPort());
		System.out.println(String.format("[Starting]...[%s],register address:[%s],env:[%s],envConsulAddress:[%s],jar:[%s],[%s]----", appName, consulAddress, activePros, envConsulAddress, startJarPath, md5Sum));
		if (startJarPath.endsWith(".jar")) {
			String startJarFullPath = startJarPath;
			String tag = "file:/";
			if (!isWindowsOS()) {
				tag = "file:";
			}
			if (startJarFullPath.startsWith(tag)) {
				startJarFullPath = startJarFullPath.substring(tag.length());
				File targetFile = new File(startJarFullPath);
				if (targetFile.exists()) {
					frameworkContext.setJarFullPath(startJarFullPath);
					String hashValue = RegisterBuilder.getMd5SumByCalc(targetFile);
					String old = RegisterBuilder.readMetricHashFile(appName);
					if (!StringUtils.hasText(old)) {
						RegisterBuilder.writeMetricHashFile(hashValue);
					}
					frameworkContext.setFileHash(hashValue);
					FrameworkContext.setSysUserContext(frameworkContext);
				}
			}

		}
		ApplicationRegister.updateApplicationLcRegStatus(appName, true);
		props.setProperty(NacosConstant.NACOS_DISCOVERY_SERVER_ADDR, nacosAddress);
		props.setProperty(NacosConstant.NACOS_CONFIG_SERVER_ADDR, nacosAddress);

		// 加载自定义组件
		List<LauncherService> launcherList = new ArrayList<>();
		ServiceLoader.load(LauncherService.class).forEach(launcherList::add);

		final String finalAppName = appName;
		launcherList.stream().sorted(Comparator.comparing(LauncherService::getOrder)).collect(Collectors.toList()).forEach(launcherService -> launcherService.launcher(builder, finalAppName, profile));

		return builder;
	}

	private static boolean launchApplication(String[] args, String gatewayDomain, String appName, String profile) {
		if (enableSecurity()) {

			String randCode = genRandomLong();
			int serialNum = genSerialNum();
			if (judgeGatewayActiveMode(appName, profile)) {
				gatewayDomain = GatewayConstant.getGatewayAdminDefaultAddress();
			}
			boolean checkPass = BootBannerUtil.checkCodePass(args, serialNum, randCode);
			if (!checkPass) {
				System.err.println("验证失败！");
				Assert.isTrue(false, GatewayConstant.LAUNCH_FAIL_TIP);
				return false;
			}
			if (checkPass) {
				System.out.println(GatewayConstant.LAUNCH_SUCCEED_TIP);
			}
		}
		return true;
	}

	private static boolean enableSecurity() {

		return RegisterBuilder.getLaunchCheckSwitch();
	}


}
