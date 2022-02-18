package live_library.wechatlog;

public class LogData {
    private String tag;
    private String msg;
    private String time;
    private int type;

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String toString() {
        return this.time + " " + this.tag + " " + this.msg + "\n";
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof LogData)) {
            return false;
        }
        if ((this.tag.equals(((LogData) o).getTag())) && (this.msg.equals(((LogData) o).getMsg())) &&
                (this.time.equals(((LogData) o).getTime()))) {
            return true;
        }
        return false;
    }
}