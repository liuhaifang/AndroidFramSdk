package com.frame.sdk.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.widget.Button;

public class CountDownButton extends CountDownTimer {
    private Context mContext;
    private Button mButton;
    private String mOriginalText;
    private Drawable mOriginalBackground;
    private int tickBgRid, tickColor;
    private int mOriginalTextColor;

    /**
     * 自定义总时间和时间间隔的倒计时
     * @param millisInFuture 总时间紧ms
     * @param countDownInterval 间隔时间ms
     */
    public CountDownButton(long millisInFuture, long countDownInterval, int tickBgRid, int tickColor, Context context, Button button) {
        super(millisInFuture, countDownInterval);
        init(tickBgRid, tickColor, context, button);
    }

    private void init(int tickBgRid, int tickColor, Context context, Button button) {
        this.mContext = context;
        this.mButton = button;
        this.mOriginalText = mButton.getText().toString();
        this.mOriginalBackground = mButton.getBackground();
        this.mOriginalTextColor = mButton.getCurrentTextColor();
        this.tickBgRid = tickBgRid;
        this.tickColor = tickColor;
    }

    @Override
    public void onFinish() {
        if (mContext != null && mButton != null) {
            mButton.setText(mOriginalText);
            mButton.setTextColor(mOriginalTextColor);
            mButton.setBackgroundDrawable(mOriginalBackground);
            mButton.setClickable(true);
        }
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (mContext != null && mButton != null) {
            mButton.setClickable(false);
            mButton.setBackgroundResource(tickBgRid);
            mButton.setTextColor(tickBgRid);
            mButton.setText(millisUntilFinished / 1000 + "秒");
        }
    }
}  