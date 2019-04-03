package cn.kuwo.player.util;

import android.text.TextUtils;

import com.avos.avoscloud.AVObject;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.RuleBean;
import cn.kuwo.player.bean.entity.SideDishEntity;
import io.realm.RealmList;

public class RealmUtil {
    public static void setProductBeanRealm(List<AVObject> list) {
        final RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        mRealmHleper.deleteAll(ProductBean.class);
        for (int i = 0; i < list.size(); i++) {
            AVObject avObject = list.get(i);
            ProductBean productBean = new ProductBean();
            productBean.setName(avObject.get("name").toString());
            productBean.setCode(avObject.get("code").toString());
            productBean.setObjectId(avObject.getAVObject("commodity").getObjectId());
            productBean.setPrice(avObject.getDouble("price"));
            productBean.setWeight(avObject.getDouble("weight"));
            productBean.setType(avObject.getInt("type"));
            productBean.setSale(avObject.getInt("sale"));
            productBean.setCombo(avObject.getInt("combo"));
            productBean.setRate(avObject.getDouble("rate"));
            productBean.setPerformance(avObject.getInt("performance"));
            productBean.setGivecode(TextUtils.isEmpty(avObject.getString("givecode")) ? "" : avObject.getString("givecode"));
            productBean.setStore(avObject.getInt("store"));
            productBean.setMeatWeight(avObject.getDouble("meatWeight"));
            productBean.setSerial(avObject.getString("serial"));
            productBean.setUrl(avObject.getAVFile("avatar").getUrl());
            productBean.setScale(avObject.getDouble("scale"));
            productBean.setRemainMoney(avObject.getDouble("remainMoney"));
            productBean.setActive(avObject.getInt("active"));
            productBean.setNb(avObject.getDouble("nb"));
            productBean.setComboMenu(avObject.getString("comboMenu") == null ? "" : MyUtils.replaceBlank(avObject.getString("comboMenu").trim().replace(" ", "")));
            productBean.setNbDiscountType(avObject.getInt("nb_discount_type"));
            productBean.setNbDiscountRate(avObject.getDouble("nb_discount_rate"));
            productBean.setNbDiscountPrice(avObject.getDouble("nb_discount_price"));
            productBean.setSpecial(avObject.getString("special"));
            RealmList<String> commentsList = new RealmList<>();
            for (int k = 0; k < avObject.getList("comments").size(); k++) {
                commentsList.add(avObject.getList("comments").get(k).toString());
            }
            productBean.setGiveRule(avObject.getInt("giveRule"));
            productBean.setComments(commentsList);
            productBean.setReviewCommodity(avObject.getString("reviewCommodity"));
            productBean.setMerge(avObject.getBoolean("merge"));
            productBean.setClassify(avObject.getInt("classify"));
            if (avObject.getList("sideDish") != null && avObject.getList("sideDish").size() > 0) {
                List<HashMap<String,Object>> sideDish = (List<HashMap<String,Object>>) avObject.getList("sideDish");
                RealmList<SideDishEntity> objects = new RealmList<>();
                for (int j = 0; j < sideDish.size(); j++) {
                    SideDishEntity sideDishEntity = new SideDishEntity();
                    sideDishEntity.setName(sideDish.get(j).get("name").toString());
                    sideDishEntity.setPrice(Double.parseDouble(sideDish.get(j).get("price").toString()));
                    objects.add(sideDishEntity);
                }
                productBean.setSidedish(objects);
                Logger.d(productBean.getSidedish());
            } else {
                productBean.setSidedish(new RealmList<SideDishEntity>());
            }
            mRealmHleper.addProduct(productBean);
        }
    }

    public static void setRuleBeanRealm(List<AVObject> list) {
        RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        for (int i = 0; i < list.size(); i++) {
            mRealmHleper.deleteAll(RuleBean.class);
            AVObject avObject = list.get(i);
            RuleBean ruleBean = new RuleBean();
            ruleBean.setNoMemberJoin(avObject.getBoolean("noMemberJoin"));
            ruleBean.setDrinkJoin(avObject.getBoolean("drinkJoin"));
            ruleBean.setOnlyMeatJoin(avObject.getBoolean("onlyMeatJoin"));
            ruleBean.setMemberNoMoneyJoin(avObject.getBoolean("MemberNoMoneyJoin"));
            ruleBean.setAllDiscount(avObject.getDouble("allDiscount"));
            ruleBean.setDiscountContent(avObject.getString("discountContent"));
            ruleBean.setNoMemberBOGO(avObject.getBoolean("NoMemberBOGO"));
            ruleBean.setMemberDiscountJoin(avObject.getBoolean("MemberDiscountJoin"));
            ruleBean.setWineJoin(avObject.getBoolean("wineJoin"));
            ruleBean.setFoldOnFoldJoin(avObject.getBoolean("foldOnFoldJoin"));
            RealmList<String> reduceList = new RealmList<>();
            for (int k = 0; k < avObject.getList("fullReduce").size(); k++) {
                reduceList.add(avObject.getList("fullReduce").get(k).toString());
            }
            ruleBean.setFullReduce(reduceList);
            mRealmHleper.addRule(ruleBean);
        }
    }
}
