/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: AsymmetricAlgorithmsUtils.java
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author: xiongchengwei
 */
public class AsymmetricAlgorithmsUtils {

    public static final int ENCRYPTION_BLOCK_SIZE = 117;

    public static final int DECRYPTION_BLOCK_SIZE = 128;

    public static final String DEFAULT_ENCODE = "UTF-8";

    private AsymmetricAlgorithmsUtils() {}

    public static byte[] rsaEncrypt(final byte[] data, final byte[] key) {
        return encrypt(AsymmetricAlgorithms.RSA, data, key);
    }

    public static byte[] rsaEncrypt(final byte[] data, final PublicKey key) {
        return encrypt(AsymmetricAlgorithms.RSA, data, key);
    }

    public static byte[] rsaEncrypt(final String data, final String key) {
        try {
            return encrypt(AsymmetricAlgorithms.RSA, data.getBytes(DEFAULT_ENCODE), key.getBytes(DEFAULT_ENCODE));
        } catch (Exception e) {
            throw fail(e.getMessage());
        }
    }

    public static byte[] encrypt(final AsymmetricAlgorithms algorithm, final String data, final String key) {
        try {
            return encrypt(algorithm, data.getBytes(DEFAULT_ENCODE), key.getBytes(DEFAULT_ENCODE));
        } catch (Exception e) {
            throw fail(e.getMessage());
        }
    }

    private static byte[] encrypt(final AsymmetricAlgorithms algorithm, final byte[] data, final byte[] publicKey) {
        final PublicKey key = (PublicKey) buildKey(algorithm, publicKey, true);
        return encrypt(algorithm, data, key);
    }

    /**
     * 加密；公秘钥加密，加密时，采用分块加密原则，块大小为117个字节为一块，完整块大小是128字节，11字节是解密算法需要的
     *
     * @param algorithm
     * @param data
     * @param publicKey
     * @return byte[]
     */
    private static byte[] encrypt(final AsymmetricAlgorithms algorithm, final byte[] data, final PublicKey publicKey) {
        // 用块加密和解密
        try (final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final ByteArrayInputStream inputStream = new ByteArrayInputStream(data);) {
            final SecureRandom secureRandom = new SecureRandom();
            final Cipher cipher = Cipher.getInstance(algorithm.getAlgorithms());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, secureRandom);

            final byte[] buff = new byte[ENCRYPTION_BLOCK_SIZE];
            int nextBuff;

            while ((nextBuff = inputStream.read(buff)) != -1) {
                byte[] block = null;
                if (ENCRYPTION_BLOCK_SIZE == nextBuff) {
                    block = buff;
                } else {
                    block = new byte[nextBuff];
                    System.arraycopy(buff, 0, block, 0, Math.min(buff.length, nextBuff));
                }
                out.write(cipher.doFinal(block));
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw fail(e.getMessage());
        } finally {
            // todo
        }
    }

    /**
     * 构建秘钥
     *
     * @param algorithm
     * @param key
     * @param publicKey
     * @return Key
     */
    public static Key buildKey(final AsymmetricAlgorithms algorithm, byte[] key, boolean publicKey) {
        try {
            KeySpec keySpec = null;
            if (publicKey) {
                keySpec = new X509EncodedKeySpec(key);
            } else {
                keySpec = new PKCS8EncodedKeySpec(key);
            }
            final KeyFactory keyFactory = KeyFactory.getInstance(algorithm.getAlgorithms());
            if (publicKey) {
                return keyFactory.generatePublic(keySpec);
            } else {
                return keyFactory.generatePrivate(keySpec);
            }
        } catch (Exception e) {
            throw fail(e.getMessage());
        }
    }

    public static byte[] rsaDecrypt(final byte[] data, final byte[] privateKey) {
        return decrypt(AsymmetricAlgorithms.RSA, data, privateKey);
    }

    public static byte[] rsaDecrypt(final byte[] data, final PrivateKey privateKey) {
        return decrypt(AsymmetricAlgorithms.RSA, data, privateKey);
    }

    public static byte[] decrypt(final AsymmetricAlgorithms algorithm, final String data, final String privateKey) {
        try {
            return decrypt(algorithm, data.getBytes(DEFAULT_ENCODE), privateKey.getBytes(DEFAULT_ENCODE));
        } catch (Exception e) {
            throw fail(e.getMessage());
        }
    }

    private static byte[] decrypt(final AsymmetricAlgorithms algorithm, final byte[] data, final byte[] privateKey) {
        final PrivateKey key = (PrivateKey) buildKey(algorithm, privateKey, false);
        return decrypt(algorithm, data, key);
    }

    /**
     * 私秘钥解密：采用分块解密，块大小为128字节为一块。
     *
     * @param algorithm
     * @param key
     * @param publicKey
     * @return Key
     */
    private static byte[] decrypt(final AsymmetricAlgorithms algorithm, final byte[] data,
            final PrivateKey privateKey) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final InputStream is = new ByteArrayInputStream(data);
        try {
            final SecureRandom secureRandom = new SecureRandom();
            final Cipher cipher = Cipher.getInstance(algorithm.getAlgorithms());
            cipher.init(Cipher.DECRYPT_MODE, privateKey, secureRandom);

            byte[] buf = new byte[DECRYPTION_BLOCK_SIZE];
            int bufl;

            while ((bufl = is.read(buf)) != -1) {
                byte[] block = null;
                if (buf.length == bufl) {
                    block = buf;
                } else {
                    block = new byte[bufl];
                    System.arraycopy(bufl, 0, block, 0, Math.min(buf.length, bufl));
                }
                os.write(cipher.doFinal(block));
            }

            return os.toByteArray();
        } catch (Exception e) {
            throw fail(e.getMessage());
        }
    }

    public static KeyPair generateKey(final AsymmetricAlgorithms algorithms, final int keysize) {
        return generateKey(algorithms.getAlgorithms(), keysize);
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

    public static IllegalStateException fail(String message) {
        return new IllegalStateException(message);
    }

    public enum AsymmetricAlgorithms {
        RSA("RSA")
        // ECC("ECC"), EI_GAMAL("EI_GAMAL"), DH("DH")
        ;
        private final String algorithms;

        private AsymmetricAlgorithms(String algorithms) {
            this.algorithms = algorithms;
        }

        public String getAlgorithms() {
            return algorithms;
        }
    }

}
