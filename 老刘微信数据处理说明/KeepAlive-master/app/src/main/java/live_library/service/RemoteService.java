package live_library.service;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.mylike.keeplive.KeepAliveAidl;
import com.mylike.keepalive.R;

import live_library.KeepAliveManager;
import live_library.config.ForegroundNotification;
import live_library.config.ForegroundNotificationClickListener;
import live_library.config.KeepAliveConfig;
import live_library.config.NotificationUtils;
import live_library.receive.NotificationClickReceiver;
import live_library.utils.SPUtils;
import live_library.wechatlog.RLog;

import static live_library.config.KeepAliveConfig.SP_NAME;
import static live_library.config.RunMode.HIGH_POWER_CONSUMPTION;


/**
 * 守护进程
 */
@SuppressWarnings(value = {"unchecked", "deprecation"})
public final class RemoteService extends Service {
    private RemoteBinder mBilder;
    private String TAG = "RemoteService";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, " onCreate");
        if (mBilder == null) {
            mBilder = new RemoteBinder();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBilder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            this.bindService(new Intent(RemoteService.this, LocalService.class),
                    connection, Context.BIND_ABOVE_CLIENT);

            shouDefNotify();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return START_STICKY;
    }

    private void shouDefNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            KeepAliveConfig.CONTENT = SPUtils.getInstance(getApplicationContext(), SP_NAME).getString(KeepAliveConfig.CONTENT);
            KeepAliveConfig.DEF_ICONS = SPUtils.getInstance(getApplicationContext(), SP_NAME).getInt(KeepAliveConfig.RES_ICON, R.mipmap.icon);
            KeepAliveConfig.TITLE = SPUtils.getInstance(getApplicationContext(), SP_NAME).getString(KeepAliveConfig.TITLE);
            String title = SPUtils.getInstance(getApplicationContext(), SP_NAME).getString(KeepAliveConfig.TITLE);
            RLog.d("JOB-->"+TAG,"KeepAliveConfig.CONTENT_"+ KeepAliveConfig.CONTENT+"    " + KeepAliveConfig.TITLE+"  "+title);
            if (!TextUtils.isEmpty(KeepAliveConfig.TITLE) && !TextUtils.isEmpty( KeepAliveConfig.CONTENT)) {
                //启用前台服务，提升优先级
                Intent intent2 = new Intent(getApplicationContext(), NotificationClickReceiver.class);
                intent2.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
                Notification notification = NotificationUtils.createNotification(RemoteService.this, KeepAliveConfig.TITLE, KeepAliveConfig.CONTENT, KeepAliveConfig.DEF_ICONS, intent2);
                startForeground(KeepAliveConfig.FOREGROUD_NOTIFICATION_ID, notification);
                RLog.d("JOB-->", TAG + "显示通知栏");
                start();
            }
        }
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
                                RLog.d("JOB-->", TAG +" 开始启动保活");
                            }
                        })
        );
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private final class RemoteBinder extends KeepAliveAidl.Stub {

        @Override
        public void wakeUp(String title, String discription, int iconRes) throws RemoteException {
            Log.i(TAG, " wakeUp");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (title != null || discription != null) {
                    KeepAliveConfig.CONTENT = title;
                    KeepAliveConfig.DEF_ICONS = iconRes;
                    KeepAliveConfig.TITLE = discription;
                } else {
                    KeepAliveConfig.CONTENT = SPUtils.getInstance(getApplicationContext(), SP_NAME).getString(KeepAliveConfig.CONTENT);
                    KeepAliveConfig.DEF_ICONS = SPUtils.getInstance(getApplicationContext(), SP_NAME).getInt(KeepAliveConfig.RES_ICON, R.mipmap.icon);
                    KeepAliveConfig.TITLE = SPUtils.getInstance(getApplicationContext(), SP_NAME).getString(KeepAliveConfig.TITLE);

                }
                if (KeepAliveConfig.TITLE != null && KeepAliveConfig.CONTENT != null) {
                    //启用前台服务，提升优先级
                    Intent intent2 = new Intent(getApplicationContext(), NotificationClickReceiver.class);
                    intent2.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
                    Notification notification = NotificationUtils.createNotification(RemoteService.this, KeepAliveConfig.TITLE, KeepAliveConfig.CONTENT, KeepAliveConfig.DEF_ICONS, intent2);
                    startForeground(KeepAliveConfig.FOREGROUD_NOTIFICATION_ID, notification);
                    Log.d("JOB-->", TAG + "2 显示通知栏");
                }
            }
        }
    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Intent remoteService = new Intent(RemoteService.this,
                    LocalService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                RemoteService.this.startForegroundService(remoteService);
            } else {
                RemoteService.this.startService(remoteService);
            }
            RemoteService.this.bindService(new Intent(RemoteService.this,
                    LocalService.class), connection, Context.BIND_ABOVE_CLIENT);
            PowerManager pm = (PowerManager) RemoteService.this.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();
            if (isScreenOn) {
                sendBroadcast(new Intent("_ACTION_SCREEN_ON"));
            } else {
                sendBroadcast(new Intent("_ACTION_SCREEN_OFF"));
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            shouDefNotify();
        }
    };

}
