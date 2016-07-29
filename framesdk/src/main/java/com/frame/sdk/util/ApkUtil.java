package com.frame.sdk.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import java.io.DataOutputStream;
import java.util.List;

public class ApkUtil {
	/**
	 * 判断包名为packageName的应用程序是否安装
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isInstall(Context context, String packageName) {
		return getPackageInfo(context, packageName) != null;
	}

	/**
	 * 获取包信息
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();
		try {
			PackageInfo packageInfo = packageManager.getPackageInfo(
					packageName, PackageManager.GET_SIGNATURES);
			return packageInfo;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取应用的名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getPackageName(Context context) {
		return context.getPackageName();
	}

	/**
	 * 获取应用程序的签名
	 * 
	 * @param context
	 * @param packageName
	 * @param algorithm
	 *            算法名称（MD5或SHA）
	 * @return
	 */
	public static String getSign(Context context, String packageName,
			String algorithm) {
		PackageInfo packageInfo = getPackageInfo(context, packageName);
		if (packageInfo.signatures == null)
			return "";
		for (int i = 0; i < packageInfo.signatures.length; i++) {
			if (packageInfo.signatures[i] != null) {
				byte[] bytes = packageInfo.signatures[i].toByteArray();
				bytes = SecurityUtil.getDigest(bytes, algorithm);
				return DataTypeConverUtil.bytesToHex(bytes);
			}
		}
		return "";
	}

	/**
	 * 获取应用程序的签名
	 * 
	 * @param context
	 * @param algorithm
	 *            算法名称（MD5或SHA）
	 * 
	 * @return
	 */
	public static String getSign(Context context, String algorithm) {
		return getSign(context, getPackageName(context), algorithm);
	}

	/**
	 * 判断该应用是否正在运行
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isRunning(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(packageName)
					&& info.baseActivity.getPackageName().equals(packageName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断该应用是否是最近运行的应用(是否处于top的应用)
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isRecentRunning(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		RunningTaskInfo info = list.get(0);
		if (info.topActivity.getPackageName().equals(packageName)
				&& info.baseActivity.getPackageName().equals(packageName)) {
			return true;
		}
		return false;
	}

	/**
	 * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限) 传入参数：getPackageCodePath()
	 * 
	 * @return 应用程序是/否获取Root权限
	 */
	public static boolean upgradeRootPermission(String pkgCodePath) {
		Process process = null;
		DataOutputStream os = null;
		try {
			String cmd = "chmod 777 " + pkgCodePath;
			process = Runtime.getRuntime().exec("su"); // 切换到root帐号
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}

	/**
	 * 获取当前应用版本名称
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
//			versioncode = pi.versionCode;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 获取当前应用版本号
	 */
	public static int getAppVersionCode(Context context) {
		int versionCode = 1;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionCode = pi.versionCode;
			if (versionCode <= 0) {
				versionCode = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionCode;
	}
}
