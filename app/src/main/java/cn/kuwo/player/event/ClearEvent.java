package cn.kuwo.player.event;

public class ClearEvent {
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ClearEvent(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ClearEvent{" +
                "code=" + code +
                '}';
    }
}
