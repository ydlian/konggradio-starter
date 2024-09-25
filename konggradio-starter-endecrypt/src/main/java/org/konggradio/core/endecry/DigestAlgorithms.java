/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: DigestAlgorithms.java
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

package org.konggradio.core.endecry;

/**
 * @author: xiongchengwei
 */
public enum DigestAlgorithms {

    MD2("MD2"),

    MD5("MD5"),

    SHA_1("SHA-1"),

    SHA_256("SHA-256"),

    SHA_384("SHA-384"),

    SHA_512("SHA-512"),

    HMAC_MD5("HmacMD5"),

    HMAC_SHA_1("HmacSHA1"),

    HMAC_SHA_256("HmacSHA256"),

    HMAC_SHA_384("HmacSHA384"),

    HMAC_SHA_512("HmacSHA512");
    ;

    private final String algorithm;

    private DigestAlgorithms(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }

}
