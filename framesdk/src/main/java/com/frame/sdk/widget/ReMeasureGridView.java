package com.frame.sdk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 重新调整高度的GridView，处理ScrollView中嵌套的GridView高度不对问题，只显示一行
 * 
 */
public class ReMeasureGridView extends GridView {
	public ReMeasureGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ReMeasureGridView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
