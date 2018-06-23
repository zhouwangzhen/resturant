package cn.kuwo.player.api;

import android.widget.TextView;

import com.avos.avoscloud.AVObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.QueryUtil;
import cn.kuwo.player.util.SharedHelper;

public class TableApi {
    /**
     * 清空桌子信息
     */
    public static AVObject clearTable(AVObject avObject) {
        avObject.put("order", new List[0]);
        avObject.put("preOrder", new List[0]);
        avObject.put("refundOrder", new List[0]);
        avObject.put("customer", 0);
        avObject.put("startedAt", null);
        avObject.put("user", null);
        return avObject;
    }

    /**
     * 下单新商品
     */
    public static AVObject addOrder(
            AVObject tableAVObject,
            List<Object> preOrders) {
        List<Object> newOrders = new ArrayList<>();
        List oldOrders = tableAVObject.getList("order");
        newOrders.addAll(oldOrders);
        for (int i = 0; i < preOrders.size(); i++) {
            newOrders.add(preOrders.get(i));
        }
        tableAVObject.put("order", newOrders);
        tableAVObject.put("preOrder", new List[0]);
        if (tableAVObject.getDate("startedAt") == null) {
            tableAVObject.put("startedAt", new Date());
        }
        if (tableAVObject.getInt("customer") == 0) {
            tableAVObject.put("customer", 1);
        }
        return tableAVObject;
    }

    /**
     * @param tableAVObject
     * @param commodity
     * @param commodityNumber 退菜数量
     * @param comment
     * @param position
     * @return 退菜品
     */
    public static AVObject refundOrder(
            AVObject tableAVObject,
            HashMap<String, Object> commodity,
            String commodityNumber,
            String comment,
            int position) {
        HashMap<String, Object> hashMap = new HashMap<>();
        String commodityId = ObjectUtil.getString(commodity, "id");
        ProductBean productBean = MyUtils.getProductById(commodityId);
        Double refundNumber = Double.valueOf(commodityNumber);
        hashMap.put("id", commodityId);
        hashMap.put("number", refundNumber);
        hashMap.put("comment", comment);
        hashMap.put("name", ObjectUtil.getString(commodity, "name"));
        hashMap.put("comboList", ObjectUtil.getList(commodity, "comboList"));
        hashMap.put("presenter", ObjectUtil.getString(commodity, "presenter"));
        hashMap.put("operator", new SharedHelper(MyApplication.getContextObject()).read("cashierName"));
        if (ObjectUtil.getString(commodity, "barcode").length() == 18) {
            hashMap.put("weight", MyUtils.formatDouble(ProductUtil.calCommodityWeight(ObjectUtil.getString(commodity, "barcode")) * refundNumber));
            hashMap.put("price", MyUtils.formatDouble(ProductUtil.calCommodityMoney(ObjectUtil.getString(commodity, "barcode")) * refundNumber));
        } else {
            hashMap.put("weight", MyUtils.formatDouble(productBean.getWeight() * refundNumber));
            hashMap.put("price", MyUtils.formatDouble(productBean.getPrice() * refundNumber));
        }


        List refundOrders = tableAVObject.getList("refundOrder");
        refundOrders.add(hashMap);
        List orders = tableAVObject.getList("order");
        if (refundNumber - ObjectUtil.getDouble(commodity, "number") == 0.0) {
            orders.remove(position);
        } else {
            commodity.put("number", ObjectUtil.getDouble(commodity, "number") - refundNumber >= 0 ? ObjectUtil.getDouble(commodity, "number") - refundNumber : 0);
            tableAVObject.getList("order").set(position, commodity);
        }
        tableAVObject.put("order", orders);
        tableAVObject.put("refundOrder", refundOrders);
        List alreadyOrders = tableAVObject.getList("order");
            for (Object o : alreadyOrders) {
                HashMap<String, Object> map = (HashMap<String, Object>) o;
                if (ObjectUtil.getString(map, "id").equals(CONST.MACHINEID)) {
                    if (ObjectUtil.getDouble(map, "number")<=refundNumber){
                        alreadyOrders.remove(map);
                    }else{
                        map.put("number", ObjectUtil.getDouble(map, "number") -refundNumber);
                        map.put("price", MyUtils.formatDouble((ObjectUtil.getDouble(map, "number") -refundNumber)*30));
                    }

                }
            }
        return tableAVObject;
    }
}
