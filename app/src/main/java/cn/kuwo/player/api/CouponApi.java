package cn.kuwo.player.api;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

import java.util.ArrayList;
import java.util.Date;

public class CouponApi {
    public static AVQuery<AVObject> getCouponOffline(){
        AVQuery<AVObject> coupon = new AVQuery<>("Coupon");
        coupon.whereEqualTo("active", 1);
        coupon.include("type");
        coupon.whereEqualTo("username", "13888888888");
        return coupon;

    }
    public static AVQuery<AVObject> getCouponOnline(String tel){
        AVQuery<AVObject> couponType = new AVQuery<>("CouponType");
        ArrayList<Integer> types = new ArrayList<>();
        types.add(-1);
        types.add(2);
        couponType.whereContainedIn("store", types);
        AVQuery<AVObject> coupon = new AVQuery<>("Coupon");
        coupon.whereGreaterThan("end", new Date());
        coupon.whereMatchesQuery("type", couponType);
        coupon.whereEqualTo("username", tel);
        coupon.whereEqualTo("use", 0);
        coupon.include("type");
        return coupon;
    }
}
