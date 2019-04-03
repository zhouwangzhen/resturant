package cn.kuwo.player.api;

import com.avos.avoscloud.AVObject;
import com.orhanobut.logger.Logger;

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
            HashMap<String, Object> hashMap = (HashMap<String, Object>) preOrders.get(i);
            String id = ObjectUtil.getString(hashMap, "id");
            if (MyUtils.getProductById(id).isMerge()) {
                boolean isExist = false;
                for (int j = 0; j < oldOrders.size(); j++) {
                    HashMap<String, Object> hashMap1 = (HashMap<String, Object>) oldOrders.get(i);
                    String oldId = ObjectUtil.getString(hashMap1, "id");
                    if (oldId.equals(id)) {
                        Logger.d("已经存在");
                        isExist = true;
                        hashMap1.put("number",ObjectUtil.getDouble(hashMap1,"number")+ObjectUtil.getDouble(hashMap,"number"));
                        hashMap1.put("weight",ObjectUtil.getDouble(hashMap1,"weight")+ObjectUtil.getDouble(hashMap,"weight"));
                        hashMap1.put("price",ObjectUtil.getDouble(hashMap1,"price")+ObjectUtil.getDouble(hashMap,"price"));
                        hashMap1.put("nb",ObjectUtil.getDouble(hashMap1,"nb")+ObjectUtil.getDouble(hashMap,"nb"));
                        break;
                    }
                }
                if (!isExist) {
                    newOrders.add(preOrders.get(i));
                }
            } else {
                newOrders.add(preOrders.get(i));
            }
        }
        for (int k=0;k<newOrders.size();k++){
            HashMap<String, Object> hashMap = (HashMap<String, Object>) newOrders.get(k);
            if (ObjectUtil.getString(hashMap,"id").equals(CONST.ACTIVITYCOMMODITY.GROUPPAYBILL)){
                Double number = ObjectUtil.getDouble(hashMap, "number");
                if (number == 1||number == 2 || number == 3) {
                    hashMap.put("price", 368);
                    hashMap.put("nb", 368);
                }else{
                    hashMap.put("price", MyUtils.formatDouble(98*number));
                    hashMap.put("nb", MyUtils.formatDouble(98*number));
                }
            }
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
            if (ObjectUtil.getString(commodity, "barcode").length() == 18) {
                if (ObjectUtil.getDouble(commodity, "price") > 0) {
                    commodity.put("price", ObjectUtil.getDouble(commodity, "number") >= 0 ? MyUtils.formatDouble(ProductUtil.calCommodityMoney(ObjectUtil.getString(commodity, "barcode")) * ObjectUtil.getDouble(commodity, "number")) : 0);
                }
                if (ObjectUtil.getDouble(commodity, "nb") > 0) {
                    commodity.put("nb", ObjectUtil.getDouble(commodity, "number") >= 0 ? MyUtils.formatDouble((ObjectUtil.getDouble(commodity, "price") / (ObjectUtil.getDouble(commodity, "number"))) * (ObjectUtil.getDouble(commodity, "number"))) : 0);
                }

            } else {
                if (ObjectUtil.getDouble(commodity, "price") > 0) {
                    commodity.put("price", ObjectUtil.getDouble(commodity, "number") >= 0 ? MyUtils.formatDouble(productBean.getPrice() * (ObjectUtil.getDouble(commodity, "number"))) : 0);
                }
                if (ObjectUtil.getDouble(commodity, "nb") > 0) {
                    commodity.put("nb", ObjectUtil.getDouble(commodity, "number") >= 0 ? MyUtils.formatDouble(productBean.getNb() * (ObjectUtil.getDouble(commodity, "number"))) : 0);
                }

            }

            tableAVObject.getList("order").set(position, commodity);
        }
        tableAVObject.put("order", orders);
        tableAVObject.put("refundOrder", refundOrders);
        return tableAVObject;
    }

}
