package live_library.wechat2.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "wechat_message")
public class RMessage {
    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField
    public String msgId;
    @DatabaseField
    public String msgSvrId;
    @DatabaseField
    public String type;
    @DatabaseField
    public String status;
    @DatabaseField
    public String isSend;
    @DatabaseField
    public String isShowTimer;
    @DatabaseField
    public String createTime;
    @DatabaseField
    public String talker;
    @DatabaseField
    public String content;
    @DatabaseField
    public String imgPath;
    @DatabaseField
    public String reserved;
    @DatabaseField
    public String lvbuffer;
    @DatabaseField
    public String transContent;
    @DatabaseField
    public String transBrandWording;
    @DatabaseField
    public String talkerId;
    @DatabaseField
    public String bizClientMsgId;
    @DatabaseField
    public String bizChatId;
    @DatabaseField
    public String bizChatUserId;
    @DatabaseField
    public String msgSeq;
    @DatabaseField
    public String flag;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgSvrId() {
        return msgSvrId;
    }

    public void setMsgSvrId(String msgSvrId) {
        this.msgSvrId = msgSvrId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsSend() {
        return isSend;
    }

    public void setIsSend(String isSend) {
        this.isSend = isSend;
    }

    public String getIsShowTimer() {
        return isShowTimer;
    }

    public void setIsShowTimer(String isShowTimer) {
        this.isShowTimer = isShowTimer;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getTalker() {
        return talker;
    }

    public void setTalker(String talker) {
        this.talker = talker;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getReserved() {
        return reserved;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public String getLvbuffer() {
        return lvbuffer;
    }

    public void setLvbuffer(String lvbuffer) {
        this.lvbuffer = lvbuffer;
    }

    public String getTransContent() {
        return transContent;
    }

    public void setTransContent(String transContent) {
        this.transContent = transContent;
    }

    public String getTransBrandWording() {
        return transBrandWording;
    }

    public void setTransBrandWording(String transBrandWording) {
        this.transBrandWording = transBrandWording;
    }

    public String getTalkerId() {
        return talkerId;
    }

    public void setTalkerId(String talkerId) {
        this.talkerId = talkerId;
    }

    public String getBizClientMsgId() {
        return bizClientMsgId;
    }

    public void setBizClientMsgId(String bizClientMsgId) {
        this.bizClientMsgId = bizClientMsgId;
    }

    public String getBizChatId() {
        return bizChatId;
    }

    public void setBizChatId(String bizChatId) {
        this.bizChatId = bizChatId;
    }

    public String getBizChatUserId() {
        return bizChatUserId;
    }

    public void setBizChatUserId(String bizChatUserId) {
        this.bizChatUserId = bizChatUserId;
    }

    public String getMsgSeq() {
        return msgSeq;
    }

    public void setMsgSeq(String msgSeq) {
        this.msgSeq = msgSeq;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
