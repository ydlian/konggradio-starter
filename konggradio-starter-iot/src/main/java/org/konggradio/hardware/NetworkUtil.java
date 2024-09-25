/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: NetworkUtil.java
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

import org.apache.commons.codec.binary.Hex;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class NetworkUtil {
	public NetworkUtil() {
	}

	public static String getLocalInnerIp(){
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
	public static List<String> getMacList(){
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		//String cpuId = hal.getComputerSystem().getHardwareUUID();
		NetworkIF[] net = hal.getNetworkIFs().toArray(new NetworkIF[0]);
		List<String> macList = new ArrayList<>();
		for(NetworkIF e:net){
			macList.add(e.getMacaddr());
		}
		Collections.sort(macList);
		return macList;
	}
	public static List<String> getMaces() {
		ArrayList var0 = new ArrayList();

		try {
			Enumeration var1 = NetworkInterface.getNetworkInterfaces();

			while(true) {
				byte[] buffer;
				do {
					if (!var1.hasMoreElements()) {
						return var0;
					}

					NetworkInterface var2 = (NetworkInterface)var1.nextElement();
					buffer = var2.getHardwareAddress();
				} while(buffer == null);

				String var4 = String.valueOf(Hex.encodeHex(buffer, false));
				ArrayList var5 = new ArrayList();

				for(int var6 = 0; var6 < var4.length(); var6 += 2) {
					var5.add(var4.substring(var6, var6 + 2));
				}

				var0.add(String.join("-", var5));
			}
		} catch (Exception var7) {
			return var0;
		}
	}
}
