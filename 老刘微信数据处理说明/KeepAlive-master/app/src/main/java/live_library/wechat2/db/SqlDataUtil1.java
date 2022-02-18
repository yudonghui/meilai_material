package live_library.wechat2.db;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.mylike.keepalive.XApp;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

import live_library.wechat2.UploadService;
import live_library.wechatlog.RLog;

import static live_library.wechatutils.RootUtil.getDbPassword;

//import static io.virtualapp.home.wechat.SqlDataUtil.getDbPassword;

/**
 * @author zhengluping
 * @date 2017/12/22
 */
@SuppressLint("SdCardPath")
public class SqlDataUtil1 {
    /**
     * 复制数据库路径
     */
    public static final String COPY_WX_DATA_DB = "wx_data1.db";
    public static final String COPY_WX_DATA_DB_WAL = "wx_data1.db-wal";
    public static final String COPY_WX_DATA_DB_SHM = "wx_data1.db-shm";
    public static final String COPY_WX_DATA_DB_sns = "wx_data_sns1.db";
    public static final String COPY_WX_DATA_DB_WAL_sns = "wx_data_sns1.db-wal";
    public static final String COPY_WX_DATA_DB_SHM_sns = "wx_data_sns1.db-shm";
    public static final String COPY_WX_DATA_DB_SHM_ini = "wx_data_sns1.db.ini";
    public static final String WX_DB_FILE_NAME = "EnMicroMsg.db";
    public static final String WX_DB_FILE_NAME_WAL = "EnMicroMsg.db-wal";
    public static final String WX_DB_FILE_NAME_SHM = "EnMicroMsg.db-shm";
    public static final String WX_DB_FILE_NAME_Index = "WxFileIndex.db";
    public static final String WX_DB_FILE_NAME_Index_WAL = "WxFileIndex.db-wal";
    public static final String WX_DB_FILE_NAME_Index_SHM = "WxFileIndex.db-shm";
    public static final String WX_DB_FILE_NAME_Index1 = "WxFileIndex1.db";
    public static final String WX_DB_FILE_NAME_Index_WAL1 = "WxFileIndex1.db-wal";
    public static final String WX_DB_FILE_NAME_Index_SHM1 = "WxFileIndex1.db-shm";
    public static final String WX_DB_FILE_NAME_sns = "SnsMicroMsg.db";
    public static final String WX_DB_FILE_NAME_WAL_sns = "SnsMicroMsg.db-wal";
    public static final String WX_DB_FILE_NAME_SHM_sns = "SnsMicroMsg.db-shm";
    public static final String WX_DB_FILE_NAME_SHM_ini = "SnsMicroMsg.db.ini";
    public static final String CURR_APK_PATH = "/data/data/com.mylike.keepalive/";
//    public static final String WX_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()+"/tencent1/";
//    public static final String WX_ROOT = "/data/data/com.mylike.keepalive/tencent1/";
    public static final String WX_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.mylike.keepalive/files/tencent1/";
    private static final String WX_ROOT_PATH1 = WX_ROOT+"com.tencent.mm/";
//    public static final String WX_ROOT_PATH = WX_ROOT;
//    public static final String WX_DB_DIR_PATH = WX_ROOT_PATH + "MicroMsg/";
//    public static final String WX_SP_UIN_PATH = WX_ROOT_PATH + "shared_prefs/auth_info_key_prefs.xml";
//    private static final String WX_SP_IMEI_PATH = WX_ROOT_PATH + "shared_prefs/DENGTA_META.xml";
    private final static String TAG = "SQLDataUtil-1";

