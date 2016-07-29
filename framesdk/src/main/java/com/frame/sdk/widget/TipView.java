package com.frame.sdk.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.frame.sdk.R;

/**
 * 包含一个图片和文字的提示view,如 全局进度等提示对话框view
 */
public class TipView extends FrameLayout {
	RotateAnimation anim;
	ImageView imgTip;
	TextView tvTip;

	/**
	 * 
	 * @param context
	 * @param info
	 *            提示文字
	 * @param imgRid
	 *            提示图片（或背景动画）id
	 * @param isRotate
	 *            显示图片的控件是否旋转
	 * @param isBgAnim
	 *            imgRid是否为背景动画，true将会播放背景动画
	 */
	public TipView(Context context, String info, int imgRid, boolean isRotate, boolean isBgAnim) {
		super(context);
		init(context, info, imgRid, isRotate, isBgAnim);
	}

	/**
	 * 
	 * @param context
	 * @param info
	 *            提示文字
	 * @param imgRid
	 *            提示图片（或背景动画）id
	 */
	public TipView(Context context, String info, int imgRid) {
		this(context, info, imgRid, false, false);
	}

	/**
	 * 
	 * @param context
	 * @param info
	 *            提示文字
	 * @param imgRid
	 *            提示图片（或背景动画）id
	 * @param isBgAnim
	 *            imgRid是否为背景动画，true将会播放背景动画
	 */
	public TipView(Context context, String info, int imgRid, boolean isBgAnim) {
		this(context, info, imgRid, false, isBgAnim);
	}

	private Runnable rotateRun = new Runnable() {
		@Override
		public void run() {
			View view = findViewById(R.id.img_tip);
			anim = new RotateAnimation(0, 360, view.getWidth() / 2, view.getHeight() / 2);
			anim.setDuration(1000);
			anim.setFillAfter(true);
			anim.setFillBefore(true);
			anim.setRepeatCount(Animation.INFINITE);
			view.setAnimation(anim);
			view.startAnimation(anim);
		}
	};

	private void init(Context context, String info, int imgRid, boolean isRotate, boolean isBgAnim) {
		LayoutInflater.from(context).inflate(R.layout.view_tip, this, true);
		tvTip = ((TextView) findViewById(R.id.tv_tip));
		imgTip = ((ImageView) findViewById(R.id.img_tip));
		if (TextUtils.isEmpty(info)) {
			tvTip.setVisibility(View.GONE);
		} else
			tvTip.setText(info);
		imgTip.setBackgroundResource(imgRid);
		if (isRotate) {
			postDelayed(rotateRun, 500);
		}
		if (isBgAnim) {
			AnimationDrawable anim = (AnimationDrawable) imgTip.getBackground();
			anim.start();
		}
	}

	public TipView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, "", 0, false, false);
	}

	public ImageView getImageView() {
		return imgTip;
	}

	public void setText(String info) {
		tvTip.setText(info);
	}

	public String getText() {
		return tvTip.getText().toString();
	}

	public void setImage(int imgRid) {
		imgTip.clearAnimation();
		imgTip.setBackgroundResource(imgRid);
	}

	public void setRotate(boolean isRotate) {
		if (isRotate) {
			post(rotateRun);
		} else {
			if (anim != null)
				anim.cancel();
			imgTip.clearAnimation();
			imgTip.setAnimation(null);
		}
	}

	public void setBgAnim(boolean isBgAnim) {
		if (isBgAnim) {
			AnimationDrawable anim = (AnimationDrawable) imgTip.getBackground();
			anim.start();
		} else {
			AnimationDrawable anim = (AnimationDrawable) imgTip.getBackground();
			anim.stop();
		}
	}

}
