package cn.kuwo.player.api;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

import java.util.ArrayList;
import java.util.HashMap;

import cn.kuwo.player.bean.RetailBean;
import cn.kuwo.player.util.SharedHelper;

public class HangUpApi {
    public static AVObject saveHangUpOrder(AVObject avObject, String remamrk) {
        AVObject hangUpOrder = new AVObject("HangUpOrder");
        hangUpOrder.put("store", 1);
        hangUpOrder.put("customer", avObject.getInt("customer"));
        hangUpOrder.put("tableNumber", "挂单" + avObject.getString("tableNumber"));
        hangUpOrder.put("cashier", AVObject.createWithoutData("_User", SharedHelper.read("cashierId")));
        if (avObject.getAVObject("user") != null) {
            hangUpOrder.put("user", avObject.getAVObject("user"));
        }
        hangUpOrder.put("startedAt", avObject.getDate("startedAt"));
        hangUpOrder.put("active", 1);
        hangUpOrder.put("remark", remamrk);
        hangUpOrder.put("order", avObject.getList("order"));
        hangUpOrder.put("preOrder", avObject.getList("preOrder"));
        hangUpOrder.put("refundOrder", avObject.getList("refundOrder"));
        return hangUpOrder;
    }

    public static AVQuery<AVObject> getHangUpOrders() {
        AVQuery<AVObject> hangUpOrder = new AVQuery<>("HangUpOrder");
        hangUpOrder.whereEqualTo("active", 1);
        hangUpOrder.orderByDescending("createdAt");
        return hangUpOrder;
    }

    public static AVObject saveHangUpOrderByRest(RetailBean retailBean, String remamrk) {
        AVObject hangUpOrder = new AVObject("HangUpOrder");
        ArrayList<Object> orders=new ArrayList<>();
        for (int i=0;i<retailBean.getIds().size();i++){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("code",retailBean.getCodes().get(i));
            hashMap.put("id",retailBean.getIds().get(i));
            hashMap.put("price",retailBean.getPrices().get(i));
            hashMap.put("weight",retailBean.getWeight().get(i));
            hashMap.put("name",retailBean.getName().get(i));
            hashMap.put("number",1);
            orders.add(hashMap);
        }
        hangUpOrder.put("order",orders);
        hangUpOrder.put("type",1);
        hangUpOrder.put("remark", remamrk);
        return hangUpOrder;
    }
}
