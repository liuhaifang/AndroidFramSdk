package com.frame.sdk.http;

import android.text.TextUtils;

import com.frame.sdk.app.FrameConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求网络的url封装类，包含基本url，请求类型，参数
 */
public class UrlString {
    private Map<String, String> urlParams = new HashMap<String, String>();// url参数
    private String baseUrl = FrameConfig.URL_BASE;
    private String action = "";

    public UrlString() {
        setBaseUrl(baseUrl);
    }

    public UrlString(String action) {
        this.action = action;
        setBaseUrl(baseUrl);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public UrlString setBaseUrl(String baseUrl) {
        if (TextUtils.isEmpty(baseUrl))
            return this;
        int lastIndex = baseUrl.length() - 1;
        if ("/".equals(baseUrl.substring(lastIndex)))
            this.baseUrl = baseUrl.substring(0, lastIndex);
        else
            this.baseUrl = baseUrl;
        return this;
    }

    public UrlString putParame(String param, String value) {
        if (value == null)
            value = "";
        try {
            urlParams.put(param, URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

    public UrlString putParamNoEncode(String param, String value) {
        if (value == null)
            value = "";
        urlParams.put(param, value);
        return this;
    }

    public UrlString putParame(String param, int value) {
        urlParams.put(param, "" + value);
        return this;
    }

    public UrlString putParame(String param, double value) {
        urlParams.put(param, "" + value);
        return this;
    }

    public UrlString putParame(String param, float value) {
        urlParams.put(param, "" + value);
        return this;
    }

    public UrlString putParame(String param, long value) {
        urlParams.put(param, "" + value);
        return this;
    }

    public UrlString putParame(String param, short value) {
        urlParams.put(param, "" + value);
        return this;
    }

    public Map<String, String> getParame() {
        return urlParams;
    }

    /**
     * 获取带参数的完整url
     */
    public String getFullUrl() {
        if (TextUtils.isEmpty(action))
            return baseUrl + "?" + getParemeStr();
        else
            return baseUrl + "/" + action + "?" + getParemeStr();
    }

    /**
     * 获取带参数的完整url
     */
    public String getPostUrl() {
        if (TextUtils.isEmpty(action))
            return baseUrl;
        else
            return baseUrl + "/" + action;
    }

    public String getParemeStr() {
        if (urlParams.size() <= 0)
            return "";
        StringBuilder pareme = new StringBuilder("");
        for (Map.Entry<String, String> entry : urlParams.entrySet()) {
            pareme.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        int lastIndex = pareme.length() - 1;
        if ("&".equals(pareme.substring(lastIndex)))
            return pareme.substring(0, lastIndex);
        else
            return pareme.toString();
    }
}
