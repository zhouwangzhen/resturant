package cn.kuwo.player.api;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

public class RuleApi {
    public static AVQuery<AVObject> getRule(){
        AVQuery<AVObject> query = new AVQuery<>("OffineControl");
        query.whereEqualTo("store", 1);
        query.whereEqualTo("active", 1);
        return query;
    }
}
