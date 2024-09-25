/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: GatewayConfigService.java
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
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.ApplicationRegister;
import org.konggradio.core.RegisterBuilder;
import org.konggradio.core.constant.AppConstant;
import org.konggradio.core.constant.ConsulConstant;
import org.konggradio.core.constant.GatewayConstant;
import org.konggradio.core.constants.RegisterConstants;
import org.konggradio.core.endecry.RsaUtil;
import org.konggradio.core.launch.constant.EnvConstant;

import org.konggradio.core.model.bo.ApplicationRegResultBO;
import org.konggradio.core.model.bo.RegisterCenterBO;
import org.konggradio.core.model.vo.GatewayChangeVO;
import org.konggradio.core.tool.api.R;
import org.konggradio.core.tool.api.ResultCode;
import org.konggradio.core.tool.utils.RedisUtil;
import org.konggradio.core.utils.HttpClientUtil;
import org.konggradio.hardware.NetworkUtil;

import org.konggradio.hardware.dto.ServerDTO;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import oshi.SystemInfo;
import oshi.hardware.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@AllArgsConstructor
public class GatewayConfigService {
	@Resource
	RedisUtil redisUtil;
	@Resource
	GatewayMetricsService gatewayMetricsService;
	@Resource
	SecurityCheckService securityCheckService;
	private final DiscoveryClient discoveryClient;
	@Resource
	GatewayAppCtlConfigService gatewayAppCtlConfigService;

	private String getRedisRegisterCenterKey(String appName) {
		return GatewayConstant.getRegisterCenterSetKey() + appName;
	}

	private String getHidKey(String appName) {
		StringBuffer name = new StringBuffer("APP_HID_LIST_");
		return name.append("HARD_SET_KEY").append(":").append(appName).toString();
	}

	public long saveApplicationHid(String appName, String hid) {
		if (hid == null) return 0;
		return redisUtil.sSet(getHidKey(appName), hid);
	}

	public ServerDTO getGatewayInfo() {
		try{
			SystemInfo si = new SystemInfo();
			HardwareAbstractionLayer hal = si.getHardware();
			CentralProcessor centralProcessor = hal.getProcessor();
			GlobalMemory m = hal.getMemory();

			HWDiskStore[] d = hal.getDiskStores().toArray(new HWDiskStore[0]);
			List<String> mac = NetworkUtil.getMaces();
			ComputerSystem s = hal.getComputerSystem();
			ServerDTO serverDTO = ServerDTO.builder()
				.centralProcessor(centralProcessor)
				.globalMemory(m)
				.hwDiskStores(d)
				.macAdders(mac)
				.computerSystem(s)
				.build();
			return serverDTO;
		}catch (Exception e){

		}

		return null;
	}

