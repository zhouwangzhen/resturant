package cn.kuwo.player.util;

import com.avos.avoscloud.AVObject;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.RetailBean;
import cn.kuwo.player.event.ComboEvent;

public class DataUtil {
    /**
     * @param event         商品信息
     * @param tableAVObject 餐桌信息
     * @param isSvip        是否是超牛会员
     * @param mode          0:点菜模式 1：赠菜模式
     * @param preOrders
     * @return 添加商品全部的信息
     */
    public static HashMap<String, Object> addHashMap(ComboEvent event,
                                                     AVObject tableAVObject,
                                                     Boolean isSvip,
                                                     String userId,
                                                     int mode) {
        String commodityId = event.getProductBean().getObjectId();
        ProductBean productBean = MyUtils.getProductById(commodityId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", commodityId);
        hashMap.put("number", event.getCommodityNumber());
        hashMap.put("comment", event.getContent());
        hashMap.put("name", event.getProductBean().getName());
        if (event.getBarcode().length() == 18) {
            hashMap.put("weight", MyUtils.formatDouble(ProductUtil.calCommodityWeight(event.getBarcode()) * event.getCommodityNumber()));
            if (mode == 0) {
                hashMap.put("price", MyUtils.formatDouble(ProductUtil.calCommodityMoney(event.getBarcode()) * event.getCommodityNumber()));
                hashMap.put("nb",MyUtils.formatDouble(ProductUtil.calCommodityMoney(event.getBarcode()) * event.getCommodityNumber()*CONST.NB.MEATDiSCOUNT));
            } else {
                hashMap.put("price", 0);
                hashMap.put("nb",0);
            }
        } else {
            hashMap.put("weight", MyUtils.formatDouble(productBean.getWeight() * event.getCommodityNumber()));
            if (mode == 0) {
                hashMap.put("price", MyUtils.formatDouble(productBean.getPrice() * event.getCommodityNumber()));
                hashMap.put("nb", MyUtils.formatDouble(productBean.getNb() * event.getCommodityNumber()));
            } else {
                hashMap.put("price", 0);
                hashMap.put("nb",0);
            }

        }
        hashMap.put("cookStyle", event.getCookStyle());
        hashMap.put("barcode", event.getBarcode());
        hashMap.put("mode", mode);
        hashMap.put("presenter", ProductUtil.calPresenter(tableAVObject, event.getProductBean(), isSvip));
        hashMap.put("cookSerial", event.getCookSerial());
        if (event.getComboList() != null && event.getComboList().size() > 0) {
            hashMap.put("comboList", event.getComboList());
        } else {
            hashMap.put("comboList", new ArrayList<Object>());
        }
        return hashMap;
    }


    /**
     * @param event         商品信息
     * @param preOrders     预下单订单
     * @param tableAVObject 餐桌信息
     * @param isSvip        是否是超牛会员
     */
    public static void updateIndexOder(ComboEvent event,
                                       List<Object> preOrders,
                                       AVObject tableAVObject,
                                       Boolean isSvip,
                                       String userId,
                                       int mode) {
        Logger.d(event.getOrderIndex());
        if (event.getOrderIndex() != -1) {
            Logger.d(event);
            if (event.getCommodityNumber() > 0) {
                String commodityId = event.getProductBean().getObjectId();
                ProductBean productBean = MyUtils.getProductById(commodityId);
                Object o = preOrders.get(event.getOrderIndex());
                HashMap<String, Object> format = ObjectUtil.format(o);
                format.put("id", event.getProductBean().getObjectId());
                format.put("number", event.getCommodityNumber());
                format.put("comment", event.getContent());
                format.put("name", event.getProductBean().getName());
                if (event.getBarcode().length() == 18) {
                    format.put("weight", MyUtils.formatDouble(ProductUtil.calCommodityWeight(event.getBarcode()) * event.getCommodityNumber()));
                    if (mode == 0) {
                        format.put("price", MyUtils.formatDouble(ProductUtil.calCommodityMoney(event.getBarcode()) * event.getCommodityNumber()));
                        format.put("price", MyUtils.formatDouble(ProductUtil.calCommodityMoney(event.getBarcode()) * event.getCommodityNumber()*CONST.NB.MEATDiSCOUNT));
                    } else {
                        format.put("price", 0);
                    }
                } else {
                    format.put("weight", MyUtils.formatDouble(productBean.getWeight() * event.getCommodityNumber()));
                    if (mode == 0) {
                        format.put("price", MyUtils.formatDouble(productBean.getPrice() * event.getCommodityNumber()));
                        format.put("nb", MyUtils.formatDouble(productBean.getNb() * event.getCommodityNumber()));
                    } else {
                        format.put("price", 0);
                    }
                }


                format.put("presenter", ProductUtil.calPresenter(tableAVObject, event.getProductBean(), isSvip));
                if (event.getProductBean().getComboMenu() != null && event.getProductBean().getComboMenu().length() > 0) {
                    format.put("comboList", event.getComboList());
                } else {
                    format.put("comboList", new ArrayList<Object>());
                }
                format.put("presenter", ProductUtil.calPresenter(tableAVObject, event.getProductBean(), isSvip));
                format.put("cookSerial", event.getCookSerial());
                format.put("cookStyle", event.getCookStyle());
            } else {
                preOrders.remove(event.getOrderIndex());
            }
        }
    }


    public static RetailBean buildRetailBean(List<Object> orders) {
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<String> codes = new ArrayList<>();
        ArrayList<Double> prices = new ArrayList<>();
        ArrayList<Double> weight = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> o = (HashMap<String, Object>) orders.get(i);
            ids.add(ObjectUtil.getString(o, "id"));
            codes.add(ObjectUtil.getString(o, "code"));
            prices.add(ObjectUtil.getDouble(o, "price"));
            weight.add(ObjectUtil.getDouble(o, "weight"));
            name.add(ObjectUtil.getString(o, "id"));
        }
        return new RetailBean(ids, codes, prices, weight, name);
    }

