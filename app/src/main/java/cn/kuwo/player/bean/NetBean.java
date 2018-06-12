package cn.kuwo.player.bean;

public class NetBean {
    private int code;

    public NetBean(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "NetBean{" +
                "code=" + code +
                '}';
    }
}
