package cn.kuwo.player.bean;

/**
 * Created by lovely on 2018/9/22
 */
public class CommodityBean {
    private String name;
    private Double price;
    private Double nb;

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

    public Double getNb() {
        return nb;
    }

    public void setNb(Double nb) {
        this.nb = nb;
    }

    @Override
    public String toString() {
        return "CommodityBean{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", nb=" + nb +
                '}';
    }
}
