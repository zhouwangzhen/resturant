package cn.kuwo.player.bean;

import java.util.Date;

import io.realm.RealmObject;

public class CouponBean extends RealmObject {
    private String id;
    private Date start;
    private Double money;
    private int active;
    private String from;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "CouponBean{" +
                "id='" + id + '\'' +
                ", start=" + start +
                ", money=" + money +
                ", active=" + active +
                ", from='" + from + '\'' +
                '}';
    }
}
