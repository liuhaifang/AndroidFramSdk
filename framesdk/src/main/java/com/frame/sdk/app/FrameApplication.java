package com.frame.sdk.app;

import android.app.Application;

import com.frame.sdk.async.AsyncPool;
import com.frame.sdk.async.ExecuteTask;
import com.frame.sdk.file.FileManager;
import com.frame.sdk.util.CrashHandler;
import com.frame.sdk.util.ScreenUtil;

public abstract class FrameApplication extends Application {
    public static CrashHandler exHandler = null;
    private static FrameApplication app;

    public static FrameApplication getInstance() {
        return app;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initFrameConfig();
        // 设置屏幕密度
        ScreenUtil.setDensity(getApplicationContext());
        //改用友盟的统计
        exHandler = CrashHandler.getInstance();
        exHandler.init(getApplicationContext(), FrameConfig.crashUploadListener);
        checkCache();
    }

    public abstract void initFrameConfig();

    public static void checkCache() {
        // 缓存过大清除
        if (FrameConfig.CACHE_MAX_SIZE < FileManager.getFilesSpace(FileManager
                .getAppDir())) {
            ExecuteTask task = new ExecuteTask() {
                @Override
                public Object onDo() {
                    FileManager.clearDirs();
                    return null;
                }
            };
            AsyncPool.getInstance().addTask(task);
        }
    }
}