package cn.kuwo.player.util;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by lovely on 2018/7/13
 */
public class ScriptUtil {
    public static void syncOfflineWithCommodityData(){
        AVQuery<AVObject> query = new AVQuery<>("OfflineCommodity");
        query.whereEqualTo("store",1);
        query.whereEqualTo("active",1);
        query.include("commodity");
        query.limit(1000);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e==null){
                    for (int i=0;i<list.size();i++){
                        AVObject avObject = list.get(i);
                        AVObject commodity = avObject.getAVObject("commodity");
                        if (commodity.getInt("active")!=1) {
                            commodity.put("name", avObject.getString("name"));
                            commodity.put("price", avObject.getDouble("price"));
                            commodity.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        Logger.d("成功");
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }
    public static void resetStyle(){
        final AVQuery<AVObject> query = new AVQuery<>("OfflineCommodity");
        query.whereEqualTo("active", 1);
        query.whereEqualTo("store", 1);
        query.limit(1000);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    Logger.d(list.size());
                    for (int i = 0; i < list.size(); i++) {
                        final AVObject avObject = list.get(i);
                        AVQuery<AVObject> query1 = new AVQuery<>("CommodityType");
                        query1.whereEqualTo("store", 1);
                        query1.whereEqualTo("active", 1);
                        query1.whereEqualTo("number",avObject.getInt("type"));
                        query1.getFirstInBackground(new GetCallback<AVObject>() {
                            @Override
                            public void done(AVObject avObjects, AVException e) {
                                if (e==null){
                                    avObject.put("style", AVObject.createWithoutData("CommodityType",avObjects.getObjectId()));
                                    avObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            if (e==null){
                                                Logger.d("修改成功");
                                            }
                                        }
                                    });
                                }
                            }
                        });

                    }
                }
            }
        });
    }
}
