package live_library.wechatlog;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public abstract class BaseLogAction {
    public static final String TAG = BaseLogAction.class.getSimpleName();
    private static final String file_direction = "yes";

    private boolean canChangeFolderName;
    protected boolean saveSdcard;
    private boolean displayCommand;
    protected String folderPath;
    protected String logPath;

    public BaseLogAction() {
        canChangeFolderName = true;
        saveSdcard = false;
        displayCommand = false;
        this.folderPath = (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                file_direction + File.separator + "logs" );
        this.logPath = (this.folderPath + File.separator + getCurrentDate() + ".log");
    }

    public void putLogData(LogData log) {
        if (this.displayCommand) {
            Log.i(this.getClass().getSimpleName(), log.getTime() + ":" + log.getTag() + ":" + log.getMsg());
        }
        putStrLog(log.toString());
    }

    private String getCurrentDate() {
        DateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        return format1.format(new Date());
    }

    public void setSaveSdcard(boolean save) {
        this.saveSdcard = save;
    }

    public void setDisplayCommand(boolean displayCommand) {
        this.displayCommand = displayCommand;
    }

    public void setFolderName(String folderName) {
        if (TextUtils.isEmpty(folderName)) {
            folderName = "default";
        }
        this.canChangeFolderName = false;
        this.folderPath = (Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + file_direction
                + File.separator + "logs");
        this.logPath = (this.folderPath + File.separator + getCurrentDate() + ".log");
    }

    public boolean canChangeFolderName() {
        return this.canChangeFolderName;
    }

    public abstract void putStrLog(String logStr); // 记录日志
    public abstract void destroy(); // 资源释放
}
