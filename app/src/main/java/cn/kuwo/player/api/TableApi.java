package cn.kuwo.player.api;

import com.avos.avoscloud.AVObject;

import java.util.List;

public class TableApi {
    public static AVObject clearTable(AVObject avObject){
        avObject.put("order", new List[0]);
        avObject.put("preOrder", new List[0]);
        avObject.put("refundOrder", new List[0]);
        avObject.put("customer", 0);
        avObject.put("startedAt", null);
        avObject.put("user", null);
        return avObject;
    }
}
