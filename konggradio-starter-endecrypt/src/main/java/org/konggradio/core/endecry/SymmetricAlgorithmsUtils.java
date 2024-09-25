/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: SymmetricAlgorithmsUtils.java
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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * <pre>
 * 对称加密算法，支持的算法{@link SymmetricAlgorithms}
 * </pre>
 *
 * @author: xiongchengwei
 */
public class SymmetricAlgorithmsUtils {

    private static final String DEFAULT_SYMMETRIC = SymmetricAlgorithms.AES.algorithms;

    private static final int DEFAULT_KEYSIZE = 1024;

    public static final String DEFAULT_ENCODE = "UTF-8";

    private SymmetricAlgorithmsUtils() {}

    public static byte[] desEncrypt(final byte[] data, final byte[] key) {
        return encrypt(SymmetricAlgorithms.DES, data, key);
    }

    public static byte[] desDecrypt(final byte[] data, final byte[] key) {
        return decrypt(SymmetricAlgorithms.DES, data, key);
    }

    public static byte[] desEncrypt(final byte[] data, final SecretKey key) {
        return encrypt(SymmetricAlgorithms.DES, data, key);
    }

    public static byte[] desDecrypt(final byte[] data, final SecretKey key) {
        return decrypt(SymmetricAlgorithms.DES, data, key);
    }

    public static byte[] desEncrypt(final String data, final String key) {
        try {
            return encrypt(SymmetricAlgorithms.DES, data.getBytes(DEFAULT_ENCODE), key.getBytes(DEFAULT_ENCODE));
        } catch (Exception e) {
            throw fail(e.getMessage());
        }
    }

    /**
     *
     * @param algorithm 算法
     * @param data 数据
     * @param key 秘钥
     * @return byte[]
     */
    public static byte[] encrypt(final SymmetricAlgorithms algorithm, final String data, final String key) {
        try {
            return encrypt(algorithm, data.getBytes(DEFAULT_ENCODE), key.getBytes(DEFAULT_ENCODE));
        } catch (Exception e) {
            throw fail(e.getMessage());
        }
    }

    /**
     * 加密
     *
     * @param algorithm
     * @param data
     * @param key
     * @return byte[]
     * @throws IllegalStateException
     */
    private static byte[] encrypt(final SymmetricAlgorithms algorithm, final byte[] data, final byte[] key) {
        final SecureRandom secureRandom = new SecureRandom();
        try {
            final Cipher cipher = Cipher.getInstance(algorithm.getAlgorithms());
            cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey(algorithm, key), secureRandom);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw fail(e.getMessage());
        }
    }

    private static byte[] encrypt(final SymmetricAlgorithms algorithm, final byte[] data, SecretKey secretKey) {
        final SecureRandom secureRandom = new SecureRandom();
        try {
            final Cipher cipher = Cipher.getInstance(algorithm.getAlgorithms());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, secureRandom);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw fail(e.getMessage());
        }
    }

    public final static SecretKey generateSecretKey(final SymmetricAlgorithms algorithm, byte[] key) {
        try {
            final KeySpec keySpec = generateKeySpec(algorithm.getAlgorithms(), key);
            final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm.getAlgorithms());
            return secretKeyFactory.generateSecret(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw fail(e.getMessage());
        }
    }

    private final static KeySpec generateKeySpec(String algorithms, byte[] key) {
        return new SecretKeySpec(key, algorithms);
    }

    public static byte[] decrypt(final SymmetricAlgorithms algorithm, final String data, final String key) {
        try {
            return decrypt(algorithm, data.getBytes(DEFAULT_ENCODE), key.getBytes(DEFAULT_ENCODE));
        } catch (Exception e) {
            throw fail(e.getMessage());
        }
    }

    /**
     * 解密
     *
     * @param algorithm
     * @param data
     * @param key
     * @return byte[]
     * @throws IllegalStateException
     */
    private static byte[] decrypt(final SymmetricAlgorithms algorithm, final byte[] data, final byte[] key) {
        final SecureRandom secureRandom = new SecureRandom();
        final SecretKey secretKey = generateSecretKey(algorithm, key);
        try {
            final Cipher cipher = Cipher.getInstance(algorithm.getAlgorithms());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, secureRandom);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw fail(e.getMessage());
        }
    }

    private static byte[] decrypt(final SymmetricAlgorithms algorithm, final byte[] data, final SecretKey secretKey) {
        final SecureRandom secureRandom = new SecureRandom();
        try {
            final Cipher cipher = Cipher.getInstance(algorithm.getAlgorithms());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, secureRandom);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw fail(e.getMessage());
        }
    }

    public static IllegalStateException fail(String message) {
        return new IllegalStateException(message);
    }

    /**
     * <pre>
     * 支持的算法： AES，DES
     * </pre>
     *
     * @author: xiongchengwei
     * @date:2017年7月13日 上午10:17:55
     */
    public enum SymmetricAlgorithms {
        AES("AES"), DES("DES");
        private final String algorithms;

        private SymmetricAlgorithms(String algorithms) {
            this.algorithms = algorithms;
        }

        public String getAlgorithms() {
            return algorithms;
        }
    }
}
