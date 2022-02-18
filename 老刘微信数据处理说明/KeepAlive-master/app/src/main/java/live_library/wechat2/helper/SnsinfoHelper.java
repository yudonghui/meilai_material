package live_library.wechat2.helper;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import live_library.wechat2.bean.Snsinfo;

public class SnsinfoHelper {
    public static List<Snsinfo> getSnsInfoList(SQLiteDatabase db, String createTime, int pageStart, int pageSize){
        Cursor cursor = db.query("SnsInfo", null, "createTime>?", new String[]{createTime},  null, null, "createTime asc",""+pageStart+","+pageSize );
        List<Snsinfo> snsinfos =new ArrayList<>();
        while (cursor.moveToNext()) {
            Snsinfo snsinfo =new Snsinfo();
            snsinfo.snsId = cursor.getString(cursor.getColumnIndex("snsId"));
            snsinfo.userName = cursor.getString(cursor.getColumnIndex("userName"));
            snsinfo.localFlag = cursor.getString(cursor.getColumnIndex("localFlag"));
            snsinfo.createTime = cursor.getString(cursor.getColumnIndex("createTime"));
            snsinfo.head = cursor.getString(cursor.getColumnIndex("head"));
            snsinfo.localPrivate = cursor.getString(cursor.getColumnIndex("localPrivate"));
            snsinfo.type = cursor.getString(cursor.getColumnIndex("type"));
            snsinfo.sourceType = cursor.getString(cursor.getColumnIndex("sourceType"));
            snsinfo.likeFlag = cursor.getString(cursor.getColumnIndex("likeFlag"));
            snsinfo.pravited = cursor.getString(cursor.getColumnIndex("pravited"));
            snsinfo.stringSeq = cursor.getString(cursor.getColumnIndex("stringSeq"));
            try {
                byte[] contentbytes =cursor.getBlob(cursor.getColumnIndex("content"));
                if (contentbytes!=null&&contentbytes.length>0) {
                    snsinfo.content = new String(contentbytes, "UTF-8");
                }
                byte[] attrBufbytes =cursor.getBlob(cursor.getColumnIndex("attrBuf"));
                if (attrBufbytes!=null&&attrBufbytes.length>0) {
                    snsinfo.attrBuf = new String(attrBufbytes, "UTF-8");
                }
                byte[] postBufbytes =cursor.getBlob(cursor.getColumnIndex("postBuf"));
                if (postBufbytes!=null&&postBufbytes.length>0) {
                    snsinfo.postBuf = new String(postBufbytes, "UTF-8");
                }
                byte[] subTypebytes =cursor.getBlob(cursor.getColumnIndex("subType"));
                if (subTypebytes!=null&&subTypebytes.length>0) {
                    snsinfo.subType = new String(subTypebytes, "UTF-8");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            snsinfos.add(snsinfo);
        }
        cursor.close();
        return snsinfos;
    }

    public static int getTotalCount(SQLiteDatabase db,String createTime){
        Cursor cursor =db.rawQuery("select count(*) from SnsInfo where createTime>?", new String[]{createTime});  //db.query("SnsInfo",new String[]{"snsId"} , null, null, null, null, null,null);
        int count =0;
        while (cursor.moveToNext()) {
            count =cursor.getInt(0);
        }

        cursor.close();
        return count;
    }
}
