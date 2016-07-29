package com.frame.sdk.util;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.frame.sdk.app.FrameConstant;

/**
 * 设备信息类
 */
public class DeviceUtil {

    /**
     * 获取设备号,获取不到设备号     */
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (deviceId == null || TextUtils.isEmpty(deviceId.trim())) {
            deviceId = Settings.System.getString(context.getContentResolver(), FrameConstant.DEVICE_ID);
        }
        if (deviceId == null || TextUtils.isEmpty(deviceId.trim())) {
            deviceId = "android_" + System.currentTimeMillis();
            Settings.System.putString(context.getContentResolver(), FrameConstant.DEVICE_ID, deviceId);
        }
        return deviceId;
    }

    /**
     * 获取设备唯一标识
     *
     * @param count 标识的位数
     */
    public static String getDeviceToken(Context context, int count) {
        String uniqueId =((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (uniqueId == null)
            uniqueId = "";
        StringBuilder sb = new StringBuilder();
        for (int i = uniqueId.length(); i < count; i++) {
            sb.append(i % 10);
        }
        return uniqueId + sb.toString();
    }

    /**
     * 获取当前手机型号
     */
    public static String getPhoneType() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取当前SDK版本号
     */
    public static int getSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 获取当前系统版本
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }
}