    public static String WX_ROOT_PATH() {
        String path = WX_ROOT_PATH1;
        if (new File(path).exists()) {
            return path;
        }else {
            path = WX_ROOT;
        }
        return path;
    }
    public static String WX_DB_DIR_PATH(){
        return WX_ROOT_PATH() + "MicroMsg/";
    }
    public static String WX_SP_UIN_PATH(){
        return WX_ROOT_PATH() + "shared_prefs/auth_info_key_prefs.xml";
    }
    public static String WX_SP_IMEI_PATH(){
        return WX_ROOT_PATH() + "shared_prefs/DENGTA_META.xml";
    }
    public static String WX_SP_IMEI_PATH1(){
        return WX_ROOT_PATH() + "shared_prefs/beacontbs_DENGTA_META.xml";
    }
    public static String WX_SP_IMEI_PATH2(){
        return WX_ROOT_PATH() + "shared_prefs/exdevice_pref.xml";
    }
    /**
     * 获取图片路径（图片地址）
     */
    public static final String WX_IMG_PATH = "/sdcard/tencent/MicroMsg/";

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            File oldFile = new File(oldPath);
            //文件存在时
            if (oldFile.exists()) {
                //读入原文件
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while (inStream.read(buffer) != -1) {
                    fs.write(buffer);
                }
                inStream.close();
                fs.close();
            }
        } catch (Exception e) {
            Log.d("WeChatUtil", "复制单个文件操作出错" + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * 获取微信的uid
     * 微信的uid存储在SharedPreferences里面
     * 存储位置\data\data\com.tencent.mm\shared_prefs\auth_info_key_prefs.xml
     */
    public static String getCurrWxUin() {
        String mCurrWxUin = null;
        File file = new File(WX_SP_UIN_PATH());
//        File file = new File("/data/user/999/com.tencent.mm/shared_prefs/auth_info_key_prefs.xml");
        try {
            if (file.exists()){
                FileInputStream in = new FileInputStream(file);
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(in);
                Element root = document.getRootElement();
                List<Element> elements = root.elements();
                for (Element element : elements) {
                    if ("_auth_uin".equals(element.attributeValue("name"))) {
                        mCurrWxUin = element.attributeValue("value");
                    }
                }
            }

        } catch (Exception e) {
//            new MainActivity().setLog("获取微信uid失败，请检查auth_info_key_prefs文件权限");
            Log.d("WeChatUtil1", "获取微信uid失败，请检查auth_info_key_prefs文件权限" + e.toString());
//            e.printStackTrace();
        }
        RLog.d(TAG, "mCurrWxUin-1---："+mCurrWxUin);
        return mCurrWxUin;
    }
    public static String getCurrWxIMEI() {
        String mCurrWxIMEI = "";
        File file = new File(WX_SP_IMEI_PATH());
        try {
            if (file.exists()){
                FileInputStream in = new FileInputStream(file);
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(in);
                Element root = document.getRootElement();
                List<Element> elements = root.elements();
                for (Element element : elements) {
                    if ("IMEI_DENGTA".equals(element.attributeValue("name"))) {
                        mCurrWxIMEI =  element.attributeValue("value");
//                        RLog.d("WeChatUtil", "获取微信IMEI成功，IMEI=" + mCurrWxIMEI);
                    }
                    RLog.d("SqlDataUtil-1", "IMEI_DENGTA--name：" + element.attributeValue("name"));
                    RLog.d("SqlDataUtil-1", "IMEI_DENGTA--value：" + element.attributeValue("value"));
                }
            }
//            RLog.d("SqlDataUtil", "获取微信IMEI时，DENGTA_META不存在");

        } catch (Exception e) {
//            new MainActivity().setLog("获取微信IMEI失败，请检查auth_info_key_prefs文件权限");
//            RLog.d("SqlDataUtil", "获取微信IMEI失败，请检查DENGTA_META文件权限" + e.toString());
//            e.printStackTrace();
        }
        if (!StringUtils.isEmpty(mCurrWxIMEI)){
            mCurrWxIMEI.toUpperCase();
        }

        return mCurrWxIMEI;
    }
    public static String getCurrWxIMEI1() {
        String mCurrWxIMEI = "";
        File file = new File(WX_ROOT_PATH()+"files/KeyInfo.bin");
        try{
            if (file.exists()) {
                SecretKeySpec secretKeySpec = new SecretKeySpec("_wEcHAT_".getBytes(), "RC4");
                Cipher instance = Cipher.getInstance("RC4");
                instance.init(2, secretKeySpec);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new CipherInputStream(new FileInputStream(file), instance)));
                while (true) {
                    try {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            break;
                        }
                        mCurrWxIMEI = readLine;
                        RLog.d("KeyInfo-1--mCurrWxIMEI:", readLine);
                    } catch (Exception e2) {

                    }
                }
            }
        } catch (Exception e3) {

        }

        if (!StringUtils.isEmpty(mCurrWxIMEI)){
            mCurrWxIMEI.toUpperCase();
        }

        return mCurrWxIMEI;
    }
    public static String getKeyValue(String path) {
        String mCurrWxUin = null;
        File file = new File(path);
        FileInputStream fis = null;
        try {
            fis =   new FileInputStream(file);
            if (file.exists()){
                ObjectInputStream ois = new ObjectInputStream(fis);
                Map<Integer,Object> maps = (Map<Integer, Object>) ois.readObject();
                for (Integer key: maps.keySet()) {
                    Log.d("WeChatUtil",path);
                    Log.d("WeChatUtil", "key====" + key+"==value:"+maps.get(key));
                }
            }

        } catch (Exception e) {
//            new MainActivity().setLog("获取微信uid失败，请检查auth_info_key_prefs文件权限");
            RLog.d("WeChatUtil-1", "获取微信uid失败，请检查auth_info_key_prefs文件权限" + e.toString());
            e.printStackTrace();
        }
        return mCurrWxUin;
    }

    /**
     * 递归查询微信本地数据库文件
     *
     * @param file     目录
     * @param fileName 需要查找的文件名称
     */
    public static File searchFile(File file, String fileName) {
        File[] files = file.listFiles();
        if (files == null) {
//            new MainActivity().setLog("复制数据库出错");
        }
        if (files != null) {
            for (File childFile : files) {
                if (fileName.equals(childFile.getName())) {
                    return childFile;
                }
            }
        }

        return null;
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
     * 连接数据库
     */
    public static SQLiteDatabase openWxDb(File dbFile, String imei) {
        SQLiteDatabase.loadLibs(UploadService.getContext());
        SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
            @Override
            public void preKey(SQLiteDatabase database) {
            }

            @Override
            public void postKey(SQLiteDatabase database) {
                //兼容2.0的数据库
                database.rawExecSQL("PRAGMA cipher_migrate;");
            }

        };
        try {
            String password = getDbPassword(imei, getCurrWxUin());
            //打开数据库连接
            return SQLiteDatabase.openOrCreateDatabase(dbFile, password, null, hook);
        } catch (Exception e) {
            Log.d("WeChatUtil-1", "读取数据库信息失败" + e.toString());
            e.printStackTrace();
            return null;
        }
    }
    public static String testCommonWxDb(File dbFile, String imei) {
        SQLiteDatabase.loadLibs(UploadService.getContext());
        SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
            @Override
            public void preKey(SQLiteDatabase database) {
            }

            @Override
            public void postKey(SQLiteDatabase database) {
                //兼容2.0的数据库
                database.rawExecSQL("PRAGMA cipher_migrate;");
            }

        };

        try {//5078aa1   255277677194494
//            String password = getDbPassword(ACache.get(XApp.getApp()).getAsString(CACHE_PHONE_IMEI), getCurrWxUin());
            String password = getDbPassword(imei, getCurrWxUin());//imei：99001068505792 867392030135845   867392030135852  654313269
//            String password = getDbPassword("99001206362065", "2142341683");//imei：99001068505792 867392030135845   867392030135852  654313269
            Log.d("TAG", "test-1密码====" + password);
            RLog.i(TAG,"test-1密码====" + password );
            if (StringUtils.isEmpty(password))return  null;
//            SQLiteDatabase.releaseMemory();
            //打开数据库连接
            SQLiteDatabase db1 = SQLiteDatabase.openOrCreateDatabase(dbFile, password, null, hook);
            RLog.i(TAG,"test-1 Open SQLiteDatabase 成功！" );
            db1.close();
            return "打开数据库成功";
            //微信数据库路径:/data/data/io.va.exposed/virtual/data/user/0/com.tencent.mm/MicroMsg/21b7c922df1e19176ece481a7137e508/EnMicroMsg.db-shm
        } catch (Exception e) {
            RLog.d("WeChatUtil-1-test-1", "读取数据库信息失败-1" + e.toString());
//            RLog.i(TAG,"test-1 Open SQLiteDatabase 失败： " + e.toString());
            e.printStackTrace();
            return "打开数据库失败-1"+e.toString();
        }
    }
    public static void updateTableStamp(String tableName,String updateTime){
        String userFileName = SqlDataUtil1.md5("mm" + SqlDataUtil1.getCurrWxUin());
        File file= new File(Environment.getExternalStorageDirectory()+"/yes/"+userFileName+".txt");
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String text =  FileIOUtils.readFile2String(file);
        if (!TextUtils.isEmpty(text)){
            Map<String,String> map = new Gson().fromJson(text,HashMap.class);
            map.put(tableName,updateTime);
            FileIOUtils.writeFileFromString(file,new Gson().toJson(map));
        }else {
            Map<String,String> map = new HashMap<>();
            map.put(tableName,updateTime);
            FileIOUtils.writeFileFromString(file,new Gson().toJson(map));
        }
    }


    public static Map<String,String> getTableStamps(){
        String userFileName = SqlDataUtil1.md5("mm" + SqlDataUtil1.getCurrWxUin());
        File file= new File(Environment.getExternalStorageDirectory()+"/yes/"+userFileName+".txt");
        String text =  FileIOUtils.readFile2String(file);
        if (!TextUtils.isEmpty(text)){
            Map<String,String> map = new Gson().fromJson(text,HashMap.class);
            return map;
        }
        return new HashMap<>();
    }


    /**
     * 连接数据库
     */
    public static SQLiteDatabase openCommonWxDb(File dbFile, String imei) {
        SQLiteDatabase.loadLibs(XApp.getApp());
        SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
            @Override
            public void preKey(SQLiteDatabase database) {
            }

            @Override
            public void postKey(SQLiteDatabase database) {
                //兼容2.0的数据库
                database.rawExecSQL("PRAGMA cipher_migrate;");
            }
        };

        try {//5078aa1   255277677194494
//            String password = getDbPassword(ACache.get(XApp.getApp()).getAsString(CACHE_PHONE_IMEI), getCurrWxUin());
            String password = getDbPassword(imei, getCurrWxUin());//imei：99001068505792 867392030135845   867392030135852  654313269
//            Log.d("WeChatUtil", "密码====" + password);
//            RLog.i(TAG,"密码====" + password );
            RLog.i(TAG,"另一个微信密码====" + password );
            RLog.i(TAG,"另一个微信imei====" + imei );
            RLog.i(TAG,"另一个微信Uin====" + getCurrWxUin() );
            if (StringUtils.isEmpty(password)){
                return null;
            }
//            SQLiteDatabase.releaseMemory();
            //打开数据库连接
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, password, null, hook);
            RLog.i(TAG," Open SQLiteDatabase-1 成功！" );
            return db;
            //微信数据库路径:/data/data/io.va.exposed/virtual/data/user/0/com.tencent.mm/MicroMsg/21b7c922df1e19176ece481a7137e508/EnMicroMsg.db-shm
        } catch (Exception e) {
            Log.d("WeChatUtil-1", "读取数据库信息失败" + e.toString());
            RLog.i(TAG," Open SQLiteDatabase 失败-1： " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 连接数据库
     */
    public static SQLiteDatabase openWxDb2(File dbFile, String imei) {

        SQLiteDatabase.loadLibs(UploadService.getContext());
        SQLiteDatabaseHook hook = new SQLiteDatabaseHook() {
            @Override
            public void preKey(SQLiteDatabase database) {
            }

            @Override
            public void postKey(SQLiteDatabase database) {
                //兼容2.0的数据库
                database.rawExecSQL("PRAGMA cipher_migrate;");
            }
        };
        try {
//            String password =  getDbPassword(imei, getCurrWxUin());
            String password ="3481ab3" ;//getDbPassword(imei, getCurrWxUin());
            //打开数据库连接
//            SQLiteDatabase db = net.sqlcipher.database.SQLiteDatabase.openOrCreateDatabase(dbFile.getPath(), password,null, SQLiteDatabase.OPEN_READWRITE);
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, password, null, hook);
            return db;
        } catch (Exception e) {
//            new MainActivity().setLog("读取数据库信息失败");
            RLog.d("WeChatUtil-1", "读取数据库信息失败" + e.toString());
            e.printStackTrace();
            return null;
        }
    }


}
