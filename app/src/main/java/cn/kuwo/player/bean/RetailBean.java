package cn.kuwo.player.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RetailBean implements Serializable {
    private ArrayList<String> ids;
    private ArrayList<String> codes;
    private ArrayList<Double> prices;
    private ArrayList<Double> weight;
    private ArrayList<String> name;


    public RetailBean( ArrayList<String> ids,ArrayList<String> codes, ArrayList<Double> prices, ArrayList<Double> weight,ArrayList<String> name) {
        this.ids=ids;
        this.codes = codes;
        this.prices = prices;
        this.weight = weight;
        this.name = name;
    }

    public ArrayList<String> getCodes() {
        return codes;
    }

    public void setCodes(ArrayList<String> codes) {
        this.codes = codes;
    }

    public ArrayList<Double> getPrices() {
        return prices;
    }

    public void setPrices(ArrayList<Double> prices) {
        this.prices = prices;
    }

    public ArrayList<Double> getWeight() {
        return weight;
    }

    public void setWeight(ArrayList<Double> weight) {
        this.weight = weight;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public ArrayList<String> getName() {
        return name;
    }

    public void setName(ArrayList<String> name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "RetailBean{" +
                "ids=" + ids +
                ", codes=" + codes +
                ", prices=" + prices +
                ", weight=" + weight +
                ", name=" + name +
                '}';
    }
}
