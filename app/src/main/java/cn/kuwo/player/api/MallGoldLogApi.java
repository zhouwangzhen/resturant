package cn.kuwo.player.api;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.DateUtil;

public class MallGoldLogApi {
    public static AVQuery<AVObject> finalAllMallGold(Date date){
        try {
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
            mallGoldLog.whereGreaterThan("createdAt", new Date(DateUtil.getZeroTimeStamp(date)));
            mallGoldLog.include("cashier");
            mallGoldLog.include("user");
            mallGoldLog.whereLessThan("createdAt", new Date(DateUtil.getLasterTimeStamp(date)));
            return mallGoldLog;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }



    }
}
