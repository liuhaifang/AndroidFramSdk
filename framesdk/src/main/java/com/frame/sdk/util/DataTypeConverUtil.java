package com.frame.sdk.util;

import org.apache.http.util.ByteArrayBuffer;

/**
 * 数据类型及进制之间的转换工具类
 */
public class DataTypeConverUtil {
	/**
	 * int类型数据（4个字节）转换成长度为4的字节数组
	 * 
	 * @return
	 */
	public static byte[] intToBytes(int value) {
		byte[] targets = new byte[4];
		targets[0] = (byte) (value & 0xff);// 最低位
		targets[1] = (byte) ((value >> 8) & 0xff);// 次低位
		targets[2] = (byte) ((value >> 16) & 0xff);// 次高位
		targets[3] = (byte) (value >> 24);// 最高位,无符号右移。
		return targets;
	}

	/**
	 * 字节数组转换成int数值
	 * 
	 * @param bytes
	 * @param offset
	 *            数组相对首位置偏移
	 * @param count
	 *            要转成int的字节个数
	 * @return
	 */
	public static int bytesToInt(byte[] bytes, int offset, int count) {
		if (count > 4) {
			LogUtils.e("byte count > 4 ");
			return 0;
		}
		int value = 0;
		for (int i = 0; i < count; i++) {
			int shift = i * 8;
			value += (bytes[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

	public static int bytesToInt(byte[] bytes) {
		return bytesToInt(bytes, 0, 4);
	}

	/**
	 * 将字节数组转成16进制的String字符串。一个字节8位对应两个16进制的字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Character.forDigit((bytes[i] & 240) >> 4, 16));
			sb.append(Character.forDigit(bytes[i] & 15, 16));
		}
		return sb.toString();
	}

	/**
	 * 将有十六进制字符表示的字符串转成byte数组
	 * 
	 * @param str
	 * @return
	 */
	public static byte[] strToBytes(String str) {
		ByteArrayBuffer buff = new ByteArrayBuffer(str.length());
		for (int i = 0; i < str.length(); i++)
			buff.append(Byte.parseByte(str.substring(i, i + 1), 16));
		return buff.toByteArray();
	}

	/**
	 * 返回bytes数组各个值组成的字符串
	 * 
	 * @param bytes
	 * @return
	 */
	public static String printBytes(byte[] bytes) {
		String str = "[";
		for (int i = 0; i < bytes.length; i++) {
			int v = DataTypeConverUtil.bytesToInt(new byte[] { bytes[i], 0 }, 0, 2);
			str += (v + ",");
		}
		return str + "]";
	}
}
