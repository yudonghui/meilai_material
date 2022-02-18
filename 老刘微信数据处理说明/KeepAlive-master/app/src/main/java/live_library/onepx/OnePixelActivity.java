package live_library.onepx;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ShellUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.mylike.keepalive.MainActivity;
import com.mylike.keepalive.R;
import com.mylike.keepalive.XApp;
import com.mylike.keepalive.update.HProgressDialogUtils;
import com.mylike.keepalive.update.XHttpUpdateHttpService;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate._XUpdate;
import com.xuexiang.xupdate.service.OnFileDownloadListener;
import com.yanzhenjie.kalle.FormBody;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.simple.SimpleResponse;

import org.json.JSONObject;

import java.io.File;

import live_library.KeepAliveManager;
import live_library.config.ForegroundNotification;
import live_library.config.ForegroundNotificationClickListener;
import live_library.wechat2.ConfigWechat;
import live_library.wechat2.sns.QueryCallBack;
import live_library.wechatlog.RLog;

import static live_library.config.RunMode.HIGH_POWER_CONSUMPTION;

public final class OnePixelActivity extends Activity {
    //注册广播接受者   当屏幕开启结果成功结束一像素的activity
    BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent()!=null) {
            try {
                String key = getIntent().getStringExtra("KEY");
                RLog.d("com.mylike.keepalive.OnePixelActivity","key："+key);
                if ("1".equals(key)){
                    pushHeart();
                }
            }catch (Exception e){

            }

        }

        //设定一像素的activity
        Window window = getWindow();
        window.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
//        downloadApk();
        verifyStoragePermissions(this);
        ShellUtils.CommandResult result = ShellUtils.execCmd("echo root", true);
        if (result.result == 0){
            RLog.d("权限判断", "有root权限");
        }else {
            ToastUtils.showLong("没有root权限");
            RLog.d("权限判断", "没有root权限");
        }
        if (result.errorMsg != null) {
            RLog.d("权限判断", "root权限异常："+result.errorMsg);
        }
        //在一像素activity里注册广播接受者    接受到广播结束掉一像素
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(br, new IntentFilter("finish activity"));
        checkScreenOn("onCreate");

    }
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
                RLog.d("权限判断", "没有存储权限");
                ToastUtils.showLong("没有存储权限");
            }else {
                RLog.d("权限判断", "有存储权限");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(br);
        } catch (IllegalArgumentException e) {
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkScreenOn("onResume");
    }

    private void checkScreenOn(String methodName) {
        PowerManager pm = (PowerManager) OnePixelActivity.this.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (isScreenOn) {
            finish();
        }
    }
    private void pushHeart(){
        SharedPreferences mSharedPreferences1 =  XApp.getApp().getSharedPreferences("yes_getImei", MODE_PRIVATE);
        String wxIdOne = mSharedPreferences1.getString("wxIdOne","");
        String wxIdTwo = mSharedPreferences1.getString("wxIdTwo","");
        if (StringUtils.isEmpty(wxIdOne)&&StringUtils.isEmpty(wxIdTwo)) {
            return;
        }
        Kalle.post(ConfigWechat.loginRecord+wxIdOne).perform(new QueryCallBack<String>() {

            @Override
            public void onResponse(SimpleResponse<String, String> response) {
                Log.e("pushHeartOne",response.succeed());
            }
        });
        if (StringUtils.isEmpty(wxIdTwo))return;
        Kalle.post(ConfigWechat.loginRecord+wxIdTwo).perform(new QueryCallBack<String>() {

            @Override
            public void onResponse(SimpleResponse<String, String> response) {
                Log.e("pushHeartTwo",response.succeed());
            }
        });
        start();
    }
    public void start() {
        //启动保活服务
        KeepAliveManager.toKeepAlive(
                getApplication()
                , HIGH_POWER_CONSUMPTION,
                "进程保活",
                "美莱服务",
                R.mipmap.icon,
                new ForegroundNotification(
                        //定义前台服务的通知点击事件
                        new ForegroundNotificationClickListener() {
                            @Override
                            public void foregroundNotificationClick(Context context, Intent intent) {
                                Log.d("JOB-->", " foregroundNotificationClick");
                            }
                        })
        );
    }
}
