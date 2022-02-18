package live_library.wechat2;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * @author zhengluping
 * @date 2018/03/12
 */

@DatabaseTable(tableName = "wechat_content")
public class WechatContent implements Serializable {

    @DatabaseField(generatedId = true)
    private Long id;//消息id
    @DatabaseField(canBeNull = true)
    private Long msgSvrId;//消息id
    @DatabaseField(canBeNull = true)
    private String talker;//微信id
    @DatabaseField(canBeNull = true)
    private String nickName;//微信昵称
    @DatabaseField(canBeNull = true)
    private String chatTime;//收发时间
    @DatabaseField(canBeNull = true)
    private String content;//内容
    @DatabaseField(canBeNull = true)
    private int isSend;//状态；0-接收，1-发送，3-系统提示
    @DatabaseField(canBeNull = true)
    private String conRemark;//微信备注名
    @DatabaseField(canBeNull = true)
    private String alias;//微信号
    @DatabaseField(canBeNull = true)
    private String type;//消息类型；1-文字，3-图片，34-语音，43-视频,1000-系统提示
    @DatabaseField(canBeNull = true)
    private String filePath;//文件路径
    @DatabaseField(canBeNull = true)
    private String headPortrait;//头像


    public int getContentHashCode(){
        StringBuffer sb = new StringBuffer();
        sb.append(msgSvrId);
        sb.append(talker);
        sb.append(nickName);
        sb.append(chatTime);
        sb.append(content);
        sb.append(isSend);
        sb.append(conRemark);
        sb.append(alias);
        sb.append(type);
        sb.append(filePath);
        sb.append(headPortrait);
        return  sb.toString().hashCode();
    }

    public String toStringContent(){
        StringBuffer sb = new StringBuffer();
        sb.append(msgSvrId+"|");
        sb.append(talker+"|");
        sb.append(nickName+"|");
        sb.append(chatTime+"|");
        sb.append(content+"|");
        sb.append(isSend+"|");
        sb.append(conRemark+"|");
        sb.append(alias+"|");
        sb.append(type+"|");
        sb.append(filePath+"|");
        sb.append(headPortrait);
        return  sb.toString();
    }




    public WechatContent() {
    }

    public WechatContent(Long msgSvrId, String talker, String nickName, String chatTime, String content, int isSend, String conRemark, String alias) {
        this.msgSvrId = msgSvrId;
        this.talker = talker;
        this.nickName = nickName;
        this.chatTime = chatTime;
        this.content = content;
        this.isSend = isSend;
        this.conRemark = conRemark;
        this.alias = alias;
    }

    public int getIsSend() {
        return isSend;
    }

    public void setIsSend(int isSend) {
        this.isSend = isSend;
    }

    public Long getMsgSvrId() {
        return msgSvrId;
    }

    public void setMsgSvrId(Long msgSvrId) {
        this.msgSvrId = msgSvrId;
    }

    public String getTalker() {
        return talker;
    }

    public void setTalker(String talker) {
        this.talker = talker;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getChatTime() {
        return chatTime;
    }

    public void setChatTime(String chatTime) {
        this.chatTime = chatTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getConRemark() {
        return conRemark;
    }

    public void setConRemark(String conRemark) {
        this.conRemark = conRemark;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

}