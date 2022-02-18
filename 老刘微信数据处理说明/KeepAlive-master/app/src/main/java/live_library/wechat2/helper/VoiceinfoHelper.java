package live_library.wechat2.helper;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import live_library.wechat2.bean.VoiceInfo;

public class VoiceinfoHelper {
    public static List<VoiceInfo> getVoiceInfoList(SQLiteDatabase db, String createTime, int pageStart, int pageSize){
        Cursor cursor = db.query("voiceinfo", null, "CreateTime>?", new String[]{createTime}, null, null, "CreateTime asc",""+pageStart+","+pageSize );
        List<VoiceInfo> voiceInfos =new ArrayList<>();
        while (cursor.moveToNext()) {
            VoiceInfo voiceInfo =new VoiceInfo();
            voiceInfo.fileName = cursor.getString(cursor.getColumnIndex("FileName"));
            voiceInfo.user = cursor.getString(cursor.getColumnIndex("User"));
            voiceInfo.msgId = cursor.getString(cursor.getColumnIndex("MsgId"));
            voiceInfo.netOffset = cursor.getString(cursor.getColumnIndex("NetOffset"));
            voiceInfo.fileNowSize = cursor.getString(cursor.getColumnIndex("FileNowSize"));
            voiceInfo.totalLen = cursor.getString(cursor.getColumnIndex("TotalLen"));
            voiceInfo.status = cursor.getString(cursor.getColumnIndex("Status"));
            voiceInfo.createTime = cursor.getString(cursor.getColumnIndex("CreateTime"));
            voiceInfo.lastModifyTime = cursor.getString(cursor.getColumnIndex("LastModifyTime"));
            voiceInfo.clientId = cursor.getString(cursor.getColumnIndex("ClientId"));
            voiceInfo.voiceLength = cursor.getString(cursor.getColumnIndex("VoiceLength"));
            voiceInfo.msgLocalId = cursor.getString(cursor.getColumnIndex("MsgLocalId"));
            voiceInfo.human = cursor.getString(cursor.getColumnIndex("Human"));
            voiceInfo.reserved1 = cursor.getString(cursor.getColumnIndex("reserved1"));
            voiceInfo.reserved2 = cursor.getString(cursor.getColumnIndex("reserved2"));
            voiceInfo.msgSource = cursor.getString(cursor.getColumnIndex("MsgSource"));
            voiceInfo.msgFlag = cursor.getString(cursor.getColumnIndex("MsgFlag"));
            voiceInfo.msgSeq = cursor.getString(cursor.getColumnIndex("MsgSeq"));
            voiceInfo.masterBufId = cursor.getString(cursor.getColumnIndex("MasterBufId"));
            voiceInfo.checksum = cursor.getString(cursor.getColumnIndex("checksum"));
            voiceInfos.add(voiceInfo);

        }

        cursor.close();
        return voiceInfos;
    }

    public static int getTotalCount(SQLiteDatabase db,String createTime){
        String sql ="select count(*) from voiceinfo where createTime > ?";
        Cursor cursor =db.rawQuery(sql, new String[]{createTime}); //db.query("SnsInfo",new String[]{"snsId"} , null, null, null, null, null,null);
        int count =0;
        while (cursor.moveToNext()) {
            count =cursor.getInt(0);
        }

        cursor.close();
        return count;
    }
}
