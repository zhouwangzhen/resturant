package cn.kuwo.player.event;

public class ProgressEvent {
    private int code;//0成功 -1失败
    private String message;

    public ProgressEvent(int code) {
        this.code = code;
    }

    public ProgressEvent(int code, String message) {
        this.code = code;
        this.message = message;
    }

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

    @Override
    public String toString() {
        return "ProgressEvent{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
