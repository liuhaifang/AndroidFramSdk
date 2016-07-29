package com.frame.sdk.util;

import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.frame.sdk.app.FrameConfig;

/**
 * SharedPreferences操作工具类
 */
public class SharedPreferUtil {

	private static final String PREFERENCES_NAME = FrameConfig.PREFERENCES_NAME;

	public static void write(Context context, String key, int value) {
		write(context, key, String.valueOf(value));
	}

	public static void write(Context context, String key, long value) {
		write(context, key, String.valueOf(value));
	}

	public static void write(Context context, String key, float value) {
		write(context, key, String.valueOf(value));
	}

	public static void write(Context context, String key, double value) {
		write(context, key, String.valueOf(value));
	}

	public static void write(Context context, String key, boolean value) {
		write(context, key, String.valueOf(value));
	}

	public static void write(Context context, String key, String value) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String read(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		return pref.getString(key, "");
	}

	public static void delete(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.remove(key);
		editor.commit();
	}

	/**
	 * 读取一个对象
	 * 
	 * @param context
	 * @return
	 */
	public static Object read(Context context, Class<?> cls) {
		Bundle bundle = new Bundle();
		Object obj = null;
		try {
			obj = cls.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		Map<String, Object[]> map = ClassUtil.getFields(obj);
		for (Entry<String, Object[]> entry : map.entrySet()) {
			String name = entry.getKey();
			String type = (String) entry.getValue()[1];
			String key = obj.getClass().getName() + entry.getKey();

			String v = read(context, key);
			if (TextUtils.isEmpty(v))
				continue;
			if (type.equals("int") || type.equals("Integer"))
				bundle.putInt(name, Integer.parseInt(v));
			else if (type.equals("double") || type.equals("Double"))
				bundle.putDouble(name, Double.parseDouble(v));
			else if (type.equals("float") || type.equals("Float"))
				bundle.putFloat(name, Float.parseFloat(v));
			else if (type.equals("long") || type.equals("Long"))
				bundle.putLong(name, Long.parseLong(v));
			else if (type.equals("String"))
				bundle.putString(name, v);
			else if (type.equals("Boolean"))
				bundle.putBoolean(name, Boolean.parseBoolean(v));
		}
		ClassUtil.setFields(obj, bundle);
		return obj;
	}

	/**
	 * 写入一个对象
	 * 
	 * @param context
	 * @param obj
	 */
	public static void write(Context context, Object obj) {
		Map<String, Object[]> map = ClassUtil.getFields(obj);
		for (Entry<String, Object[]> entry : map.entrySet()) {
			Object value = entry.getValue()[0];
			String type = (String) entry.getValue()[1];
			String key = obj.getClass().getName() + entry.getKey();
			if (type.equals("int") || type.equals("Integer"))
				write(context, key, (Integer) value);
			else if (type.equals("double") || type.equals("Double"))
				write(context, key, (Double) value);
			else if (type.equals("float") || type.equals("Float"))
				write(context, key, (Float) value);
			else if (type.equals("long") || type.equals("Long"))
				write(context, key, (Long) value);
			else if (type.equals("String"))
				write(context, key, (String) value);
			else if (type.equals("Boolean")) {
				write(context, key, (Boolean) value);
			}
		}
	}

	/**
	 * 删除一个对象
	 * 
	 * @param context
	 * @param obj
	 */
	public static void delete(Context context, Object obj) {
		Map<String, Object[]> map = ClassUtil.getFields(obj);
		for (Entry<String, Object[]> entry : map.entrySet()) {
			String key = obj.getClass().getName() + entry.getKey();
			delete(context, key);
		}
	}

	public static void clear(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}

}
