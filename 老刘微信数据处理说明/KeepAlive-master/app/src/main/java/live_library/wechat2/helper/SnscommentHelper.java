package live_library.wechat2.helper;


import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import live_library.wechat2.bean.Snscomment;

public class SnscommentHelper {
    public static List<Snscomment> getSnscommentList(SQLiteDatabase db, String createTime, int pageStart, int pageSize){
        Cursor cursor = db.query("snscomment", null, "createTime>?", new String[]{createTime}, null, null, "createTime asc",""+pageStart+","+pageSize );
        List<Snscomment> snscomments =new ArrayList<>();
        while (cursor.moveToNext()) {
            Snscomment snscomment =new Snscomment();
            snscomment.snsID = cursor.getString(cursor.getColumnIndex("snsID"));
            snscomment.parentID = cursor.getString(cursor.getColumnIndex("parentID"));
            snscomment.isRead = cursor.getString(cursor.getColumnIndex("isRead"));
            snscomment.createTime = cursor.getString(cursor.getColumnIndex("createTime"));
            snscomment.talker = cursor.getString(cursor.getColumnIndex("talker"));
            snscomment.type = cursor.getString(cursor.getColumnIndex("type"));
            snscomment.isSend = cursor.getString(cursor.getColumnIndex("isSend"));
            try {
                byte[] bytes =cursor.getBlob(cursor.getColumnIndex("curActionBuf"));
                if (bytes!=null&&bytes.length>0) {
                    snscomment.curActionBuf = new String(bytes, "UTF-8");
                }
                byte[] refActionBufBytes =cursor.getBlob(cursor.getColumnIndex("refActionBuf"));
                if (refActionBufBytes!=null&&refActionBufBytes.length>0) {
                    snscomment.refActionBuf = new String(refActionBufBytes, "UTF-8");
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            snscomment.commentSvrID = cursor.getString(cursor.getColumnIndex("commentSvrID"));
            snscomment.clientId = cursor.getString(cursor.getColumnIndex("clientId"));
            snscomment.commentflag = cursor.getString(cursor.getColumnIndex("commentflag"));
            snscomment.isSilence = cursor.getString(cursor.getColumnIndex("isSilence"));
            snscomments.add(snscomment);
        }

        cursor.close();
        return snscomments;
    }

    public static int getTotalCount(SQLiteDatabase db,String createTime){
        Cursor cursor =db.rawQuery("select count(*) from snscomment where createTime>?", new String[]{createTime});  //db.query("SnsInfo",new String[]{"snsId"} , null, null, null, null, null,null);
        int count =0;
        while (cursor.moveToNext()) {
            count =cursor.getInt(0);
        }

        cursor.close();
        return count;
    }
}
