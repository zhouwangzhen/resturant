package cn.kuwo.player.bean;

import io.realm.RealmObject;

public class RuleBean extends RealmObject {
    private Boolean noMemberJoin;
    private Boolean drinkJoin;
    private Boolean onlyMeatJoin;
    private Boolean MemberNoMoneyJoin;
    private Double allDiscount;
    private String discountContent;
    private Boolean noMemberBOGO;
    private Boolean memberDiscountJoin;
    private Boolean wineJoin;
    private Boolean foldOnFoldJoin;

    public Boolean getNoMemberJoin() {
        return noMemberJoin;
    }

    public void setNoMemberJoin(Boolean noMemberJoin) {
        this.noMemberJoin = noMemberJoin;
    }

    public Boolean getDrinkJoin() {
        return drinkJoin;
    }

    public void setDrinkJoin(Boolean drinkJoin) {
        this.drinkJoin = drinkJoin;
    }

    public Boolean getOnlyMeatJoin() {
        return onlyMeatJoin;
    }

    public void setOnlyMeatJoin(Boolean onlyMeatJoin) {
        this.onlyMeatJoin = onlyMeatJoin;
    }

    public Boolean getMemberNoMoneyJoin() {
        return MemberNoMoneyJoin;
    }

    public void setMemberNoMoneyJoin(Boolean memberNoMoneyJoin) {
        MemberNoMoneyJoin = memberNoMoneyJoin;
    }

    public Double getAllDiscount() {
        return allDiscount;
    }

    public void setAllDiscount(Double allDiscount) {
        this.allDiscount = allDiscount;
    }

    public String getDiscountContent() {
        return discountContent;
    }

    public void setDiscountContent(String discountContent) {
        this.discountContent = discountContent;
    }

    public Boolean getNoMemberBOGO() {
        return noMemberBOGO;
    }

    public void setNoMemberBOGO(Boolean noMemberBOGO) {
        this.noMemberBOGO = noMemberBOGO;
    }

    public Boolean getMemberDiscountJoin() {
        return memberDiscountJoin;
    }

    public void setMemberDiscountJoin(Boolean memberDiscountJoin) {
        this.memberDiscountJoin = memberDiscountJoin;
    }

    public Boolean getWineJoin() {
        return wineJoin;
    }

    public void setWineJoin(Boolean wineJoin) {
        this.wineJoin = wineJoin;
    }

    public Boolean getFoldOnFoldJoin() {
        return foldOnFoldJoin;
    }

    public void setFoldOnFoldJoin(Boolean foldOnFoldJoin) {
        this.foldOnFoldJoin = foldOnFoldJoin;
    }

    @Override
    public String toString() {
        return "RuleBean{" +
                "noMemberJoin=" + noMemberJoin +
                ", drinkJoin=" + drinkJoin +
                ", onlyMeatJoin=" + onlyMeatJoin +
                ", MemberNoMoneyJoin=" + MemberNoMoneyJoin +
                ", allDiscount=" + allDiscount +
                ", discountContent='" + discountContent + '\'' +
                ", noMemberBOGO=" + noMemberBOGO +
                ", memberDiscountJoin=" + memberDiscountJoin +
                ", wineJoin=" + wineJoin +
                ", foldOnFoldJoin=" + foldOnFoldJoin +
                '}';
    }
}
