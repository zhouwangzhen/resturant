package cn.kuwo.player.api;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

public class CommodityTypeApi {
    public static AVQuery<AVObject> getCommodityType(){
        AVQuery<AVObject> query = new AVQuery<>("CommodityType");
        query.whereEqualTo("active", 1);
        query.whereEqualTo("store", 1);
        query.addAscendingOrder("number");
        return query;
    }
}
