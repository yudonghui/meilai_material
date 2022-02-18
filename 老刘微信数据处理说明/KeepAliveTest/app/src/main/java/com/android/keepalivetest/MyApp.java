package com.android.keepalivetest;

import android.app.AlertDialog;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.simple.SimpleResponse;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class MyApp extends Application {
    private int num = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(MyApp.this, "美莱守护已被启动！", Toast.LENGTH_LONG).show();
//        Log.e("MyApp","-------onCreate()");
//        ShellUtils.CommandResult result = ShellUtils.execCmd("echo root", true);
//        if (result.result == 0){
//            Log.e("MyApp","-------有root权限");
//        }
//        if (result.errorMsg != null) {
//            Log.e("MyApp", "onCreate--isAppRoot() ：" + result.errorMsg);
//        }

        handler.sendEmptyMessageDelayed(0, 10000);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("keepalivetest", "MyApp--handler ：" + num);
            num++;
            handler.sendEmptyMessageDelayed(0, 10000);//SystemClock.uptimeMillis() + delayMillis
            boolean isAvilible = isApplicationAvilible(MyApp.this, "com.mylike.keepalive");
            if (!isAvilible) {//新美莱服务没有安装
                Toast.makeText(MyApp.this, "请先安装新美莱服务", Toast.LENGTH_LONG).show();
//                AlertDialog alertDialog = new AlertDialog.Builder(MyApp.this)
//                        .setTitle("提示")
//                        .setMessage("请先安装新美莱服务")
//                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
//
//                        })
//                        .create();
//                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                alertDialog.show();
                return;
            }
            boolean isAvilible1 = isApplicationAvilible(MyApp.this, "com.tencent.mm");
            if (!isAvilible1) {//新美莱服务没有安装
                Toast.makeText(MyApp.this, "请先安装微信", Toast.LENGTH_LONG).show();
//                AlertDialog alertDialog = new AlertDialog.Builder(MyApp.this)
//                        .setTitle("提示")
//                        .setMessage("请先安装微信")
//                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
//
//                        })
//                        .create();
//                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                alertDialog.show();
                return;
            }
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("HH");
            String hourStr = format.format(date);
            int hour = Integer.parseInt(hourStr);
            if (hour > 22 || hour < 8) {
                num = 0;
                return;
            }
            if (num >= 5) {//5分钟
                num = 0;
                sendDelayMeg();
                getVersionNet();
            }
        }
    };

    private void sendDelayMeg() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            /**知道要跳转应用的包命与目标Activity*/
            ComponentName componentName = new ComponentName("com.mylike.keepalive", "live_library.onepx.OnePixelActivity");
            intent.setComponent(componentName);
            //这里Intent传值
            Bundle bundle = new Bundle();
            bundle.putString("KEY", "1");//"1"需要处理心跳
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("MyApp--跳转到新美莱服务异常", e.getMessage());
        }
    }

    public static boolean isApplicationAvilible(Context context, String appPackageName) {
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (appPackageName.equals(pn)) {
                    return true;
                }
            }
        }
        return false;

    }
    public static int getPackageCode(Context context, String appPackageName) {
        PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (appPackageName.equals(pn)) {
                    return pinfo.get(i).versionCode;
                }
            }
        }
        return 0;

    }

    private void getVersionNet() {
     Kalle.get("http://monitor.shmylike.com//groupCtrlApi/his_cloud/wechatVersion").

        perform(new QueryCallBack<String>() {

        @Override
        public void onResponse (SimpleResponse< String, String > response){
            try {
                JSONObject jsonObject = new JSONObject(response.succeed());
                int VersionCode = jsonObject.optInt("VersionCode");
                int vcode = getPackageCode(MyApp.this,"com.mylike.keepalive");
                if (vcode<VersionCode) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    /**知道要跳转应用的包命与目标Activity*/
                    ComponentName componentName = new ComponentName("com.mylike.keepalive", "com.mylike.keepalive.MainActivity");
                    intent.setComponent(componentName);
                    //这里Intent传值
                    Bundle bundle = new Bundle();
                    bundle.putString("KEY", "2");
                    intent.putExtras(bundle);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            } catch (Exception e) {

            }

        }
    });
}
    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Toast.makeText(MyApp.this, "美莱守护已被伤害！", Toast.LENGTH_LONG).show();
        super.onTerminate();
    }
}
