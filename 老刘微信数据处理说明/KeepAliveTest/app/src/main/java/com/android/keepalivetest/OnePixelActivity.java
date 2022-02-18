package com.android.keepalivetest;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class OnePixelActivity extends AppCompatActivity  {

    BroadcastReceiver br;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设定一像素的activity
        Window window = getWindow();
        window.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               finish();
            }
        };
        registerReceiver(br,new IntentFilter("finish activity"));//过滤器设置为finish activity，只接收这个广播。
        checkScreenOn();
    }


    private void checkScreenOn(){
        PowerManager pm = (PowerManager)OnePixelActivity.this.getSystemService(Context.POWER_SERVICE);
        if (pm.isScreenOn()){//如果屏幕已经打开
            finish();
        }
    }
}
