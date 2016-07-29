package com.frame.sdk.util;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * 图片缓存工具类，封装了ImageLoader
 * 使用参考：
 * http://blog.csdn.net/vipzjyno1/article/details/23206387
 */
public class ImageLoadUtil {
    // 内存缓存的最大值(byte)
    public static final int MAX_MEMORY_CACHE_SIZE = 5 * 1024 * 1024;
    // 本地缓存的最大值(byte)
    public static final int MAX_DISC_CACHE_SIZE = 50 * 1024 * 1024;// 50 MiB
    public static FileNameGenerator fileNameGenerator = new Md5FileNameGenerator();

    public static void init(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.memoryCacheSize(MAX_MEMORY_CACHE_SIZE);
        config.diskCacheFileNameGenerator(fileNameGenerator);
        config.diskCacheSize(MAX_DISC_CACHE_SIZE);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
//        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
}
