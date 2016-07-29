package com.frame.sdk.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 可以传入指示器的viewpager。先初始化然后调用setIndicator设置指示图标
 */
public class IndicatorViewPager extends ViewPager {
    OnPageChangeListener listener;
    Context context;
    LinearLayout vGroup;
    int count;
    int rIdIndicatorOn;
    int rIdIndicatorOff;
    int index;// 当前选中项

    public IndicatorViewPager(Context context) {
        super(context);
        init(context);
    }

    public IndicatorViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.listener = listener;
    }

    private void init(Context context) {
        this.context = context;
        super.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int pos) {
                if (listener != null)
                    listener.onPageSelected(pos);
                setIndicator(pos);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if (listener != null)
                    listener.onPageScrolled(arg0, arg1, arg2);
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                if (listener != null)
                    listener.onPageScrollStateChanged(arg0);
            }
        });
    }

    /**
     * 设置指示图标
     *
     * @param vGroup          存放指示图标的根容器
     * @param count           指示图标个数
     * @param rIdIndicatorOn  指示图标选中时图片id
     * @param rIdIndicatorOff 指示图标未选中时图片id
     * @param margin          各个指示图标间距
     * @param width           指示图标宽度
     * @param height          指示图标高度
     */
    public void setIndicator(LinearLayout vGroup, int count, int rIdIndicatorOn, int rIdIndicatorOff, float margin, float width, float height) {
        this.vGroup = vGroup;
        this.count = count;
        this.rIdIndicatorOn = rIdIndicatorOn;
        this.rIdIndicatorOff = rIdIndicatorOff;

        for (int i = 0; i < count; i++) {
            ImageView imgView = new ImageView(context);
            if (i == index)
                imgView.setBackgroundResource(rIdIndicatorOn);
            else
                imgView.setBackgroundResource(rIdIndicatorOff);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) width, (int) height);
            if (i != 0)
                lp.leftMargin = (int) margin;
            vGroup.addView(imgView, lp);
        }
    }

    /**
     * 设置当前选中项
     *
     * @param index
     */
    private void setIndicator(int index) {
        if (index < 0 || index >= count || index == this.index || vGroup == null) {
            return;
        }
        vGroup.getChildAt(this.index).setBackgroundResource(rIdIndicatorOff);
        vGroup.getChildAt(index).setBackgroundResource(rIdIndicatorOn);
        this.index = index;
    }

}
