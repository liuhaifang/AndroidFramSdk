package com.frame.sdk.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.RemoteViews;

import com.frame.sdk.R;

public class NotificationUtil {

	/**
	 * 设置通知
	 * 
	 * @param context
	 * @param notificationID
	 *            必须唯一，用来取消通知
	 * @param notification
	 *            调用{@link #getCustomNotification}或者
	 *            {@link #getDefaultNotification}生成
	 */
	public static void setNotification(Context context, int notificationID, Notification notification) {
		NotificationManager nm = getNotificationManager(context);
		nm.notify(notificationID, notification);
	}

	/**
	 * 设置通知
	 * 
	 * @param context
	 * @param notificationID
	 * @param intent
	 * @param isVibrat
	 * @param iconRes
	 * @param contentTitle
	 * @param contentText
	 * @param ticker
	 */
	public static void setNotification(Context context, int notificationID, PendingIntent intent, boolean isVibrat, int iconRes, String contentTitle, String contentText, String ticker) {
		NotificationManager nm = getNotificationManager(context);
		Builder builder = getDefaultSetting(context, intent, iconRes, contentTitle, contentText, ticker, true, false);
		builder = setVibrating(builder);
		nm.notify(notificationID, getCustomNotification(context, intent, builder));
	}

	/**
	 * 取消通知
	 * 
	 * @param context
	 * @param notificationID
	 */
	public static void cancelNotification(Context context, int notificationID) {
		NotificationManager nm = getNotificationManager(context);
		nm.cancel(notificationID);
	}

	/**
	 * 默认通知
	 * 
	 * @param context
	 * @param intent
	 *            要进行的操作
	 * @param iconRes
	 *            图标的resID
	 * @param contentTitle
	 *            标题
	 * @param contentText
	 *            内容
	 * @param ticker
	 *            刚出现时的文字
	 * @param autoCancel
	 *            自动取消
	 * @param ongoing
	 *            一直赖着
	 * @return
	 */
	public static Notification getDefaultNotification(Context context, PendingIntent intent, int iconRes, String contentTitle, String contentText, String ticker, boolean autoCancel, boolean ongoing) {
		return getDefaultSetting(context, intent, iconRes, contentTitle, contentText, ticker, autoCancel, ongoing).getNotification();
	}

	/**
	 * 自定义通知
	 * 
	 * @param context
	 * @param intent
	 * @param customSetting
	 *            如何生成,参考
	 *            {@link #getDefaultSetting(Context, PendingIntent, int, String, String, String, boolean, boolean)}
	 * @return
	 */
	public static Notification getCustomNotification(Context context, PendingIntent intent, Builder customSetting) {
		return customSetting.getNotification();
	}

	/**
	 * 自定义通知
	 * 
	 * @param context
	 * @param intent
	 * @param customSetting
	 *            如何生成,参考
	 *            {@link #getDefaultSetting(Context, PendingIntent, int, String, String, String, boolean, boolean)}
	 * @param views
	 *            自定义内容
	 * @return
	 */
	public static Notification getCustomNotification(Context context, PendingIntent intent, Builder customSetting, RemoteViews views) {
		customSetting.setContent(views);
		return customSetting.getNotification();
	}

	/**
	 * 设置默认的NotificationBuilder
	 * 
	 * @param context
	 * @param intent
	 *            要进行的操作
	 * @param iconRes
	 *            图标的resID
	 * @param contentTitle
	 *            标题
	 * @param contentText
	 *            内容
	 * @param ticker
	 *            刚出现时的文字
	 * @param autoCancel
	 *            自动取消
	 * @param ongoing
	 *            一直赖着
	 * @return
	 */
	public static Builder getDefaultSetting(Context context, PendingIntent intent, int iconRes, String contentTitle, String contentText, String ticker, boolean autoCancel, boolean ongoing) {
		return new Builder(context).setContentIntent(intent).setSmallIcon(iconRes).setContentTitle(contentTitle).setContentText(contentText).setTicker(ticker).setAutoCancel(autoCancel)
				.setOngoing(ongoing);
	}

	/**
	 * 设置震动 必须先调用{@link #getDefaultSetting}或者参考其自己生成
	 * 
	 * @param setting
	 * @return
	 */
	public static Builder setVibrating(Builder setting) {
		long[] vibrate = new long[] { 0, 500, 1000, 500 };
		setting.setVibrate(vibrate);
		return setting;
	}

	/**
	 * 设置震动 必须先调用{@link #getDefaultSetting}或者参考其自己生成
	 * 
	 * @param setting
	 * @param vibrate
	 *            [静止时长，震动时长，静止时长，震动时长。。。]
	 * @return
	 */
	public static Builder setVibrating(Builder setting, long[] vibrate) {
		setting.setVibrate(vibrate);
		return setting;
	}

	/**
	 * 取消震动
	 * 
	 * @param setting
	 * @return
	 */
	public static Builder cancelVibrating(Builder setting) {
		setting.setVibrate(new long[] { 0 });
		return setting;
	}

	/**
	 * 设置彩灯 必须先调用{@link #getDefaultSetting}或者参考其自己生成
	 * 
	 * @param setting
	 * @param colorARGB
	 * @return
	 */
	public static Builder setLights(Builder setting, int colorARGB) {
		setting.setLights(colorARGB, 500, 500);
		return setting;
	}

	/**
	 * 设置彩灯 必须先调用{@link #getDefaultSetting}或者参考其自己生成
	 * 
	 * @param setting
	 * @param colorARGB
	 * @param onMs
	 * @param offMs
	 * @return
	 */
	public static Builder setLights(Builder setting, int colorARGB, int onMs, int offMs) {
		setting.setLights(colorARGB, onMs, offMs);
		return setting;
	}

	/**
	 * 设置默认声音 必须先调用{@link #getDefaultSetting}或者参考其自己生成
	 * 
	 * @param context
	 * @param setting
	 * @return
	 */
	public static Builder setSound(Context context, Builder setting) {
		Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.ringtone);
		if (uri != null)
			setting.setSound(uri);
		return setting;
	}

	/**
	 * 设置自定义声音 必须先调用{@link #getDefaultSetting}或者参考其自己生成
	 * 
	 * @param context
	 * @param setting
	 * @param sound
	 * @return
	 */
	public static Builder setSound(Context context, Builder setting, Uri sound) {
		if (sound != null)
			setting.setSound(sound);
		return setting;
	}

	public static NotificationManager getNotificationManager(Context context) {
		return (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
	}

	/**
	 * 震动
	 * 
	 * @param context
	 * @return
	 */
	public static void vibrate(final Context context, long milliseconds) {
		Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}
}
