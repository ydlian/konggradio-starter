/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KeyContent.java
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

import javax.security.auth.x500.X500Principal;

/**
 * @author: xiongchengwei
 */
public class KeyContent {
    // ==========================工作模式============================
    public static final String BLOCK_MODE_ECB = "ECB";
    public static final String BLOCK_MODE_CBC = "CBC";
    public static final String DEFAULT_BLOCK_MODE = BLOCK_MODE_ECB;
    // ===========================填充方式==========================
    public static final String PADDING_PKCS_1 = "PKCS1Padding";
    public static final String PADDING_PKCS_7 = "PKCS7Padding";
    public static final String PADDING_PKCS_8 = "PKCS8Padding";
    public static final String DEFAULT_PADDING = PADDING_PKCS_1;

    String algorithm;
    String blockMode;// 工作模式
    String padding;// 填充方式

    public KeyContent(String algorithm) {
        super();
        this.algorithm = algorithm;
        this.blockMode = DEFAULT_BLOCK_MODE;
        this.padding = DEFAULT_PADDING;
    }

    public String buildCipherAlgorithm() {
        return algorithm + "/" + blockMode + "/" + padding;
    }

    public class SymmetricContent extends KeyContent {
        int keySize;

        public SymmetricContent(String algorithm) {
            super(algorithm);
        }

        public SymmetricContent(String algorithm, int keySize) {
            super(algorithm);
            this.keySize = keySize;
        }

        public SymmetricContent setKeySize(int keySize) {
            this.keySize = keySize;
            return this;
        }
    }
    public class AsymmetricContent extends KeyContent {
        int keySize;
        int blockSize;
        String encryptionPaddings;
        X500Principal subject;

        public AsymmetricContent(String algorithm) {
            super(algorithm);
        }

        public AsymmetricContent setBlockSize(int blockModes) {
            this.blockSize = blockModes;
            return this;
        }

        public AsymmetricContent setEncryptionPaddings(String encryptionPaddings) {
            this.encryptionPaddings = encryptionPaddings;
            return this;
        }

        public AsymmetricContent setSubject(X500Principal subject) {
            this.subject = subject;
            return this;
        }

        public AsymmetricContent setKeySize(int keySize) {
            this.keySize = keySize;
            return this;
        }
    }

}
