package com.frame.sdk.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.os.Bundle;

/**
 * 运行时类信息工具类
 */
public class ClassUtil {

	/**
	 * 获取一个类的所以字段信息
	 * 
	 * @return 返回map，key=字段名字,Object[]=[字段值(Object)，字段类型(String)，字段修饰符(String)]
	 */
	public static Map<String, Object[]> getFields(Object obj) {
		Map<String, Object[]> map = new HashMap<String, Object[]>();
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				Object[] values = new Object[3];
				values[0] = field.get(obj);
				values[1] = field.getType().getSimpleName();
				values[2] = Modifier.toString(field.getModifiers());
				map.put(field.getName(), values);
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 设置一个类的各个字段值
	 * 
	 * @param map
	 *            通过getFields方法得到的字段信息
	 * @return
	 */
	public static void setFields(Object obj, Map<String, Object[]> map) {
		Bundle bundle = new Bundle();
		for (Entry<String, Object[]> entry : map.entrySet()) {
			Object value = entry.getValue()[0];
			String type = (String) entry.getValue()[1];
			if (type.equals("int") || type.equals("Integer"))
				bundle.putInt(entry.getKey(), (Integer) value);
			else if (type.equals("double") || type.equals("Double"))
				bundle.putDouble(entry.getKey(), (Double) value);
			else if (type.equals("float") || type.equals("Float"))
				bundle.putFloat(entry.getKey(), (Float) value);
			else if (type.equals("long") || type.equals("Long"))
				bundle.putLong(entry.getKey(), (Long) value);
			else if (type.equals("String"))
				bundle.putString(entry.getKey(), (String) value);
		}
		setFields(obj, bundle);
	}

	/**
	 * 设置一个类的各个字段值
	 * 
	 * @param bundle 需要设置的字段名称和字段值
	 * @return
	 */
	public static void setFields(Object obj, Bundle bundle) {
		try {
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);
				String name = field.getName();
				String type = field.getType().getSimpleName();

				if (type.equals("int") || type.equals("Integer"))
					field.setInt(obj, bundle.getInt(name));
				else if (type.equals("double") || type.equals("Double"))
					field.setDouble(obj, bundle.getDouble(name));
				else if (type.equals("float") || type.equals("Float"))
					field.setFloat(obj, bundle.getFloat(name));
				else if (type.equals("long") || type.equals("Long"))
					field.setLong(obj, bundle.getLong(name));
				else if (type.equals("String"))
					field.set(obj, bundle.getString(name));
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回一个类的字段名称和对应的字段值,如uid=1234,name=hello
	 * 
	 * @param obj
	 * @return
	 */
	public static String getFieldValue(Object obj) {
		StringBuffer sb = new StringBuffer();
		Map<String, Object[]> map = getFields(obj);
		sb.append("class name=" + obj.getClass().getName());
		sb.append(",");
		for (Entry<String, Object[]> entry : map.entrySet()) {
			sb.append(entry.getKey() + "=" + entry.getValue()[0]);
			sb.append(",");
		}
		return sb.substring(0, sb.length() - 1);
	}

}
