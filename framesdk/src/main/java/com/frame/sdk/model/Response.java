package com.frame.sdk.model;

import com.frame.sdk.app.FrameConfig;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 服务器返回的请求先通过此类进行解析，根据code来做相应处理，如：{"code":1000,"msg":"msg","error":
 * "error"}
 */
public class Response extends Model {
    public String error;
    public int code;
    public Object msg;

    public static Response parse(String res) {
        Response response = new Response();
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jobj != null) {
            response.code = jobj.optInt(FrameConfig.CODE_JSON);
            response.msg = jobj.opt(FrameConfig.MSG_JSON);
            response.error = jobj.optString(FrameConfig.ERROR_JSON);
        }
        return response;
    }

    public boolean isSuccess() {
        return code == FrameConfig.CODE_RIGHT;
    }

}
