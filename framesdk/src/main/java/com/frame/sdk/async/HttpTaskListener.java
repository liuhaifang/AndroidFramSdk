package com.frame.sdk.async;

import com.frame.sdk.util.LogUtils;

public abstract class HttpTaskListener implements TaskListener {
    /**
     * http请求任务执行成功
     *
     * @param resMsg 执行成功后返回的msg
     */
    public void onSuccess(Object resMsg) {
    }

    /**
     * http请求错误
     *
     * @param errCode 错误码编号
     * @param errMsg  错误信息
     */
    public void onError(int errCode, String errMsg) {
    }

    @Override
    public void onComplete(Object result) {
        LogUtils.i("http result="+result);
    }


}
