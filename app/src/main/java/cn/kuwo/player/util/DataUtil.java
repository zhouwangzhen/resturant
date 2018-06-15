package cn.kuwo.player.util;

import com.avos.avoscloud.AVObject;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.RetailBean;
import cn.kuwo.player.event.ComboEvent;

public class DataUtil {
    /**
     * @param event         商品信息
     * @param tableAVObject 餐桌信息
     * @param isSvip        是否是超牛会员
     * @param mode          0:点菜模式 1：赠菜模式
     * @return 添加商品全部的信息
     */
    public static HashMap<String, Object> addHashMap(ComboEvent event,
                                                     AVObject tableAVObject,
                                                     Boolean isSvip,
                                                     String userId,
                                                     int mode) {
        String commodityId = event.getProductBean().getObjectId();
        ProductBean productBean = MyUtils.getProductById(commodityId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", commodityId);
        hashMap.put("number", event.getCommodityNumber());
        hashMap.put("comment", event.getContent());
        hashMap.put("name", event.getProductBean().getName());
        hashMap.put("weight", MyUtils.formatDouble(productBean.getWeight() * event.getCommodityNumber()));
        if (mode == 0) {
            hashMap.put("price", MyUtils.formatDouble(productBean.getPrice() * event.getCommodityNumber()));
        } else {
            hashMap.put("price", 0);
        }
        hashMap.put("mode", mode);
        hashMap.put("presenter", ProductUtil.calPresenter(tableAVObject, event.getProductBean(), isSvip));
        hashMap.put("cookSerial", event.getCookSerial());
        if (event.getComboList() != null && event.getComboList().size() > 0) {
            hashMap.put("comboList", event.getComboList());
        } else {
            hashMap.put("comboList", new ArrayList<Object>());
        }

        return hashMap;
    }


    /**
     * @param event         商品信息
     * @param preOrders     预下单订单
     * @param tableAVObject 餐桌信息
     * @param isSvip        是否是超牛会员
     */
    public static void updateIndexOder(ComboEvent event,
                                       List<Object> preOrders,
                                       AVObject tableAVObject,
                                       Boolean isSvip,
                                       String userId,
                                       int mode) {
        if (event.getOrderIndex() != -1) {
            if (event.getCommodityNumber() > 0) {
                String commodityId = event.getProductBean().getObjectId();
                ProductBean productBean = MyUtils.getProductById(commodityId);
                Object o = preOrders.get(event.getOrderIndex());
                HashMap<String, Object> format = ObjectUtil.format(o);
                format.put("id", event.getProductBean().getObjectId());
                format.put("number", event.getCommodityNumber());
                format.put("comment", event.getContent());
                format.put("name", event.getProductBean().getName());
                format.put("weight", MyUtils.formatDouble(productBean.getWeight() * event.getCommodityNumber()));
                if (mode == 0) {
                    format.put("price", MyUtils.formatDouble(productBean.getPrice() * event.getCommodityNumber()));
                } else {
                    format.put("price", 0);
                }
                format.put("presenter", ProductUtil.calPresenter(tableAVObject, event.getProductBean(), isSvip));
                Logger.d(event.getProductBean().getComboMenu());
                if (event.getProductBean().getComboMenu() != null && event.getProductBean().getComboMenu().length() > 0) {
                    format.put("comboList", event.getComboList());
                } else {
                    format.put("comboList", new ArrayList<Object>());
                }
                format.put("presenter", ProductUtil.calPresenter(tableAVObject, event.getProductBean(), isSvip));
                format.put("cookSerial", event.getCookSerial());
            } else {
                preOrders.remove(event.getOrderIndex());
            }
        }
    }


    public static RetailBean buildRetailBean(List<Object> orders) {
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> codes = new ArrayList<>();
        ArrayList<Double> prices = new ArrayList<>();
        ArrayList<Double> weight = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> o = (HashMap<String, Object>)orders.get(i);
            ids.add(ObjectUtil.getString(o, "id"));
            codes.add(ObjectUtil.getString(o, "code"));
            prices.add(ObjectUtil.getDouble(o, "price"));
            weight.add(ObjectUtil.getDouble(o, "weight"));
            name.add(ObjectUtil.getString(o, "id"));
        }
        return new RetailBean(ids, codes, prices, weight, name);
    }
}
