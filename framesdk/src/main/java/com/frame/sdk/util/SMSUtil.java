package com.frame.sdk.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.text.TextUtils;

import com.frame.sdk.model.SmsInfo;

/**
 * 获取手机中的各种短信信息
 */
public class SMSUtil {
	private Context context;
	/**
	 * 所有的短信
	 */
	public static final String SMS_URI_ALL = "content://sms/";
	/**
	 * 收件箱短信
	 */
	public static final String SMS_URI_INBOX = "content://sms/inbox";
	/**
	 * 发件箱短信
	 */
	public static final String SMS_URI_SEND = "content://sms/sent";
	/**
	 * 草稿箱短信
	 */
	public static final String SMS_URI_DRAFT = "content://sms/draft";

	private static SMSUtil instance;

	private SMSUtil(Context context) {
		this.context = context;
	}

	public static SMSUtil getInstance(Context context) {
		if (instance == null) {
			instance = new SMSUtil(context);
		}
		return instance;
	}

	/**
	 * 获取所有的短信
	 */
	public List<SmsInfo> getAllSmsInfoList() {
		return getSmsInfoList(0);

	}

	/**
	 * 获取时间大于minTime的短信列表 minTime :短信的最小时间
	 */
	public List<SmsInfo> getSmsInfoList(long minTime) {
		List<SmsInfo> smsList = new ArrayList<SmsInfo>();

		ContentResolver cr = context.getContentResolver();
		String[] projection = new String[] { "_id", "address", "person",
				"body", "date", "type" };
		Uri uri = Uri.parse(SMS_URI_ALL);
		Cursor cur = cr.query(uri, projection, null, null, "date desc");
		try {
			if (cur != null && cur.moveToFirst()) {
				int nameColumn = cur.getColumnIndex("person");
				int phoneNumberColumn = cur.getColumnIndex("address");
				int smsbodyColumn = cur.getColumnIndex("body");
				int dateColumn = cur.getColumnIndex("date");
				int typeColumn = cur.getColumnIndex("type");

				do {
					SmsInfo sms = new SmsInfo();
					sms.time = Long.parseLong(cur.getString(dateColumn));
					if (sms.time < minTime) {
						continue;
					}
					sms.name = cur.getString(nameColumn);
					if (TextUtils.isEmpty(sms.name)) {
						sms.name = "陌生人";
					}
					sms.phone = cur.getString(phoneNumberColumn);
					sms.content = cur.getString(smsbodyColumn);
					sms.type = cur.getInt(typeColumn);
					sms.localName = SIMCardUtil.getInstance(context)
							.getProvidersName()
							+ "  设备号:"
							+ DeviceUtil.getDeviceId(context);
					sms.localPhone = SIMCardUtil.getInstance(context)
							.getNativePhoneNumber();
					smsList.add(sms);
				} while (cur.moveToNext());
			}

		} catch (SQLiteException ex) {
			ex.printStackTrace();
		}finally{
			if(cur!=null){
				cur.close();
			}
		}
		return smsList;
	}
}
