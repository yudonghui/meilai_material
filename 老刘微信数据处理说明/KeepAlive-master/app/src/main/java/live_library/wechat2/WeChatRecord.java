package live_library.wechat2;

import java.io.Serializable;
import java.util.List;

/**
 * @author cdm
 * @date 2017/1/3
 */
public class WeChatRecord implements Serializable {

    private List<WechatContent> busiWechatContentList;
    private String serverPhone;
    private String serverNickName;
    private String serverWxId;

    private String password;

    @Override
    public String toString() {
        return "BusiWeChatRecord{" +
                "serverWxId='" + serverWxId + '\'' +
                ", serverNickName='" + serverNickName + '\'' +
                ", busiWechatContentList=" + busiWechatContentList +
                '}';
    }

    public String getServerWxId() {
        return serverWxId;
    }

    public void setServerWxId(String serverWxId) {
        this.serverWxId = serverWxId;
    }

    public String getServerNickName() {
        return serverNickName;
    }

    public void setServerNickName(String serverNickName) {
        this.serverNickName = serverNickName;
    }

    public List<WechatContent> getWechatContentList() {
        return busiWechatContentList;
    }

    public void setWechatContentList(List<WechatContent> wechatContentList) {
        this.busiWechatContentList = wechatContentList;
    }

    public String getServerPhone() {
        return serverPhone;
    }

    public void setServerPhone(String serverPhone) {
        this.serverPhone = serverPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
