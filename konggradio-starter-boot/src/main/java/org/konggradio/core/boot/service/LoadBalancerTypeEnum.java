/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: LoadBalancerTypeEnum.java
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

package org.konggradio.core.boot.service;

import java.util.Arrays;

public enum LoadBalancerTypeEnum {
	ROUND_ROBIN(0, "ROUND_ROBIN"),
	DEV(1, "DEV"),
	GATEWAY(2, "GATEWAY"),
	RANDOM(3, "RANDOM"),

	FIX_FIRST(4, "FIX_FIRST"),
	DENY(100, "DENY"),
	;

	private int code;
	private String name;

	LoadBalancerTypeEnum(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static LoadBalancerTypeEnum findByCode(int code) {
		return Arrays.asList(LoadBalancerTypeEnum.values()).stream()
			.filter(binaryEnum -> binaryEnum.getCode() == code)
			.findFirst().orElse(null);
	}

	public static LoadBalancerTypeEnum findByName(String name) {
		return Arrays.asList(LoadBalancerTypeEnum.values()).stream()
			.filter(binaryEnum -> binaryEnum.getName().equalsIgnoreCase(name))
			.findFirst().orElse(null);
	}

}
