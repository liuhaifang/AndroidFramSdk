package com.frame.sdk.anim;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * 动画进度由外部条件控制的动画
 */
public class TouchMoveAnimation extends Animation {
	private float transX, transY, degrees, alpha, centerX, centerY;

	public TouchMoveAnimation(float centerX, float centerY) {
		super();
		setCenter(centerX, centerY);
	}

	public TouchMoveAnimation(Context context, AttributeSet attrs, float centerX, float centerY) {
		super(context, attrs);
		setCenter(centerX, centerY);
	}

	public void setCenter(float centerX, float centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
	}

	/**
	 * 调用这个接口控制动画的进度
	 */
	public void setInterpolation(float transX, float transY, float degrees, float alpha) {
		this.transX = transX;
		this.transY = transY;
		this.degrees = degrees;
		this.alpha = alpha;
	}

	// 继承这个基类函数，通过返回true表示动画继续，否则动画结束
	@Override
	public boolean getTransformation(long currentTime, Transformation outTransformation) {
		applyTransformation(currentTime, outTransformation);
		return true;
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final Matrix matrix = t.getMatrix();
		t.setAlpha(alpha);
		matrix.postTranslate(transX, transY);
		matrix.postRotate(degrees, centerX + transX, centerY);
	}
}