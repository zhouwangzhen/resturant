package cn.kuwo.player.api;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.SharedHelper;

/**
 * Created by lovely on 2018/6/18
 */
public class InventoryApi {
    public static AVObject addInventory(int type, HashMap<String,Object>map){
        AVObject offlineInventory = new AVObject("OfflineInventory");
        offlineInventory.put("commodityDetail",map);
        offlineInventory.put("store",1);
        offlineInventory.put("type",type);
        offlineInventory.put("operator", AVObject.createWithoutData("_User",SharedHelper.read("cashierId")));
        return offlineInventory;
    }

    public static AVQuery<AVObject> finalCurrentOrder(Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Long time = date.getTime();
            Long nextTime = time + 24 * 60 * 60 * 1000;
            String d = sdf.format(nextTime);
            Date nextDate = null;
            nextDate = sdf.parse(d);
            AVQuery<AVObject> query = new AVQuery<>("OfflineInventory");
            query.whereEqualTo("store",1);
            List<AVObject> testUsers = new ArrayList<>();
            for (int i=0;i< CONST.TESTUSERID.length;i++){
                testUsers.add(AVObject.createWithoutData("_User",CONST.TESTUSERID[i]));
            }
            if (!CONST.isShowTEST){
                query.whereNotContainedIn("operator",testUsers);
            }
            query.whereGreaterThan("createdAt", date);
            query.whereLessThan("createdAt", nextDate);
            query.addDescendingOrder("createdAt");
            return query;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
