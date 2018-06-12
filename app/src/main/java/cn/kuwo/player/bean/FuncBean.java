package cn.kuwo.player.bean;

public class FuncBean {
    private int code;

    public FuncBean(int code) {
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
        return "FuncBean{" +
                "code=" + code +
                '}';
    }
}
