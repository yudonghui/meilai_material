package live_library.wechat2.db;


import it.sauronsoftware.cron4j.Scheduler;
import jonathanfinerty.once.Once;
import live_library.wechatlog.RLog;

public class UpTask {
    private static final String TAG = "UpTask";
    Scheduler scheduler = new Scheduler();

    private boolean isRuning = false;

    private volatile static UpTask mInstance;
    private UpTask(){
        new Thread(new Runnable() {
            long preTime =0;
            @Override
            public void run() {
                while (true){
                    if (isRuning){
                        long nowTime =System.currentTimeMillis();
                        if (nowTime-preTime>10*60*1000){
                            if (!scheduler.isStarted()){
                                scheduler = new Scheduler();
                                scheduler.schedule("* * * * *", new Runnable() {
                                    public void run() {
                                        RLog.d(TAG, "Scheduler 执行了定时任务 ");
                                        try {
                                            Once.markDone(TAG);
//                                            UploadService.getInstance().startUpload();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            RLog.d(TAG, e.getMessage());
                                        }
                                    }
                                });
                                scheduler.start();
                            }
                        }
                        preTime= nowTime;
                    }
                }

            }
        }).start();
    }
    public static UpTask getInstance(){
        if (mInstance==null){
            synchronized (UpTask.class){
                if (mInstance==null){
                    synchronized (UpTask.class){
                        mInstance = new UpTask();
                    }
                }
            }
        }
        return mInstance;
    }


    public void startUpload(){
        scheduler.schedule("* * * * *", new Runnable() {
            public void run() {
                RLog.d(TAG, "Scheduler 执行了定时任务 ");
                try {
                    isRuning =true;
                    Once.markDone(TAG);
//                    UploadService.getInstance().startUpload();
                } catch (Exception e) {
                    e.printStackTrace();
                    RLog.d(TAG, e.getMessage());
                }
            }
        });
        scheduler.start();
    }


}
