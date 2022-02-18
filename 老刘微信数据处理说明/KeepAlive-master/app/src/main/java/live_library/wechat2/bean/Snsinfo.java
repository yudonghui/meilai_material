package live_library.wechat2.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "wechat_sns_info")
public class Snsinfo {
    @DatabaseField(generatedId = true)
    public Long id;
    @DatabaseField
    public String snsId;
    @DatabaseField
    public String userName;
    @DatabaseField
    public String localFlag;
    @DatabaseField
    public String createTime;
    @DatabaseField
    public String head;
    @DatabaseField
    public String localPrivate;
    @DatabaseField
    public String type;
    @DatabaseField
    public String sourceType;
    @DatabaseField
    public String likeFlag;
    @DatabaseField
    public String pravited;
    @DatabaseField
    public String stringSeq;
    @DatabaseField
    public String content;
    @DatabaseField
    public String attrBuf;
    @DatabaseField
    public String postBuf;
    @DatabaseField
    public String subType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSnsId() {
        return snsId;
    }

    public void setSnsId(String snsId) {
        this.snsId = snsId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLocalFlag() {
        return localFlag;
    }

    public void setLocalFlag(String localFlag) {
        this.localFlag = localFlag;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getLocalPrivate() {
        return localPrivate;
    }

    public void setLocalPrivate(String localPrivate) {
        this.localPrivate = localPrivate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getLikeFlag() {
        return likeFlag;
    }

    public void setLikeFlag(String likeFlag) {
        this.likeFlag = likeFlag;
    }

    public String getPravited() {
        return pravited;
    }

    public void setPravited(String pravited) {
        this.pravited = pravited;
    }

    public String getStringSeq() {
        return stringSeq;
    }

    public void setStringSeq(String stringSeq) {
        this.stringSeq = stringSeq;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttrBuf() {
        return attrBuf;
    }

    public void setAttrBuf(String attrBuf) {
        this.attrBuf = attrBuf;
    }

    public String getPostBuf() {
        return postBuf;
    }

    public void setPostBuf(String postBuf) {
        this.postBuf = postBuf;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }
}
