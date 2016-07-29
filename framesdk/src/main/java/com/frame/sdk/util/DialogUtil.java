package com.frame.sdk.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.frame.sdk.R;
import com.frame.sdk.app.FrameConfig;
import com.frame.sdk.widget.TipView;

/**
 * toast提示以及全局的处理进度提示对话框
 */
public class DialogUtil {
    private static Dialog dlgProcessing;

    /**
     * 显示处理中对话框
     */
    public static void showProgressDialog(Context ctx, String title, String msg) {
        dlgProcessing = ProgressDialog.show(ctx, title, msg);
    }

    /**
     * 隐藏处理中对话框
     */
    public static void hideProgressDialog() {
        if (dlgProcessing != null)
            dlgProcessing.dismiss();
    }

    /**
     * 显示在屏幕中间的Dialog(点击dialog外部消息，点击返回键消失)
     *
     * @param ctx
     * @param contentView 显示的dialog的内容
     */
    public static Dialog showDialog(Context ctx, View contentView) {
        return showDialog(ctx, contentView, true, true);
    }

    /**
     * 显示在屏幕中间的Dialog
     *
     * @param ctx
     * @param contentView        显示的dialog的内容
     * @param cancelable         按返回键dialog是否消失
     * @param cancelTouchOutside 点击dialog外部dialog是否消失
     */
    public static Dialog showDialog(Context ctx, View contentView,
                                    boolean cancelable, boolean cancelTouchOutside) {
        return showDialog(ctx, contentView, FrameConfig.DIALOG_DIM, cancelable,
                cancelTouchOutside);
    }

