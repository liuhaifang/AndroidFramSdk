package com.frame.sdk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 键盘弹出，重新布局的RelativeLayout。主要用于监测键盘是否弹出
 */
public class ResizeRelativeLayout extends RelativeLayout {
	private ResizeObserver mResizeObserver;
	private int orgHeight;

	public ResizeRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if(orgHeight<=0)
			orgHeight=h;
		if (h < orgHeight) {
			if (mResizeObserver != null)
				mResizeObserver.showInput();
		} else {
			if (mResizeObserver != null)
				mResizeObserver.hideInput();
		}
	}

	public interface ResizeObserver {
		void showInput();

		void hideInput();
	}

	public void addResizeObserver(ResizeObserver resizeObserver) {
		this.mResizeObserver = resizeObserver;
	}
}
