/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: GatewayAppCtlConfigService.java
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

import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.constant.AppConstant;
import org.konggradio.core.constant.GatewayConstant;
import org.konggradio.core.tool.api.R;
import org.konggradio.core.tool.api.ResultCode;
import org.konggradio.core.tool.utils.RedisUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@Slf4j
public class GatewayAppCtlConfigService {
	@Resource
	RedisUtil redisUtil;

	@Resource
	SecurityCheckService securityCheckService;

	private String getRedisAppWhiteListKey() {
		return GatewayConstant.getWhiteListAppSetKey();
	}
	private String getRedisBlackListAppKey() {
		return GatewayConstant.getBlackListAppSetKey();
	}

	public Set<Object> getWhiteAppList(List<String> initList) {
		//List<String> initList = AuthProvider.getDefaultSkipUrl();
		Set<Object> result = redisUtil.sGet(getRedisAppWhiteListKey());
		if (result == null) {
			result = new HashSet<>();
		}
		for (String uri : initList) {
			result.add(uri);
		}
		return result;
	}
	public R<Long> addWhiteAppList(String appName, String password) {
		if (!securityCheckService.checkGateSecondaryPass(password)) {
			return R.fail(ResultCode.UN_AUTHORIZED);
		}
		long val = redisUtil.sSet(getRedisAppWhiteListKey(), appName);
		return R.success(ResultCode.SUCCESS, val);
	}

	public R<Long> delWhiteAppList(String appName, String password) {
		if (!securityCheckService.checkGateSecondaryPass(password)) {
			return R.fail(ResultCode.UN_AUTHORIZED);
		}
		long val = redisUtil.setRemove(getRedisAppWhiteListKey(), appName);
		return R.success(ResultCode.SUCCESS, val);
	}

	////////////////////////////black list ///////////////////////////
	public R<Long> addBlackAppList(String appName, String password) {
		if (!securityCheckService.checkGateSecondaryPass(password)) {
			return R.fail(ResultCode.UN_AUTHORIZED);
		}
		long val = redisUtil.sSet(getRedisBlackListAppKey(), appName);
		return R.success(ResultCode.SUCCESS, val);
	}


	public R<Long> delBlackAppList(String appName, String password) {
		if (!securityCheckService.checkGateSecondaryPass(password)) {
			return R.fail(ResultCode.UN_AUTHORIZED);
		}
		long val = redisUtil.setRemove(getRedisBlackListAppKey(), appName);
		return R.success(ResultCode.SUCCESS, val);
	}


	public Set<Object> getBlackAppList(List<String> initList) {

		Set<Object> result = redisUtil.sGet(getRedisBlackListAppKey());
		if (result == null) {
			result = new HashSet<>();
		}
		for (String uri : initList) {
			result.add(uri);
		}
		return result;
	}

}