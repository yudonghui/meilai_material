package live_library.wechat2.helper;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import live_library.wechat2.bean.ContactLabel;

public class ContactLabelHelper {

    public final static String TABLE_CONTACT_LABEL ="contactlabel";

    public static List<ContactLabel> getContactLabelList(SQLiteDatabase db, String updateTime, int pageStart, int pageSize){
        Cursor cursor = db.query(TABLE_CONTACT_LABEL, null, "createTime>?", new String[]{updateTime}, null, null, "createTime asc",""+pageStart+","+pageSize );
        List<ContactLabel> list =new ArrayList<>();
        while (cursor.moveToNext()) {
            ContactLabel data =new ContactLabel();
            data.labelID = cursor.getString(cursor.getColumnIndex("labelID"));
            data.labelName = cursor.getString(cursor.getColumnIndex("labelName"));
            data.labelPYFull = cursor.getString(cursor.getColumnIndex("labelPYFull"));
            data.labelPYShort = cursor.getString(cursor.getColumnIndex("labelPYShort"));
            data.createTime = cursor.getString(cursor.getColumnIndex("createTime"));
            data.isTemporary = cursor.getString(cursor.getColumnIndex("isTemporary"));
            list.add(data);
        }

        cursor.close();
        return list;
    }

    public static int getTotalCount(SQLiteDatabase db,String updateTime){
        Cursor cursor =db.rawQuery("select count(*) from contactlabel"+" where  createTime > ?", new String[]{updateTime}); //db.query("SnsInfo",new String[]{"snsId"} , null, null, null, null, null,null);
        int count =0;
        while (cursor.moveToNext()) {
            count =cursor.getInt(0);
        }

        cursor.close();
        return count;
    }
}
