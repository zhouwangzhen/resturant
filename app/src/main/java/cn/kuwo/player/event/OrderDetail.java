package cn.kuwo.player.event;

import com.avos.avoscloud.AVObject;

import java.io.Serializable;
import java.util.List;

import cn.kuwo.player.bean.UserBean;

public class OrderDetail implements Serializable {
    private AVObject avObject;
    private Double myMeatWeight;
    private Double totalMoney;
    private Double actualMoney;
    private Double maxReduceWeight;
    private Double maxReduceMoney;
    private Double myReduceWeight;
    private Double myReduceMoney;
    private Boolean isChooseReduce;
    private CouponEvent onlineCouponEvent;
    private CouponEvent offlineCouponEvent;
    private Double activityMoney;
    private Boolean isSvip;
    private List<Object> useExchangeList;
    private String useMeatId;
    private List<Object> svipMaxExchangeList;
    private UserBean userBean;
    private List<Object> orders;
    private List<Object> finalOrders;
    private List<String> selectTableIds;
    private List<String> selectTableNumbers;
    private Double fullReduceMoney;
    private boolean isHangUp;
    private Double deleteoddMoney;
    private int rate;
    private Double rateReduceMoney;

    public OrderDetail(AVObject avObject,
                       Double myMeatWeight,
                       Double totalMoney,
                       Double actualMoney,
                       Double maxReduceWeight,
                       Double maxReduceMoney,
                       Double myReduceWeight,
                       Double myReduceMoney,
                       Boolean isChooseReduce,
                       CouponEvent onlineCouponEvent,
                       CouponEvent offlineCouponEvent,
                       Double activityMoney,
                       Boolean isSvip,
                       List<Object> useExchangeList,
                       String useMeatId,
                       List<Object> svipMaxExchangeList,
                       List<Object> finalOrders,
                       List<String> selectTableIds,
                       List<String> selectTableNumbers,
                       Double fullReduceMoney,
                       boolean isHangUp,
                       Double deleteoddMoney,
                       int rate,
                       Double rateReduceMoney) {//餐饮版
        this.avObject = avObject;
        this.myMeatWeight = myMeatWeight;
        this.totalMoney = totalMoney;
        this.actualMoney = actualMoney;
        this.maxReduceWeight = maxReduceWeight;
        this.maxReduceMoney = maxReduceMoney;
        this.myReduceWeight = myReduceWeight;
        this.myReduceMoney = myReduceMoney;
        this.isChooseReduce = isChooseReduce;
        this.onlineCouponEvent = onlineCouponEvent;
        this.offlineCouponEvent = offlineCouponEvent;
        this.activityMoney = activityMoney;
        this.isSvip = isSvip;
        this.useExchangeList = useExchangeList;
        this.useMeatId = useMeatId;
        this.svipMaxExchangeList = svipMaxExchangeList;
        this.finalOrders = finalOrders;
        this.selectTableIds = selectTableIds;
        this.selectTableNumbers = selectTableNumbers;
        this.fullReduceMoney = fullReduceMoney;
        this.isHangUp=isHangUp;
        this.deleteoddMoney=deleteoddMoney;
        this.rate=rate;
        this.rateReduceMoney=rateReduceMoney;

    }

    public OrderDetail(AVObject avObject,
                       Double myMeatWeight,
                       Double totalMoney,
                       Double actualMoney,
                       Double maxReduceWeight,
                       Double maxReduceMoney,
                       Double myReduceWeight,
                       Double myReduceMoney,
                       Boolean isChooseReduce,
                       CouponEvent onlineCouponEvent,
                       CouponEvent offlineCouponEvent,
                       Double activityMoney,
                       Boolean isSvip,
                       List<Object> useExchangeList,
                       String useMeatId,
                       List<Object> svipMaxExchangeList,
                       UserBean userBean,
                       List<Object>orders,
                       Double fullReduceMoney,
                       Double deleteoddMoney,
                       int rate,
                       Double rateReduceMoney) {//零售版
        this.avObject = avObject;
        this.myMeatWeight = myMeatWeight;
        this.totalMoney = totalMoney;
        this.actualMoney = actualMoney;
        this.maxReduceWeight = maxReduceWeight;
        this.maxReduceMoney = maxReduceMoney;
        this.myReduceWeight = myReduceWeight;
        this.myReduceMoney = myReduceMoney;
        this.isChooseReduce = isChooseReduce;
        this.onlineCouponEvent = onlineCouponEvent;
        this.offlineCouponEvent = offlineCouponEvent;
        this.activityMoney = activityMoney;
        this.isSvip = isSvip;
        this.useExchangeList = useExchangeList;
        this.useMeatId = useMeatId;
        this.svipMaxExchangeList = svipMaxExchangeList;
        this.userBean = userBean;
        this.orders=orders;
        this.fullReduceMoney = fullReduceMoney;
        this.deleteoddMoney=deleteoddMoney;
        this.rate=rate;
        this.rateReduceMoney=rateReduceMoney;
    }

    public AVObject getAvObject() {
        return avObject;
    }

    public void setAvObject(AVObject avObject) {
        this.avObject = avObject;
    }

    public Double getMyMeatWeight() {
        return myMeatWeight;
    }

