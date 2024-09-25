/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: RegisterConstants.java
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

package org.konggradio.core.constants;

import org.konggradio.core.constant.AppConstant;
import org.springframework.util.StringUtils;

public class RegisterConstants {
	public static final String PWD_FILE_ROOT = "/root/.local/";
	public static final String PWD_FILE_SHORT = "pwd.gd";
	public static final String APP_LIC_FILE_SHORT = ".lic.key";
	public static final String COM_LIC_FILE_SHORT = "lic.key";
	public static final String APP_MD5SUM_SHORT = ".app.md5";

	public static final String GW_X_AUTH_TOKEN = "x-auth-token";

	public static final String GW_X_HID = "x-hid";

	public static final String DOT = ".";
	public static final String GATEWAY_ACTIVE_MODE_PROP = "gateway.active.mode";
	public static final String GATEWAY_ADMIN_TYPE_PROP = "gateway.admin.type";

	public static final String GATEWAY_ENCODE_KEY = "gateway.sec.encode.key";

	public static final String DIAMOND_APP_SUFFIX_KEY = ".diamond.application.key";

	public static final String SWAN_SUFFIX_KEY = ".swan.application.key";

	public static final String MENI_TAG_SUFFIX_KEY = ".meni.tag.key";
	public static final String SWAN_FIX_SUFFIX_KEY = ".swan.fix.application.key";

	public static final String APP_PROFILE_SUFFIX_KEY = ".application.profile.key";
	public static String CONSUL_ADDR_DENY = "http://8.8.8.8";

	public static final String APP_REG_RESULT_BO_KEY = "app.reg.result.bo.key";

	public static final String APP_REG_NAME_KEY = "app.reg.name.key";
	public static String PRINT_REQUEST_LOG_SWITCH_KEY = "KongGradio.log.print.switch";
	public static String CLIENT_PROXY_SWITCH_KEY = "KongGradio.client.proxy.switch";

	public static String APP_LAUNCH_CHECK_SWITCH_KEY = "KongGradio.launch.check.switch";
	public static String GATE_WAY_ADMIN_CTL_SWITCH_KEY = "gateway.admin.ctl.switch";

	public static String ENV_RECONNECT_CHECK_STATUS_KEY = "env.system.reconnect.check.status";

	public static String ENV_RELOAD_CHECK_STATUS_KEY = "env.system.reload.check.status";

	public static String ENV_PERF_CHECK_STATUS_KEY = "env.system.perf.check.status";
	public static String EVENT_TASK_KEY="event:task:key:";
	/**
	 * 判断是否为本地开发环境
	 *
	 * @return boolean
	 */
	public static boolean isLocalDev() {
		String osName = System.getProperty("os.name");
		return StringUtils.hasText(osName) && !(AppConstant.OS_NAME_LINUX.equals(osName.toUpperCase()));
	}
}
