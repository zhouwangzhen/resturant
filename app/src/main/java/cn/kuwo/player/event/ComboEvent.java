package cn.kuwo.player.event;

import java.util.List;

import cn.kuwo.player.bean.ProductBean;

public class ComboEvent {
    private ProductBean productBean;
    private List<String> comboList;
    private int cookSerial;
    private String content;
    private Double CommodityNumber;
    private Boolean isEdit;
    private int orderIndex;

    public ComboEvent(ProductBean productBean, List<String> comboList,int cookSerial,String content,Double CommodityNumber,Boolean isEdit,int orderIndex) {
        this.productBean = productBean;
        this.comboList = comboList;
        this.cookSerial = cookSerial;
        this.content = content;
        this.CommodityNumber = CommodityNumber;
        this.isEdit=isEdit;
        this.orderIndex=orderIndex;
    }

    public ProductBean getProductBean() {
        return productBean;
    }

    public void setProductBean(ProductBean productBean) {
        this.productBean = productBean;
    }

    public List<String> getComboList() {
        return comboList;
    }

    public void setComboList(List<String> comboList) {
        this.comboList = comboList;
    }

    public int getCookSerial() {
        return cookSerial;
    }

    public void setCookSerial(int cookSerial) {
        this.cookSerial = cookSerial;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getCommodityNumber() {
        return CommodityNumber;
    }

    public void setCommodityNumber(Double commodityNumber) {
        CommodityNumber = commodityNumber;
    }

    public Boolean getEdit() {
        return isEdit;
    }

    public void setEdit(Boolean edit) {
        isEdit = edit;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Override
    public String toString() {
        return "ComboEvent{" +
                "productBean=" + productBean +
                ", comboList=" + comboList +
                ", cookSerial=" + cookSerial +
                ", content='" + content + '\'' +
                ", CommodityNumber=" + CommodityNumber +
                ", isEdit=" + isEdit +
                '}';
    }
}
