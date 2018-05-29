package cn.kuwo.player.bean;

public class MeatBean {
    private String id;
    private String name;
    private Double price;
    private Double weight;

    public MeatBean(String id, String name, double price, Double meatWeight) {
        this.id=id;
        this.name=name;
        this.price=price;
        this.weight=meatWeight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "MeatBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", weight=" + weight +
                '}';
    }
}
