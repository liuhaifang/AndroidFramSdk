package com.frame.sdk.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 软引用保存整个应用程序的activity
 */
public class ActivityUtil {
	private static List<SoftReference<Activity>> activityList = new ArrayList<SoftReference<Activity>>();

	public static void addActivity(Activity activity) {
		activityList.add(new SoftReference<Activity>(activity));
	}

	public static void removeActivity(Activity activity) {
		activityList.remove(new SoftReference<Activity>(activity));
	}

	public static void clear() {
		for (SoftReference<Activity> activity : activityList) {
			if (activity != null) {
				Activity act = activity.get();
				if (act != null)
					act.finish();
			}
		}
		activityList.clear();
	}

	public static Activity get(Class<?> clazz) {
		for (int i = activityList.size() - 1; i >= 0; i--) {
			if (activityList.get(i) == null)
				continue;
			Activity act = activityList.get(i).get();
			if (act != null && act.getClass() == clazz)
				return act;
		}
		return null;
	}



	/**
	 * 
	 * @param context
	 *            activity的context
	 * @param clazz
	 *            欲关闭Service的class类型
	 */
	public static void stopService(Context context, Class<?> clazz) {

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningServiceInfo> serviceList = am.getRunningServices(100);

		if (serviceList == null)
			return;

		if (serviceList.size() == 0)
			return;

		if (isServiceRunning(context, clazz))
			context.stopService(new Intent(context, clazz));
	}

	/**
	 * 
	 * @param context
	 *            activity的context
	 * @param clazz
	 *            欲关闭Service的class类型
	 * @return 该Service是否运行
	 */
	public static boolean isServiceRunning(Context context, Class<?> clazz) {
		boolean isRunning = false;

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceList = am.getRunningServices(100);

		if (serviceList == null)
			return false;

		if (serviceList.size() == 0)
			return false;

		String className = clazz.getCanonicalName();

		for (RunningServiceInfo info : serviceList) {
			if (info.service.getClassName().equals(className)) {
				isRunning = true;
				break;
			}
		}

		return isRunning;
	}

	private static Activity currActivity;
	public static void setCurrentActivity(Activity currActivity){
		ActivityUtil.currActivity=currActivity;
	}
	public static Activity getCurrActivity(){
		return currActivity;
	}

	public static void goToSystemAPPSetting(Activity act) {
		String SCHEME = "package";
		/**
		 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
		 */
		String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
		/**
		 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
		 */
		String APP_PKG_NAME_22 = "pkg";
		/**
		 * InstalledAppDetails所在包名
		 */
		String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
		/**
		 * InstalledAppDetails类名
		 */
		String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
		/**
		 * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
		 * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
		 * 
		 * @param context
		 * 
		 * @param packageName
		 *            应用程序的包名
		 */

		Intent intent = new Intent();
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts(SCHEME, act.getPackageName(), null);
			intent.setData(uri);
		} else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
			// 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
			final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
			intent.putExtra(appPkgName, act.getPackageName());
		}
		act.startActivity(intent);

	}
}
