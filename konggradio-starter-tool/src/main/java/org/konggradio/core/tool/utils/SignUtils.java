/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: SignUtils.java
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

import org.konggradio.core.tool.constant.PluginConstants;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SignUtils.
 */
public final class SignUtils {

    private static final SignUtils SIGN_UTILS = new SignUtils();

    private SignUtils() {
    }

    /**
     * getInstance.
     *
     * @return {@linkplain SignUtils}
     */
    public static SignUtils getInstance() {
        return SIGN_UTILS;
    }

    /**
     * acquired sign.
     *
     * @param signKey sign key
     * @param params  params
     * @return sign
     */
    public static String generateSign(final String signKey, final Map<String, String> params) {
        List<String> storedKeys = Arrays.stream(params.keySet()
                .toArray(new String[]{}))
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
        final String sign = storedKeys.stream()
                .filter(key -> !Objects.equals(key, PluginConstants.SIGN))
                .map(key -> String.join("", key, params.get(key)))
                .collect(Collectors.joining()).trim()
                .concat(signKey);
        // TODO this is a risk for error charset coding with getBytes
        return DigestUtils.md5DigestAsHex(sign.getBytes()).toUpperCase();
    }

    /**
     * isValid.
     *
     * @param sign    sign
     * @param params  params
     * @param signKey signKey
     * @return boolean
     */
    public boolean isValid(final String sign, final Map<String, String> params, final String signKey) {
        return Objects.equals(sign, generateSign(signKey, params));
    }

    /**
     * Generate key string.
     *
     * @return the string
     */
    public String generateKey() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

}
