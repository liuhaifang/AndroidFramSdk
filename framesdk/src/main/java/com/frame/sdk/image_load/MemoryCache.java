package com.frame.sdk.image_load;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 普通对象内存缓存管理
 */
public class MemoryCache {
	private static Map<String, SoftReference<Object>> objCache = new ConcurrentHashMap<String, SoftReference<Object>>();

	public static void save(String key, Object value) {
		if (objCache.size() > 95)
			objCache.clear();
		objCache.put(key, new SoftReference<Object>(value));
	}

	public static Object get(String key) {
		Object obj = null;
		if (objCache.containsKey(key) && objCache.get(key) != null)
			obj = objCache.get(key).get();
		if (obj == null)
			objCache.remove(key);
		return obj;
	}

	public static void remove(String key) {
		objCache.remove(key);
	}

	public static void clear(String key) {
		objCache.clear();
	}

}
