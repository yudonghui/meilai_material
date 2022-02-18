package live_library.wechat2;

public class ConfigWechat {

    public final static String staticHost = "http://wechat.shmylike.com:8080";
//    public final static String staticHost = "http://172.16.63.180:8083";//敬爱的兰总


    public final static String uploadFilePicOSS = "http://oss.mylikesh.cn/server/oss/material/1000/uploadMaterial";
//    public final static String uploadFilePicOSS = "http://172.16.63.218:8081/1000/uploadMaterial";
    public final static String uploadFileAmrOSS = "http://oss.mylikesh.cn/voice/server/oss/material/voice/uploadMaterial";

    public final static String uploadPicAddresss = staticHost+ "/wechat-record-admin/reflect/file/";
    public final static String loginRecord = staticHost+ "/wechat-record-admin/loginRecord/";
    public final static String uploadAmrAddresss =  staticHost+"/wechat-record-admin/reflect/voice/";
    public final static String insertWeChatRecord = staticHost+"/wechat-record-admin/insertWeChatRecord";
    public final static String snsUpload = staticHost+"/wechat-record-admin/sns/upload";
    public final static String getAreaList = "http://monitor.shmylike.com/groupCtrlApi/wechat-record-admin/getAreaList/";
    public final static String setArea = "http://monitor.shmylike.com/groupCtrlApi/wechat-record-admin/editWeChatBindingByArea/app";

}
