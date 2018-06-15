package cn.kuwo.player.api;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.kuwo.player.util.CONST;

public class MallGoldLogApi {
    public static AVQuery<AVObject> finalAllMallGold(Date date){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Long time = date.getTime();
            Long nextTime = time + 24 * 60 * 60 * 1000;
            String d = sdf.format(nextTime);
            Date nextDate = null;
            nextDate = sdf.parse(d);
            AVQuery<AVObject> mallGoldLog = new AVQuery<>("MallGoldLog");
            mallGoldLog.whereEqualTo("store",1);
            List<AVObject> testUsers = new ArrayList<>();
            for (int i=0;i< CONST.TESTUSERID.length;i++){
                testUsers.add(AVObject.createWithoutData("_User",CONST.TESTUSERID[i]));
            }
            if (!CONST.isShowTEST){
                mallGoldLog.whereNotContainedIn("user",testUsers);
                mallGoldLog.whereNotContainedIn("cashier",testUsers);
            }
            mallGoldLog.whereGreaterThan("createdAt", date);
            mallGoldLog.include("cashier");
            mallGoldLog.include("user");
            mallGoldLog.whereLessThan("createdAt", nextDate);
            return mallGoldLog;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }



    }
}
