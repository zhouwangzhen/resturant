package cn.kuwo.player.bean;

import io.realm.RealmObject;

public class TypeBean extends RealmObject {
    private int number;
    private String name;
    private int store;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStore() {
        return store;
    }

    public void setStore(int store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return "TypeBean{" +
                "number=" + number +
                ", name='" + name + '\'' +
                ", store=" + store +
                '}';
    }
}
