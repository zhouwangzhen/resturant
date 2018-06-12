package cn.kuwo.player.util;

import com.avos.avoscloud.AVObject;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsUtil {
    /**
     * 统计总单的信息
     */
    public static HashMap<String, Object> TotalOrder(List<AVObject> orders) {
        HashMap<String, Object> detail = new HashMap<>();
        HashMap<String, Double> numbers = new HashMap<>();
        HashMap<String, Double> weights = new HashMap<>();
        HashMap<String, Integer> offlineCoupon = new HashMap<>();
        HashMap<String, Integer> onlineCoupon = new HashMap<>();
        HashMap<Integer, Integer> orderTypes = new HashMap<>();
        Double online = 0.0;
        Double offline = 0.0;
        int retail = 0;
        int restaurarnt = 0;
        int member = 0;
        int noMember = 0;
        Double reduceWeight = 0.0;
        for (AVObject order : orders) {
            Double actualMoney = order.getDouble("paysum") - order.getDouble("reduce");
            online += order.getDouble("actuallyPaid");
            offline += actualMoney - order.getDouble("actuallyPaid");
            if (order.getString("tableNumber") != null && order.getDate("startedAt") != null) {
                retail++;
            } else {
                restaurarnt++;
            }
            if (!order.getAVObject("user").getObjectId().equals(CONST.ACCOUNT.SYSTEMACCOUNT)) {
                member++;
            } else {
                noMember++;
            }
            if (order.getList("meatWeights").size() > 0) {
                for (int i = 0; i < order.getList("meatWeights").size(); i++) {
                    reduceWeight += MyUtils.formatDouble(Double.parseDouble(order.getList("meatWeights").get(i) + ""));
                }
            }
            List commodityDetail = order.getList("commodityDetail");
            for (int j = 0; j < commodityDetail.size(); j++) {
                HashMap<String, Object> format = ObjectUtil.format(commodityDetail.get(j));
                String name = ObjectUtil.getString(format, "name");
                if (numbers.containsKey(name)) {
                    numbers.put(name, numbers.get(name) + ObjectUtil.getDouble(format, "number"));
                } else {
                    numbers.put(name, ObjectUtil.getDouble(format, "number"));
                }
                if (order.getInt("type") == 1) {
                    if (weights.containsKey(name)) {
                        weights.put(name, MyUtils.formatDouble(weights.get(name) + ObjectUtil.getDouble(format, "weight")));
                    } else {
                        Logger.d(format);
                        weights.put(name, MyUtils.formatDouble(ObjectUtil.getDouble(format, "weight")));
                    }
                }
            }
            if (order.getAVObject("useSystemCoupon") != null) {
                String name = order.getAVObject("useSystemCoupon").getAVObject("type").getString("name");
                if (offlineCoupon.containsKey(name)) {
                    offlineCoupon.put(name, offlineCoupon.get(name) + 1);
                } else {
                    offlineCoupon.put(name, 1);
                }
            }
            if (order.getAVObject("useUserCoupon") != null) {
                String name = order.getAVObject("useUserCoupon").getAVObject("type").getString("name");
                if (onlineCoupon.containsKey(name)) {
                    onlineCoupon.put(name, onlineCoupon.get(name) + 1);
                } else {
                    onlineCoupon.put(name, 1);
                }
            }
            if (orderTypes.containsKey(order.getInt("type"))){
                orderTypes.put(order.getInt("type"),orderTypes.get(order.getInt("type"))+1);
            }else{
                orderTypes.put(order.getInt("type"),1);
            }
        }
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(numbers.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        detail.put("onlineMoney", MyUtils.formatDouble(online) + "元");
        detail.put("offlineMoney", MyUtils.formatDouble(offline) + "元");
        detail.put("totalMoney", MyUtils.formatDouble(offline + online) + "元");
        detail.put("member", member + "单");
        detail.put("noMember", noMember + "单");
        detail.put("retailNumber", retail + "单");
        detail.put("restaurarntNumber", restaurarnt + "单");
        detail.put("reduceWeight", MyUtils.formatDouble(reduceWeight) + "kg");
        detail.put("numbers", list);
        detail.put("weights", weights);
        detail.put("offlineCoupon", offlineCoupon);
        detail.put("onlineCoupon", onlineCoupon);
        detail.put("orderTypes", orderTypes);
        Logger.d(detail);
        return detail;
    }
}
