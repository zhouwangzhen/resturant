package cn.kuwo.player.util;

import com.avos.avoscloud.AVObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.event.ComboEvent;

public class DataUtil {
    /**
     * @param event         商品信息
     * @param tableAVObject 餐桌信息
     * @param isSvip        是否是超牛会员
     * @return 添加商品全部的信息
     */
    public static HashMap<String, Object> addHashMap(ComboEvent event,
                                                     AVObject tableAVObject,
                                                     Boolean isSvip) {
        String commodityId = event.getProductBean().getObjectId();
        ProductBean productBean = MyUtils.getProductById(commodityId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", commodityId);
        hashMap.put("number", event.getCommodityNumber());
        hashMap.put("comment", event.getContent());
        hashMap.put("name", event.getProductBean().getName());
        hashMap.put("weight", productBean.getWeight());
        hashMap.put("price", MyUtils.formatDouble(productBean.getPrice() * event.getCommodityNumber()));
        hashMap.put("presenter", ProductUtil.calPresenter(tableAVObject, event.getProductBean(), isSvip));
        hashMap.put("cookSerial", event.getCookSerial());
        if (event.getProductBean().getComboMenu() != null && event.getProductBean().getComboMenu().length() > 0) {
            hashMap.put("comboList", event.getComboList());
        } else {
            hashMap.put("comboList", new ArrayList<>());
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
                                       Boolean isSvip) {
        if (event.getOrderIndex() != -1) {
            if (event.getCommodityNumber() > 0) {
                Object o = preOrders.get(event.getOrderIndex());
                HashMap<String, Object> format = ObjectUtil.format(o);
                format.put("id", event.getProductBean().getObjectId());
                format.put("number", event.getCommodityNumber());
                format.put("comment", event.getContent());
                format.put("name", event.getProductBean().getName());
                if (event.getProductBean().getComboMenu() != null && event.getProductBean().getComboMenu().length() > 0) {
                    format.put("comboList", event.getComboList());
                } else {
                    format.put("comboList", new ArrayList<>());
                }
                format.put("presenter", ProductUtil.calPresenter(tableAVObject, event.getProductBean(), isSvip));
                format.put("cookSerial", event.getCookSerial());
            }
        }else{
            preOrders.remove(event.getOrderIndex());
        }
    }

}
