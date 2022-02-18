package live_library.wechat2.helper;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import live_library.wechat2.bean.ChatRoom;

public class ChatRoomHelper {
    public final static String TABLE_CHAT_ROOM ="chatroom";
    public static List<ChatRoom> getChatRoomList(SQLiteDatabase db, String updateTime, int pageStart, int pageSize){
        Cursor cursor = db.query(TABLE_CHAT_ROOM, null, "modifytime>?", new String[]{updateTime}, null, null, "modifytime asc",""+pageStart+","+pageSize );
        List<ChatRoom> list =new ArrayList<>();
        while (cursor.moveToNext()) {
            ChatRoom data =new ChatRoom();
            data.chatroomname = cursor.getString(cursor.getColumnIndex("chatroomname"));
            data.addtime = cursor.getString(cursor.getColumnIndex("addtime"));
            data.memberlist = cursor.getString(cursor.getColumnIndex("memberlist"));
            data.displayname = cursor.getString(cursor.getColumnIndex("displayname"));
            data.chatroomnick = cursor.getString(cursor.getColumnIndex("chatroomnick"));
            data.roomflag = cursor.getString(cursor.getColumnIndex("roomflag"));
            data.roomowner = cursor.getString(cursor.getColumnIndex("roomowner"));
            try {
                byte[] bytes =cursor.getBlob(cursor.getColumnIndex("roomdata"));
                if (bytes!=null&&bytes.length>0){
                    data.roomdata =new String(bytes,"UTF-8");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            data.isShowname = cursor.getString(cursor.getColumnIndex("isShowname"));
            data.selfDisplayName = cursor.getString(cursor.getColumnIndex("selfDisplayName"));
            data.style = cursor.getString(cursor.getColumnIndex("style"));
            data.chatroomdataflag = cursor.getString(cursor.getColumnIndex("chatroomdataflag"));
            data.modifytime = cursor.getString(cursor.getColumnIndex("modifytime"));
            data.chatroomnotice = cursor.getString(cursor.getColumnIndex("chatroomnotice"));
            data.chatroomVersion = cursor.getString(cursor.getColumnIndex("chatroomVersion"));
            data.chatroomnoticeEditor = cursor.getString(cursor.getColumnIndex("chatroomnoticeEditor"));
            data.chatroomnoticePublishTime = cursor.getString(cursor.getColumnIndex("chatroomnoticePublishTime"));
            data.chatroomLocalVersion = cursor.getString(cursor.getColumnIndex("chatroomLocalVersion"));
            data.chatroomStatus = cursor.getString(cursor.getColumnIndex("chatroomStatus"));
            data.memberCount = cursor.getString(cursor.getColumnIndex("memberCount"));
            list.add(data);
        }

        cursor.close();
        return list;
    }

    public static int getTotalCount(SQLiteDatabase db,String updateTime){
        Cursor cursor =db.rawQuery("select count(*) from "+TABLE_CHAT_ROOM+" where  modifytime > ?", new String[]{updateTime}); //db.query("SnsInfo",new String[]{"snsId"} , null, null, null, null, null,null);
        int count =0;
        while (cursor.moveToNext()) {
            count =cursor.getInt(0);
        }

        cursor.close();
        return count;
    }
}
