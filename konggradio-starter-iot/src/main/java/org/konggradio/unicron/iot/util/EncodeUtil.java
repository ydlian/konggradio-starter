package org.konggradio.unicron.iot.util;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EncodeUtil {

	public static final String CHARSET = "ISO-8859-1";// 默认字符集

	public static String replaceAllCRLF(String myString) {
		String newString = null;
		Pattern CRLF = Pattern.compile("(\r\n|\r|\n|\n\r)");
		Matcher m = CRLF.matcher(myString);
		if (m.find()) {
			newString = m.replaceAll("");
		} else {
			newString = myString;
		}
		return newString;
	}

	public static String formatGunNo(String index) {
		String str = "";
		if (index == null || index.length() <= 0) {
			return str;
		}
		index = index.trim();
		if (index.length() == 1) {
			str = "0" + index;
		}
		return str;
	}

	public static String byteToString(byte b) {
		char ch = (char) b;
		return ch + "";
	}

	public static String byte2UnsignValue(byte b){
		byte a = (byte)b;
		int i = a;
		i = a & 0xff;
		return ""+i;
	}
	public static String byteToValue(byte b) {
		return b + "";
	}

	public static String bytesToValue(byte[] bytes) {
		String ret = "";
		for (byte b : bytes) {
			ret += b;
		}
		return ret;
	}

	// 字符串转byte,用于编码
	public static byte ValueToByte(String s) {
		return (byte) Integer.parseInt(s);
	}

	// byte数组转成long
	public static long byteToLong(byte[] b) {
		long s = 0;
		long s0 = b[0] & 0xff;// 最低位
		long s1 = b[1] & 0xff;
		long s2 = b[2] & 0xff;
		long s3 = b[3] & 0xff;
		long s4 = b[4] & 0xff;// 最低位
		long s5 = b[5] & 0xff;
		long s6 = b[6] & 0xff;
		long s7 = b[7] & 0xff;

		// s0不变
		s1 <<= 8;
		s2 <<= 16;
		s3 <<= 24;
		s4 <<= 8 * 4;
		s5 <<= 8 * 5;
		s6 <<= 8 * 6;
		s7 <<= 8 * 7;
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
		return s;
	}

	/**
	 * 注释：字节数组到int的转换！
	 *
	 * @param b
	 * @return
	 */
	public static int byteToInt(byte[] b) {
		int s = 0;
		int s0 = b[0] & 0xff;// 最低位
		int s1 = b[1] & 0xff;
		int s2 = b[2] & 0xff;
		int s3 = b[3] & 0xff;
		s3 <<= 24;
		s2 <<= 16;
		s1 <<= 8;
		s = s0 | s1 | s2 | s3;
		return s;
	}

	/**
	 * 注释：字节数组到int的转换！
	 *
	 * @param b
	 * @return
	 */
	public static long byteToUnsignedInt(byte[] b) {
		long s = 0;
		int s0 = b[0] & 0xff;// 最低位
		int s1 = b[1] & 0xff;
		int s2 = b[2] & 0xff;
		int s3 = b[3] & 0xff;
		s3 <<= 24;
		s2 <<= 16;
		s1 <<= 8;
		s = s0 | s1 | s2 | s3;
		// 只要超过32位，就需要在字面常量后加L强转long，否则编译时出错
		return s & (0x0ffffffffL);
	}

	/**
	 * 注释：字节数组到short的转换！
	 *
	 * @param b
	 * @return
	 */
	public static short byteToShort(byte[] b) {
		short s = 0;
		short s0 = (short) (b[0] & 0xff);// 最低位
		short s1 = (short) (b[1] & 0xff);
		s1 <<= 8;
		s = (short) (s0 | s1);
		return s;
	}

	/**
	 * 注释：字节数组到char序列
	 *
	 * @param b
	 * @return
	 */
	public static String byteToCharsequence(byte[] b, boolean doCut) {
		// Character.
		String s = "";
		try {
			s = new String(b, EncodeUtil.CHARSET);
			// Character.
			if (doCut) {
				s = cutCharsequence(s);
			}
		} catch (UnsupportedEncodingException e) {
			log.error(" parse charsequenceToByte error{}", e);
		}

		return s;
	}

	// 16进制
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	// 16进制
	public static String bytesToReverseHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = src.length-1; i >=0; i--) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		String s=stringBuilder.toString();
		if(s!=null){
			s=s.toUpperCase();
		}
		return s;
	}


	/**
	 * Convert hex string to byte[]
	 *
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 *
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String byte4ToIp(byte[] b) {
		// Character.
		if (b == null || b.length != 4) {
			return null;
		}
		String s = "";
		String arr[] = new String[4];
		for (int i = 0; i < 4; i++) {
			arr[i] = EncodeUtil.getUnsignedByte(b[i]) + "";
		}
		s = arr[0] + "." + arr[1] + "." + arr[2] + "." + arr[3];

		return s;
	}

	public static String byte4ToPort(byte[] b) {
		if (b == null || b.length != 4) {
			return null;
		}

		String s = EncodeUtil.byteToInt(b) + "";

		return s;
	}

	public static String byteToStr(byte[] b) {
		String s = "";
		for (int i = 0; i < b.length; i++) {
			s += b[i];
		}
		return s;
	}

	public static String byteDateToDateStr(byte[] byteDate) {
		if (byteDate == null || byteDate.length != 8)
			return null;
		String retData = "";
		for (int i = 0; i < 7; i++) {
			String s = Integer.toHexString(byteDate[i]);
			if (s.length() == 1) {
				s = "0" + s;
			}
			retData += s;
		}
		return retData;
	}

	// 字符串时间转byte数组
	public static byte[] dateStrToByteDate(String byteDate) {
		byte[] retData = new byte[8];
		if (StringUtils.isBlank(byteDate))
			return null;
		String year = byteDate.substring(0, 3);
		Short shortYear = Short.parseShort(year);
		byte[] yearByte = shortToByte(shortYear);
		System.arraycopy(yearByte, 0, retData, 0, 2);
		for (int i = 4; i < byteDate.length(); i += 2) {
			String data = byteDate.substring(i, i + 2);
			byte b;
			if (data.startsWith("0")) {
				b = ValueToByte(data.substring(1));
			} else {
				b = ValueToByte(data);
			}
			System.arraycopy(new byte[] { b }, 0, yearByte, 0, 1);
		}
		return retData;
	}

	/**
	 * 注释：字节数组到char序列，cut掉多余的\u0000
	 *
	 *
	 * @return
	 */
	public static String cutCharsequence(String str) {
		byte[] byt = null;
		try {
			byt = str.getBytes(CHARSET);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int destPos = -1;
		for (int i = 0; i < byt.length; i++) {

			if (byt[i] == 0) {
				destPos = i;
				break;
			}
		}
		if (destPos < 0) {
			return str;
		}
		byte[] dest = new byte[destPos];
		System.arraycopy(byt, 0, dest, 0, destPos);
		String s = "";
		try {
			s = new String(dest, CHARSET);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return s;
	}

	public static byte[] cutCharsequence(byte[] bytes) {
		int destPos = 0;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == 0) {
				destPos = i;
				break;
			}
		}
		if (destPos == 0)
			return bytes;
		byte[] dest = new byte[destPos];
		System.arraycopy(bytes, 0, dest, 0, destPos);
		return dest;
	}

	/**
	 * 注释：byteToCharsequence反转换
	 *
	 *
	 * @return
	 */
	public static byte[] charsequenceToByte(String str) {
		byte[] bytes = null;
		try {
			bytes = str.getBytes(EncodeUtil.CHARSET);
		} catch (UnsupportedEncodingException e) {
			log.error(" parse charsequenceToByte error{}", e);
		}

		return bytes;
	}

	// long类型转成byte数组
	public static byte[] longToByte(long number) {
		long temp = number;
		byte[] b = new byte[8];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Long(temp & 0xff).byteValue();// 将最低位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}

	/**
	 * 注释：short到字节数组的转换！
	 *
	 *
	 * @return
	 */
	public static byte[] shortToByte(short number) {
		int temp = number;
		byte[] b = new byte[2];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}

	public static byte[] stringToByte(String str) {
		try {
			return str.getBytes(CHARSET);
		} catch (UnsupportedEncodingException e) {
			log.error(" parse byteToString error{}", e);
		}
		return null;
	}

	/**
	 * 注释：int到字节数组的转换！
	 *
	 * @param number
	 * @return
	 */
	public static byte[] intToByte(int number) {
		int temp = number;
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++) {
			b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}

	public static byte getChecksum(byte[] data) {
		if (data.length <= 12) {
			return 0;
		}
		int sum = 0;
		for (int i = 12; i < data.length - 1; i++) {
			sum += data[i];
		}
		sum = sum % Byte.MAX_VALUE;
		return (byte) sum;
	}

	public static String printHex(byte[] byt) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < byt.length; i++) {
			String temp = Integer.toHexString((byte) byt[i]);
			if (temp.length() > 2) {
				temp = temp.substring(temp.length() - 2, temp.length());
			} else if (temp.length() == 1) {
				temp = "0" + temp;
			} else {

			}
			sb.append(temp).append(" ");
		}
		log.info("printHex:{}", sb.toString());
		return sb.toString();
	}

	public static byte[] hexStr2Bytes(String src) {
		/* 对输入值进行规范化整理 */
		src = src.trim().replace(" ", "").toUpperCase(Locale.US);
		// 处理值初始化
		int m = 0, n = 0;
		int iLen = src.length() / 2; // 计算长度
		byte[] ret = new byte[iLen]; // 分配存储空间

		for (int i = 0; i < iLen; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = (byte) (Integer.decode("0x" + src.substring(i * 2, m) + src.substring(m, n)) & 0xFF);
		}
		return ret;
	}

	public static String print(byte[] byt) {
		StringBuffer sb = new StringBuffer();
		if (byt == null || byt.length < 1) {
			return "";
		}

		for (int i = 0; i < byt.length; i++) {
			sb.append(byt[i] + " ");
		}
		log.info("print bytes:{}", sb.toString());
		return sb.toString();
	}

	public static byte[] ip2Byte(String ip) {
		byte[] b = new byte[4];
		if (StringUtils.isBlank(ip) || ip.indexOf(".") < 0) {
			return b;
		}
		String[] arr = ip.split("\\.");
		for (int i = 0; i < arr.length; i++) {
			b[i] = (byte) Short.parseShort(arr[i]);
		}
		return b;
	}

	public static byte[] port2Byte(String port) {
		// TODO Auto-generated method stub
		byte[] b = new byte[4];
		if (StringUtils.isBlank(port) || !NumberUtils.isNumber(port)) {
			return b;
		}
		return intToByte(Integer.parseInt(port));
	}

	public static byte[] dataBody4BitString2Bytes(String data, int para_cnt) {
		byte[] byt = new byte[para_cnt * 4];
		String arr[] = data.split(",");
		if (arr.length != para_cnt) {
			return null;
		}
		for (int i = 0; i < arr.length; i++) {
			byte[] byte4;
			if (arr[i].indexOf(".") != -1) {
				byte4 = ip2Byte(arr[i]);
			} else {
				int val = Integer.parseInt(arr[i]);
				byte4 = intToByte(val);
			}
			System.arraycopy(byte4, 0, byt, 4 * i, 4);
		}

		return byt;
	}

	public static void main(String[] args) throws Exception {
		//String str=Md5Util.pwdDigest("20180808A");
		//System.out.println(str);
		// String aaa="sdfsfdsfsdf\r\n,dsfsdfsdf\r\n";
		// //aaa.replace("s", "x");
		// String newStr=replaceAllCRLF(aaa);
		// System.out.println(newStr);
		//
		//
		byte[] b = {0x0d,0x10};
		String ss = EncodeUtil.bytesToReverseHexString(b);
		System.out.println(byte2UnsignValue((byte)(234)));
		// String old =
		// "2017082111030001\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000";
		//
		// String str = cutCharsequence(old);
		// str = cutCharsequence(str);
		// byte byt = (byte) 255;
		// byte byt1 = (byte) 0xdf;
		// byte byt2 = (byte) 0xaa;
		// System.out.println(byteToString((byte) 0x31));
		//
		// byte[] byteArr = new byte[] { 0x31, 0x32, 0x39, byt1, byt2 };
		// String s1 = byteToCharsequence(byteArr, true);
		// System.out.println(s1);
		// byteArr = charsequenceToByte(s1);
		// //7fffffff->2147483647 ffffffff->4294967295
		// //8fffffff->-2130706433
		// //0~4294967295
		// //Integer.MAX_VALUE->2147483647
		//
		// byte bys[]=new byte[]{(byte)0xff ,(byte)0xff,(byte)0xff,(byte)0xff};
		// System.out.println("有符号:"+byteToInt(bys));
		// System.out.println("无符号:"+byteToUnsignedInt(bys));
		// System.out.println(byt);
		// print(intToByte(17));
		// print(intToByte(Integer.MAX_VALUE));
		// print(intToByte(Integer.MIN_VALUE));

		byte array[] = new byte[] { 0x7d, (byte) 0xd0, 0x15, 0x00, 0x01, 0x00, 0x0b, 0x00, 0x05, 0x00, 0x00, 0x00, 0x65,
				0x00, 0x00, 0x00, 00, 0x00, 0x12, 0x00, 0x6b };

		String s=byteDateToDateStr(new byte[]{0x20,0x18,0x09,0x01,0x00,0x00,0x00,0x00});
		System.out.println(s);
		byte checkSum = getChecksum(array);
		System.out.println("checkSum:" + checkSum);
	}

	/*
	 * Java中的基本类型都是有符号类型，也就是数值类型都有正负号。所占位数，不随硬件变化。其基本数据类型大小如下： char 2个字节，16位； byte
	 * 1个字节， 8位； short 2个字节，16位； int 4个字节，32位； long 8个字节，64位； float 4个字节，32位； double
	 * 8个字节，64位； 以上基本类型都有一位符号位。 网络传输时，解码时需要注意
	 */
	public static long getUnsignedInt(int data) {
		// 将data字节型数据转换为0~4294967295 (0xFFFFFFFF 即 DWORD),并且是以“低地址低字节”的方式返回
		// 只要超过32位，就需要在字面常量后加L强转long，否则编译时出错
		return data & 0x0FFFFFFFFL;
	}

	/*
	 * Java中的基本类型都是有符号类型，也就是数值类型都有正负号。所占位数，不随硬件变化。其基本数据类型大小如下： char 2个字节，16位； byte
	 * 1个字节， 8位； short 2个字节，16位； int 4个字节，32位； long 8个字节，64位； float 4个字节，32位； double
	 * 8个字节，64位； 以上基本类型都有一位符号位。 网络传输时，解码时需要注意
	 */
	public static int getUnsignedShort(short data) {
		// 将data字节型数据转换为0~65535 (0xFFFF 即 WORD),并且是以“低地址低字节”的方式返回
		return data & 0x0FFFF;
	}

	public static int getUnsignedByte(byte data) {
		// 将data字节型数据转换为0~255 (0xFF),并且是以“低地址低字节”的方式返回
		return data & 0x0FF;
	}

	/*
	 * 将字符串转为BCD的byte数组
	 */
	public static byte[] strToBCDByte(String asc) {
		int len = asc.length();
		int mod = len % 2;
		if (mod != 0) {
			asc = "0" + asc;
			len = asc.length();
		}
		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}
		byte bbt[] = new byte[len];
		abt = asc.getBytes();
		int j, k;
		for (int p = 0; p < asc.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}
			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}
			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

}
