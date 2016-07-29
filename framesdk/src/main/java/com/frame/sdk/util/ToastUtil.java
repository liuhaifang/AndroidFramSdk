package com.frame.sdk.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.frame.sdk.app.FrameConfig;

/**
 * Toast提示工具类
 *
 */
public class ToastUtil {
	private static Toast toast;

	public static void showToast(Context ctx, String text) {
		if (TextUtils.isEmpty(text)) {
			return;
		}
		if (toast == null) {
			if (isDefaultToast()) {
				toast = Toast.makeText(ctx, text, FrameConfig.toastTime);
			} else {
				toast = new Toast(ctx);
				toast.setView(FrameConfig.toastView);
			}
			toast.setDuration(FrameConfig.toastTime);
			if (FrameConfig.toastTopMargin >= 0)
				toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0,
						FrameConfig.toastTopMargin);
		}
		if (isDefaultToast())
			toast.setText(text);
		else
			FrameConfig.toastTipView.setText(text);
		toast.show();
	}

	public static void showToast(Context ctx, int rId) {
		showToast(ctx, ctx.getResources().getString(rId));
	}

	private static boolean isDefaultToast() {
		return FrameConfig.toastView == null
				|| FrameConfig.toastTipView == null;
	}

}
