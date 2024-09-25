/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: Md5Utils.java
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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.konggradio.core.constants.RegisterConstants;
import org.konggradio.core.tool.exceptions.KongGradioRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The type Md5 utils.
 */
public class Md5Utils {
    /**
     * logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Md5Utils.class);

    /**
     * Md 5 string.
     *
     * @param src     the src
     * @param charset the charset
     *
     * @return the string
     */
    private static String md5(final String src, final String charset) {
        MessageDigest md5;
        StringBuilder hexValue = new StringBuilder(32);
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new KongGradioRuntimeException("MD5 not supported", e);
        }
        byte[] byteArray = new byte[0];
        try {
            byteArray = src.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage(), e);
        }
        byte[] md5Bytes = md5.digest(byteArray);
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * Md 5 string.
     *
     * @param src the src
     * @return the string
     */
    public static String md5(final String src) {
        return md5(src, StandardCharsets.UTF_8.name());
    }

	public static void main(String[] args) throws JsonProcessingException {
		String key = Md5Utils.md5("/assets/search") + RegisterConstants.SWAN_FIX_SUFFIX_KEY;
		//zhengqitong jiami mima
		key = Md5Utils.md5("123456");
		System.out.println(key);
	}
}
