/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: AppCtlService.java
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

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.konggradio.core.RegisterBuilder;
import org.konggradio.core.constant.AppConstant;
import org.konggradio.core.constant.GatewayConstant;
import org.konggradio.core.constant.SecDictionary;
import org.konggradio.core.constants.RegisterConstants;
import org.konggradio.core.endecry.RsaUtil;
import org.konggradio.core.tool.api.R;
import org.konggradio.core.utils.HttpClientUtil;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Properties;
import java.util.Set;

public class AppCtlService {




	public static boolean doFirstLevelFilter(String appName, String pass) {
		if (!StringUtils.hasText(pass)) {
			return false;
		}
		String encryptedCode = pass;
		String key = RegisterConstants.GATEWAY_ENCODE_KEY;
		String authKey = cn.hutool.crypto.SecureUtil.md5(key + pass);
		boolean result;
		if (pass.length() < SecurityCheckService.PASS_LENGTH_LIMIT) {
			result = RegisterBuilder.checkFirstSecurityInTable(appName, authKey);
		} else {
			result = checkFirstLevelEncryptedCode(encryptedCode);
		}
		return result;
	}

	public static boolean doSecondLevelFilter(String pass) {
		if (!StringUtils.hasText(pass)) {
			return false;
		}
		String encryptedCode = pass;
		String key = RegisterConstants.GATEWAY_ENCODE_KEY;
		String authKey = cn.hutool.crypto.SecureUtil.md5(key + pass);
		boolean result;
		if (pass.length() < SecurityCheckService.PASS_LENGTH_LIMIT) {
			result = RegisterBuilder.checkSecondarySecurityInTable(authKey);
		} else {
			result = checkSecondLevelEncryptedCode(encryptedCode);
		}
		return result;
	}


	public static void changeApplicationMetric(boolean value, boolean reset) {
		Properties environment = System.getProperties();
		if (reset) {
			environment.setProperty(RegisterConstants.GATE_WAY_ADMIN_CTL_SWITCH_KEY, "");
		}
		environment.setProperty(RegisterConstants.GATE_WAY_ADMIN_CTL_SWITCH_KEY, String.valueOf(value));

	}

	public static boolean getApplicationMetric() {
		Properties environment = System.getProperties();
		String value = environment.getProperty(RegisterConstants.GATE_WAY_ADMIN_CTL_SWITCH_KEY);
		return Boolean.parseBoolean(value);
	}

	public static String getEncodeAuthKeyByEncryptedData(String encryptedCode) {
		if (!StringUtils.hasText(encryptedCode)) return "";
		String privateKey = GatewayConstant.getDefaultCtlPrivate();
		int pos = GatewayConstant.getNoiseEndPos(privateKey);
		privateKey = privateKey.substring(pos);
		String code = RsaUtil.decrypt(encryptedCode, privateKey);
		String key = RegisterConstants.GATEWAY_ENCODE_KEY;
		String authKey = cn.hutool.crypto.SecureUtil.md5(key + code);
		return authKey;
	}

	public static boolean checkFirstLevelEncryptedCode(String encryptedCode) {
		String tableCode = getEncodeAuthKeyByEncryptedData(encryptedCode);
		if (StringUtils.hasText(tableCode) && SecDictionary.getPassTable().contains(tableCode)) {
			return true;
		}
		return false;
	}



	public static boolean checkSecondLevelEncryptedCode(String encryptedCode) {
		String tableCode = getEncodeAuthKeyByEncryptedData(encryptedCode);
		Set<String> whiteList = SecDictionary.getWhiteListPassTable();
		if (StringUtils.hasText(tableCode) && whiteList.contains(tableCode)) {
			return true;
		}
		return false;
	}

	public static void updateMasterGateStatusByResp(String resp) {
		Gson gson = new Gson();
		Type founderSetType = new TypeToken<R<String>>() {
		}.getType();
		R<String> founderResult = null;
		try {
			founderResult = gson.fromJson(resp, founderSetType);
		} catch (Exception e) {

		}
		if (!StringUtils.hasText(resp) || founderResult == null || !founderResult.isSuccess()) {
			RegisterBuilder.updateMasterGateStatus(String.valueOf(false));
			return;
		}
		String body = founderResult.getData();
		//System.out.println(body);
		boolean status = false;
		int pos = body.indexOf(",");
		String timeStr = body.substring(pos + 1);
		long timestamp = getTime(timeStr);

		if (Math.abs(System.currentTimeMillis() - timestamp) <= 30 * 60 * 1000) {
			status = true;
			//System.out.println(timestamp);
		}
		RegisterBuilder.writeMetricRecordLogFile(String.valueOf(timestamp) + "," + status, true);
		RegisterBuilder.updateMasterGateStatus(String.valueOf(status));
	}

	private static long getTime(String data) {
		long timestamp = 0L;
		if (!StringUtils.hasText(data)) return timestamp;
		String pr = GatewayConstant.getDefaultCtlPrivate();
		int pos = GatewayConstant.getNoiseEndPos(pr);
		pr = pr.substring(pos);
		try {
			String result = RsaUtil.decrypt(data, pr);
			timestamp = Long.parseLong(result);
		} catch (Exception e) {
			timestamp = 0L;
		}
		return timestamp;

	}

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

	public static boolean buildStlStatus(String gatewayDomain, String appName) {
		StringBuffer sb = new StringBuffer(gatewayDomain);
		String queryUrl = sb.append(AppConstant.GATEWAY_STL_STATUS_URI).append("?appName=").append(appName).toString();
		String resp = HttpClientUtil.getRequestAsString(queryUrl, SecureUtil.md5(appName));
		if (!StringUtils.hasText(resp)) {
			return false;
		}
		R<Boolean> changeVO = null;
		if (StringUtils.hasText(resp)) {
			Gson gson = new Gson();
			try {
				changeVO = gson.fromJson(resp, R.class);
			} catch (Exception e) {
				changeVO = null;
			}
			if (ObjectUtil.isNull(changeVO)) {
				return false;
			}
		}
		if (!changeVO.getData()) {
			return false;
		}
		return true;
	}
}
