package live_library.wechat2.helper;

import android.content.SharedPreferences;

import com.blankj.utilcode.util.AppUtils;
import com.mylike.keepalive.XApp;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import live_library.wechat2.bean.UserInfo;

import static android.content.Context.MODE_PRIVATE;

public class UserInfoHelper {
    public static List<UserInfo> getUserInfoList(SQLiteDatabase db){
        SharedPreferences mSharedPreferences1 =  XApp.getApp().getSharedPreferences("yes_getImei", MODE_PRIVATE);
        String RegId = mSharedPreferences1.getString("RegId","");
        Cursor cursor = db.query("userinfo", new String[]{"id", "value"}, "id>? and id<?", new String[]{"0", "7"}, null, null, null);
        List<UserInfo> userInfos =new ArrayList<>();
        while (cursor.moveToNext()) {
            UserInfo userInfo =new UserInfo();
            String serverNickId = cursor.getString(cursor.getColumnIndex("id"));
            String serverValue = cursor.getString(cursor.getColumnIndex("value"));
            userInfo.id=serverNickId;
            userInfo.value=serverValue;
            userInfos.add(userInfo);
//                if ("2".equals(serverNickId)) {
//                    busiWeChatRecord.setServerWxId(serverValue);
//                } else if ("4".equals(serverNickId)) {
//                    busiWeChatRecord.setServerNickName(serverValue);
//                } else if ("6".equals(serverNickId)) {
//                    busiWeChatRecord.setServerPhone(serverValue);
//                }
        }

        cursor.close();
        return userInfos;
    }

    public static int getTotalCount(SQLiteDatabase db){
        Cursor cursor =db.rawQuery("select count(*) from userinfo", null);  //db.query("SnsInfo",new String[]{"snsId"} , null, null, null, null, null,null);
        int count =0;
        while (cursor.moveToNext()) {
            count =cursor.getInt(0);
        }

        cursor.close();
        return count;
    }
}
