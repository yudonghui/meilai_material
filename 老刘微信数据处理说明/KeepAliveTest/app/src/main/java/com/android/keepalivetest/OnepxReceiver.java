package com.android.keepalivetest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnepxReceiver extends BroadcastReceiver {

    boolean appIsForeground = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){//屏幕关闭的时候接收，锁屏。
            appIsForeground = KeepAliveUtil.IsForeground(context);
            try {
                Intent it = new Intent(context,OnePixelActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(it);
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){//屏幕打开的时候，手机打开屏幕
                //TODO
            if (!appIsForeground){
                appIsForeground = false;
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                home.addCategory(Intent.CATEGORY_HOME);
                context.getApplicationContext().startActivity(home);
            }

        }
    }
}
