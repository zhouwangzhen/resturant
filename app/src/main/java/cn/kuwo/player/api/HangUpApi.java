package cn.kuwo.player.api;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

import cn.kuwo.player.util.SharedHelper;

public class HangUpApi {
    public static AVObject saveHangUpOrder(AVObject avObject,String remamrk){
        AVObject hangUpOrder = new AVObject("HangUpOrder");
        hangUpOrder.put("store",1);
        hangUpOrder.put("customer",avObject.getInt("customer"));
        hangUpOrder.put("tableNumber","挂单"+avObject.getString("tableNumber"));
        hangUpOrder.put("cashier",AVObject.createWithoutData("_User", SharedHelper.read("cashierId")));
        if (avObject.getAVObject("user")!=null){
            hangUpOrder.put("user",avObject.getAVObject("user"));
        }
        hangUpOrder.put("startedAt",avObject.getDate("startedAt"));
        hangUpOrder.put("active",1);
        hangUpOrder.put("remark",remamrk);
        hangUpOrder.put("order",avObject.getList("order"));
        hangUpOrder.put("preOrder",avObject.getList("preOrder"));
        hangUpOrder.put("refundOrder",avObject.getList("refundOrder"));
        return hangUpOrder;
    }
    public static AVQuery<AVObject> getHangUpOrders(){
        AVQuery<AVObject> hangUpOrder = new AVQuery<>("HangUpOrder");
        hangUpOrder.whereEqualTo("active",1);
        hangUpOrder.orderByDescending("createdAt");
        return hangUpOrder;
    }

}
