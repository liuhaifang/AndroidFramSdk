package com.frame.sdk.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

/**
 * 屏幕信息及全屏幕操作相关的方法
 */
public class ScreenUtil {

    private static int screenWidth, screenHeight, stateBarHeight, titileBarHeight;
    private static float density;

    private ScreenUtil() {
    }

    public static void hideInput(Context context, View view) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showInput(Context context, View view) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        im.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 获取某个view在屏幕上的位置
     */
    public static Rect getRect(View v) {
        int[] loc = new int[2];
        v.getLocationOnScreen(loc);
        return new Rect(loc[0], loc[1], v.getWidth() + loc[0], v.getHeight() + loc[1]);
    }

    public static int getTitleBarHeight(Activity act) {
        if (titileBarHeight <= 0) {
            View window = act.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
            Rect rect = new Rect();
            window.getWindowVisibleDisplayFrame(rect);
            titileBarHeight = rect.height() - window.getHeight();
        }
        return titileBarHeight;
    }

    public static int getScreenWidth(Context context) {
        if (screenWidth <= 0) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            screenWidth = outMetrics.widthPixels;
        }
        return screenWidth;
    }

    public static int getScreenHeight(Context context) {
        if (screenHeight <= 0) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            screenHeight = outMetrics.heightPixels;
        }
        return screenHeight;
    }

    public static float getDensity(Context context) {
        if (density <= 0f) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            DisplayMetrics outMetrics = new DisplayMetrics();
            display.getMetrics(outMetrics);
            density = outMetrics.density;
        }
        return density;
    }

    /**
     * 设置屏幕密度
     */
    public static void setDensity(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float screenWidth = display.getWidth();
        float newScaledDensity = screenWidth / 320;
        float newDensity = screenWidth / 320;
        outMetrics.density = newDensity;
        outMetrics.scaledDensity = newScaledDensity;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getStateBarHeight(Activity act) {
        if (stateBarHeight <= 0) {
            View window = act.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
            Rect rect = new Rect();
            window.getWindowVisibleDisplayFrame(rect);
            stateBarHeight = rect.top;
        }
        return stateBarHeight;
    }

    public static void hideView(View view, boolean isGone) {
        if (isGone)
            view.setVisibility(View.GONE);
        else
            view.setVisibility(View.INVISIBLE);
    }

    public static void showView(View view) {
        view.setVisibility(View.VISIBLE);
    }

    /**
     * 获取某个view在屏幕上的位置
     */
    public static Rect getRectOnScreen(View v) {
        int[] loc = new int[2];
        v.getLocationOnScreen(loc);
        return new Rect(loc[0], loc[1], v.getWidth() + loc[0], v.getHeight() + loc[1]);
    }

    public static Rect getRectGlobal(View v) {
        Rect rect = new Rect();
        v.getGlobalVisibleRect(rect);
        return rect;
    }

    public static Rect getRectOnWin(View v) {
        int[] loc = new int[2];
        v.getLocationInWindow(loc);
        return new Rect(loc[0], loc[1], v.getWidth() + loc[0], v.getHeight() + loc[1]);
    }

    public static Rect getRectVisible(View v) {
        Rect rect = new Rect();
        v.getLocalVisibleRect(rect);
        return rect;
    }

    /*
* 设置控件所在的位置X，并且不改变宽高，
* X为绝对位置，此时Y可能归0
*/
    public static void setLayoutX(View view, int x) {
        MarginLayoutParams margin = new MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(x, margin.topMargin, x + margin.width, margin.bottomMargin);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }

    /*
    * 设置控件所在的位置Y，并且不改变宽高，
    * Y为绝对位置，此时X可能归0
    */
    public static void setLayoutY(View view, int y) {
        MarginLayoutParams margin = new MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(margin.leftMargin, y, margin.rightMargin, y + margin.height);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }

    /*
    * 设置控件所在的位置YY，并且不改变宽高，
    * XY为绝对位置
    */
    public static void setLayout(View view, int x, int y) {
        MarginLayoutParams margin = new MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(x, y, x + margin.width, y + margin.height);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

}
