package live_library.wechat2.uploadpic;

import android.os.FileObserver;

import com.blankj.utilcode.util.StringUtils;

import live_library.wechatlog.RLog;

//import io.virtualapp.XApp;

public class SDCardListener extends FileObserver {

    public SDCardListener(String path) {
        /*
         * 这种构造方法是默认监听所有事件的,如果使用 super(String,int)这种构造方法，
         * 则int参数是要监听的事件类型.
         */
        super(path);
    }

    @Override
    public void onEvent(int event, String path) {
//        switch(event) {
//            case FileObserver.ALL_EVENTS:
//                RLog.d("all", "path:"+ path);
//                MediaScannerConnection.scanFile(XApp.getApp(),new String[]{ path},
//                        null, new MediaScannerConnection.OnScanCompletedListener() {
//                            @Override
//                            public void onScanCompleted(String path, Uri uri) {
//
//                                XApp.getApp().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Config.SD_WECHAT_PIC_DIR+path)));
//
//                            }
//                        });
//                break;
//            case FileObserver.CREATE:
        if (StringUtils.isEmpty(path)){
            return;
        }
//        XApp.getApp().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Config.SD_WECHAT_PIC_DIR+path)));

        RLog.d("FileObserver", "event+:"+event+"path:"+ path);
//                MediaScannerConnection.scanFile(XApp.getApp(),new String[]{ Config.SD_WECHAT_PIC_DIR},
//                        null, new MediaScannerConnection.OnScanCompletedListener() {
//                            @Override
//                            public void onScanCompleted(String path, Uri uri) {
//
//                                XApp.getApp().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Config.SD_WECHAT_PIC_DIR+path)));
//
//                            }
//                        });
//                break;
//        }
    }
}