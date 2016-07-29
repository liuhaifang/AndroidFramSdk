package com.frame.sdk.file;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.frame.sdk.app.FrameConfig;
import com.frame.sdk.util.LogUtils;

import android.text.TextUtils;

/**
 * 文件及文件目录目录管理器,通过此类的静态方法获取目录及文件路径
 */
public class FileManager {
	private static final String DIR_BASE = ExternalStorageInfo.getDirectory();
	private static String appDir = FrameConfig.APP_DIR;
	private static final String DIR_IMAGE = "image";
	private static final String DIR_LOG = "log";
	private static final String DIR_VOICE = "voice";
	private static final String DIR_CRASH = "crash";
	private static final String DIR_OTHER = "other";

	static {
		if (TextUtils.isEmpty(appDir)) {
			LogUtils.e("应用程序的缓存根目录为空");
		} else
			makeDirs();
	}

	public static void makeDirs() {
		if (!ExternalStorageInfo.isExistExternalStorage()) {
			LogUtils.e("没有外部存储器");
			return;
		}
		File dir = new File(getAppDir());
		if (!dir.exists())
			dir.mkdirs();
		dir = new File(getImageDir());
		if (!dir.exists())
			dir.mkdirs();
		dir = new File(getVoiceDir());
		if (!dir.exists())
			dir.mkdirs();
		dir = new File(getLogDir());
		if (!dir.exists())
			dir.mkdirs();
		dir = new File(getCrashDir());
		if (!dir.exists())
			dir.mkdirs();
		dir = new File(getOtherDir());
		if (!dir.exists())
			dir.mkdirs();
	}

	/**
	 * 获取外部存储器的路径
	 */
	public static String getBaseDir() {
		return DIR_BASE;
	}

	public static String getAppDir() {
		return DIR_BASE + "/" + appDir;
	}

	public static String getImageDir() {
		return getAppDir() + "/" + DIR_IMAGE;
	}

	public static String getImagePath(String imageName) {
		return getImageDir() + "/" + imageName;
	}

	public static String getVoiceDir() {
		return getAppDir() + "/" + DIR_VOICE;
	}

	public static String getVoicePath(String voiceName) {
		return getVoiceDir() + "/" + voiceName;
	}

	public static String getLogDir() {
		return getAppDir() + "/" + DIR_LOG;
	}

	public static String getLogPath(String logName) {
		return getLogDir() + "/" + logName;
	}

	public static String getCrashDir() {
		return getAppDir() + "/" + DIR_CRASH;
	}

	public static String getCrashPath(String crashName) {
		return getCrashDir() + "/" + crashName;
	}

	public static String getOtherDir() {
		return getAppDir() + "/" + DIR_OTHER;
	}

	public static String getOtherPath(String otherName) {
		return getOtherDir() + "/" + otherName;
	}

	public static String getFileName(String filePath) {
		int index = filePath.lastIndexOf(File.separatorChar);
		String result = filePath.substring(index);

		if (result.endsWith("/"))
			return result.substring(0, result.length() - 1);
		else
			return result;
	}

	/**
	 * 返回当前时间命名的文件名称
	 */
	public synchronized static String getFileNameByTime() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'file'_yyyy_MM_dd_HHmmss");
		return dateFormat.format(date);
	}

	/**
	 * 清空目录
	 */
	public static boolean clearDirs() {
		boolean res = deleteDir(getAppDir());
		makeDirs();
		return res;
	}

	/**
	 * 删除整个文件目录及子文件
	 */
	public static boolean deleteDir(String dirPath) {
		File dirFile = new File(dirPath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		File newFile = new File(dirFile.getAbsolutePath() + System.currentTimeMillis());
		dirFile.renameTo(newFile);// 重命名之后再删除，防止出现异常

		File[] files = newFile.listFiles();
		int i = 0;
		for (; i < files.length; i++) {
			if (files[i].isFile()) {
				if (false == files[i].delete())
					break;
			} else {
				if (!deleteDir(files[i].getAbsolutePath()))
					break;
			}
		}
		if (i < files.length)
			return false;
		return newFile.delete();
	}

	/**
	 * 返回目录下包含的文件和目录的总数目
	 * 
	 * @param dirPath
	 * @return
	 */
	public static int getFileNum(String dirPath) {
		File dirFile = new File(dirPath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return 0;
		}
		return dirFile.listFiles().length;
	}

	/**
	 * 返回目录下所有文件的大小总和（多少个字节）
	 * 
	 * @param dirPath
	 * @return 所有文件的大小总和(单位字节)
	 */
	public static long getFilesSpace(String dirPath) {
		File dirFile = new File(dirPath);
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return 0;
		}
		int totalSpace = 0;
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				totalSpace += files[i].length();
			} else {
				totalSpace += getFilesSpace(files[i].getAbsolutePath());
			}
		}
		return totalSpace;
	}
}
