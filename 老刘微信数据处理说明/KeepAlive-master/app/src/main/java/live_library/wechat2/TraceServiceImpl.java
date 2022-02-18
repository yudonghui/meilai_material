package live_library.wechat2;

import android.content.Intent;
import android.os.IBinder;

import com.blankj.utilcode.util.SPUtils;
//import com.xdandroid.hellodaemon.AbsWorkService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import live_library.wechat2.db.SqlDataUtil1;
import live_library.wechatlog.RLog;

/**
 * @author zhengluping
 * @date 2017/12/22
 */
public class TraceServiceImpl
//        extends AbsWorkService
{
//    private final static String TAG = "TraceServiceImpl";
//    /**
//     * 是否 任务完成, 不再需要服务运行?
//     */
//    public static boolean sShouldStopService;
//    public static Disposable sDisposable;
//
//    public static void stopService() {
//        //我们现在不再需要服务运行了, 将标志位置为 true
//        sShouldStopService = true;
//        //取消对任务的订阅
//        if (sDisposable != null) {
//            sDisposable.dispose();
//        }
//        //取消 Job / Alarm / Subscription
//        cancelJobAlarmSub();
//    }
//
//    /**
//     * 是否 任务完成, 不再需要服务运行?
//     *
//     * @return 应当停止服务, true; 应当启动服务, false; 无法判断, 什么也不做, null.
//     */
//    @Override
//    public Boolean shouldStopService(Intent intent, int flags, int startId) {
//        return sShouldStopService;
//    }
//
//    @Override
//    public void startWork(Intent intent, int flags, int startId) {
//        RLog.i(TAG, "默认启动一次");
//        // UploadService.getInstance().startUpload();
//        sDisposable = Observable
//                .interval(30, TimeUnit.SECONDS)
//                //取消任务时取消定时唤醒
//                .doOnDispose(() -> {
//                    RLog.i(TAG, "保存数据到磁盘。");
//                    cancelJobAlarmSub();
//                })
//                .subscribe(count -> {
//                    Date date = new Date();
//                    SimpleDateFormat format = new SimpleDateFormat("HH");
//                    String hourStr = format.format(date);
//                    int hour = Integer.parseInt(hourStr);
//                    RLog.d(TAG, "当前时间-hour:" + hour);
//                    RLog.i(TAG, "每 25 分钟采集一次数据... count = " + count);
//                    if (hour > 22 || hour < 8) {
//                        return;
//                    }
//                    long times = SPUtils.getInstance().getLong("times");
//                    long times_other = SPUtils.getInstance().getLong("times_other");
//                    long currentTimes = System.currentTimeMillis();
//                    if (times < 1) {//第一次
//                        long time = System.currentTimeMillis();
//                        SPUtils.getInstance().put("times", time);
//                        SPUtils.getInstance().put("times_other", time);
//                        RLog.d(TAG, "onCreate-times-第一次保存的时间：" + time);
////                    } else if ((currentTimes - times) > (40 * 60 * 1000)) {//大于20分钟
//                    } else if ((currentTimes - times) > (1  *60* 1000)) {//大于20分钟
//                        SPUtils.getInstance().put("times", currentTimes);
//                        RLog.i(TAG, "sleep-start: " + System.currentTimeMillis());
//                        Thread.sleep(2 *60* 1000);
//                        RLog.i(TAG, "sleep-end: " + System.currentTimeMillis());
//
////                        UploadService.getInstance().startUpload();
////                        SqlDataUtil.copyFile(SqlDataUtil.WX_ROOT_PATH+SqlDataUtil.WX_DB_FILE_NAME_sns, SqlDataUtil.CURR_APK_PATH + "wx_data1.db");
////                            File file = new File(SqlDataUtil1.WX_DB_DIR_PATH);
////                            if (file!=null&&file.exists()) {
//                        RLog.i(TAG, "准备上传第一个微信，此时时间: " + currentTimes);
////                                UploadService1.getInstance().startUpload();
////                            }
//                    } else if ((currentTimes - times_other) > (27*60 * 1000)) {//大于25分钟
//                        SPUtils.getInstance().put("times_other", currentTimes);
//                        Thread.sleep(3 *60* 1000);
//                        File file = new File(SqlDataUtil1.WX_DB_DIR_PATH());
//                        if (file != null && file.exists()) {
//                            RLog.i(TAG, "发现另一个微信: " + currentTimes);
//                            UploadService1.getInstance().startUpload();
//                        }
//                        RLog.i(TAG, "准备上传第二个微信，此时时间: " + currentTimes);
//                    }
////                    else if (count > 0 && count % 40 == 0) {
////                        SPUtils.getInstance().put("times", System.currentTimeMillis());
//////                        submitData();
////                        UploadService.getInstance().startUpload();
//////                        File file = new File(SqlDataUtil1.WX_DB_DIR_PATH);
//////                        if (file!=null&&file.exists()) {
//////                            RLog.i(TAG, "发现另一个微信: " );
//////                            UploadService1.getInstance().startUpload();
//////                        }
////                    }else if (count > 0 && count % 50 == 0) {
//////                        submitData();
//////                        UploadService.getInstance().startUpload();
////                        File file = new File(SqlDataUtil1.WX_DB_DIR_PATH);
////                        if (file!=null&&file.exists()) {
////                            SPUtils.getInstance().put("times_other", System.currentTimeMillis());
////                            RLog.i(TAG, "发现另一个微信: " );
////                            UploadService1.getInstance().startUpload();
////                        }
////                    }
//
//                });
//    }
//
//    @Override
//    public void stopWork(Intent intent, int flags, int startId) {
//        stopService();
//    }
//
//    /**
//     * 任务是否正在运行?
//     *
//     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
//     */
//    @Override
//    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
//        //若还没有取消订阅, 就说明任务仍在运行.
//        return sDisposable != null && !sDisposable.isDisposed();
//    }
//
//    @Override
//    public IBinder onBind(Intent intent, Void v) {
//        return null;
//    }
//
//    @Override
//    public void onServiceKilled(Intent rootIntent) {
//        RLog.i(TAG, "保存数据到磁盘。");
//    }
}
