package com.frame.sdk.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class JSONUtil {
	public static <T> List<T> getJsonList(String src, Class<T> cls) {
		Gson gson = new Gson();
		List<T> tmpList = gson.fromJson(src, new TypeToken<List<T>>() {
		}.getType());

		if (tmpList == null)
			return null;

		List<T> list = new ArrayList<T>(tmpList.size());
		for (T map : tmpList) {
			String tmpJson = gson.toJson(map);
			list.add(gson.fromJson(tmpJson, cls));
		}
		return list;
	}

	public static <T> T getObjFromJson(String src, Class<T> cls) {
		return new Gson().fromJson(src, cls);
	}

	public static String toJSON(Object src) {
		return new Gson().toJson(src);
	}

	public static <T> String toJSONArry(List<T> list) {
		Gson gson = new Gson();
		StringBuffer json = new StringBuffer("[");
		for (T t : list) {
			json.append(gson.toJson(t));
			json.append(",");
		}
		if (json.charAt(json.length() - 1) == ',')
			json.deleteCharAt(json.length() - 1);
		json.append("]");
		return json.toString();
	}
}
