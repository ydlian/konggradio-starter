/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: GatewayProvider.java
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
package org.konggradio.gateway;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 鉴权配置
 *
 * @author ydlianwill
 */
public class GatewayProvider {
	private static final List<String> DEFAULT_BLACK_URL = new ArrayList<>();
	private static final List<String> DEFAULT_SKIP_URL = new ArrayList<>();
	private static final List<String> INNER_SERVICE_URL = new ArrayList<>();

	private static final Set<String> SKIP_APP_NAME_SETS = new HashSet<>();
	private static final Set<String> DEFAULT_BLACK_APP = new HashSet<>();

	static {

		DEFAULT_SKIP_URL.add("/example");
		DEFAULT_SKIP_URL.add("/heartbeat");
		DEFAULT_SKIP_URL.add("/token/**");
		DEFAULT_SKIP_URL.add("/captcha/**");
		DEFAULT_SKIP_URL.add("/demo/**");
		DEFAULT_SKIP_URL.add("/health/**");
		DEFAULT_SKIP_URL.add("/actuator/health/**");
		DEFAULT_SKIP_URL.add("/v2/api-docs/**");
		DEFAULT_SKIP_URL.add("/auth/**");
		DEFAULT_SKIP_URL.add("/*/auth/**");
		DEFAULT_SKIP_URL.add("/oauth/**");
		DEFAULT_SKIP_URL.add("/log/**");
		DEFAULT_SKIP_URL.add("/menu/routes");
		DEFAULT_SKIP_URL.add("/menu/auth-routes");
		DEFAULT_SKIP_URL.add("/tenant/info");
		DEFAULT_SKIP_URL.add("/order/create/**");
		DEFAULT_SKIP_URL.add("/storage/deduct/**");
		DEFAULT_SKIP_URL.add("/error/**");
		DEFAULT_SKIP_URL.add("/assets/**");
		DEFAULT_SKIP_URL.add("/config/**");

	}

	/**
	 * 默认无需鉴权的API
	 */
	public static List<String> getDefaultSkipUrl() {
		return DEFAULT_SKIP_URL;
	}


	public static Set<String> getDefaultWhiteAppList() {
		return SKIP_APP_NAME_SETS;
	}
	public static Set<String> getDefaultBlackAppList() {
		return DEFAULT_BLACK_APP;
	}

	public static List<String> getDefaultBlackUrl() {
		return DEFAULT_BLACK_URL;
	}

	/**
	 * 默认内部服务的API
	 */
	public static List<String> getInnerServiceUrl() {
		return INNER_SERVICE_URL;
	}

	/**
	 * 默认内部服务的API
	 */
	public static Set<String> getSkipAppNameList() {
		return SKIP_APP_NAME_SETS;
	}
}
