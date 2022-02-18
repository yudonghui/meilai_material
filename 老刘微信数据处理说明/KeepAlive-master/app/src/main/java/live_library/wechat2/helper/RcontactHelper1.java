package live_library.wechat2.helper;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import live_library.wechat2.bean.Rcontact;

public class RcontactHelper1 {
    public static List<Rcontact> getRcontactList(SQLiteDatabase db, int pageStart, int pageSize){
        Cursor cursor=null;
        try {
            cursor = db.query("rcontact", null, null, null, null, null, null, "" + pageStart + "," + pageSize);
            List<Rcontact> rcontacts = new ArrayList<>();
            while (cursor.moveToNext()) {
                Rcontact rcontact = new Rcontact();
                rcontact.username = cursor.getString(cursor.getColumnIndex("username"));
                rcontact.alias = cursor.getString(cursor.getColumnIndex("alias"));
                rcontact.conRemark = cursor.getString(cursor.getColumnIndex("conRemark"));
                rcontact.domainList = ""; // = cursor.getString(cursor.getColumnIndex("domainList"));
                rcontact.nickname = cursor.getString(cursor.getColumnIndex("nickname"));
                rcontact.pyInitial = ""; // = cursor.getString(cursor.getColumnIndex("pyInitial"));
                rcontact.quanPin = cursor.getString(cursor.getColumnIndex("quanPin"));
                rcontact.showHead = ""; // = cursor.getString(cursor.getColumnIndex("showHead"));
                rcontact.type = cursor.getString(cursor.getColumnIndex("type"));
                rcontact.weiboFlag = ""; //= cursor.getString(cursor.getColumnIndex("weiboFlag"));
                rcontact.weiboNickname = ""; //= cursor.getString(cursor.getColumnIndex("weiboNickname"));
                rcontact.conRemarkPYFull = ""; //= cursor.getString(cursor.getColumnIndex("conRemarkPYFull"));
                rcontact.conRemarkPYShort = ""; //= cursor.getString(cursor.getColumnIndex("conRemarkPYShort"));
                rcontact.lvbuff = "";// 先賦空值
//                try {
//                    byte[] bytes =cursor.getBlob(cursor.getColumnIndex("lvbuff"));
//                    if (bytes!=null&&bytes.length>0) {
//                        rcontact.lvbuff = new String(bytes, "UTF-8");
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                rcontact.verifyFlag = cursor.getString(cursor.getColumnIndex("verifyFlag"));
                rcontact.encryptUsername = cursor.getString(cursor.getColumnIndex("encryptUsername"));
                rcontact.chatroomFlag = cursor.getString(cursor.getColumnIndex("chatroomFlag"));
                rcontact.deleteFlag = cursor.getString(cursor.getColumnIndex("deleteFlag"));
                rcontact.contactLabelIds = cursor.getString(cursor.getColumnIndex("contactLabelIds"));
                rcontact.openImAppid = cursor.getString(cursor.getColumnIndex("openImAppid"));
                rcontact.descWordingId = ""; //= cursor.getString(cursor.getColumnIndex("descWordingId"));
                rcontacts.add(rcontact);
            }
            cursor.close();
            return rcontacts;
        }finally {
            if (cursor!=null){
                cursor.close();
            }
        }
    }

    public static int getTotalCount(SQLiteDatabase db){
        Cursor cursor =db.rawQuery("select count(*) from rcontact",null); //db.query("SnsInfo",new String[]{"snsId"} , null, null, null, null, null,null);
        int count =0;
        while (cursor.moveToNext()) {
            count =cursor.getInt(0);
        }

        cursor.close();
        return count;
    }
}
