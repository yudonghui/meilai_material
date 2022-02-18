package live_library;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import live_library.config.ForegroundNotification;
import live_library.config.KeepAliveConfig;
import live_library.config.NotificationUtils;
import live_library.config.RunMode;
import live_library.service.JobHandlerService;
import live_library.service.LocalService;
import live_library.service.RemoteService;
import live_library.utils.KeepAliveUtils;
import live_library.utils.SPUtils;

import static live_library.config.KeepAliveConfig.SP_NAME;

/**
 * 进程保活管理
 */
public class KeepAliveManager {
    private static final String TAG = "KeepAliveManager";

    /**
     * 启动保活
     *
     * @param application            your application
     * @param runMode
     * @param foregroundNotification 前台服务
     */
    public static void toKeepAlive(@NonNull Application application, @NonNull int runMode, String title, String content, int res_icon, ForegroundNotification foregroundNotification) {
        if (KeepAliveUtils.isRunning(application)) {
            KeepAliveConfig.foregroundNotification = foregroundNotification;
            SPUtils.getInstance(application, SP_NAME).put(KeepAliveConfig.TITLE, title);
            SPUtils.getInstance(application, SP_NAME).put(KeepAliveConfig.CONTENT, content);
            SPUtils.getInstance(application, SP_NAME).put(KeepAliveConfig.RES_ICON, res_icon);
            SPUtils.getInstance(application, SP_NAME).put(KeepAliveConfig.RUN_MODE, runMode);
            //优化后的枚举
            RunMode.setShape(runMode);
            KeepAliveConfig.runMode = RunMode.getShape();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //启动定时器，在定时器中启动本地服务和守护进程
                JobHandlerService.startJob(application);
            } else {
                Intent localIntent = new Intent(application, LocalService.class);
                //启动守护进程
                Intent guardIntent = new Intent(application, RemoteService.class);
                if (Build.VERSION.SDK_INT >= 26) {
                    application.startForegroundService(localIntent);
                    application.startForegroundService(guardIntent);
                } else {
                    application.startService(localIntent);
                    application.startService(guardIntent);
                }
            }
        }
    }

    public static void stopWork(Application application) {
        try {
            KeepAliveConfig.foregroundNotification = null;
            KeepAliveConfig.keepLiveService = null;
            KeepAliveConfig.runMode = RunMode.getShape();
            JobHandlerService.stopJob();
            //启动本地服务
            Intent localIntent = new Intent(application, LocalService.class);
            //启动守护进程
            Intent guardIntent = new Intent(application, RemoteService.class);
            application.stopService(localIntent);
            application.stopService(guardIntent);
            application.stopService(new Intent(application, JobHandlerService.class));
        } catch (Exception e) {
            Log.e(TAG, "stopWork-->" + e.getMessage());
        }
    }


    public static void sendNotification(Context context, String title, String content, int icon, Intent intent2) {
        NotificationUtils.sendNotification(context, title, content, icon, intent2);
    }

    /**
     * 启动系统保活
     * @param cex
     */
    public static void launcherSyskeepAlive(Context cex){
        DevicesLaunchConfig.launchSystemKeepAlive(cex);
    }

    /**
     * 启动电量优化
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void batteryOptimizations(Context context) {
        if (!isIgnoringBatteryOptimizations(context)) {
            requestIgnoreBatteryOptimizations(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean isIgnoringBatteryOptimizations(Context context) {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return isIgnoring;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void requestIgnoreBatteryOptimizations(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
