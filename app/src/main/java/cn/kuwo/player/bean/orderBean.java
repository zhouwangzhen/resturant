package cn.kuwo.player.bean;

import java.util.List;

public class orderBean {
    private String id;
    private Double number;
    private String comment;
    private List<String> comboList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getNumber() {
        return number;
    }

    public void setNumber(Double number) {
        this.number = number;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getComboList() {
        return comboList;
    }

    public void setComboList(List<String> comboList) {
        this.comboList = comboList;
    }

    @Override
    public String toString() {
        return "orderBean{" +
                "id='" + id + '\'' +
                ", number=" + number +
                ", comment='" + comment + '\'' +
                ", comboList=" + comboList +
                '}';
    }
}
