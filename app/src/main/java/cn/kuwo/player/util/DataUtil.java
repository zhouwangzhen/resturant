package cn.kuwo.player.util;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.orhanobut.logger.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
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
        Double sideDishPrice=0.0;
        if (event.getSideDishPrice()!=0.0&&event.getSideDish()!=null){
            sideDishPrice=MyUtils.formatDouble(event.getCommodityNumber()*event.getSideDishPrice());
        }
        if (event.getBarcode().length() == 18) {
            hashMap.put("weight", MyUtils.formatDouble(ProductUtil.calCommodityWeight(event.getBarcode()) * event.getCommodityNumber()));
            if (mode == 0) {
                hashMap.put("price", MyUtils.formatDouble(ProductUtil.calCommodityMoney(event.getBarcode()) * event.getCommodityNumber()));
                if (productBean.getType()==6||productBean.getType()==7){

                    if (productBean.getNbDiscountType()==2){
                        hashMap.put("nb", MyUtils.formatDouble(MyUtils.formatDouble(ProductUtil.calCommodityWeight(event.getBarcode()) * event.getCommodityNumber())*productBean.getNbDiscountPrice()+sideDishPrice));
                    }else{
                        hashMap.put("nb", MyUtils.formatDouble(ProductUtil.calCommodityMoney(event.getBarcode()) * event.getCommodityNumber() * CONST.NB.MEATDiSCOUNT+sideDishPrice));
                    }
                }else{
                    hashMap.put("nb", MyUtils.formatDouble(ProductUtil.calCommodityMoney(event.getBarcode()) * event.getCommodityNumber() * CONST.NB.OTHERDISCOUNT+sideDishPrice));
                }

            } else {
                hashMap.put("price", 0);
                hashMap.put("nb", 0);
            }
        } else {
            hashMap.put("weight", MyUtils.formatDouble(productBean.getWeight() * event.getCommodityNumber()));
            if (mode == 0) {
                if(productBean.getSpecial()!=null&&productBean.getSpecial().length()>0&&productBean.getSpecial().split("-").length==2&&(DateUtil.getWeekNumber()+"").equals(productBean.getSpecial().split("-")[0])&&new Date().getHours()<=14){
                    hashMap.put("price",MyUtils.formatDouble(Double.parseDouble(productBean.getSpecial().split("-")[1]) * event.getCommodityNumber()));
                    hashMap.put("nb",MyUtils.formatDouble(Double.parseDouble(productBean.getSpecial().split("-")[1]) * event.getCommodityNumber()));
                }else {
                    hashMap.put("price", MyUtils.formatDouble(productBean.getPrice() * event.getCommodityNumber())+sideDishPrice);
                    if (productBean.getSerial()==null){
                        if (productBean.getType()==6||productBean.getType()==7){
                            hashMap.put("nb", MyUtils.formatDouble(productBean.getNb() * event.getCommodityNumber()*CONST.NB.MEATDiSCOUNT+sideDishPrice));
                        }else if(productBean.getType()==9){
                            hashMap.put("nb", MyUtils.formatDouble(productBean.getNb() * event.getCommodityNumber()+sideDishPrice));
                        }else{
                            hashMap.put("nb", MyUtils.formatDouble(productBean.getNb() * event.getCommodityNumber()*CONST.NB.OTHERDISCOUNT+sideDishPrice));
                        }
                    }else{
                        hashMap.put("nb", MyUtils.formatDouble(productBean.getNb() * event.getCommodityNumber()+sideDishPrice));
                    }
                }
            } else {
                hashMap.put("price", 0);
                hashMap.put("nb", 0);
            }

        }
        if (event.getSideDish()!=null){
            hashMap.put("sideDishPrice",sideDishPrice);
            hashMap.put("sideDishIndex",event.getSideDishIndex());
            hashMap.put("sideDishCommodity",event.getSideDish().getName());
        }else{
            hashMap.put("sideDishPrice",0.0);
            hashMap.put("sideDishIndex",event.getSideDishIndex());
            hashMap.put("sideDishCommodity","");
        }

        hashMap.put("cookStyle", event.getCookStyle());
        hashMap.put("barcode", event.getBarcode());
        hashMap.put("mode", mode);
        hashMap.put("presenter", ProductUtil.calPresenter(tableAVObject, event.getProductBean(), isSvip));
        hashMap.put("cookSerial", event.getCookSerial());
        hashMap.put("date",DateUtil.formatLongDate(new Date()));
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
        if (event.getOrderIndex() != -1) {
            if (event.getCommodityNumber() > 0) {
                String commodityId = event.getProductBean().getObjectId();
                ProductBean productBean = MyUtils.getProductById(commodityId);
                Object o = preOrders.get(event.getOrderIndex());
                HashMap<String, Object> format = ObjectUtil.format(o);
                format.put("id", event.getProductBean().getObjectId());
                format.put("number", event.getCommodityNumber());
                format.put("comment", event.getContent());
                format.put("name", event.getProductBean().getName());
                Double sideDishPrice=0.0;
                if (event.getSideDishPrice()!=0.0&&event.getSideDish()!=null){
                    sideDishPrice=MyUtils.formatDouble(event.getCommodityNumber()*event.getSideDishPrice());
                }
                if (event.getBarcode().length() == 18) {
                    format.put("weight", MyUtils.formatDouble(ProductUtil.calCommodityWeight(event.getBarcode()) * event.getCommodityNumber()));
                    if (mode == 0) {
                        format.put("price", MyUtils.formatDouble(ProductUtil.calCommodityMoney(event.getBarcode()) * event.getCommodityNumber()+sideDishPrice));
                        format.put("nb", MyUtils.formatDouble(ProductUtil.calCommodityMoney(event.getBarcode()) * event.getCommodityNumber() * CONST.NB.MEATDiSCOUNT+sideDishPrice));
                    } else {
                        format.put("price", 0);
                        format.put("nb", 0);
                    }
                } else {
                    format.put("weight", MyUtils.formatDouble(productBean.getWeight() * event.getCommodityNumber()));
                    if (mode == 0) {
                        format.put("price", MyUtils.formatDouble(productBean.getPrice() * event.getCommodityNumber()+sideDishPrice));
                        format.put("nb", MyUtils.formatDouble(productBean.getNb() * event.getCommodityNumber()+sideDishPrice));
                    } else {
                        format.put("price", 0);
                        format.put("nb", 0);
                    }
                }
                if (event.getSideDish()!=null){
                    format.put("sideDishPrice",sideDishPrice);
                    format.put("sideDishIndex",event.getSideDishIndex());
                    format.put("sideDishCommodity",event.getSideDish().getName());
                }else{
                    format.put("sideDishPrice",0.0);
                    format.put("sideDishIndex",event.getSideDishIndex());
                    format.put("sideDishCommodity","");
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
                format.put("updateDate",DateUtil.formatLongDate(new Date()));
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
     * @param preOrders    预下单订单
     * @param event        修改商品的信息
     * @param mode         模式
     * @param isEdit       是否是修改模式
     * @param OriginNumber 原订单数量
     */
    public static void additionalCharge(List<Object> preOrders, ComboEvent event, int mode, boolean isEdit, int OriginNumber) {
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
                                map.put("price", ObjectUtil.getDouble(map, "price") + 0);
                            } else {
                                map.put("price", MyUtils.formatDouble(ObjectUtil.getDouble(map, "price") * event.getCommodityNumber()));
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
                if (event.getCommodityNumber() > 0) {
                    for (Object o : preOrders) {
                        HashMap<String, Object> map = (HashMap<String, Object>) o;
                        if (ObjectUtil.getString(map, "id").equals(CONST.MACHINEID)) {
                            if (ObjectUtil.getDouble(map, "price") != 0) {
                                double price = ObjectUtil.getDouble(map, "price") - ((OriginNumber - event.getCommodityNumber()) * 30);
                                map.put("price", MyUtils.formatDouble(price > 0 ? price : 0));
                            }
                            map.put("number", cookMeatNumber);
                        }
                    }
                } else {
                    for (Object o : preOrders) {
                        HashMap<String, Object> map = (HashMap<String, Object>) o;
                        if (ObjectUtil.getString(map, "id").equals(CONST.MACHINEID)) {
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

    public static void changeDZDPCommodity(AVObject avObject, List<Object> orders, int postion, double v) {
        HashMap<String, Object> hashMap = (HashMap<String, Object>) orders.get(orders.size() - postion - 1);
        ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(hashMap, "id"));
        Double number = ObjectUtil.getDouble(hashMap, "number");
        if (v > 0) {
            if (number > v) {
                HashMap<String, Object> hashMap1 = (HashMap<String, Object>) ObjectUtil.deepClone(hashMap);
                hashMap.put("number", number - v);
                hashMap.put("price", MyUtils.formatDouble(ObjectUtil.getDouble(hashMap, "price") * (number - v) / number));
                hashMap.put("nb", MyUtils.formatDouble(ObjectUtil.getDouble(hashMap, "nb") * (number - v) / number));

                hashMap1.put("name", MyUtils.getProductById(productBean.getReviewCommodity()).getName());
                hashMap1.put("price", 0);
                hashMap1.put("nb", 0);
                hashMap1.put("number", v);
                hashMap1.put("id", MyUtils.getProductById(productBean.getReviewCommodity()).getObjectId());
                hashMap1.put("barcode", MyUtils.getProductById(productBean.getReviewCommodity()).getCode());
                orders.add(hashMap1);
            } else if (number == v) {
                hashMap.put("name", MyUtils.getProductById(productBean.getReviewCommodity()).getName());
                hashMap.put("price", 0);
                hashMap.put("nb", 0);
                hashMap.put("id", MyUtils.getProductById(productBean.getReviewCommodity()).getObjectId());
                hashMap.put("barcode", MyUtils.getProductById(productBean.getReviewCommodity()).getCode());
            }
        }
        avObject.put("order", orders);
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                Logger.d("修改成功");
            }
        });
    }


    public static void changeGroupNumber(AVObject avObject, List<Object> orders, int postion, double v) {
        HashMap<String, Object> hashMap = (HashMap<String, Object>) orders.get(orders.size() - postion - 1);
        if (v == 1||v == 2 || v == 3) {
            hashMap.put("price", 368);
            hashMap.put("nb", 368);
        }else{
            hashMap.put("price", MyUtils.formatDouble(98*v));
            hashMap.put("nb", MyUtils.formatDouble(98*v));
        }
        avObject.put("order", orders);
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                Logger.d("修改成功");
            }
        });
    }
    public static void changeAnniversaryNumber(AVObject avObject, List<Object> orders, int postion) {
        HashMap<String, Object> hashMap = (HashMap<String, Object>) orders.get(orders.size() - postion - 1);
        hashMap.put("price",0);
        hashMap.put("nb", 0);
        avObject.put("order", orders);
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                Logger.d("修改成功");
            }
        });
    }
    public static void addHangUpOrder(AVObject avObject, List<Object> orders, int postion, double v) {
        HashMap<String, Object> hashMap = (HashMap<String, Object>) orders.get(orders.size() - postion - 1);
        final HashMap<String,Object> hashMap1=(HashMap<String,Object>)hashMap.clone();
        HashMap<String,Object> hashMap2=(HashMap<String,Object>)hashMap.clone();
        HashMap<String,Object> OriginHashMap=(HashMap<String,Object>)hashMap.clone();
        hashMap.put("price",MyUtils.formatDouble(ObjectUtil.getDouble(hashMap, "price")/ObjectUtil.getDouble(hashMap, "number")*(ObjectUtil.getDouble(hashMap, "number")-v)));
        hashMap.put("nb", MyUtils.formatDouble(ObjectUtil.getDouble(hashMap, "nb")/ObjectUtil.getDouble(hashMap, "number")*(ObjectUtil.getDouble(hashMap, "number")-v)));
        hashMap.put("weight", MyUtils.formatDouble(ObjectUtil.getDouble(hashMap, "weight")/ObjectUtil.getDouble(hashMap, "number")*(ObjectUtil.getDouble(hashMap, "number")-v)));
        hashMap.put("discountNumber",v);
        hashMap.put("number",MyUtils.formatDouble(ObjectUtil.getDouble(hashMap, "number")-v));
        ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(hashMap, "id"));
        hashMap1.put("price",MyUtils.formatDouble(productBean.getPrice()*v));
        hashMap1.put("nb",MyUtils.formatDouble(productBean.getNb()*v));
        hashMap1.put("weight",MyUtils.formatDouble(productBean.getWeight()*v));
        hashMap1.put("number",MyUtils.formatDouble(v));
        hashMap1.put("reason",DateUtil.formatLongDate(new Date())+","+SharedHelper.read("cashierName"));
        hashMap2.put("price",0.0);
        hashMap2.put("nb",0.0);
        hashMap2.put("mode",2);
        hashMap2.put("number",v);
        hashMap2.put("reason",DateUtil.formatLongDate(new Date())+","+SharedHelper.read("cashierName"));
        if ((Double)hashMap.get("number")<=0.0){
            orders.remove(orders.size() - postion - 1);
        }
        orders.add(hashMap2);
        avObject.put("order",orders);
        avObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e==null){
                    T.L("修改成功");
                    AVQuery<AVObject> query = new AVQuery<>("Table");
                    query.getInBackground("5b868f57a22b9d0037dbcc33", new GetCallback<AVObject>() {
                        @Override
                        public void done(AVObject avObject, AVException e) {
                            if (e==null){
                                List<Object> newOrders=avObject.getList("order");
                                newOrders.add(hashMap1);
                                if (avObject.getInt("customer") == 0){
                                    avObject.put("customer",2);
                                    avObject.put("startedAt",new Date());
                                }
                                avObject.put("order",newOrders);
                                avObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e==null){
                                            T.L("挂账成功");
                                        }
                                    }
                                });
                            }
                        }
                    });
                }

            }
        });

    }
}
