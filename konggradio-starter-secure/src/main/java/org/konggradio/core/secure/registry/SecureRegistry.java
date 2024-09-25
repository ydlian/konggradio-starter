/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: SecureRegistry.java
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
package org.konggradio.core.secure.registry;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * secure api放行配置
 *
 * @author ydlian
 */
@Data
public class SecureRegistry {

	private boolean enabled = false;

	private final List<String> defaultExcludePatterns = new ArrayList<>();

	private final List<String> excludePatterns = new ArrayList<>();

	public SecureRegistry() {
		this.defaultExcludePatterns.add("/actuator/health/**");
		this.defaultExcludePatterns.add("/v2/api-docs/**");
		this.defaultExcludePatterns.add("/auth/**");
		this.defaultExcludePatterns.add("/token/**");
		this.defaultExcludePatterns.add("/log/**");
		this.defaultExcludePatterns.add("/user/user-info");
		this.defaultExcludePatterns.add("/user/user-info-by-id");
		this.defaultExcludePatterns.add("/menu/auth-routes");
		this.defaultExcludePatterns.add("/error/**");
		this.defaultExcludePatterns.add("/assets/**");
	}

	/**
	 * 设置放行api
	 */
	public SecureRegistry excludePathPatterns(String... patterns) {
		return excludePathPatterns(Arrays.asList(patterns));
	}

	/**
	 * 设置放行api
	 */
	public SecureRegistry excludePathPatterns(List<String> patterns) {
		this.excludePatterns.addAll(patterns);
		return this;
	}

}
