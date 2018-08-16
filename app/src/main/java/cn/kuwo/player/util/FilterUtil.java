package cn.kuwo.player.util;

import com.avos.avoscloud.AVObject;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.kuwo.player.bean.ProductBean;

/**
 * Created by lovely on 2018/8/13
 */
public class FilterUtil {
    public static List<Object> getGiveList(List<AVObject> orders) {
        List<Object> freeCommoditys = new ArrayList<>();
        for (AVObject order : orders) {
            List<Object> comodityDetail = order.getList("commodityDetail");
            for (Object o:comodityDetail){
                HashMap<String, Object> format = ObjectUtil.format(o);
                if (ObjectUtil.getDouble(format,"price")==0.0){
                    if(!ObjectUtil.getString(format,"id").equals("5b42f7cd67f356003a71ddc8")){//囧囧兔
                        ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));

                        format.put("price",MyUtils.formatDouble(productBean.getPrice()*ObjectUtil.getDouble(format,"number")));
                        format.put("nb",MyUtils.formatDouble(productBean.getPrice()*ObjectUtil.getDouble(format,"number")));
                        freeCommoditys.add(format);
                    }
                }
            }
        }
        return freeCommoditys;
    }
}
