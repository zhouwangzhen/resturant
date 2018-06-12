package cn.kuwo.player.api;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

public class CommodityApi {
    public static AVQuery<AVObject> getOfflineCommodity(){
        final AVQuery<AVObject> query = new AVQuery<>("OfflineCommodity");
        query.addAscendingOrder("type");
        query.whereEqualTo("store", 1);
        query.addAscendingOrder("serial");
        query.limit(1000);
        return query;
    }
}
