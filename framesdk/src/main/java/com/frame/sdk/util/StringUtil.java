package com.frame.sdk.util;

import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理类
 */
public class StringUtil {
	/**
	 * 将字符串数组转换为splitStr分割的字符串
	 */
	public static String arrayToString(ArrayList<String> arr, String splitStr) {
		if (arr == null || arr.size() == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for (String string : arr) {
			sb.append(splitStr);
			sb.append(string);
		}
		return sb.substring(splitStr.length());
	}

	/**
	 * 获取一个url的参数
	 */
	public static Bundle getParams(String url) {
		int index = url.indexOf('?');
		String params = url.substring(index + 1);
		return decodeParams(params);
	}

	/**
	 * 获取一个url的base url（除参数之外）
	 */
	public static String getBaseUrl(String url) {
		int index = url.indexOf('?');
		return url.substring(0, index);
	}

	public static Bundle parseUrl(String url) {
		int index = url.indexOf('#');
		String params = url.substring(index + 1);
		return decodeParams(params);
	}

	public static Bundle decodeParams(String p) {
		Bundle params = new Bundle();
		if (p != null) {
			String[] array = p.split("&");
			for (String parameter : array) {
				String[] v = parameter.split("=");
				String value = v.length < 2 ? "" : v[1];
				params.putString(URLDecoder.decode(v[0]),
						URLDecoder.decode(value));
			}
		}
		return params;
	}

	public static String getMD5(Object o) {
		return SecurityUtil.getDigest(o.hashCode() + "", "MD5");
	}

	public static boolean isCorrectPhoneNumber(String phone) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(phone);
		return m.matches();

	}

	public static String UTF8Wrapper(String src) {
		String s = null;
		try {
			s = new String(src.getBytes(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			s = src;
		}
		return s;
	}

	public static String get2BitStr(int num){
		if(num<10){
			return "0"+num;
		}else{
			return ""+num;
		}
	}
}
