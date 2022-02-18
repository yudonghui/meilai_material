package live_library.wechat2.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "wechat_voice_info")
public class VoiceInfo {

    @DatabaseField(generatedId = true)
    private Long id;
    @DatabaseField
    public String fileName;
    @DatabaseField
    public String user ;
    @DatabaseField
    public String msgId ;
    @DatabaseField
    public String netOffset ;
    @DatabaseField
    public String fileNowSize ;
    @DatabaseField
    public String totalLen ;
    @DatabaseField
    public String status ;
    @DatabaseField
    public String createTime ;
    @DatabaseField
    public String lastModifyTime ;
    @DatabaseField
    public String clientId ;
    @DatabaseField
    public String voiceLength ;
    @DatabaseField
    public String msgLocalId ;
    @DatabaseField
    public String human ;
    @DatabaseField
    public String reserved1 ;
    @DatabaseField
    public String reserved2 ;
    @DatabaseField
    public String msgSource ;
    @DatabaseField
    public String msgFlag ;
    @DatabaseField
    public String msgSeq ;
    @DatabaseField
    public String masterBufId ;
    @DatabaseField
    public String checksum ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getNetOffset() {
        return netOffset;
    }

    public void setNetOffset(String netOffset) {
        this.netOffset = netOffset;
    }

    public String getFileNowSize() {
        return fileNowSize;
    }

    public void setFileNowSize(String fileNowSize) {
        this.fileNowSize = fileNowSize;
    }

    public String getTotalLen() {
        return totalLen;
    }

    public void setTotalLen(String totalLen) {
        this.totalLen = totalLen;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getVoiceLength() {
        return voiceLength;
    }

    public void setVoiceLength(String voiceLength) {
        this.voiceLength = voiceLength;
    }

    public String getMsgLocalId() {
        return msgLocalId;
    }

    public void setMsgLocalId(String msgLocalId) {
        this.msgLocalId = msgLocalId;
    }

    public String getHuman() {
        return human;
    }

    public void setHuman(String human) {
        this.human = human;
    }

    public String getReserved1() {
        return reserved1;
    }

    public void setReserved1(String reserved1) {
        this.reserved1 = reserved1;
    }

    public String getReserved2() {
        return reserved2;
    }

    public void setReserved2(String reserved2) {
        this.reserved2 = reserved2;
    }

    public String getMsgSource() {
        return msgSource;
    }

    public void setMsgSource(String msgSource) {
        this.msgSource = msgSource;
    }

    public String getMsgFlag() {
        return msgFlag;
    }

    public void setMsgFlag(String msgFlag) {
        this.msgFlag = msgFlag;
    }

    public String getMsgSeq() {
        return msgSeq;
    }

    public void setMsgSeq(String msgSeq) {
        this.msgSeq = msgSeq;
    }

    public String getMasterBufId() {
        return masterBufId;
    }

    public void setMasterBufId(String masterBufId) {
        this.masterBufId = masterBufId;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
