package live_library.wechat2.uploadpic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;
import android.os.IBinder;

import io.reactivex.annotations.Nullable;

import static android.os.FileObserver.CREATE;

//import io.virtualapp.XApp;
//import io.virtualapp.home.log.RLog;

public class FileWatcherService1 extends Service {

    static String PATH = "/storage/emulated/0/tencent/MicroMsg/WeiXin/";
    private static String TAG = "FileWatcherService1";

    public static FileObserver fileObserver = new FileObserver(PATH, CREATE) {
        @Override
        public void onEvent(int i, @Nullable String s) {
//            RLog.d(TAG, "event:"+i+",path:"+s); //never triggered
//            try {
//                MediaStore.Images.Media.insertImage(XApp.getApp().getContentResolver(),
//                        Config.SD_WECHAT_PIC_DIR+s, "标题name", "描述desc");
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }

//            MediaScannerConnection.scanFile(XApp.getApp(),new String[]{PATH+s},
//                        null, new MediaScannerConnection.OnScanCompletedListener() {
//                            @Override
//                            public void onScanCompleted(String path, Uri uri) {
//
////                                XApp.getApp().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Config.SD_WECHAT_PIC_DIR+path)));
//
//                            }
//                        });
//            XApp.getApp().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Config.SD_WECHAT_PIC_DIR+s)));
//            XApp.getApp().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse( Config.SD_WECHAT_PIC_DIR+s)));

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    public void onDestroy() {
//        RLog.d(TAG, "onDestroy: service destroyed");
//    }

    @Override
    public void onCreate() {
        super.onCreate();
//        RLog.d(TAG, "onCreate: service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int res = super.onStartCommand(intent, flags, startId);

        fileObserver.startWatching();

//        RLog.d(TAG,"Service started!");
        return Service.START_STICKY;
    }

    public static void start(Context ctx) {
        Intent i = new Intent(ctx, FileWatcherService1.class);
        ctx.startService(i);
    }
}