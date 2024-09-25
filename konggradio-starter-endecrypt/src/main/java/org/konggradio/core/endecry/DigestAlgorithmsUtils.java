/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: DigestAlgorithmsUtils.java
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <pre>
 * 单项hash算法，信息摘要算法{@link DigestAlgorithms}
 *
 * 默认返回小写字符
 * </pre>
 *
 * @author: xiongchengwei
 */
public class DigestAlgorithmsUtils {

    private DigestAlgorithmsUtils() {}

    private static final int STREAM_BUFFER_LENGTH = 1024;

    private static byte[] digest(final MessageDigest digest, final InputStream data) throws IOException {
        return updateDigest(digest, data).digest();
    }

    public static MessageDigest getDigest(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            throw fail(e.getMessage());
        }
    }

    /**
     * @param algorithm
     * @param key
     * @return Mac
     * @throws {@link IllegalArgumentException}
     */
    public static Mac getInitializedMac(final String algorithm, final byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException("key must not null");
        }
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(key, algorithm);

            final Mac mac = Mac.getInstance(algorithm);

            mac.init(keySpec);
            return mac;
        } catch (NoSuchAlgorithmException e) {
            throw fail(e.getMessage());
        } catch (InvalidKeyException e) {
            throw fail(e.getMessage());
        }
    }

    private static IllegalArgumentException fail(String e) {
        return new IllegalArgumentException(e);
    }

    public static MessageDigest getMd2Digest() {
        return getDigest(DigestAlgorithms.MD2.getAlgorithm());
    }

    public static MessageDigest getMd5Digest() {
        return getDigest(DigestAlgorithms.MD5.getAlgorithm());
    }

    public static MessageDigest getSha1Digest() {
        return getDigest(DigestAlgorithms.SHA_1.getAlgorithm());
    }

    public static MessageDigest getSha256Digest() {
        return getDigest(DigestAlgorithms.SHA_256.getAlgorithm());
    }

    public static MessageDigest getSha384Digest() {
        return getDigest(DigestAlgorithms.SHA_384.getAlgorithm());
    }

    public static MessageDigest getSha512Digest() {
        return getDigest(DigestAlgorithms.SHA_512.getAlgorithm());
    }

    // ==========================hmac============================

    public static byte[] hmacMd5(final byte[] data, final byte[] key) {
        try {
            return getInitializedMac(DigestAlgorithms.HMAC_MD5.getAlgorithm(), key).doFinal(data);
        } catch (IllegalStateException e) {
            throw fail(e.getMessage());
        }
    }

    public static String hmacMd5(final String data, final String key) {
        final byte[] bs = hmacMd5(getBytesUtf8(data), getBytesUtf8(key));
        return encodeHexLowerString(bs);
    }

    public static byte[] hmacSha1(final byte[] data, final byte[] key) {
        try {
            return getInitializedMac(DigestAlgorithms.HMAC_SHA_1.getAlgorithm(), key).doFinal(data);
        } catch (IllegalStateException e) {
            throw fail(e.getMessage());
        }
    }

    public static String hmacSha1(final String data, final String key) {
        final byte[] bs = hmacSha1(getBytesUtf8(data), getBytesUtf8(key));
        return encodeHexLowerString(bs);
    }

    public static byte[] hmacSha256(final byte[] data, final byte[] key) {
        try {
            return getInitializedMac(DigestAlgorithms.HMAC_SHA_256.getAlgorithm(), key).doFinal(data);
        } catch (IllegalStateException e) {

            throw fail(e.getMessage());
        }
    }

    public static String hmacSha256(final String data, final String key) {
        final byte[] bs = hmacSha256(getBytesUtf8(data), getBytesUtf8(key));
        return encodeHexLowerString(bs);
    }

    public static byte[] hmacSha384(final byte[] data, final byte[] key) {
        try {
            return getInitializedMac(DigestAlgorithms.HMAC_SHA_384.getAlgorithm(), key).doFinal(data);
        } catch (IllegalStateException e) {

            throw fail(e.getMessage());
        }
    }

    public static String hmacSha384(final String data, final String key) {
        final byte[] bs = hmacSha384(getBytesUtf8(data), getBytesUtf8(key));
        return encodeHexLowerString(bs);
    }

    public static byte[] hmacSha512(final byte[] data, final byte[] key) {
        try {
            return getInitializedMac(DigestAlgorithms.HMAC_SHA_512.getAlgorithm(), key).doFinal(data);
        } catch (IllegalStateException e) {

            throw fail(e.getMessage());
        }
    }

    public static String hmacSha512(final String data, final String key) {
        final byte[] bs = hmacSha512(getBytesUtf8(data), getBytesUtf8(key));
        return encodeHexLowerString(bs);
    }

    // =========================hmac==============================

    public static byte[] md2(final byte[] data) {
        return getMd2Digest().digest(data);
    }

    public static byte[] md2(final InputStream data) throws IOException {
        return digest(getMd2Digest(), data);
    }

    public static byte[] md2(final String data) {
        return md2(getBytesUtf8(data));
    }

    public static String md2Hex(final byte[] data) {
        return encodeHexLowerString(md2(data));
    }

    public static String md2Hex(final InputStream data) throws IOException {
        return encodeHexLowerString(md2(data));
    }

    public static String md2Hex(final String data) {
        return encodeHexLowerString(md2(data));
    }

    public static byte[] md5(final byte[] data) {
        return getMd5Digest().digest(data);
    }

    public static byte[] md5(final InputStream data) throws IOException {
        return digest(getMd5Digest(), data);
    }

    public static byte[] md5(final String data) {
        return md5(getBytesUtf8(data));
    }

    public static String md5Base64(final byte[] data) {
        return Base64Codec.encodeToString(md5(data));
    }

    public static String md5Base64(final InputStream data) throws IOException {
        return Base64Codec.encodeToString(md5(data));
    }

    public static String md5Base64(final String data) {
        return Base64Codec.encodeToString(md5(data));
    }

    public static String md5Hex(final byte[] data) {
        return encodeHexLowerString(md5(data));
    }

    public static String md5Hex(final InputStream data) throws IOException {
        return encodeHexLowerString(md5(data));
    }

    public static String md5Hex(final String data) {
        return encodeHexLowerString(md5(data));
    }

    public static byte[] sha1(final byte[] data) {
        return getSha1Digest().digest(data);
    }


    public static byte[] sha1(final InputStream data) throws IOException {
        return digest(getSha1Digest(), data);
    }


    public static byte[] sha1(final String data) {
        return sha1(getBytesUtf8(data));
    }


    public static String sha1Hex(final byte[] data) {
        return encodeHexLowerString(sha1(data));
    }


    public static String sha1Hex(final InputStream data) throws IOException {
        return encodeHexLowerString(sha1(data));
    }


    public static String sha1Hex(final String data) {
        return encodeHexLowerString(sha1(data));
    }


    public static byte[] sha256(final byte[] data) {
        return getSha256Digest().digest(data);
    }


    public static byte[] sha256(final InputStream data) throws IOException {
        return digest(getSha256Digest(), data);
    }


    public static byte[] sha256(final String data) {
        return sha256(getBytesUtf8(data));
    }


    public static String sha256Hex(final byte[] data) {
        return encodeHexLowerString(sha256(data));
    }


    public static String sha256Hex(final InputStream data) throws IOException {
        return encodeHexLowerString(sha256(data));
    }


    public static String sha256Hex(final String data) {
        return encodeHexLowerString(sha256(data));
    }

    public static byte[] sha384(final byte[] data) {
        return getSha384Digest().digest(data);
    }


    public static byte[] sha384(final InputStream data) throws IOException {
        return digest(getSha384Digest(), data);
    }


    public static byte[] sha384(final String data) {
        return sha384(getBytesUtf8(data));
    }


    public static String sha384Hex(final byte[] data) {
        return encodeHexLowerString(sha384(data));
    }


    public static String sha384Hex(final InputStream data) throws IOException {
        return encodeHexLowerString(sha384(data));
    }


    public static String sha384Hex(final String data) {
        return encodeHexLowerString(sha384(data));
    }


    public static byte[] sha512(final byte[] data) {
        return getSha512Digest().digest(data);
    }


    public static byte[] sha512(final InputStream data) throws IOException {
        return digest(getSha512Digest(), data);
    }


    public static byte[] sha512(final String data) {
        return sha512(getBytesUtf8(data));
    }


    public static String sha512Hex(final byte[] data) {
        return encodeHexLowerString(sha512(data));
    }


    public static String sha512Hex(final InputStream data) throws IOException {
        return encodeHexLowerString(sha512(data));
    }

    public static String sha512Hex(final String data) {
        return encodeHexLowerString(sha512(data));
    }

    public static MessageDigest updateDigest(final MessageDigest messageDigest, final byte[] valueToDigest) {
        messageDigest.update(valueToDigest);
        return messageDigest;
    }

    public static MessageDigest updateDigest(final MessageDigest digest, final InputStream data) throws IOException {
        final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);

        while (read > -1) {
            digest.update(buffer, 0, read);
            read = data.read(buffer, 0, STREAM_BUFFER_LENGTH);
        }

        return digest;
    }

    private static byte[] getBytesUtf8(final String valueToDigest) {
        if (valueToDigest == null) {
            throw fail("valueToDigest is must not null");
        }
        try {
            return valueToDigest.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw fail("UnsupportedEncoding");
        }
    }

    public static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static final char[] DIGITS_UPPER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String encodeHexLowerString(byte[] byteArray) {
        return encodeHexString(byteArray, DIGITS_LOWER);
    }

    public static String encodeHexUpperString(byte[] byteArray) {
        return encodeHexString(byteArray, DIGITS_UPPER);
    }

    public static byte[] decodeHex(final char[] data) {
        final int len = data.length;

        if ((len & 0x01) != 0) {
            throw fail("Odd number of characters.");
        }
        final byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

    private static int toDigit(final char ch, final int index) {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw fail("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    public static String encodeHexString(byte[] byteArray, char[] toDigits) {
        // two characters form the hex value.
        char[] resultCharArray = new char[byteArray.length << 1];
        // 遍历字节数组，通过位运算（位运算效率高）
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = toDigits[(0xF0 & b) >>> 4];
            resultCharArray[index++] = toDigits[0x0F & b];
        }
        return new String(resultCharArray);
    }

}
