package cn.kuwo.player.event;

/**
 * Created by lovely on 2018/6/21
 */
public class RefundEvent {
    private int code;

    public RefundEvent(int code) {
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
        return "RefundEvent{" +
                "code=" + code +
                '}';
    }
}