	public long saveGatewayHidExt(String appName, ServerDTO serverDTO) {
		Gson gson = new Gson();
		try {
			return redisUtil.sSet(getHidKey(appName), gson.toJson(serverDTO));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L;
	}

	public Set<Object> getApplicationHid(String appName) {
		return redisUtil.sGet(getHidKey(appName));
	}

	private String getApplicationMetricInitDataKey(String appName) {
		return GatewayConstant.getRegisterCenterSetKey() + ":app:init:" + appName;
	}

	private String getGatewayRegApplicationKey() {
		return GatewayConstant.getRegisterCenterSetKey() + ":app:register";
	}


	public R<Boolean> enableFramework(String password, long stl) {
		if (!securityCheckService.checkGateSecondaryPass(password)) {
			return R.fail(ResultCode.UN_AUTHORIZED);
		}
		if (stl <= 0) stl = AppConstant.GW_CONFIG_CACHE_NUMBER;
		if (stl <= EnvConstant.MATRIX[0][2]) {
			RegisterBuilder.stlStatus(String.valueOf(false));
		} else {
			RegisterBuilder.stlStatus(String.valueOf(true));
		}
		return R.success(ResultCode.SUCCESS, true);
	}
	public R<Long> addRegisterCenter(String appName, String address, String password) {
		if (!securityCheckService.checkGateSecondaryPass(password)) {
			return R.fail(ResultCode.UN_AUTHORIZED);
		}
		//从缓存取出来
		ApplicationRegResultBO applicationFirstRegResultBO = ApplicationRegister.handleApplicationFirstTimeQueryGateway(appName);
		ApplicationRegister.initApplicationWithData(appName, applicationFirstRegResultBO);
		gatewayMetricsService.initCachedRegInfo(appName, applicationFirstRegResultBO);
		long val = redisUtil.sSetAndTime(getRedisRegisterCenterKey(appName), AppConstant.GW_CONFIG_CACHE_NUMBER, address);
		return R.success(ResultCode.SUCCESS, val);
	}

	public R<Long> delRegisterCenter(String appName, String address, String password) {
		if (!securityCheckService.checkGateSecondaryPass(password)) {
			return R.fail(ResultCode.UN_AUTHORIZED);
		}
		long val = redisUtil.setRemove(getRedisRegisterCenterKey(appName), address);
		return R.success(ResultCode.SUCCESS, val);
	}

	public R<Set<Object>> clearRegisterCenter(String appName) {
		if (!StringUtils.hasText(appName)) appName = AppConstant.APPLICATION_GATEWAY_NAME;

		Set<Object> addressList = redisUtil.sGet(getRedisRegisterCenterKey(appName));
		for (Object address : addressList) {
			String item = (String) address;
			redisUtil.setRemove(getRedisRegisterCenterKey(appName), item);
		}
		return R.success(ResultCode.SUCCESS, redisUtil.sGet(getRedisRegisterCenterKey(appName)));
	}

	public HashSet<RegisterCenterBO> getRegisterCenter(String appName, String authCode, String hid) {
		//gateway获取
		boolean masterStatus = RegisterBuilder.getMasterGateStatus();
		saveApplicationHid(appName, hid);
		RegisterBuilder.writeMetricRecordLogFile(appName + "," + masterStatus, true);
		Set<Object> consulAddressSets = redisUtil.sGet(getRedisRegisterCenterKey(appName));
		if (ObjectUtil.isEmpty(consulAddressSets)) {
			consulAddressSets = redisUtil.sGet(getRedisRegisterCenterKey(GatewayConstant.getApplicationGatewayName()));
		}
		boolean isBlackApp = false;
		Set<Object> blackAppSets = gatewayAppCtlConfigService.getBlackAppList(new ArrayList<>());
		if (ObjectUtil.isNotNull(blackAppSets)) {
			for (Object black : blackAppSets) {
				String name = (String) black;
				if (appName.equalsIgnoreCase(name)) {
					isBlackApp = true;
					System.out.println(appName + " is denied!");
					break;
				}
			}
		}
		HashSet<RegisterCenterBO> registerCenterBOHashSet = new HashSet<RegisterCenterBO>();
		//todo：检查必要性，这里有bug
		ApplicationRegResultBO applicationRegResultBO = connectApplication(appName);
		//检查注册状态
		if (/*!applicationRegResultBO.isRegResult() */ isBlackApp /* || !masterStatus */) {
			String add = ConsulConstant.CONSUL_ADDR_LOCAL;
			Long code = HashUtil.mixHash(add);
			RegisterCenterBO bo = RegisterCenterBO.builder().address(add)
				.applicationRegInfo(applicationRegResultBO).hashCode(code).build();
			registerCenterBOHashSet.add(bo);
			return registerCenterBOHashSet;
		}

		for (Object obj : consulAddressSets) {
			String add = (String) obj;
			Long code = HashUtil.mixHash(add);
			RegisterCenterBO bo = RegisterCenterBO.builder()
				.masterStatus(masterStatus)
				.applicationRegInfo(applicationRegResultBO).address(add).hashCode(code).build();
			registerCenterBOHashSet.add(bo);
		}
		return registerCenterBOHashSet;
	}

	public long saveGatewayRegApplication(String appName) {
		return redisUtil.sSet(getGatewayRegApplicationKey(), appName);
	}

	public Set<Object> listGatewayRegApplication() {
		return redisUtil.sGet(getGatewayRegApplicationKey());
	}

	public R<Boolean> setApplicationMetricDataByGate(GatewayChangeVO changeVO, String password) {
		if (!securityCheckService.checkGateSecondaryPass(password)) {
			return R.fail(ResultCode.UN_AUTHORIZED);
		}
		Gson gson = new Gson();
		boolean val = redisUtil.set(getApplicationMetricInitDataKey(changeVO.getAppName()), gson.toJson(changeVO));
		return R.success(ResultCode.SUCCESS, val);
	}

	public R<GatewayChangeVO> updateApplicationMetricDataByGate(String appName, int var) {
		return null;
	}

	//联系gateway时获取数据，同步给app本地的环境变量
	public R<GatewayChangeVO> getApplicationMetricDataFromGate(String appName) {
		saveGatewayRegApplication(appName);
		Gson gson = new Gson();
		Object obj = redisUtil.get(getApplicationMetricInitDataKey(appName));
		if (ObjectUtil.isNull(obj)) {
			return R.fail(ResultCode.PARAM_VALID_ERROR);
		}
		String value = String.valueOf(obj);
		GatewayChangeVO appInitData = gson.fromJson(value, GatewayChangeVO.class);
		if (ObjectUtil.isNull(appInitData)) {
			return R.fail(ResultCode.PARAM_VALID_ERROR);
		}
		return R.success(ResultCode.SUCCESS, appInitData);
	}

	private ApplicationRegResultBO connectApplication(String appName) {
		ApplicationRegResultBO cachedApplicationRegResult = gatewayMetricsService.getCachedRegInfo(appName);
		if (cachedApplicationRegResult == null) {
			ApplicationRegResultBO applicationRegResultBO = ApplicationRegister.handleApplicationFirstTimeQueryGateway(appName);
			gatewayMetricsService.initCachedRegInfo(appName, applicationRegResultBO);
			return applicationRegResultBO;
		} else {
			DateTime now = new DateTime();
			String appRegTimeVal = cachedApplicationRegResult.getRegTime();
			DateTime appRegTime = DateUtil.parse(appRegTimeVal, ApplicationRegister.YYYYMMDDHHMMSS);
			long usedSeconds = DateUtil.between(now, appRegTime, DateUnit.SECOND, true);
			ApplicationRegResultBO updateVal = gatewayMetricsService.updateCachedApplicationRegInfoWhenConnect(appName, usedSeconds);
			ApplicationRegister.initApplicationWithData(appName, updateVal);
			return updateVal;
		}

	}


	public boolean init(String appName, String password) {
		if (!StringUtils.hasText(password)) {
			return false;
		}
		ApplicationRegResultBO applicationRegResultBO;
		Properties environment = System.getProperties();
		String key = environment.getProperty(RegisterConstants.GATEWAY_ENCODE_KEY);
		password = SecureUtil.md5(key + password);
		try {
			applicationRegResultBO = gatewayMetricsService.reset(appName, password);
		} catch (Exception e) {
			applicationRegResultBO = null;
		}
		return ObjectUtil.isNotNull(applicationRegResultBO);
	}

	public R<String> getMasterStatus(String auth, String appName) {
		//String encodeLevel0 = GatewayConstant.getLevel0Code();
		boolean check = AppCtlService.checkFirstLevelEncryptedCode(auth);
		if (!check) {
			return R.fail(ResultCode.UN_AUTHORIZED, appName);
		}

		String pub = GatewayConstant.getDefaultCtlPublic();
		int pos = GatewayConstant.getNoiseEndPos(pub);
		pub = pub.substring(pos);
		String encrypt = RsaUtil.encrypt(String.valueOf(System.currentTimeMillis()), pub);
		StringBuffer sb = new StringBuffer(appName);
		sb.append(",").append(encrypt);
		return R.success(ResultCode.SUCCESS, sb.toString(), ResultCode.SUCCESS.getMessage());
	}


	public Map<String, List<ServiceInstance>> getALLInstances() {
		Map<String, List<ServiceInstance>> instances = new HashMap<>(16);
		List<String> services = discoveryClient.getServices();
		log.info("services->{}", JSON.toJSONString(services));
		services.forEach(s -> {
			List<ServiceInstance> list = discoveryClient.getInstances(s);
			instances.put(s, list);
		});
		return instances;
	}

	public List<String> getAllConsulServerIp() {
		List<String> serverList = new ArrayList<>();
		Map<String, List<ServiceInstance>> instances = getALLInstances();
		for (String name : instances.keySet()) {
			if ("consul".equalsIgnoreCase(name)) {
				List<ServiceInstance> instanceList = instances.get(name);
				for (ServiceInstance item : instanceList) {
					serverList.add(item.getHost());
				}
			}
		}
		log.info("consul:{}", new Gson().toJson(serverList));
		return serverList;
	}

}
