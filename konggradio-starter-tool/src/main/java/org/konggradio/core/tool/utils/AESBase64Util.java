/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: AESBase64Util.java
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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.konggradio.core.tool.exceptions.KongGradioRuntimeException;
import org.springframework.util.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author xiaoxu
 * @date 2022-11-06 21:24
 * crawlerJ:com.xiaoxu.crawler.utils.AESUtils
 */
public class AESBase64Util {

	private static final String AES_ALGORITHM = "AES";

	private static final String UTF8 = StandardCharsets.UTF_8.name();

	/* AES加密 String */
	public static String encrypt(String text, String key) {
		if (!StringUtils.hasLength(text)) {
			throw new KongGradioRuntimeException("encode text should not be null or empty.");
		}
		byte[] encodeBytes = encryptByteAES(text.getBytes(StandardCharsets.UTF_8), key);
		return Base64.encodeBase64String(encodeBytes);
	}

	/* AES解密 String*/
	public static String decrypt(String text, String key) {
		if (!StringUtils.hasLength(text)) {
			throw new KongGradioRuntimeException("decode text should not be null or empty.");
		}
		byte[] decodeBytes = decryptByteAES(Base64.decodeBase64(text.getBytes(StandardCharsets.UTF_8)), key);
		return new String(decodeBytes, StandardCharsets.UTF_8);
	}

	/* AES加密 originalBytes */
	public static byte[] encryptByteAES(byte[] originalBytes, String key) {
		if (ArrayUtils.isEmpty(originalBytes)) {
			throw new KongGradioRuntimeException("encode originalBytes should not be empty.");
		}
		if (!StringUtils.hasLength(key)) {
			throw new KongGradioRuntimeException("key :" + key + ", encode key should not be null or empty.");
		}
		Cipher cipher = getAESCipher(key, Cipher.ENCRYPT_MODE);
		byte[] encodeBytes = null;
		try {
			encodeBytes = cipher.doFinal(originalBytes);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new KongGradioRuntimeException(e.getClass().getName() + ": encode byte fail. " + e.getMessage());
		}
		return encodeBytes;
	}

	/* AES解密 encryptedBytes */
	public static byte[] decryptByteAES(byte[] encryptedBytes, String key) {
		if (ArrayUtils.isEmpty(encryptedBytes)) {
			throw new KongGradioRuntimeException("decode encryptedBytes should not be empty.");
		}
		if (!StringUtils.hasLength(key)) {
			throw new KongGradioRuntimeException("key :" + key + ", decode key should not be null or empty.");
		}
		Cipher cipher = getAESCipher(key, Cipher.DECRYPT_MODE);
		byte[] decodeBytes = null;
		try {
			decodeBytes = cipher.doFinal(encryptedBytes);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new KongGradioRuntimeException(e.getClass().getName() + ": decode byte fail. " + e.getMessage());
		}
		return decodeBytes;
	}

	public static Cipher getAESCipher(String key, int mode) {
		if (!StringUtils.hasLength(key)) {
			throw new KongGradioRuntimeException("key :" + key + ", should not be null or empty.");
		}
		Cipher cipher = null;
		SecretKey secretKey;
		try {
			cipher = Cipher.getInstance(AES_ALGORITHM);
			byte[] keyBytes = key.getBytes(UTF8);
			secretKey = new SecretKeySpec(keyBytes, AES_ALGORITHM);
			cipher.init(mode, secretKey);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new KongGradioRuntimeException(e.getClass().getName() + ": get cipher instance wrong. " + e.getMessage());
		} catch (UnsupportedEncodingException u) {
			throw new KongGradioRuntimeException(u.getClass().getName() + ": key transfer bytes fail. " + u.getMessage());
		} catch (InvalidKeyException i) {
			throw new KongGradioRuntimeException(i.getClass().getName() + ": key is invalid. " + i.getMessage());
		}
		return cipher;
	}


}
