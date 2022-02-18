package live_library.wechat2.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "wechat_sns_comment")
public class Snscomment {
    @DatabaseField(generatedId = true)
    public Long id;
    @DatabaseField
    public String snsID;
    @DatabaseField
    public String parentID;
    @DatabaseField
    public String isRead;
    @DatabaseField
    public String createTime;
    @DatabaseField
    public String talker;
    @DatabaseField
    public String type;
    @DatabaseField
    public String isSend;
    @DatabaseField
    public String curActionBuf;
    @DatabaseField
    public String refActionBuf;
    @DatabaseField
    public String commentSvrID;
    @DatabaseField
    public String clientId;
    @DatabaseField
    public String commentflag;
    @DatabaseField
    public String isSilence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSnsID() {
        return snsID;
    }

    public void setSnsID(String snsID) {
        this.snsID = snsID;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsSend() {
        return isSend;
    }

    public void setIsSend(String isSend) {
        this.isSend = isSend;
    }

    public String getCurActionBuf() {
        return curActionBuf;
    }

    public void setCurActionBuf(String curActionBuf) {
        this.curActionBuf = curActionBuf;
    }

    public String getRefActionBuf() {
        return refActionBuf;
    }

    public void setRefActionBuf(String refActionBuf) {
        this.refActionBuf = refActionBuf;
    }

    public String getCommentSvrID() {
        return commentSvrID;
    }

    public void setCommentSvrID(String commentSvrID) {
        this.commentSvrID = commentSvrID;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCommentflag() {
        return commentflag;
    }

    public void setCommentflag(String commentflag) {
        this.commentflag = commentflag;
    }

    public String getIsSilence() {
        return isSilence;
    }

    public void setIsSilence(String isSilence) {
        this.isSilence = isSilence;
    }
}
