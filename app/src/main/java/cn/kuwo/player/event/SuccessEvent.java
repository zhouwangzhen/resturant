package cn.kuwo.player.event;

import com.avos.avoscloud.AVObject;

import java.util.List;

public class SuccessEvent {
    private int code;
    private String message;
    private List<Object> orders;
    private AVObject tableAVObject;

    public SuccessEvent(int code, String message, List<Object> orders, AVObject tableAVObject) {//下单打印小票回调
        this.code = code;
        this.message = message;
        this.orders = orders;
        this.tableAVObject = tableAVObject;
    }

    public SuccessEvent(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public SuccessEvent(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Object> getOrders() {
        return orders;
    }

    public void setOrders(List<Object> orders) {
        this.orders = orders;
    }

    public AVObject getTableAVObject() {
        return tableAVObject;
    }

    public void setTableAVObject(AVObject tableAVObject) {
        this.tableAVObject = tableAVObject;
    }

    @Override
    public String toString() {
        return "SuccessEvent{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", orders=" + orders +
                ", tableAVObject=" + tableAVObject +
                '}';
    }
}
