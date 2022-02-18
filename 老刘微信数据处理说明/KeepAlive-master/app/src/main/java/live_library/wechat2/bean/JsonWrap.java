package live_library.wechat2.bean;

import java.util.List;

public class JsonWrap<T> {
    private String type;// 2 message 3userinfo 4rcontact 5 chatroom 6 img_flag
    private String over;//over /process
    private List<T> jsonInfo;

    public String getOver() {
        return over;
    }

    public void setOver(String over) {
        this.over = over;
    }

    private String vxid; //微信id

    public String getVxid() {
        return vxid;
    }

    public void setVxid(String vxid) {
        this.vxid = vxid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<T> getJsonInfo() {
        return jsonInfo;
    }

    public void setJsonInfo(List<T> jsonInfo) {
        this.jsonInfo = jsonInfo;
    }
}
