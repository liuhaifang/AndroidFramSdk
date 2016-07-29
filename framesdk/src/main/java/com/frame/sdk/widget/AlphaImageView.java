package com.frame.sdk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 按下透明度改变的ImageView
 */
public class AlphaImageView extends ImageView {

	private boolean isCanTouch = true;

	public AlphaImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AlphaImageView(Context context) {
		super(context);
	}

	public void setIsCanTouch(boolean isCanTouch) {
		this.isCanTouch = isCanTouch;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isCanTouch)
			return super.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			setAlpha(0x7f);
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			setAlpha(0xff);
		} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
			setAlpha(0xff);
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			// Rect rect = ScreenUtil.getRect(this);
			// if (rect.contains((int) event.getX(), (int) event.getY())) {
			// setAlpha(0x7f);
			// } else {
			// setAlpha(0xff);
			// }
		}
		return super.onTouchEvent(event);
	}
}
