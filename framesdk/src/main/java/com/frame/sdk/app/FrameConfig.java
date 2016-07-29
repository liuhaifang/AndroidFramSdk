package com.frame.sdk.app;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.frame.sdk.util.CrashHandler;


/**
 * 框架的配置信息，使用此框架的应用需要重新给这个类的各个字段赋值，配置框架 如：FrameConfig.APP_DIR="xxx";配置应用缓存文件的根目录
 */
public class FrameConfig {
    // 网络请求基地址
    public static String URL_BASE = "";
    // 缓存文件的根目录名称
    public static String APP_DIR = "frame_file";
    // SharedPreferences名称
    public static String PREFERENCES_NAME = "frame_preferences";
    // 缓存文件最大值(字节数)，超过该值会清空缓存
    public static int CACHE_MAX_SIZE = 50 * 1024 * 1024;
    // 线程池一直保持的活跃的线程个数
    public static int CORE_POOL_SIZE = 5;
    // 线程池最大线程数
    public static int MAXIMUM_POOL_SIZE = 10;
    // 线程池中线程没有任务时活跃时间（ms）
    public static int KEEP_ALIVE_TIME = 1000;
    // 统一的网络请求返回的json字符串中对应Response.msg字段的key
    public static String MSG_JSON = "msg";
    // 统一的网络请求返回的json字符串中对应Response.error字段的key
    public static String ERROR_JSON = "error";
    // 统一的网络请求返回的json字符串中对应Response.code字段的key
    public static String CODE_JSON = "code";
    // 统一的网络请求返回正确时code值
    public static int CODE_RIGHT = 0;
    // 上下弹出的dialog的非dialog部分的昏暗程度dim
    public static float DIALOG_DIM = 0.7f;
    // Toast提示的root view
    public static View toastView;
    // Toast提示的显示内容的TextView
    public static TextView toastTipView;
    // Toast提示与顶部距离
    public static int toastTopMargin = -1;
    // Toast提示显示的时间
    public static int toastTime = Toast.LENGTH_SHORT;
    //是否输出日志
    public static boolean ALLOW_LOG = true;
    // 日志输出的tag前缀
    public static String LOG_TAG_PREFIX = "";
    // 未捕获的异常导致程序异常退出的提示msg
    public static String EXIT_ABNOROMAL_MSG = "抱歉,程序出现未知错误,即将退出";
    // 异常信息写入文件后上传该文件的监听器
    public static CrashHandler.CrashUploadListener crashUploadListener = null;

}
