/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: XorUtil.java
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

package org.konggradio.core.tool.utils;

import cn.hutool.core.codec.Base64;

/**
 * StringFog base64+xor encrypt and decrypt implementation.
 *
 * @author Megatron King
 * @since 2018/9/2 14:34
 */
public  class XorUtil {
	private static final String CHARSET_NAME_UTF_8 = "UTF-8";
	public static String encrypt(String data, String key) {
		String newData;
		try {
			newData = Base64.encode(xor(data.getBytes(CHARSET_NAME_UTF_8), key));
		} catch (Exception e) {
			newData = Base64.encode(xor(data.getBytes(), key));
		}
		return newData;
	}
	public static String decrypt(String data, String key) {
		String newData;
		try {
			newData = new String(xor(Base64.decode(data), key), CHARSET_NAME_UTF_8);
		} catch (Exception e) {
			newData = new String(xor(Base64.decode(data), key));
		}
		return newData;
	}
	/**
	 public boolean overflow(String data, String key) {
	 return data != null && data.length() * 4 / 3 >= 1024;
	 }*/
	private static byte[] xor(byte[] data, String key) {
		int len = data.length;
		int lenKey = key.length();
		int i = 0;
		int j = 0;
		while (i < len) {
			if (j >= lenKey) {
				j = 0;
			}
			data[i] = (byte) (data[i] ^ key.charAt(j));
			i++;
			j++;
		}
		return data;
	}

}
