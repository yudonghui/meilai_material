package live_library.wechat2.uploadpic;

import org.json.JSONArray;

public class HttpPicBean {

    private int code;
    private String message;
    private JSONArray data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JSONArray getData() {
        return data;
    }

    public void setData(JSONArray data) {
        this.data = data;
    }
}
