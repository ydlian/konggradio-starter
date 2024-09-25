/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: RsaExtUtil.java
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
import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

/**
 * rsa utils 支持openssl
 *
 * @author
 */
public class RsaExtUtil {

	public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTXbg9E0hwiujbf0MVchGuRxw3k5ilE506CpjHwhMI86k9G3THVxov9XMXyrukOC1TR0ujaHPMnM7VMYZaGK4m6PuqqpbrzqV/iXJDJtHXE4jgDEavVPGXM1ZpO+t1BvA7XHMScFe70CsmkxTpdcTP0sKooLSCPqlFPz4frq/DyQIDAQAB";
	public static final String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJNduD0TSHCK6Nt/QxVyEa5HHDeTmKUTnToKmMfCEwjzqT0bdMdXGi/1cxfKu6Q4LVNHS6Noc8ycztUxhloYribo+6qqluvOpX+JckMm0dcTiOAMRq9U8ZczVmk763UG8DtccxJwV7vQKyaTFOl1xM/SwqigtII+qUU/Ph+ur8PJAgMBAAECgYBVJcKtb3XcqTaQlQDC5Gz44NeZ+SsqvqGLBtJuIWH0Oy2fRDz+bQKRkWXV6mrvIRJ3WuuGWHUIVdZgcsQpTLdalsCLk3JS4GicRz3S8r8cbOBDTKOz0WIiof1PW1P0TF+H4JS4JGo5MVGMNPzvgOZX1aSxt556lXkn7UQH9erpXQJBAPjo7e4Q+d5i8YZsfcN/+82lMn29NKs46cFKotDYdsV4rheX4ZNAJ5Ju5/PYcNidpyvsGf0GsFXPxvBbfTvJsicCQQCXkFMtjz0TmcD68WuvjQDGCyh6F99fpZlpzG7+bkhlXc5fMqdEJZG3QBkSD/oZUv0rEesx//H33BK9KbTKWMCPAkAEsMcDDHjY8v5gLR01mOzS1EEeU3lxnJHzHYfx7ZJXaE3HjgonLzdPsB1Y4ARIYLgswLdAqGacR10VXHQAs21TAkAgktcdkoxY2xGbnSk8qHxDFADWBK1wPAH1uAcezYrnpjqFQTirr7tae/8nX6GrsadRi19V9qEFWRn5563AU0THAkBt6WAEq3JKIuj9Tn/Mh2CQInfwPBx6zDcaomou6KgkRBKPWuC86LWqjUrprOotoabMra93am4SUmNet7Og3oG0";


	// RSA最大加密明文大小
	private static final int MAX_ENCRYPT_BLOCK = 117;
	private static final int KEYSIZE = 1024;
	private static final int MAX_DECRYPT_BLOCK = KEYSIZE / 8;

	public static void main(String[] args) throws Exception {
		//		//生成公钥和私钥
		//		genKeyPair();
		String message = "数据库访问出现异常";
		//加密
		String encrypted= encrypt(message,publicKey);
		System.out.println("encrypt:"+encrypted);
		//解密
		String decrypted= decrypt(encrypted,privateKey);
		System.out.println("decrypt:"+decrypted);
		if (decrypted.equals(message)){
			System.out.println("success....");
		}

	}

	/**
	 * 随机生成密钥对
	 * @throws NoSuchAlgorithmException
	 */
	public static void genKeyPair() throws NoSuchAlgorithmException {
		// KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		// 初始化密钥对生成器，密钥大小为96-1024位
		keyPairGen.initialize(1024,new SecureRandom());
		// 生成一个密钥对，保存在keyPair中
		KeyPair keyPair = keyPairGen.generateKeyPair();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
		String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
		// 得到私钥字符串
		String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
		System.out.println("publicKeyString:"+publicKeyString);
		System.out.println("privateKeyString:"+privateKeyString);
	}
	/**
	 * RSA公钥加密
	 *
	 * @param str
	 *            加密字符串
	 * @param publicKey
	 *            公钥
	 * @return 密文
	 * @throws Exception
	 *             加密过程中的异常信息
	 */
	public static String encrypt( String str, String publicKey ) throws Exception{
//		//base64编码的公钥
//		byte[] decoded = Base64.decodeBase64(publicKey);
//		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
//		//RSA加密
//		Cipher cipher = Cipher.getInstance("RSA");
//		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
//		String outStr = Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
//		return outStr;

		//分段加密
		byte[] decoded = Base64.decodeBase64(publicKey);
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		byte[] bytes = str.getBytes();
		int inputLen = bytes.length;
		int offLen = 0;//偏移量
		int i = 0;
		ByteArrayOutputStream bops = new ByteArrayOutputStream();
		while(inputLen - offLen > 0){
			byte [] cache;
			if(inputLen - offLen > MAX_ENCRYPT_BLOCK){
				cache = cipher.doFinal(bytes, offLen,MAX_ENCRYPT_BLOCK);
			}else{
				cache = cipher.doFinal(bytes, offLen,inputLen - offLen);
			}
			bops.write(cache);
			i++;
			offLen = MAX_ENCRYPT_BLOCK * i;
		}
		bops.close();
		byte[] encryptedData = bops.toByteArray();
		String encodeToString = Base64.encodeBase64String(encryptedData);
		return encodeToString;

	}

	/**
	 * RSA私钥解密
	 *
	 * @param str
	 *            加密字符串
	 * @param privateKey
	 *            私钥
	 * @return 铭文
	 * @throws Exception
	 *             解密过程中的异常信息
	 */
	public static String decrypt(String str, String privateKey) throws Exception{
//		//64位解码加密后的字符串
//		byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
//		//base64编码的私钥
//		byte[] decoded = Base64.decodeBase64(privateKey);
//        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
//		//RSA解密
//		Cipher cipher = Cipher.getInstance("RSA");
//		cipher.init(Cipher.DECRYPT_MODE, priKey);
//		String outStr = new String(cipher.doFinal(inputByte));
//		return outStr;

		//分段解密
		byte[] decoded = Base64.decodeBase64(privateKey);
		RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);

		byte[] bytes = Base64.decodeBase64(str);
		int inputLen = bytes.length;
		int offLen = 0;
		int i = 0;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		while(inputLen - offLen > 0){
			byte[] cache;
			if(inputLen - offLen > 128){
				cache = cipher.doFinal(bytes,offLen,128);
			}else{
				cache = cipher.doFinal(bytes,offLen,inputLen - offLen);
			}
			byteArrayOutputStream.write(cache);
			i++;
			offLen = 128 * i;

		}
		byteArrayOutputStream.close();
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		return new String(byteArray);
	}

}