    /**
     * @param preOrders  预下单订单
     * @param event  修改商品的信息
     * @param mode  模式
     * @param isEdit 是否是修改模式
     * @param OriginNumber 原订单数量
     */
    public static void additionalCharge(List<Object> preOrders, ComboEvent event, int mode, boolean isEdit,int OriginNumber) {
        if (!isEdit) {
            if (!event.getCookStyle().equals("")) {
                if (QueryUtil.findMachine(preOrders) == 0) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    RealmHelper realmHelper = new RealmHelper(MyApplication.getContextObject());
                    List<ProductBean> productBeans = realmHelper.queryAdditionals();
                    if (productBeans.size() > 0) {
                        ProductBean productBean = productBeans.get(0);
                        hashMap.put("id", productBean.getObjectId());
                        hashMap.put("number", event.getCommodityNumber());
                        hashMap.put("comment", "");
                        hashMap.put("name", productBean.getName());
                        hashMap.put("weight", 0);
                        if (mode == 1) {
                            hashMap.put("price", 0);
                        } else {
                            hashMap.put("price", MyUtils.formatDouble(productBean.getPrice() * event.getCommodityNumber()));
                        }
                        hashMap.put("barcode", productBean.getCode());
                        hashMap.put("mode", "mode");
                        hashMap.put("cookSerial", "");
                        hashMap.put("cookStyle", "");
                        hashMap.put("comboList", new ArrayList<Object>());
                        preOrders.add(hashMap);
                    }
                } else {
                    for (Object o : preOrders) {
                        HashMap<String, Object> map = (HashMap<String, Object>) o;
                        if (ObjectUtil.getString(map, "id").equals(CONST.MACHINEID)) {
                            map.put("number", ObjectUtil.getDouble(map, "number") + event.getCommodityNumber());
                            if (mode == 1) {
                                map.put("price", ObjectUtil.getDouble(map,"price")+0);
                            } else {
                                map.put("price", MyUtils.formatDouble(ObjectUtil.getDouble(map,"price") * event.getCommodityNumber()));
                            }
                            return;
                        }
                    }
                }

            }
        } else {
            if (!event.getCookStyle().equals("")) {
                int cookMeatNumber = QueryUtil.findCookMeatNumber(preOrders);
                Logger.d(event.getCommodityNumber());
                    if (event.getCommodityNumber()>0){
                        for (Object o : preOrders) {
                            HashMap<String, Object> map = (HashMap<String, Object>) o;
                            if (ObjectUtil.getString(map,"id").equals(CONST.MACHINEID)){
                                if (ObjectUtil.getDouble(map,"price")!=0){
                                    Logger.d(ObjectUtil.getDouble(map, "price"));
                                    Logger.d(event.getCommodityNumber());
                                    Logger.d((OriginNumber - event.getCommodityNumber() * 30));
                                    double price = ObjectUtil.getDouble(map, "price") - ((OriginNumber - event.getCommodityNumber() )* 30);
                                    map.put("price",MyUtils.formatDouble(price>0?price:0));
                                }
                                map.put("number",cookMeatNumber);
                            }
                        }
                    }else{
                        for (Object o : preOrders) {
                            HashMap<String, Object> map = (HashMap<String, Object>) o;
                            if (ObjectUtil.getString(map,"id").equals(CONST.MACHINEID)){
                                preOrders.remove(map);
                            }
                        }
                    }
            }
        }
    }
    public static String JSONTokener(String str_json) {
        // consume an optional byte order mark (BOM) if it exists
        if (str_json != null && str_json.startsWith("\ufeff")) {
            str_json = str_json.substring(1);
        }
        return str_json;
    }
}
