package com.frame.sdk.app;

/**
 * 常量类
 */
public class FrameConstant {
    public static final int NET_EMPTY = 0xddd;
    public static final String NET_EMPTY_STR = "服务器开小差，返回了空字符串";
    public static final int NET_NULL = 0xddd + 1;
    public static final String NET_NULL_STR = "服务器开小差，返回了空字符串（null）";
    public static final int NET_ERROR = 0xddd + 2;
    public static final String NET_ERROR_STR = "网络连接失败，请查看网络设置";
    public static final String DEVICE_ID = "DEVICE_ID";
}