    /**
     * 显示在屏幕中间的Dialog
     *
     * @param ctx
     * @param contentView        显示的dialog的内容
     * @param cancelable         按返回键dialog是否消失
     * @param cancelTouchOutside 点击dialog外部dialog是否消失
     */
    public static Dialog showDialog(Context ctx, View contentView,
                                    float dimAmount, boolean cancelable, boolean cancelTouchOutside) {
        Dialog dialog = new Dialog(ctx, R.style.dlg_common);
        dialog.setContentView(contentView);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelTouchOutside);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = dimAmount;
        window.setAttributes(lp);
        dialog.show();
        return dialog;
    }

    /**
     * 底部弹出的dialog
     *
     * @param ctx
     * @param contentView        显示的dialog的内容
     * @param cancelable         按返回键dialog是否消失
     * @param cancelTouchOutside 点击dialog外部dialog是否消失
     */
    public static Dialog showDialogBottom(Context ctx, View contentView,
                                          boolean cancelable, boolean cancelTouchOutside) {
        return showDialog(ctx, contentView, R.style.window_translate_bottom,
                FrameConfig.DIALOG_DIM, cancelable, cancelTouchOutside);
    }

    /**
     * 底部弹出的dialog(点击dialog外部消息，点击返回键消失)
     *
     * @param ctx
     * @param contentView 显示的dialog的内容
     */
    public static Dialog showDialogBottom(Context ctx, View contentView) {
        return showDialog(ctx, contentView, R.style.window_translate_bottom,
                FrameConfig.DIALOG_DIM, true, true);
    }


    /**
     * 底部弹出的dialog(点击dialog外部消息，点击返回键消失)
     *
     * @param ctx
     * @param contentView 显示的dialog的内容
     */
    public static Dialog showDialogBottom(Context ctx, View contentView, float dimAmount) {
        return showDialog(ctx, contentView, R.style.window_translate_bottom,
                dimAmount, true, true);
    }

    /**
     * 顶部弹出的dialog
     *
     * @param ctx
     * @param contentView        显示的dialog的内容
     * @param cancelable         按返回键dialog是否消失
     * @param cancelTouchOutside 点击dialog外部dialog是否消失
     */
    public static Dialog showDialogTop(Context ctx, View contentView,
                                       boolean cancelable, boolean cancelTouchOutside) {
        return showDialog(ctx, contentView, R.style.window_translate_top,
                FrameConfig.DIALOG_DIM, cancelable, cancelTouchOutside);
    }

    /**
     * 顶部弹出的dialog(点击dialog外部消息，点击返回键消失)
     *
     * @param ctx
     * @param contentView 显示的dialog的内容
     */
    public static Dialog showDialogTop(Context ctx, View contentView) {
        return showDialog(ctx, contentView, R.style.window_translate_top,
                FrameConfig.DIALOG_DIM, true, true);
    }

    private static Dialog showDialog(Context ctx, View contentView,
                                     int styleId, float dimAmount, boolean cancelable,
                                     boolean cancelTouchOutside) {
        Dialog dialog = new Dialog(ctx, R.style.dlg_common);
        dialog.setContentView(contentView);
        Window window = dialog.getWindow();
        if (R.style.window_translate_bottom == styleId)
            window.setGravity(Gravity.BOTTOM);
        else
            window.setGravity(Gravity.TOP);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.dimAmount = dimAmount;
        window.setAttributes(lp);
        window.setWindowAnimations(styleId);
        dialog.show();
        return dialog;
    }

    /**
     * 显示全屏的dialog
     *
     * @param ctx
     * @param contentView
     * @param cancelable
     * @param cancelTouchOutside
     * @return
     */
    public static Dialog showDialogFull(Context ctx, View contentView,
                                        boolean cancelable, boolean cancelTouchOutside) {
        Dialog dialog = new Dialog(ctx,
                android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(contentView);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelTouchOutside);
        dialog.show();
        return dialog;
    }

    /**
     * 显示在指定位置的dialog
     *
     * @param ctx
     * @param contentView
     * @param x                  对话框左上角x坐标
     * @param y                  对话框左上角y坐标
     * @param cancelable
     * @param cancelTouchOutside
     * @return
     */
    public static Dialog showDialog(Context ctx, View contentView, int x,
                                    int y, float dimAmount, boolean cancelable,
                                    boolean cancelTouchOutside) {
        Dialog dialog = new Dialog(ctx, R.style.dlg_common);
        dialog.setContentView(contentView);
        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelTouchOutside);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = dimAmount;
        lp.x = x;
        lp.y = y;
        window.setAttributes(lp);
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        dialog.show();
        return dialog;
    }

    /**
     * 显示正在加载的dialog
     *
     * @param ctx
     * @param msg 提示文字，没有文字该参数可传空
     * @return
     */
    public static Dialog showLoadingDialog(Context ctx, String msg,boolean cancelable, boolean cancelTouchOutside ) {
        TipView tipView = new TipView(ctx, msg, R.anim.loading, true);
        return DialogUtil.showDialog(ctx, tipView, 0.0f, cancelable, cancelTouchOutside);
    }
    /**
     * 显示正在加载的dialog
     *
     * @param ctx
     * @param msg 提示文字，没有文字该参数可传空
     * @return
     */
    public static Dialog showLoadingDialog(Context ctx, String msg) {
        return showLoadingDialog(ctx, msg, false, false);
    }

    /**
     * 显示提示的dialog
     *
     * @param ctx
     * @param msg
     * @param imgRid   提示的图片id
     * @param isRotate 这个图片是否旋转
     * @return
     */
    public static Dialog showTipDialog(Context ctx, String msg, int imgRid,
                                       boolean isRotate) {
        TipView tipView = new TipView(ctx, msg, imgRid, isRotate, false);
        return DialogUtil.showDialog(ctx, tipView);
    }

    /**
     * 在屏幕底部显示带文本的dialog，上面为文本，下面两个按钮
     *
     * @param ctx
     * @param text       文本提示
     * @param textFirst  第一个按钮文本
     * @param textSecond 第二个按钮文本
     * @param listener
     * @return
     */
    public static Dialog showTextDialogBottom(Context ctx, String text,
                                              String textFirst, String textSecond,
                                              final CommonDialogListener listener) {
        return showTextDialogBottom(ctx, text, textFirst, textSecond, "",
                listener);
    }

    /**
     * 在屏幕底部显示带文本的dialog，上面为文本，下面两个按钮
     *
     * @param ctx
     * @param text       文本提示
     * @param textFirst  第一个按钮文本
     * @param textSecond 第二个按钮文本
     * @param listener
     * @return
     */
    public static Dialog showTextDialogBottom(Context ctx, String text,
                                              String textFirst, String textSecond, String textThird,
                                              final CommonDialogListener listener) {
        View contentView = LayoutInflater.from(ctx).inflate(
                R.layout.dialog_text_bottom, null);
        final Dialog dialog = showDialogBottom(ctx, contentView, true, true);
        Button btnFirst = (Button) contentView.findViewById(R.id.first);
        Button btnSecond = (Button) contentView.findViewById(R.id.second);
        Button btnThird = (Button) contentView.findViewById(R.id.third);
        TextView textView = (TextView) contentView.findViewById(R.id.textview);
        textView.setText(text);
        btnFirst.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClickFirst();
                dialog.dismiss();
            }
        });
        btnSecond.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClickSecond();
                dialog.dismiss();
            }
        });
        btnThird.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClickThird();
                dialog.dismiss();
            }
        });
        btnFirst.setText(textFirst);
        btnSecond.setText(textSecond);
        if (TextUtils.isEmpty(textThird)) {
            btnThird.setVisibility(View.GONE);
            contentView.findViewById(R.id.view03).setVisibility(View.GONE);
        } else {
            btnThird.setText(textThird);
        }
        return dialog;
    }

    /**
     * 在屏幕中间显示带文本的dialog，上面为文本，左下角按钮和右下角按钮
     *
     * @param ctx
     * @param text       文本提示
     * @param textFirst  左下角的按钮文本
     * @param textSecond 右下角的按钮文本
     * @param listener
     * @return
     */
    public static Dialog showTextDialogCenter(Context ctx, String text,
                                              String textFirst, String textSecond,
                                              final CommonDialogListener listener) {
        return showTextDialogCenter(ctx, text, textFirst, textSecond, listener,
                true, true);
    }

    /**
     * 在屏幕中间显示带文本的dialog，上面为文本，左下角按钮和右下角按钮
     *
     * @param ctx
     * @param text       文本提示
     * @param textFirst  左下角的按钮文本
     * @param textSecond 右下角的按钮文本
     * @param listener
     * @return
     */
    public static Dialog showTextDialogCenter(Context ctx, String text,
                                              String textFirst, String textSecond,
                                              final CommonDialogListener listener, boolean cancelable,
                                              boolean cancelTouchOutside) {
        View contentView = LayoutInflater.from(ctx).inflate(
                R.layout.dialog_text_center, null);
        final Dialog dialog = showDialog(ctx, contentView, cancelable,
                cancelTouchOutside);
        TextView tvLeft = (TextView) contentView.findViewById(R.id.tv_dlg_left);
        TextView tvRight = (TextView) contentView
                .findViewById(R.id.tv_dlg_right);
        TextView textView = (TextView) contentView
                .findViewById(R.id.tv_dlg_tip);
        textView.setText(text);
        tvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClickFirst();
                dialog.dismiss();
            }
        });
        tvRight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClickSecond();
                dialog.dismiss();
            }
        });
        tvLeft.setText(textFirst);
        tvRight.setText(textSecond);
        if(TextUtils.isEmpty(textFirst))
            tvLeft.setVisibility(View.GONE);
        if(TextUtils.isEmpty(textSecond))
            tvRight.setVisibility(View.GONE);
        return dialog;
    }

    /**
     * 显示底部有两个或三个按钮的dialog
     *
     * @param ctx
     * @param textFirst  第一个按钮的文本
     * @param textSecond 第二个按钮文本，如果只有一个按钮，该参数为空
     * @param textThird  第三个按钮文本，如果只有两个按钮，该参数为空
     * @param listener
     * @return
     */
    public static Dialog showOperationDialog(Context ctx, String textFirst,
                                             String textSecond, String textThird,
                                             final CommonDialogListener listener) {
        View contentView = LayoutInflater.from(ctx).inflate(
                R.layout.dialog_operation, null);
        final Dialog dialog = showDialogBottom(ctx, contentView, true, true);
        Button btnFirst = (Button) contentView.findViewById(R.id.first);
        Button btnSecond = (Button) contentView.findViewById(R.id.second);
        Button btnThird = (Button) contentView.findViewById(R.id.third);
        btnFirst.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClickFirst();
                dialog.dismiss();
            }
        });
        btnSecond.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClickSecond();
                dialog.dismiss();
            }
        });
        btnThird.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClickThird();
                dialog.dismiss();
            }
        });
        btnFirst.setText(textFirst);

        if (TextUtils.isEmpty(textSecond)) {
            btnSecond.setVisibility(View.GONE);
            btnThird.setVisibility(View.GONE);
            contentView.findViewById(R.id.line1).setVisibility(View.GONE);
            contentView.findViewById(R.id.line2).setVisibility(View.GONE);
        } else
            btnSecond.setText(textSecond);

        if (TextUtils.isEmpty(textThird)) {
            btnThird.setVisibility(View.GONE);
            contentView.findViewById(R.id.line2).setVisibility(View.GONE);
        } else
            btnThird.setText(textThird);
        return dialog;
    }

    public static interface CommonDialogListener {
        public void onClickFirst();

        public void onClickSecond();

        public void onClickThird();
    }
}
