package cn.kuwo.player.api;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

import cn.kuwo.player.util.CONST;

public class CommodityApi {
    public static AVQuery<AVObject> getOfflineCommodity(){
        final AVQuery<AVObject> query = new AVQuery<>("OfflineCommodity");
        query.addAscendingOrder("type");
        query.whereEqualTo("store", 1);
        query.addAscendingOrder("serial");
        query.limit(CONST.MAX_LIMIT);
        return query;
    }
}
