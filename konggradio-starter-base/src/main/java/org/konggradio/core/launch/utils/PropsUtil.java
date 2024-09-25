/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: PropsUtil.java
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
package org.konggradio.core.launch.utils;


import io.micrometer.core.instrument.util.StringUtils;

import java.util.Properties;

/**
 * 配置工具类
 *
 * @author ydlian
 */
public class PropsUtil {

	/**
	 * 设置配置值，已存在则跳过
	 *
	 * @param props property
	 * @param key   key
	 * @param value value
	 */
	public static void setProperty(Properties props, String key, String value) {
		if (StringUtils.isBlank(props.getProperty(key))) {
			props.setProperty(key, value);
		}
	}

}
