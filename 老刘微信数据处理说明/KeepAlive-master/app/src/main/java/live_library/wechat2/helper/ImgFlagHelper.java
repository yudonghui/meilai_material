package live_library.wechat2.helper;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import live_library.wechat2.bean.ImgFlag;

public class ImgFlagHelper {
    public final static String TABLE_IMAG_FLAG ="img_flag";

    public static List<ImgFlag> getImgFlagList(SQLiteDatabase db, String updateTime, int pageStart, int pageSize){
        Cursor cursor = db.query(TABLE_IMAG_FLAG, null, "lastupdatetime>?", new String[]{updateTime}, null, null, "lastupdatetime asc",""+pageStart+","+pageSize );
        List<ImgFlag> list =new ArrayList<>();
        while (cursor.moveToNext()) {
            ImgFlag data =new ImgFlag();
            data.username = cursor.getString(cursor.getColumnIndex("username"));
            data.imgflag = cursor.getString(cursor.getColumnIndex("imgflag"));
            data.lastupdatetime = cursor.getString(cursor.getColumnIndex("lastupdatetime"));
            data.reserved1 = cursor.getString(cursor.getColumnIndex("reserved1"));
            data.reserved2 = cursor.getString(cursor.getColumnIndex("reserved2"));
            data.reserved3 = cursor.getString(cursor.getColumnIndex("reserved3"));
            data.reserved4 = cursor.getString(cursor.getColumnIndex("reserved4"));
            list.add(data);
        }

        cursor.close();
        return list;
    }

    public static int getTotalCount(SQLiteDatabase db,String updateTime){
        Cursor cursor =db.rawQuery("select count(*) from "+TABLE_IMAG_FLAG+" where  lastupdatetime > ?", new String[]{updateTime}); //db.query("SnsInfo",new String[]{"snsId"} , null, null, null, null, null,null);
        int count =0;
        while (cursor.moveToNext()) {
            count =cursor.getInt(0);
        }

        cursor.close();
        return count;
    }
}
