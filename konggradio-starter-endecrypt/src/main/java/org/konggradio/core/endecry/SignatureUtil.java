/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: SignatureUtil.java
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

import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author: xiongchengwei
 */
public class SignatureUtil {

    private SignatureUtil() {}

    /**
     * 签名
     *
     * @param algorithm
     * @param signAlgorithm
     * @param data
     * @param privateKey
     * @return byte[]
     * @throws IllegalStateException
     */
    public static byte[] sign(final String algorithm, final String signAlgorithm, final byte[] data,
            final byte[] privateKey) {
        try {
            final PrivateKey key = (PrivateKey) buildKey(algorithm, privateKey, false);

            java.security.Signature signature = java.security.Signature.getInstance(signAlgorithm);

            signature.initSign(key);

            signature.update(data);

            return signature.sign();

        } catch (Exception e) {

            throw fail(e.getMessage());
        }
    }

    /**
     * 验签名
     *
     * @param algorithm
     * @param signAlgorithm
     * @param data
     * @param sign
     * @param publicKey
     * @return boolean
     * @throws IllegalStateException
     */
    public static boolean verify(final String algorithm, final String signAlgorithm, final byte[] data,
            final byte[] sign, final byte[] publicKey) {
        try {
            final PublicKey key = (PublicKey) buildKey(algorithm, publicKey, true);

            java.security.Signature signature = java.security.Signature.getInstance(signAlgorithm);

            signature.initVerify(key);

            signature.update(data);

            return signature.verify(sign);
        } catch (Exception e) {

            throw fail(e.getMessage());
        }
    }

    public static KeyPair generateKey(final String algorithms) {
        return generateKey(algorithms, 512);
    }

    public static KeyPair generateKey(final String algorithms, final int keysize) {
        final SecureRandom secureRandom = new SecureRandom();
        try {

            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithms);

            keyPairGenerator.initialize(keysize, secureRandom);

            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {

            throw fail(e.getMessage());
        }
    }

    public static Key buildKey(final String algorithm, byte[] key, boolean publicKey) {
        try {
            KeySpec keySpec = null;
            if (publicKey) {
                keySpec = new X509EncodedKeySpec(key);
            } else {
                keySpec = new PKCS8EncodedKeySpec(key);
            }
            final KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

            if (publicKey) {
                return keyFactory.generatePublic(keySpec);
            } else {
                return keyFactory.generatePrivate(keySpec);
            }

        } catch (Exception e) {
            throw fail(e.getMessage());
        }
    }

    /**
     * 支持的签名算法
     *
     * @author: xiongchengwei
     * @date:2017年7月24日 下午2:55:54
     */
    public enum Signature {
        SHA256WithRSA("SHA256WithRSA"), MD5withRSA("MD5withRSA"), SHA512WithRSA("SHA512WithRSA");
        private final String signature;

        Signature(String signature) {
            this.signature = signature;
        }

        public String getSignature() {
            return signature;
        }
    }

    public static IllegalStateException fail(String message) {
        return new IllegalStateException(message);
    }
}
