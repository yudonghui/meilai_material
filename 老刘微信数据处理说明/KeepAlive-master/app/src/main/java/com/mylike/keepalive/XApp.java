package com.mylike.keepalive;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;

//import com.blankj.utilcode.util.SPUtils;
//import com.lody.virtual.client.NativeEngine;
//import com.lody.virtual.client.core.VirtualCore;
//import com.lody.virtual.client.stub.VASettings;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mylike.keepalive.update.XHttpUpdateHttpService;
import com.pgyersdk.crash.PgyCrashManager;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;
//import com.xdandroid.hellodaemon.DaemonEnv;
import java.util.Date;
import java.util.List;

import live_library.KeepAliveManager;
import live_library.config.ForegroundNotification;
import live_library.config.ForegroundNotificationClickListener;
import live_library.wechatlog.RLog;
import live_library.wechatutils.RootUtil;

import static com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION;
import static live_library.config.RunMode.HIGH_POWER_CONSUMPTION;

//import io.virtualapp.delegate.MyVirtualInitializer;


//import androidx.work.PeriodicWorkRequest;
//import androidx.work.WorkManager;
//import io.virtualapp.home.RepeatWorker;

/**
 * @author Lody
 */
public class XApp extends Application {
//    public static final String APP_ID = "2882303761518518856";
    public static final String APP_ID = "2882303761518601820";
//    public static final String APP_KEY = "5261851831856";
    public static final String APP_KEY = "5761860149820";
//    public static final String TAG = "your packagename";
    private static final String TAG = "XApp";

    public static final String XPOSED_INSTALLER_PACKAGE = "de.robv.android.xposed.installer";

    private static XApp gApp;

    /**
     * .创建请求队列
     */
//    public static RequestQueue queue;

    public static XApp getApp() {
        return gApp;
    }

    @Override
    protected void attachBaseContext(Context base) {
        gApp = this;
        super.attachBaseContext(base);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            NativeEngine.disableJit(Build.VERSION.SDK_INT);
//        }
//        VASettings.ENABLE_IO_REDIRECT = true;
//        VASettings.ENABLE_INNER_SHORTCUT = false;
//        try {
//            VirtualCore.get().startup(base);
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//        queue = Volley.newRequestQueue(this);
//初始化push推送服务

    }
    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getApplicationInfo().processName;
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
   /* *//**
     * 暴露一个方法用于获取全局的请求队列
     *//*
    public static RequestQueue getHttpQueue() {
        return queue;
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        RLog.d(TAG,"是否root:"+ AppUtils.isAppRoot());
        XHttp.init(this);
        XUpdate.get().isAutoMode(false).supportSilentInstall(false).init(this);
        XUpdate.get().debug(true);
//        PgyCrashManager.register(); //蒲公英
//        sendDelayMeg();
//        if(shouldInit()) {
//            MiPushClient.registerPush(this, APP_ID, APP_KEY);
//            String RegId = MiPushClient.getRegId(this);
//            SharedPreferences mSharedPreferences =  getSharedPreferences("yes_getImei", MODE_PRIVATE);
//            SharedPreferences.Editor editor = mSharedPreferences.edit();
//            editor.putString("RegId", RegId);
//            editor.commit();
//        }
//        String RegId = MiPushClient.getRegId(this);
//        MiPushClient.setAlias(this,"17621979975",null);
//        RLog.d(TAG,"小米RegId:"+ RegId);
//        SharedPreferences mSharedPreferences =  getSharedPreferences("yes_getImei", MODE_PRIVATE);
//        SharedPreferences.Editor editor = mSharedPreferences.edit();
//        editor.putString("RegId", RegId);
//        editor.commit();
        //打开Log
        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
        //启动保活服务
        KeepAliveManager.toKeepAlive(
                this
                , HIGH_POWER_CONSUMPTION,
                "进程保活",
                "美莱服务",
                R.mipmap.icon,
                new ForegroundNotification(
                        //定义前台服务的通知点击事件
                        new ForegroundNotificationClickListener() {
                            @Override
                            public void foregroundNotificationClick(Context context, Intent intent) {
                                RLog.d("JOB-->", " foregroundNotificationClick");
                            }
                        })
        );
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RLog.d("XApp--handler",new Date()+"");

//            sendDelayMeg();

        }
    };
    private void sendDelayMeg() {
//        ToastUtils.showShort("sendDelayMeg1："+new Date());
//        RLog.d("com.mylike.keepalive.sendDelayMeg",new Date()+"");
        handler.sendEmptyMessageDelayed(0, 5000);
        KeepAliveManager.toKeepAlive(
                this
                , HIGH_POWER_CONSUMPTION,
                "进程保活",
                "美莱服务",
                R.mipmap.icon,
                new ForegroundNotification(
                        //定义前台服务的通知点击事件
                        new ForegroundNotificationClickListener() {
                            @Override
                            public void foregroundNotificationClick(Context context, Intent intent) {
                                RLog.d("JOB-->", " foregroundNotificationClick");
                            }
                        })
        );
    }
}
