package cn.kuwo.player.event;

import org.json.JSONObject;

public class PrintEvent {
    private int code;//0成功 1帐台成功 厨房失败 2帐台失败 厨房成功
    private String message;
    private OrderDetail orderDetail;
    private JSONObject jsonObject;
    private int escrow;


    public PrintEvent(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public PrintEvent(int code, String message, OrderDetail orderDetail, JSONObject jsonObject, int escrow) {
        this.code = code;
        this.message = message;
        this.orderDetail = orderDetail;
        this.jsonObject = jsonObject;
        this.escrow = escrow;
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

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public int getEscrow() {
        return escrow;
    }

    public void setEscrow(int escrow) {
        this.escrow = escrow;
    }

    @Override
    public String toString() {
        return "PrintEvent{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", orderDetail=" + orderDetail +
                ", jsonObject=" + jsonObject +
                ", escrow=" + escrow +
                '}';
    }
}
