package com.android.keepalivetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.simple.SimpleResponse;

import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                /**知道要跳转应用的包命与目标Activity*/
                ComponentName componentName = new ComponentName("com.mylike.keepalive","live_library.onepx.OnePixelActivity");
                intent.setComponent(componentName);
                //这里Intent传值
                Bundle bundle = new Bundle();
                bundle.putString("KEY", "1");
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity(intent);
            }
        });
        findViewById(R.id.check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVersionNet();
            }
        });
        findViewById(R.id.getVersionCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int vcode = getPackageCode(MainActivity.this,"com.mylike.keepalive");
                Toast.makeText(MainActivity.this,"新美莱服务版本号："+vcode,Toast.LENGTH_LONG).show();
            }
        });
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
                            int Code = jsonObject.optInt("Code");//code=1:有版本更新，code=0无版本更新
                            if (Code==1) {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                /**知道要跳转应用的包命与目标Activity*/
                                ComponentName componentName = new ComponentName("com.mylike.keepalive", "com.mylike.keepalive.MainActivity");
                                intent.setComponent(componentName);
                                //这里Intent传值
                                Bundle bundle = new Bundle();
//            bundle.putString("KEY", "你好");
                                intent.putExtras(bundle);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } catch (Exception e) {

                        }

                    }
                });
    }

}
