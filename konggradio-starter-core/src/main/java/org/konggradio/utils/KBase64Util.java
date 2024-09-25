/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KBase64Util.java
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

package org.konggradio.utils;

import org.konggradio.core.tool.utils.Charsets;
import org.springframework.util.Base64Utils;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

public class KBase64Util extends Base64Utils {
    public KBase64Util() {
    }

    public static String encode(String value) {
        return encode(value, Charsets.UTF_8);
    }

    public static String encode(String value, Charset charset) {
        byte[] val = value.getBytes(charset);
        return new String(encode(val), charset);
    }

    public static String encodeUrlSafe(String value) {
        return encodeUrlSafe(value, Charsets.UTF_8);
    }

    public static String encodeUrlSafe(String value, Charset charset) {
        byte[] val = value.getBytes(charset);
        return new String(encodeUrlSafe(val), charset);
    }

    public static String decode(String value) {
        return decode(value, Charsets.UTF_8);
    }

    public static String decode(String value, Charset charset) {
        byte[] val = value.getBytes(charset);
        byte[] decodedValue = decode(val);
        return new String(decodedValue, charset);
    }

    public static String decodeUrlSafe(String value) {
        return decodeUrlSafe(value, Charsets.UTF_8);
    }

    public static String decodeUrlSafe(String value, Charset charset) {
        byte[] val = value.getBytes(charset);
        byte[] decodedValue = decodeUrlSafe(val);
        return new String(decodedValue, charset);
    }

    public static boolean isBase64(String str) {
        String base64Rule = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        return Pattern.matches(base64Rule, str);
    }

}
