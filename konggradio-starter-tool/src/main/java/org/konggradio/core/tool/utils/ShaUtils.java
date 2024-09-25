/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ShaUtils.java
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

import org.apache.commons.lang3.StringUtils;
import org.konggradio.core.tool.exceptions.KongGradioRuntimeException;


import java.security.MessageDigest;
import java.util.Optional;

/**
 * The type SHA utils.
 */
public class ShaUtils {

    /**
     * sh512 Encryption string.
     *
     * @param src the src
     * @return the string
     */
    public static String shaEncryption(final String src) {
        return Optional.ofNullable(src).map(item -> {
            if (StringUtils.isEmpty(src)) {
                return null;
            }
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
                messageDigest.update(item.getBytes());
                byte[] byteBuffer = messageDigest.digest();
                StringBuffer strHexString = new StringBuffer();
                for (byte b:byteBuffer) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        strHexString.append('0');
                    }
                    strHexString.append(hex);
                }
                return strHexString.toString();
            } catch (Exception e) {
                throw new KongGradioRuntimeException(e);
            }
        }).orElse(null);
    }
}
