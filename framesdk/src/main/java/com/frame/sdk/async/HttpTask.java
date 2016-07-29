package com.frame.sdk.async;

import android.text.TextUtils;

import com.frame.sdk.app.FrameApplication;
import com.frame.sdk.app.FrameConstant;
import com.frame.sdk.model.Response;
import com.frame.sdk.util.NetworkManager;

/**
 * 执行http请求任务类
 */
public abstract class HttpTask extends ExecuteTask {
    private HttpTaskListener httpTaskListener;

    public HttpTask(HttpTaskListener httpTaskListener) {
        super();
        init();
        setHttpTaskListener(httpTaskListener);
    }

    private void init() {
        super.setTaskListener(new TaskListener() {
            @Override
            public void onComplete(Object result) {
//                LogUtils.i("http response=" + result);
                if(httpTaskListener!=null){
                    httpTaskListener.onComplete(result);
                }
                if (result == null) {
                    if (httpTaskListener != null) {
                        if (NetworkManager.getInstance(FrameApplication.getInstance()).isNetworkConnected()) {
                            httpTaskListener.onError(FrameConstant.NET_NULL, FrameConstant.NET_NULL_STR);
                        } else {
                            httpTaskListener.onError(FrameConstant.NET_ERROR, FrameConstant.NET_ERROR_STR);
                        }
                    }
                    return;
                }
                String resStr = (String) result;
                if (TextUtils.isEmpty(resStr)) {
                    if (httpTaskListener != null) {
                        if (NetworkManager.getInstance(FrameApplication.getInstance()).isNetworkConnected()) {
                            httpTaskListener.onError(FrameConstant.NET_EMPTY, FrameConstant.NET_EMPTY_STR);
                        } else {
                            httpTaskListener.onError(FrameConstant.NET_ERROR, FrameConstant.NET_ERROR_STR);
                        }
                    }
                    return;
                }
                Response response = Response.parse(resStr);
                if (response.isSuccess()) {
                    if (httpTaskListener != null)
                        httpTaskListener.onSuccess(response.msg);
                } else {
                    if (httpTaskListener != null) {
                        String errMsg = response.error;
                        httpTaskListener.onError(response.code, errMsg);
                    }
                }
            }
        });
    }

    @Override
    public void setTaskListener(TaskListener taskListener) {
    }

    public void setHttpTaskListener(HttpTaskListener httpTaskListener) {
        this.httpTaskListener = httpTaskListener;
    }
}
