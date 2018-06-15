package cn.kuwo.player.event;

import java.io.Serializable;

public class CouponEvent implements Serializable{
    private int type;
    private String id;
    private Double money;
    private String content;

    public CouponEvent(int type, String id, Double money, String content) {
        this.type = type;
        this.id = id;
        this.money = money;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CouponEvent{" +
                "type=" + type +
                ", id='" + id + '\'' +
                ", money=" + money +
                ", content='" + content + '\'' +
                '}';
    }
}
