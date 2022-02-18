package live_library.wechat2.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "wechat_rcontact")
public class Rcontact {
    @DatabaseField(generatedId = true)
    public Long id;
    @DatabaseField
    public String username;
    @DatabaseField
    public String alias;
    @DatabaseField
    public String conRemark;
    @DatabaseField
    public String domainList;
    @DatabaseField
    public String nickname;
    @DatabaseField
    public String pyInitial;
    @DatabaseField
    public String quanPin;
    @DatabaseField
    public String showHead;
    @DatabaseField
    public String type;
    @DatabaseField
    public String weiboFlag;
    @DatabaseField
    public String weiboNickname;
    @DatabaseField
    public String conRemarkPYFull;
    @DatabaseField
    public String conRemarkPYShort;
    @DatabaseField
    public String lvbuff;
    @DatabaseField
    public String verifyFlag;
    @DatabaseField
    public String encryptUsername;
    @DatabaseField
    public String chatroomFlag;
    @DatabaseField
    public String deleteFlag;
    @DatabaseField
    public String contactLabelIds;
    @DatabaseField
    public String openImAppid;
    @DatabaseField
    public String descWordingId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getConRemark() {
        return conRemark;
    }

    public void setConRemark(String conRemark) {
        this.conRemark = conRemark;
    }

    public String getDomainList() {
        return domainList;
    }

    public void setDomainList(String domainList) {
        this.domainList = domainList;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPyInitial() {
        return pyInitial;
    }

    public void setPyInitial(String pyInitial) {
        this.pyInitial = pyInitial;
    }

    public String getQuanPin() {
        return quanPin;
    }

    public void setQuanPin(String quanPin) {
        this.quanPin = quanPin;
    }

    public String getShowHead() {
        return showHead;
    }

    public void setShowHead(String showHead) {
        this.showHead = showHead;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWeiboFlag() {
        return weiboFlag;
    }

    public void setWeiboFlag(String weiboFlag) {
        this.weiboFlag = weiboFlag;
    }

    public String getWeiboNickname() {
        return weiboNickname;
    }

    public void setWeiboNickname(String weiboNickname) {
        this.weiboNickname = weiboNickname;
    }

    public String getConRemarkPYFull() {
        return conRemarkPYFull;
    }

    public void setConRemarkPYFull(String conRemarkPYFull) {
        this.conRemarkPYFull = conRemarkPYFull;
    }

    public String getConRemarkPYShort() {
        return conRemarkPYShort;
    }

    public void setConRemarkPYShort(String conRemarkPYShort) {
        this.conRemarkPYShort = conRemarkPYShort;
    }

    public String getLvbuff() {
        return lvbuff;
    }

    public void setLvbuff(String lvbuff) {
        this.lvbuff = lvbuff;
    }

    public String getVerifyFlag() {
        return verifyFlag;
    }

    public void setVerifyFlag(String verifyFlag) {
        this.verifyFlag = verifyFlag;
    }

    public String getEncryptUsername() {
        return encryptUsername;
    }

    public void setEncryptUsername(String encryptUsername) {
        this.encryptUsername = encryptUsername;
    }

    public String getChatroomFlag() {
        return chatroomFlag;
    }

    public void setChatroomFlag(String chatroomFlag) {
        this.chatroomFlag = chatroomFlag;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getContactLabelIds() {
        return contactLabelIds;
    }

    public void setContactLabelIds(String contactLabelIds) {
        this.contactLabelIds = contactLabelIds;
    }

    public String getOpenImAppid() {
        return openImAppid;
    }

    public void setOpenImAppid(String openImAppid) {
        this.openImAppid = openImAppid;
    }

    public String getDescWordingId() {
        return descWordingId;
    }

    public void setDescWordingId(String descWordingId) {
        this.descWordingId = descWordingId;
    }
}
