/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: HardwareUtil.java
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

package org.konggradio.hardware;

import oshi.SystemInfo;

import java.util.Collections;
import java.util.List;

public class HardwareUtil {
	public static String generateLicenseKey() {
		SystemInfo si = new SystemInfo();
		List<String> macList = NetworkUtil.getMaces();
		Collections.sort(macList);
		StringBuffer sb = new StringBuffer("");
		for (String str : macList) {
			sb.append("|").append(str);
		}
		return sb.toString();
	}

}
