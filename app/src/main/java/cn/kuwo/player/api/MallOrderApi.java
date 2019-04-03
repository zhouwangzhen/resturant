package cn.kuwo.player.api;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.DateUtil;

public class MallOrderApi {
    public static AVQuery<AVObject> findMallOrder(Date date) {
        try {
            AVObject mallOrderStatusFinsh = AVObject.createWithoutData("MallOrderStatus", CONST.OrderState.ORDER_STATUS_FINSIH);
            AVObject mallOrderStatusRefund = AVObject.createWithoutData("MallOrderStatus", CONST.OrderState.ORDER_STATUS_CANCEL);
            List<AVObject> orderSratus = new ArrayList<AVObject>();
            orderSratus.add(mallOrderStatusFinsh);
//            orderSratus.add(mallOrderStatusRefund);
            AVQuery<AVObject> mallOrder = new AVQuery<>("MallOrder");
            mallOrder.whereEqualTo("store", CONST.STORECODE);
            mallOrder.whereEqualTo("offline", true);
            mallOrder.whereEqualTo("active", 1);
            List<AVObject> testUsers = new ArrayList<>();
            for (int i=0;i< CONST.TESTUSERID.length;i++){
                testUsers.add(AVObject.createWithoutData("_User",CONST.TESTUSERID[i]));
            }
            if (!CONST.isShowTEST){
                mallOrder.whereNotContainedIn("user",testUsers);
                mallOrder.whereNotContainedIn("cashier",testUsers);
            }
            mallOrder.include("user");
            mallOrder.include("orderStatus");
            mallOrder.include("paymentType");
            mallOrder.include("cashier");
            mallOrder.include("market");
            mallOrder.include("useSystemCoupon.type");
            mallOrder.include("useUserCoupon.type");
            mallOrder.orderByDescending("createdAt");
            mallOrder.addDescendingOrder("endAt");
            mallOrder.whereGreaterThan("createdAt",  new Date(DateUtil.getZeroTimeStamp(date)));
            mallOrder.whereLessThan("createdAt", new Date(DateUtil.getLasterTimeStamp(date)));
            mallOrder.whereContainedIn("orderStatus", orderSratus);
            mallOrder.limit(CONST.MAX_LIMIT);
            return mallOrder;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
