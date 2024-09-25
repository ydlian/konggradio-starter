/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongGradioAccountGetter.java
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
package org.konggradio.core.cloud.header;

import org.konggradio.core.secure.KongGradioUser;
import org.konggradio.core.secure.utils.SecureUtil;
import org.konggradio.core.tool.utils.Charsets;
import org.konggradio.core.tool.utils.UrlUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户信息获取器
 *
 * @author ydlian
 */
public class KongGradioAccountGetter implements KongGradioFeignAccountGetter {

	@Override
	public String get(HttpServletRequest request) {
		KongGradioUser account = SecureUtil.getUser();
		if (account == null) {
			return null;
		}
		// 增加用户头, 123[admin]
		String xAccount = String.format("%s[%s]", account.getUserId(), account.getUserName());
		return UrlUtil.encodeURL(xAccount, Charsets.UTF_8);
	}

}
