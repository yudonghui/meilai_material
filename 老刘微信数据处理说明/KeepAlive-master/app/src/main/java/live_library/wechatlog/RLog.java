package live_library.wechatlog;

import android.os.Environment;
import android.text.TextUtils;

import com.blankj.utilcode.BuildConfig;
import com.blankj.utilcode.util.AppUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//import com.mylike.live_library.BuildConfig;


public class RLog {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    private static SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static String sCurrentDate;
    private static LogAction sLogAction;
    private static boolean versionWrite = false;

    static {
        sLogAction = LogAction.getInstance();
        sLogAction.setDisplayCommand(true);
        // save sdcard config
        sLogAction.setSaveSdcard(true);
        sCurrentDate = getCurrentDate();
        sLogAction.initLogThread();
    }

    public static void d(String tag, String format, Object... param) {
        d(tag, String.format(format, param));
    }

    public static void v(String tag, String format, Object... param) {
        v(tag, String.format(format, param));
    }

    public static void i(String tag, String format, Object... param) {
        i(tag, String.format(format, param));
    }

    public static void w(String tag, String format, Object... param) {
        w(tag, String.format(format, param));
    }

    public static void e(String tag, String format, Object... param) {
        e(tag, String.format(format, param));
    }

    public static void d(String tag, String msg) {
        log("[d]" + tag, msg);
    }

    public static void v(String tag, String msg) {
        log("[v]" + tag, msg);
    }

    public static void i(String tag, String msg) {
        log("[i]" + tag, msg);
    }

    public static void w(String tag, String msg) {
        log("[w]" + tag, msg);
    }

	public static void e(String tag, String msg) {
        log("[e]" + tag, msg);
    }


    public static String getCurrentDate() {
        return format.format(new Date());
    }

    private static void writeLog(String tag, String msg) {
        if(!TextUtils.isEmpty(msg) && !TextUtils.isEmpty(tag)) {
            LogData log = new LogData();
            log.setTag(tag);
            log.setMsg(msg);
            log.setTime(format1.format(new Date()));
            log.setType(10);
            sLogAction.putLogData(log);
        }
    }

    private static void log(String tag, String msg) {
        if (!versionWrite || !TextUtils.equals(sCurrentDate, getCurrentDate())) {
            sCurrentDate = getCurrentDate();
//            sLogAction.setFolderName(FOLDER_NAME);
            writeLog("KeepAlive版本号: ", AppUtils.getAppVersionName());
            versionWrite = true;
        }
        writeLog(tag, msg);
    }


    // 刷新内存中日志
    public static void logFlush() {
        for (int i=0; i<10; i++) {
            writeEmptyLog();
        }
    }

    public static void writeEmptyLog(){
        sLogAction.putStrLog("");
    }


    public static String rootPath() {
        String rootPath = Environment.getExternalStorageDirectory().getPath();
        if (!rootPath.endsWith("/")) {
            rootPath += "/";
        }
        return rootPath;
    }
}