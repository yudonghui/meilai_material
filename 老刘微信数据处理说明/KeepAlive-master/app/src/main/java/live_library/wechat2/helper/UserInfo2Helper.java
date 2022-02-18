package live_library.wechat2.helper;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import live_library.wechat2.bean.UserInfo2;

public class UserInfo2Helper {
    public static List<UserInfo2> getUserInfo2List(SQLiteDatabase db){
        Cursor cursor = db.query("userinfo2", null, null, null, null, null, null);
        List<UserInfo2> userInfo2s =new ArrayList<>();
        while (cursor.moveToNext()) {
            UserInfo2 userInfo2 =new UserInfo2();
            userInfo2.sid = cursor.getString(cursor.getColumnIndex("sid"));
            userInfo2.type = cursor.getString(cursor.getColumnIndex("type"));
            userInfo2.value = cursor.getString(cursor.getColumnIndex("value"));
            userInfo2s.add(userInfo2);
        }

        cursor.close();
        return userInfo2s;
    }

    public static int getTotalCount(SQLiteDatabase db){
        Cursor cursor =db.rawQuery("select count(*) from userinfo2", null);  //db.query("SnsInfo",new String[]{"snsId"} , null, null, null, null, null,null);
        int count =0;
        while (cursor.moveToNext()) {
            count =cursor.getInt(0);
        }

        cursor.close();
        return count;
    }
}
