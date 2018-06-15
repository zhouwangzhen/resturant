package cn.kuwo.player.util;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatisticsUtil {
    public static LinkedHashMap<String, Double> getCapitalDetail(List<AVObject> orders, List<AVObject> rechargeOrders) {
        LinkedHashMap<String, Double> capitalDetail = new LinkedHashMap<>();
        capitalDetail.put("白条", 0.0);
        capitalDetail.put("消费金", 0.0);
        capitalDetail.put("支付宝", 0.0);
        capitalDetail.put("微信", 0.0);
        capitalDetail.put("银行卡", 0.0);
        capitalDetail.put("现金", 0.0);
        capitalDetail.put("招商信用卡", 0.0);
        capitalDetail.put("浦发信用卡", 0.0);
        for (AVObject recharge : rechargeOrders) {
            switch (recharge.getInt("escrow")) {
                case 3:
                    capitalDetail.put("支付宝", capitalDetail.get("支付宝") + recharge.getDouble("change"));
                    break;
                case 4:
                    capitalDetail.put("微信", capitalDetail.get("微信") + recharge.getDouble("change"));
                    break;
                case 5:
                    capitalDetail.put("银行卡", capitalDetail.get("银行卡") + recharge.getDouble("change"));
                    break;
                case 6:
                    capitalDetail.put("现金", capitalDetail.get("现金") + recharge.getDouble("change"));
                    break;
            }
        }
        for (AVObject order : orders) {
            Double actualMoney = order.getDouble("paysum") - order.getDouble("reduce");
            double actuallyPaid = order.getDouble("actuallyPaid");
            switch (order.getInt("escrow")) {
                case 1:
                    capitalDetail.put("消费金", capitalDetail.get("消费金") + actualMoney);
                    break;
                case 3:
                    capitalDetail.put("支付宝", capitalDetail.get("支付宝") + actualMoney);
                    break;
                case 4:
                    capitalDetail.put("微信", capitalDetail.get("微信") + actualMoney);
                    break;
                case 5:
                    capitalDetail.put("银行卡", capitalDetail.get("银行卡") + actualMoney);
                    break;
                case 6:
                    capitalDetail.put("现金", capitalDetail.get("现金") + actualMoney);
                    break;
                case 7:
                    capitalDetail.put("消费金", capitalDetail.get("消费金") + actuallyPaid);
                    capitalDetail.put("支付宝", capitalDetail.get("支付宝") + actualMoney - actuallyPaid);
                    break;
                case 8:
                    capitalDetail.put("消费金", capitalDetail.get("消费金") + actuallyPaid);
                    capitalDetail.put("微信", capitalDetail.get("微信") + actualMoney - actuallyPaid);
                    break;
                case 9:
                    capitalDetail.put("消费金", capitalDetail.get("消费金") + actuallyPaid);
                    capitalDetail.put("银行卡", capitalDetail.get("银行卡") + actualMoney - actuallyPaid);
                    break;
                case 10:
                    capitalDetail.put("消费金", capitalDetail.get("消费金") + actuallyPaid);
                    capitalDetail.put("现金", capitalDetail.get("现金") + actualMoney - actuallyPaid);
                    break;
                case 11:
                    capitalDetail.put("白条", capitalDetail.get("白条") + actualMoney);
                    break;
                case 12:
                    capitalDetail.put("消费金", capitalDetail.get("消费金") + actualMoney);
                    break;
                case 13:
                    capitalDetail.put("白条", capitalDetail.get("白条") + actuallyPaid);
                    capitalDetail.put("支付宝", capitalDetail.get("支付宝") + actualMoney - actuallyPaid);
                    break;
                case 14:
                    capitalDetail.put("白条", capitalDetail.get("白条") + actuallyPaid);
                    capitalDetail.put("微信", capitalDetail.get("微信") + actualMoney - actuallyPaid);
                    break;
                case 15:
                    capitalDetail.put("白条", capitalDetail.get("白条") + actuallyPaid);
                    capitalDetail.put("银行卡", capitalDetail.get("银行卡") + actualMoney - actuallyPaid);
                    break;
                case 16:
                    capitalDetail.put("白条", capitalDetail.get("白条") + actuallyPaid);
                    capitalDetail.put("现金", capitalDetail.get("现金") + actualMoney - actuallyPaid);
                    break;
                case 17:
                    capitalDetail.put("消费金", capitalDetail.get("消费金") + actuallyPaid);
                    capitalDetail.put("支付宝", capitalDetail.get("支付宝") + actualMoney - actuallyPaid);
                    break;
                case 18:
                    capitalDetail.put("消费金", capitalDetail.get("消费金") + actuallyPaid);
                    capitalDetail.put("微信", capitalDetail.get("微信") + actualMoney - actuallyPaid);
                    break;
                case 19:
                    capitalDetail.put("消费金", capitalDetail.get("消费金") + actuallyPaid);
                    capitalDetail.put("银行卡", capitalDetail.get("银行卡") + actualMoney - actuallyPaid);
                    break;
                case 20:
                    capitalDetail.put("消费金", capitalDetail.get("消费金") + actuallyPaid);
                    capitalDetail.put("现金", capitalDetail.get("现金") + actualMoney - actuallyPaid);
                    break;
                case 21:
                    capitalDetail.put("招商信用卡", capitalDetail.get("招商信用卡") + actualMoney);
                    break;
                case 22:
                    capitalDetail.put("浦发信用卡", capitalDetail.get("浦发信用卡") + actualMoney);
                    break;

            }
        }
        return capitalDetail;
    }

    /**
     * 统计总单的信息
     */
    public static HashMap<String, Object> TotalOrder(List<AVObject> orders, List<AVObject> rechargeOrders) {
        HashMap<String, Object> detail = new HashMap<>();
        HashMap<String, Double> numbers = new HashMap<>();
        HashMap<String, Double> weights = new HashMap<>();
        LinkedHashMap<String, Double> capitalDetail = new LinkedHashMap<>();
        HashMap<String, Integer> offlineCoupon = new HashMap<>();
        HashMap<String, Integer> onlineCoupon = new HashMap<>();
        HashMap<Integer, Integer> orderTypes = new HashMap<>();
        Double online = 0.0;
        Double offline = 0.0;
        int retail = 0;
        int restaurarnt = 0;
        int svip = 0;
        int hangup = 0;
        int member = 0;
        int noMember = 0;
        int recharge = rechargeOrders.size();
        Double reduceWeight = 0.0;
        for (AVObject rechargeOrder : rechargeOrders) {
            offline += rechargeOrder.getDouble("change");
        }
        capitalDetail=getCapitalDetail(orders,rechargeOrders);
        for (AVObject order : orders) {
            Double actualMoney = order.getDouble("paysum") - order.getDouble("reduce");
            online += order.getDouble("actuallyPaid");
            offline += actualMoney - order.getDouble("actuallyPaid");
            if (order.getInt("type") == 0) {
                restaurarnt++;
            } else if (order.getInt("type") == 1) {
                retail++;
            } else if (order.getInt("type") == 2) {
                svip++;
            } else if (order.getInt("type") == 3) {
                hangup++;
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
            if (orderTypes.containsKey(order.getInt("type"))) {
                orderTypes.put(order.getInt("type"), orderTypes.get(order.getInt("type")) + 1);
            } else {
                orderTypes.put(order.getInt("type"), 1);
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
        detail.put("svipNumber", svip + "单");
        detail.put("hangupNumber", hangup + "单");
        detail.put("reduceWeight", MyUtils.formatDouble(reduceWeight) + "kg");
        detail.put("numbers", list);
        detail.put("weights", weights);
        detail.put("offlineCoupon", offlineCoupon);
        detail.put("onlineCoupon", onlineCoupon);
        detail.put("orderTypes", orderTypes);
        detail.put("capitalDetail", capitalDetail);
        detail.put("storedRechargeNumber", recharge + "单");
        Logger.d(detail);
        return detail;
    }
}
