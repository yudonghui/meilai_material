package live_library.wechat2.helper;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import live_library.wechat2.bean.RMessage;

public class MessageHelper {
    public static List<RMessage> getMessageList(SQLiteDatabase db, String createTime, int pageStart, int pageSize){
        Cursor cursor = db.query("message", null, "createTime>?", new String[]{createTime}, null, null, "createTime asc",""+pageStart+","+pageSize );
        List<RMessage> rMessages =new ArrayList<>();
        while (cursor.moveToNext()) {
            RMessage RMessage =new RMessage();
            RMessage.msgId = cursor.getString(cursor.getColumnIndex("msgId"));
            RMessage.msgSvrId = cursor.getString(cursor.getColumnIndex("msgSvrId"));
            RMessage.type = cursor.getString(cursor.getColumnIndex("type"));
            RMessage.status = cursor.getString(cursor.getColumnIndex("status"));
            RMessage.isSend = cursor.getString(cursor.getColumnIndex("isSend"));
            RMessage.isShowTimer = cursor.getString(cursor.getColumnIndex("isShowTimer"));
            RMessage.createTime = cursor.getString(cursor.getColumnIndex("createTime"));
            RMessage.talker = cursor.getString(cursor.getColumnIndex("talker"));
            RMessage.content = cursor.getString(cursor.getColumnIndex("content"));
            RMessage.imgPath = cursor.getString(cursor.getColumnIndex("imgPath"));
            RMessage.reserved = cursor.getString(cursor.getColumnIndex("reserved"));
            try {
                byte[] data =cursor.getBlob(cursor.getColumnIndex("lvbuffer"));
                if (data!=null&&data.length>0){
                    RMessage.lvbuffer =new String(data,"UTF-8");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            RMessage.transContent = cursor.getString(cursor.getColumnIndex("transContent"));
            RMessage.transBrandWording = cursor.getString(cursor.getColumnIndex("transBrandWording"));
            RMessage.talkerId = cursor.getString(cursor.getColumnIndex("talkerId"));
            RMessage.bizClientMsgId = cursor.getString(cursor.getColumnIndex("bizClientMsgId"));
            RMessage.bizChatId = cursor.getString(cursor.getColumnIndex("bizChatId"));
            RMessage.bizChatUserId = cursor.getString(cursor.getColumnIndex("bizChatUserId"));
            RMessage.msgSeq = cursor.getString(cursor.getColumnIndex("msgSeq"));
            RMessage.flag = cursor.getString(cursor.getColumnIndex("flag"));
            rMessages.add(RMessage);
        }

        cursor.close();
        return rMessages;
    }

    public static int getTotalCount(SQLiteDatabase db,String createTime){
        Cursor cursor =db.rawQuery("select count(*) from message where createTime > ?", new String[]{createTime}); //db.query("SnsInfo",new String[]{"snsId"} , null, null, null, null, null,null);
        int count =0;
        while (cursor.moveToNext()) {
            count =cursor.getInt(0);
        }

        cursor.close();
        return count;
    }
}
