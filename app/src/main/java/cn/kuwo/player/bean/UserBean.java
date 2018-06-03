package cn.kuwo.player.bean;

import java.io.Serializable;

public class UserBean implements Serializable{
    private int callbackCode;
    private String id;
    private String username;
    private String realName;
    private int vip;
    private Double credit;
    private Double stored;
    private Double balance;
    private Double meatWeight;
    private Boolean test;
    private int clerk;
    private String meatId;
    private Boolean isSvip;
    private Boolean alreadySvip;
    private String avatar;

    /**
     *
     * 销售扫描登录
     */
    public UserBean(int callbackCode,String id, String username,Double balance,Boolean test,int clerk) {
        this.callbackCode = callbackCode;
        this.id = id;
        this.username = username;
        this.balance=balance;
        this.test=test;
        this.clerk=clerk;
    }

    /**
     * 用户扫描登录
     */
    public UserBean(int callbackCode,String id, String username,String realName, int vip, Double credit, Double stored, Double balance,Boolean test,int clerk, Double meatWeight,String meatId,boolean isSvip,String avatar,boolean alreadySvip) {
        this.callbackCode = callbackCode;
        this.id = id;
        this.username = username;
        this.vip = vip;
        this.credit = credit;
        this.stored = stored;
        this.balance = balance;
        this.test=test;
        this.realName=realName;
        this.clerk=clerk;
        this.meatWeight = meatWeight;
        this.meatId=meatId;
        this.isSvip=isSvip;
        this.avatar=avatar;
        this.alreadySvip=alreadySvip;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Double getStored() {
        return stored;
    }

    public void setStored(Double stored) {
        this.stored = stored;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getMeatWeight() {
        return meatWeight;
    }

    public void setMeatWeight(Double meatWeight) {
        this.meatWeight = meatWeight;
    }

    public int getCallbackCode() {
        return callbackCode;
    }

    public void setCallbackCode(int callbackCode) {
        this.callbackCode = callbackCode;
    }

    public Boolean getTest() {
        return test;
    }

    public void setTest(Boolean test) {
        this.test = test;
    }

    public int getClerk() {
        return clerk;
    }

    public void setClerk(int clerk) {
        this.clerk = clerk;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getMeatId() {
        return meatId;
    }

    public void setMeatId(String meatId) {
        this.meatId = meatId;
    }

    public Boolean getSvip() {
        return isSvip;
    }

    public void setSvip(Boolean svip) {
        isSvip = svip;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getAlreadySvip() {
        return alreadySvip;
    }

    public void setAlreadySvip(Boolean alreadySvip) {
        this.alreadySvip = alreadySvip;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "callbackCode=" + callbackCode +
                ", id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", vip=" + vip +
                ", credit=" + credit +
                ", stored=" + stored +
                ", balance=" + balance +
                ", meatWeight=" + meatWeight +
                ", test=" + test +
                ", clerk=" + clerk +
                ", meatId='" + meatId + '\'' +
                ", isSvip=" + isSvip +
                ", alreadySvip=" + alreadySvip +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}