    public void setMyMeatWeight(Double myMeatWeight) {
        this.myMeatWeight = myMeatWeight;
    }

    public Double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public Double getActualMoney() {
        return actualMoney;
    }

    public void setActualMoney(Double actualMoney) {
        this.actualMoney = actualMoney;
    }

    public Double getMaxReduceWeight() {
        return maxReduceWeight;
    }

    public void setMaxReduceWeight(Double maxReduceWeight) {
        this.maxReduceWeight = maxReduceWeight;
    }

    public Double getMaxReduceMoney() {
        return maxReduceMoney;
    }

    public void setMaxReduceMoney(Double maxReduceMoney) {
        this.maxReduceMoney = maxReduceMoney;
    }

    public Double getMyReduceWeight() {
        return myReduceWeight;
    }

    public void setMyReduceWeight(Double myReduceWeight) {
        this.myReduceWeight = myReduceWeight;
    }

    public Double getMyReduceMoney() {
        return myReduceMoney;
    }

    public void setMyReduceMoney(Double myReduceMoney) {
        this.myReduceMoney = myReduceMoney;
    }

    public Boolean getChooseReduce() {
        return isChooseReduce;
    }

    public void setChooseReduce(Boolean chooseReduce) {
        isChooseReduce = chooseReduce;
    }

    public CouponEvent getOnlineCouponEvent() {
        return onlineCouponEvent;
    }

    public void setOnlineCouponEvent(CouponEvent onlineCouponEvent) {
        this.onlineCouponEvent = onlineCouponEvent;
    }

    public CouponEvent getOfflineCouponEvent() {
        return offlineCouponEvent;
    }

    public void setOfflineCouponEvent(CouponEvent offlineCouponEvent) {
        this.offlineCouponEvent = offlineCouponEvent;
    }

    public Double getActivityMoney() {
        return activityMoney;
    }

    public void setActivityMoney(Double activityMoney) {
        this.activityMoney = activityMoney;
    }

    public Boolean getSvip() {
        return isSvip;
    }

    public void setSvip(Boolean svip) {
        isSvip = svip;
    }

    public List<Object> getUseExchangeList() {
        return useExchangeList;
    }

    public void setUseExchangeList(List<Object> useExchangeList) {
        this.useExchangeList = useExchangeList;
    }

    public String getUseMeatId() {
        return useMeatId;
    }

    public void setUseMeatId(String useMeatId) {
        this.useMeatId = useMeatId;
    }

    public List<Object> getSvipMaxExchangeList() {
        return svipMaxExchangeList;
    }

    public void setSvipMaxExchangeList(List<Object> svipMaxExchangeList) {
        this.svipMaxExchangeList = svipMaxExchangeList;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public List<Object> getOrders() {
        return orders;
    }

    public void setOrders(List<Object> orders) {
        this.orders = orders;
    }

    public List<Object> getFinalOrders() {
        return finalOrders;
    }

    public void setFinalOrders(List<Object> finalOrders) {
        this.finalOrders = finalOrders;
    }

    public List<String> getSelectTableIds() {
        return selectTableIds;
    }

    public void setSelectTableIds(List<String> selectTableIds) {
        this.selectTableIds = selectTableIds;
    }

    public List<String> getSelectTableNumbers() {
        return selectTableNumbers;
    }

    public void setSelectTableNumbers(List<String> selectTableNumbers) {
        this.selectTableNumbers = selectTableNumbers;
    }

    public Double getFullReduceMoney() {
        return fullReduceMoney;
    }

    public void setFullReduceMoney(Double fullReduceMoney) {
        this.fullReduceMoney = fullReduceMoney;
    }

    public boolean isHangUp() {
        return isHangUp;
    }

    public void setHangUp(boolean hangUp) {
        isHangUp = hangUp;
    }

    public Double getDeleteoddMoney() {
        return deleteoddMoney;
    }

    public void setDeleteoddMoney(Double deleteoddMoney) {
        this.deleteoddMoney = deleteoddMoney;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public Double getRateReduceMoney() {
        return rateReduceMoney;
    }

    public void setRateReduceMoney(Double rateReduceMoney) {
        this.rateReduceMoney = rateReduceMoney;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "avObject=" + avObject +
                ", myMeatWeight=" + myMeatWeight +
                ", totalMoney=" + totalMoney +
                ", actualMoney=" + actualMoney +
                ", maxReduceWeight=" + maxReduceWeight +
                ", maxReduceMoney=" + maxReduceMoney +
                ", myReduceWeight=" + myReduceWeight +
                ", myReduceMoney=" + myReduceMoney +
                ", isChooseReduce=" + isChooseReduce +
                ", onlineCouponEvent=" + onlineCouponEvent +
                ", offlineCouponEvent=" + offlineCouponEvent +
                ", activityMoney=" + activityMoney +
                ", isSvip=" + isSvip +
                ", useExchangeList=" + useExchangeList +
                ", useMeatId='" + useMeatId + '\'' +
                ", svipMaxExchangeList=" + svipMaxExchangeList +
                ", userBean=" + userBean +
                ", orders=" + orders +
                ", finalOrders=" + finalOrders +
                '}';
    }
}
