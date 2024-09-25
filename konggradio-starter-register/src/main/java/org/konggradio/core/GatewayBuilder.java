/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: GatewayBuilder.java
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

import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.konggradio.core.constant.AppConstant;
import org.konggradio.core.constant.ConsulConstant;
import org.konggradio.core.constant.GatewayConstant;
import org.konggradio.core.constants.RegisterConstants;
import org.konggradio.core.encryption.utils.MessageSenderUtil;
import org.konggradio.core.model.bo.ApplicationRegResultBO;
import org.konggradio.core.model.bo.RegisterCenterBO;
import org.konggradio.core.model.vo.GatewayChangeVO;
import org.konggradio.core.utils.HttpClientUtil;
import org.konggradio.hardware.HardwareUtil;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class GatewayBuilder {
	public static boolean isMasterHealthStatus() {
		return MASTER_HEALTH_STATUS;
	}

	public static void setMasterHealthStatus(boolean masterHealthStatus) {
		MASTER_HEALTH_STATUS = masterHealthStatus;
	}

	private static boolean MASTER_HEALTH_STATUS = false;

	private static void print(String str) {

		System.out.println(str);
	}

	public static GatewayChangeVO buildCounterConfig(String gatewayDomain, String appName, String profile) {
		StringBuffer sb = new StringBuffer(gatewayDomain);
		//此时注册app，调用getApplicationMetricDataFromGate
		String queryUrl = sb.append(AppConstant.GATEWAY_METRIC_INIT_DATA_URI).append("?appName=").append(appName).toString();
		String resp = HttpClientUtil.getRequestAsString(queryUrl, SecureUtil.md5(appName));
		if (!StringUtils.hasText(resp)) {
			return null;
		}
		GatewayChangeVO changeVO = null;
		if (StringUtils.hasText(resp)) {
			//print("[check " + appName + "]:" + resp);
			Gson gson = new Gson();
			try {
				changeVO = gson.fromJson(resp, GatewayChangeVO.class);
			} catch (Exception e) {
				changeVO = null;
			}
			if (ObjectUtil.isNull(changeVO)) {
				return null;
			}
		}
		return changeVO;
	}

	public static boolean checkMasterHealth(String masterDomain) {
		boolean masterHealthStatus = false;
		StringBuffer sb = new StringBuffer(masterDomain);
		String queryUrl = sb.append(AppConstant.APP_HEALTH_URI).toString();
		String resp = HttpClientUtil.getRequestAsString(queryUrl, "");

		if (!StringUtils.hasText(resp)) {
			return masterHealthStatus;
		} else if (resp.contains("\"UP\"")) {
			masterHealthStatus = true;
			setMasterHealthStatus(true);
		}
		if (!masterHealthStatus) {
			StringBuffer sbNext = new StringBuffer(GatewayConstant.getGatewayAdminDefaultLocalHostAddress());
			queryUrl = sbNext.append(AppConstant.GATEWAY_ADMIN_INTER_PROXY_URI_PREFIX).append(AppConstant.APP_HEALTH_URI).toString();
			resp = HttpClientUtil.getRequestAsString(queryUrl, "");
			if (resp.contains("\"UP\"")) {
				masterHealthStatus = true;
				setMasterHealthStatus(true);
			}
		}
		return masterHealthStatus;

	}

	private static boolean isLocalDev() {
		String osName = System.getProperty("os.name");
		return StringUtils.hasText(osName) && !(AppConstant.OS_NAME_LINUX.equals(osName.toUpperCase()));
	}

	public static boolean reportService(String gatewayDomain, String appName, String host ,int port) {
		StringBuffer sb = new StringBuffer(gatewayDomain);
		String queryUrl = sb.append("/config/reportService").append("?appName=").append(appName)
			.append("&host=").append(host).append("&port=").append(port).toString();
		String resp = HttpClientUtil.getRequestAsString(queryUrl, SecureUtil.md5(appName));
		return true;
	}
	public static String buildConsulCenter(String gatewayDomain, String appName, String profile) {
		//gatewayDomain = gatewayDomain.endsWith("/") ? gatewayDomain : gatewayDomain + AppConstant.URL_SEPARATOR;
		StringBuffer sb = new StringBuffer(gatewayDomain);
		String hid = HardwareUtil.generateLicenseKey();
		String addresses = ConsulConstant.CONSUL_ADDR_LOCAL;
		String queryUrl = sb.append(AppConstant.GATEWAY_REGISTER_URI).append("?appName=").append(appName).toString();
		String resp = HttpClientUtil.getHidRequestAsString(queryUrl, SecureUtil.md5(appName), hid);
		if (!StringUtils.hasText(resp)) {
			if (!isLocalDev()) {
				Assert.hasText(resp, ApplicationRegister.APP_ENV_ERROR);
			}
			//如果是生产环境
			if (AppConstant.DEV_CODE.equalsIgnoreCase(profile) || AppConstant.TEST_CODE.equalsIgnoreCase(profile)
				|| ApplicationRegister.getGatewayActiveMode(appName)) {
				//本地开发模式
			} else {
				//Assert.hasText(resp,ApplicationRegister.APP_CANNOT_FOUND_GATEWAY);
				return RegisterConstants.CONSUL_ADDR_DENY;
			}
		}
		if (StringUtils.hasText(resp)) {
			print("[check " + appName + "]:" + resp);
			Gson gson = new Gson();
			Type founderSetType = new TypeToken<HashSet<RegisterCenterBO>>() {
			}.getType();
			HashSet<RegisterCenterBO> founderSet = null;
			try {
				founderSet = gson.fromJson(resp, founderSetType);
			} catch (Exception e) {

			}

			boolean applicationCanWork = true;
			if (!ObjectUtil.isEmpty(founderSet) && founderSet.size() > 0) {
				HashSet<String> resultSets = new HashSet<>();
				for (RegisterCenterBO registerCenterBO : founderSet) {
					ApplicationRegResultBO applicationRegInfo = registerCenterBO.getApplicationRegInfo();
					if (!applicationRegInfo.isRegResult()) {
						//todo: need func 不用判断
						//applicationCanWork = false;
					} else {
						applicationRegInfo.setAppName(appName);
						ApplicationRegister.setApplicationRegResultBo(applicationRegInfo);
					}
					//System.out.println(registerCenterBO.getAddress() + "," + registerCenterBO.getHashCode());
					if (HashUtil.mixHash(registerCenterBO.getAddress()) == registerCenterBO.getHashCode().longValue()) {
						resultSets.add(registerCenterBO.getAddress());
					} else {
						//System.out.println("<>");
					}
				}
				addresses = String.join(",", resultSets);
				//here is app,gateway is ok,application not gateway
				//对时检查,看返回内容信息
				if (!applicationCanWork) {
					print(ApplicationRegister.APP_RUN_LOCAL_MOD + appName);
					addresses = ConsulConstant.CONSUL_ADDR_LOCAL;
				}

			}

		} else {
			//联系不上网关,或者自己就是网关
		}
		System.out.println("[Init configuration]register: " + addresses);
		return addresses;
	}

	// for gateway connect super admin
	public static String buildMasterStatus(String appName) {
		String auth = GatewayConstant.getLevel0Code();
		StringBuffer sb = new StringBuffer(GatewayConstant.getGatewayAdminDefaultAddress());
		String queryUrl = sb.append(AppConstant.GATEWAY_MASTER_STATUS_URI).append("?appName=").append(appName).toString();
		String resp = null;
		try {
			resp = HttpClientUtil.getRequestAsString(queryUrl, auth);
		} catch (Exception e) {

		}
		if (!StringUtils.hasText(resp)) {
			return null;
		}
		return resp;
	}

	public static boolean checkGatewayByName(String name) {
		if (!StringUtils.hasText(name)) return false;
		return name.toLowerCase().startsWith(AppConstant.APPLICATION_GATEWAY_NAME);
	}
}
