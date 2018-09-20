package cn.kuwo.player.bean;

import java.io.Serializable;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class ProductBean extends RealmObject implements Serializable {
    private String name;//品名
    private String code;//编码
    private double price;//价格
    private double weight;//重量
    private String objectId;//id
    private int type;//类型
    private int sale;//促销类型
    private int combo;//侧边栏显示分类
    private double rate;//折扣率
    private int performance;//是否有提成
    private String givecode;//赠送商品编码
    private int store;//对应的店铺
    private Double meatWeight;//可兑换肉的重量
    private String serial;//商品编码
    private String url;//图片地址
    private Double scale;//兑换的比例
    private Double remainMoney;//抵扣后还需要支付的金额
    private String comboMenu;//套餐内容
    private int active;//是否显示
    private RealmList<String> comments;//备注提示
    private int giveRule;//不同类型的会员赠送规则
    private Double nb;//牛币
    private String reviewCommodity;//大众点评赠送的商品
    private boolean merge;//是否可以合并
    private int classify;//点餐分类


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }

    public int getCombo() {
        return combo;
    }

    public void setCombo(int combol) {
        this.combo = combol;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getPerformance() {
        return performance;
    }

    public void setPerformance(int performance) {
        this.performance = performance;
    }

    public String getGivecode() {
        return givecode;
    }

    public void setGivecode(String givecode) {
        this.givecode = givecode;
    }

    public int getStore() {
        return store;
    }

    public void setStore(int store) {
        this.store = store;
    }

    public Double getMeatWeight() {
        return meatWeight;
    }

    public void setMeatWeight(Double meatWeight) {
        this.meatWeight = meatWeight;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getScale() {
        return scale;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }

    public Double getRemainMoney() {
        return remainMoney;
    }

    public void setRemainMoney(Double remainMoney) {
        this.remainMoney = remainMoney;
    }

    public String getComboMenu() {
        return comboMenu;
    }

    public void setComboMenu(String comboMenu) {
        this.comboMenu = comboMenu;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public RealmList<String> getComments() {
        return comments;
    }

    public void setComments(RealmList<String> comments) {
        this.comments = comments;
    }

    public int getGiveRule() {
        return giveRule;
    }

    public void setGiveRule(int giveRule) {
        this.giveRule = giveRule;
    }

    public Double getNb() {
        return nb;
    }

    public void setNb(Double nb) {
        this.nb = nb;
    }

    public String getReviewCommodity() {
        return reviewCommodity;
    }

    public void setReviewCommodity(String reviewCommodity) {
        this.reviewCommodity = reviewCommodity;
    }

    public boolean isMerge() {
        return merge;
    }

    public void setMerge(boolean merge) {
        this.merge = merge;
    }

    public int getClassify() {
        return classify;
    }

    public void setClassify(int classify) {
        this.classify = classify;
    }

    @Override
    public String toString() {
        return "ProductBean{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", price=" + price +
                ", weight=" + weight +
                ", objectId='" + objectId + '\'' +
                ", type=" + type +
                ", sale=" + sale +
                ", combo=" + combo +
                ", rate=" + rate +
                ", performance=" + performance +
                ", givecode='" + givecode + '\'' +
                ", store=" + store +
                ", meatWeight=" + meatWeight +
                ", serial='" + serial + '\'' +
                ", url='" + url + '\'' +
                ", scale=" + scale +
                ", remainMoney=" + remainMoney +
                ", comboMenu='" + comboMenu + '\'' +
                ", active=" + active +
                ", comments=" + comments +
                ", giveRule=" + giveRule +
                ", nb=" + nb +
                ", reviewCommodity='" + reviewCommodity + '\'' +
                ", merge=" + merge +
                ", classify=" + classify +
                '}';
    }
}


