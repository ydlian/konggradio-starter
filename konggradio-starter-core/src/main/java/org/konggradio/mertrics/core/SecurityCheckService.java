/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: SecurityCheckService.java
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

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.RegisterBuilder;
import org.konggradio.core.constant.AppConstant;
import org.konggradio.core.constants.RegisterConstants;
import org.konggradio.core.tool.utils.AESBase64Util;
import org.konggradio.core.tool.utils.Md5Utils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class SecurityCheckService {
	public static final int PASS_LENGTH_LIMIT = 32;

	protected boolean checkGateSecondaryPass(String password) {
		if (!StringUtils.hasText(password)) {
			return false;
		}
		//log.info("password ={}",password);
		if (password.endsWith("==")) {
			String temp;
			try {
				String key = Md5Utils.md5(AppConstant.APPLICATION_GATEWAY_NAME).substring(0, 16);
				temp = AESBase64Util.decrypt(password, key);
				//log.info("key ={},{}",key,temp);
			} catch (Exception e) {
				temp = "";
			}
			if(StringUtils.hasText(temp)){
				password = temp;
			}

		}
		if (password.length() < PASS_LENGTH_LIMIT) {
			String key = RegisterConstants.GATEWAY_ENCODE_KEY;
			String enPassword = SecureUtil.md5(key + password);
			boolean ck = RegisterBuilder.checkSecondarySecurityInTable(enPassword);
			return ck;
		}
		boolean result = AppCtlService.checkSecondLevelEncryptedCode(password);
		return result;
	}


}
