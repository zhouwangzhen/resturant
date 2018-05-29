package cn.kuwo.player.bean;

import java.util.Date;

import io.realm.RealmObject;

public class ProgressBean extends RealmObject {
    private int code;//0成功 -1失败
    private String message;
    private Date date;


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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ProgressBean{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}
