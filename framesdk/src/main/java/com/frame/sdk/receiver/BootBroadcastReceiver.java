package com.frame.sdk.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.frame.sdk.util.LogUtils;

public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String action_boot = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(action_boot)) {
            LogUtils.i("penderie start");
//            Intent StartIntent = new Intent(context, MainActivity.class); //接收到广播后，跳转到MainActivity
//            StartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(StartIntent);
        }

    }

}
