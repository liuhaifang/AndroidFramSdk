package com.frame.sdk.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;

import com.frame.sdk.app.FrameConfig;
import com.frame.sdk.file.FileManager;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements UncaughtExceptionHandler {
	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler defaultHandler;
	// CrashHandler实例
	private static CrashHandler crashHandler;
	// 程序的Context对象
	private Context context;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos;

	CrashUploadListener uploadCrashInfoListener;

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (crashHandler == null) {
			crashHandler = new CrashHandler();
		}
		return crashHandler;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context, CrashUploadListener uploadCrashInfoListener) {
		this.context = context;
		this.uploadCrashInfoListener = uploadCrashInfoListener;
		// 获取系统默认的UncaughtException处理器
		defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		LogUtils.e("thread.id==" + thread.getId() + ",ex==" + ex);
		if (ex == null) {
			defaultHandler.uncaughtException(thread, ex);
			return;
		}
		handleException(ex);
		ToastUtil.showToast(context, FrameConfig.EXIT_ABNOROMAL_MSG);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			LogUtils.e("an error occured before process exit", e);
		}
		// 退出程序
		LogUtils.e("exit pid==" + android.os.Process.myPid());
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}

	/**
	 * 自定义异常处理方法，会输出异常信息及把异常信息写入文件
	 * 
	 */
	public void handleException(Throwable ex) {
		handleException(ex, true);
	}

	/**
	 * 自定义异常处理方法，会输出异常信息
	 * 
	 * @param isWriteToFile
	 *            该异常信息是否写入文件
	 */
	public void handleException(Throwable ex, boolean isWriteToFile) {
		if (ex == null) {
			LogUtils.w("ex==null");
			return;
		}
		ex.printStackTrace();
		if (isWriteToFile) {
			// 收集设备参数信息
			collectDeviceInfo(context);
			// 保存日志文件
			String filePath=saveCrashInfo2File(ex);
			LogUtils.i("uploadCrashInfoListener==" + uploadCrashInfoListener+",filePath=="+filePath);
			if (uploadCrashInfoListener != null)
				uploadCrashInfoListener.upload(filePath);
		}
	}

	/**
	 * 收集设备参数信息
	 * 
	 * @param ctx
	 */
	private void collectDeviceInfo(Context ctx) {
		if (infos != null)
			return;
		infos = new HashMap<String, String>();
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			LogUtils.e("an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
			} catch (Exception e) {
				LogUtils.e("an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return 返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {
		StringBuffer sb = new StringBuffer();
		String fileName = FileManager.getFileNameByTime();
		String filePath = FileManager.getCrashPath(fileName);
		FileOutputStream fos;
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				fos = new FileOutputStream(filePath);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return filePath;
		} catch (Exception e) {
			LogUtils.e("an error occured while writing file...", e);
		}
		return null;
	}

	/**
	 * 程序异常退出后的上传监听器
	 */
	public interface CrashUploadListener {
		/**
		 * 异常信息文件上传监听器，在异常信息写入文件后会触发此函数进行上传
		 * 
		 * @param filePath
		 */
		public void upload(String filePath);
	}
}