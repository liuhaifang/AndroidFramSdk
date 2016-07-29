package com.frame.sdk.widget;
import com.frame.sdk.util.ScreenUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 自动换行的View容器,用于标签显示时如果标签超过这个控件宽度，标签会自动放在下一行。 需要注意保持标签高度一致。 使用时需指定该控件的宽度
 * 
 */
public class LabelGroup extends ViewGroup {
	public LabelGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		VIEW_MARGIN=(int) (VIEW_MARGIN*ScreenUtil.getDensity(context));
	}

	public LabelGroup(Context context) {
		super(context);
		VIEW_MARGIN=(int) (VIEW_MARGIN*ScreenUtil.getDensity(context));
	}

	private int VIEW_MARGIN = 5;// 子view间的距离及控件padding

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		// 测量各个子View
		for (int index = 0; index < getChildCount(); index++) {
			final View child = getChildAt(index);
			LayoutParams lp = child.getLayoutParams();
			child.measure(MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY));
		}

		// 以下代码根据子view有多上行确定控件高度
		final int count = getChildCount();
		int row = 0;
		int lengthX = 0;
		int lengthY = 0;
		for (int i = 0; i < count; i++) {
			final View child = this.getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();
			lengthX += width + VIEW_MARGIN;
			lengthY = row * (height + VIEW_MARGIN) + VIEW_MARGIN + height;
			if (lengthX > widthSize) {
				lengthX = width + VIEW_MARGIN;
				row++;
				lengthY = row * (height + VIEW_MARGIN) + VIEW_MARGIN + height;
			}
		}
		super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(lengthY + VIEW_MARGIN, MeasureSpec.EXACTLY));
	}

	@Override
	protected void onLayout(boolean arg0, int left, int top, int right, int bottom) {
		final int count = getChildCount();
		int row = 0;
		int lengthX = 0;
		int lengthY = 0;
		// 布局各个子view
		for (int i = 0; i < count; i++) {
			final View child = this.getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();
			lengthX += width + VIEW_MARGIN;
			lengthY = row * (height + VIEW_MARGIN) + VIEW_MARGIN + height;
			// 一行放不下，则换行
			if (lengthX + left > right) {
				lengthX = width + VIEW_MARGIN;
				row++;
				lengthY = row * (height + VIEW_MARGIN) + VIEW_MARGIN + height;
			}
			child.layout(lengthX - width, lengthY - height, lengthX, lengthY);
		}
	}
}
