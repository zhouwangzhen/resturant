package cn.kuwo.player.bean.entity;

import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

/**
 * Created by lovely on 2018/10/12
 */
@RealmClass
public class SideDishEntity implements RealmModel {
    private String name;
    private Double price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "SideDishEntity{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
