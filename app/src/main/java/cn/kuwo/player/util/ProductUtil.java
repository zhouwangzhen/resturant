package cn.kuwo.player.util;

import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.SaveCallback;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.RuleBean;
import cn.kuwo.player.bean.UserBean;
import cn.kuwo.player.comparator.MapValueComparator;
import cn.kuwo.player.event.OrderDetail;
import io.realm.RealmList;
import io.realm.RealmObject;

public class ProductUtil {


    public static int getTotalNumber(List<Integer> commodityNumber) {
        int totalNumber = 0;
        for (int i = 0; i < commodityNumber.size(); i++) {
            totalNumber += commodityNumber.get(i);
        }
        return totalNumber;
    }

    public static int remainTable(List<AVObject> tableAvObject) {
        int tables = 0;
        for (int i = 0; i < tableAvObject.size(); i++) {
            AVObject avObject = tableAvObject.get(i);
            if (avObject.getInt("customer") == 0) {
                ++tables;
            }
        }
        return tables;
    }

    public static double calculateTotalMoney(List<ProductBean> preProductBeans, List<Double> preProductNumbers, List<ProductBean> productBeans, List<Double> productNumbers) {
        double total = 0.0;
        for (int i = 0; i < preProductBeans.size(); i++) {
            total += preProductBeans.get(i).getPrice() * preProductNumbers.get(i);
        }
        for (int i = 0; i < productBeans.size(); i++) {
            total += (Double) (productBeans.get(i).getPrice() * productNumbers.get(i));
        }
        return MyUtils.formatDouble(total);
    }

    public static double calculateTotalMoney(AVObject avObject) {
        double totalMoney = 0.0;
        List<Object> orders = avObject.getList("order");
        List<Object> preOrders = avObject.getList("preOrder");
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            totalMoney += ObjectUtil.getDouble(format, "price");
        }
        for (int j = 0; j < preOrders.size(); j++) {
            HashMap<String, Object> format = ObjectUtil.format(preOrders.get(j));
            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            totalMoney += ObjectUtil.getDouble(format, "price");
        }
        return MyUtils.formatDouble(totalMoney);
    }

    public static double calculateTotalMoney(List<Object> orders, List<Object> preOrders) {
        double totalMoney = 0.0;
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            totalMoney += ObjectUtil.getDouble(format, "price");
        }
        for (int j = 0; j < preOrders.size(); j++) {
            HashMap<String, Object> format = ObjectUtil.format(preOrders.get(j));
            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
//            totalMoney += productBean.getPrice() * ObjectUtil.getDouble(format, "number");
            totalMoney += ObjectUtil.getDouble(format, "price");
        }
        return MyUtils.formatDouble(totalMoney);
    }

    public static double calculateMinMoney(AVObject avObject) {
        double svipTotalMoney = 0.0;
        List<Object> orders = avObject.getList("order");
        List<Object> preOrders = avObject.getList("preOrder");
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
            if (ObjectUtil.getDouble(format, "price") > 0) {
                ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                if (productBean.getScale() == 0) {
                    svipTotalMoney += productBean.getPrice() * ObjectUtil.getDouble(format, "number");
                } else {
                    svipTotalMoney += productBean.getRemainMoney() * ObjectUtil.getDouble(format, "number");
                }
            }
        }
        for (int j = 0; j < preOrders.size(); j++) {
            HashMap<String, Object> format = ObjectUtil.format(preOrders.get(j));
            if (ObjectUtil.getDouble(format, "price") > 0) {
                ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                if (productBean.getScale() == 0) {
                    svipTotalMoney += productBean.getPrice() * ObjectUtil.getDouble(format, "number");
                } else {
                    svipTotalMoney += productBean.getRemainMoney() * ObjectUtil.getDouble(format, "number");
                }
            }
        }
        return MyUtils.formatDouble(svipTotalMoney);
    }

    public static double calculateMinMoney(List<Object> orders, List<Object> preOrders) {
        double svipTotalMoney = 0.0;
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
            if (ObjectUtil.getDouble(format, "price") > 0) {
                ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                if (productBean.getScale() == 0) {
                    svipTotalMoney += productBean.getPrice() * ObjectUtil.getDouble(format, "number");
                } else {
                    svipTotalMoney += productBean.getRemainMoney() * ObjectUtil.getDouble(format, "number");
                }
            }
        }
        for (int j = 0; j < preOrders.size(); j++) {
            HashMap<String, Object> format = ObjectUtil.format(preOrders.get(j));
            if (ObjectUtil.getDouble(format, "price") > 0) {
                ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                if (productBean.getScale() == 0) {
                    svipTotalMoney += productBean.getPrice() * ObjectUtil.getDouble(format, "number");
                } else {
                    svipTotalMoney += productBean.getRemainMoney() * ObjectUtil.getDouble(format, "number");
                }
            }
        }
        return MyUtils.formatDouble(svipTotalMoney);
    }

    public static List<ProductBean> searchBySerial(String serial) {
        RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        List<ProductBean> productBeans = mRealmHleper.queryCommodityBySerial(serial);
        return productBeans;
    }

    /**
     * 计算牛肉可抵扣的金额
     */
    public static double calMeatduceMoney(List<Object> orders, List<Double> prices) {
        double TotalMeatReduceMoney = 0.0;
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            if (productBean.getScale() > 0 && ObjectUtil.getDouble(format, "price") > 0) {
                if (prices.size() > 0 && prices.size() == orders.size()) {
                    TotalMeatReduceMoney += prices.get(i);
                } else {
                    TotalMeatReduceMoney += (productBean.getPrice() - productBean.getRemainMoney()) * ObjectUtil.getDouble(format, "number");
                }

            }
        }

        return MyUtils.formatDouble(TotalMeatReduceMoney);
    }

    /**
     * 计算牛肉可抵扣的金额
     */
    public static double calMeatduceWeight(List<Object> orders, List<Double> weights) {
        double TotalMeatReduceWeight = 0.0;
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            if (productBean.getScale() > 0 && ObjectUtil.getDouble(format, "price") > 0) {
                if (weights.size() > 0 && weights.size() == orders.size()) {
                    TotalMeatReduceWeight += weights.get(i) * productBean.getScale();
                } else {
                    TotalMeatReduceWeight += (productBean.getWeight() * productBean.getScale()) * ObjectUtil.getDouble(format, "number");
                }
            }
        }

        return MyUtils.formatDouble(TotalMeatReduceWeight);
    }

    public static List<Object> calExchangeMeatList(List<Object> orders) {
        List<Object> meatList = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            if (productBean.getScale() > 0 && ObjectUtil.getDouble(format, "price") > 0) {
                try {
                    if (productBean.getCode().length() == 5) {
                        format.put("meatWeight", MyUtils.formatDouble(ObjectUtil.getDouble(format, "weight") * productBean.getScale() * ObjectUtil.getDouble(format, "number")));
                        format.put("reduceMoeny", calReduceMoney(productBean, format));
                    } else {
                        format.put("meatWeight", MyUtils.formatDouble(productBean.getWeight() * productBean.getScale() * ObjectUtil.getDouble(format, "number")));
                        format.put("reduceMoeny", calReduceMoney(productBean, format));
                    }

                } catch (Exception e) {
                    format.put("meatWeight", 0.0);
                    format.put("reduceMoeny", 0.0);

                }
                meatList.add(format);
            }
        }
        return meatList;
    }

    /**
     * 计算商品的优惠价格
     */
    private static Double calReduceMoney(ProductBean productBean, HashMap<String, Object> format) {
        Double number = ObjectUtil.getDouble(format, "number");
        Double totalMoney = ObjectUtil.getDouble(format, "price");
        double totalRemainMoney = productBean.getRemainMoney() * number;
        return MyUtils.formatDouble(totalMoney - totalRemainMoney);
    }

    /**
     * 计算出可扣得最大的牛肉列表
     */
    public static List<Object> canExchangeMeatList(List<Object> orders, Double hasMeatWeight, List<Double> weights) {
        Logger.d(orders);
        List<Object> meatList = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            if (productBean.getScale() > 0 && ObjectUtil.getDouble(format, "price") > 0) {
                try {
                    if (weights.size() == orders.size() && weights.size() > 0) {
                        format.put("meatWeight", MyUtils.formatDouble(weights.get(i) * productBean.getScale()));
                    } else {
                        format.put("meatWeight", MyUtils.formatDouble(productBean.getWeight() * productBean.getScale() * ObjectUtil.getDouble(format, "number")));

                    }
                } catch (Exception e) {
                    format.put("meatWeight", 0.0);

                }
                meatList.add(format);
            }
        }
        Logger.d("+++++" + meatList);
        Collections.sort(meatList, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                HashMap<String, Object> format = ObjectUtil.format(o1);
                HashMap<String, Object> format1 = ObjectUtil.format(o2);
                return ObjectUtil.getDouble(format1, "meatWeight") - ObjectUtil.getDouble(format, "meatWeight") < 0 ? -1 : 0;
            }
        });
        double totalWeight = 0.0;
        List<Object> exchangeMeatList = new ArrayList<>();
        int signIndex = 0;
        for (int i = 0; i < meatList.size(); i++) {
            Object o = meatList.get(i);
            HashMap<String, Object> format = ObjectUtil.format(o);
            ProductBean productBean1 = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            if (totalWeight + ObjectUtil.getDouble(format, "meatWeight") <= hasMeatWeight) {
                totalWeight += ObjectUtil.getDouble(format, "meatWeight");
                format.put("reduceMoeny", MyUtils.formatDouble(ObjectUtil.getDouble(format, "price") - productBean1.getRemainMoney() * ObjectUtil.getDouble(format, "number")));
                exchangeMeatList.add(format);
            } else if (totalWeight + MyUtils.formatDouble(ObjectUtil.getDouble(format, "meatWeight") / ObjectUtil.getDouble(format, "number")) <= hasMeatWeight) {
                int number = ObjectUtil.getDouble(format, "number").intValue();
                int num = 0;
                ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                for (int j = 0; j < number; j++) {
                    if (totalWeight + productBean.getWeight() * productBean.getScale() <= hasMeatWeight) {
                        num++;
                        totalWeight += productBean.getWeight() * productBean.getScale();
                    }

                }
                if (num > 0) {
                    format.put("number", num);
                    format.put("meatWeight", MyUtils.formatDouble(productBean.getWeight() * productBean.getScale() * num));
                    format.put("reduceMoeny", MyUtils.formatDouble((productBean1.getPrice() - productBean1.getRemainMoney()) * num));
                    format.put("price", MyUtils.formatDouble((productBean1.getPrice()) * num));
                    exchangeMeatList.add(format);
                }
            } else {
                break;

            }
        }
        signIndex = exchangeMeatList.size();
        for (int k = meatList.size() - 1; k >= signIndex; k--) {
            Object o = meatList.get(k);
            HashMap<String, Object> format = ObjectUtil.format(o);
            ProductBean productBean1 = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            if (totalWeight + ObjectUtil.getDouble(format, "meatWeight") <= hasMeatWeight) {
                totalWeight += ObjectUtil.getDouble(format, "meatWeight");
                format.put("reduceMoeny", MyUtils.formatDouble((ObjectUtil.getDouble(format, "price") - productBean1.getRemainMoney() * ObjectUtil.getDouble(format, "number"))));
                exchangeMeatList.add(format);
            } else if (totalWeight + MyUtils.formatDouble(ObjectUtil.getDouble(format, "meatWeight") / ObjectUtil.getDouble(format, "number")) <= hasMeatWeight) {
                int number = ObjectUtil.getDouble(format, "number").intValue();
                int num = 0;
                ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                for (int j = 0; j < number; j++) {
                    if (totalWeight + productBean.getWeight() * productBean.getScale() <= hasMeatWeight) {
                        num++;
                        totalWeight += productBean.getWeight() * productBean.getScale();
                    }
                }
                if (num > 0) {
                    format.put("number", num);
                    format.put("meatWeight", MyUtils.formatDouble(productBean.getWeight() * productBean.getScale() * num));
                    format.put("reduceMoeny", MyUtils.formatDouble((productBean1.getPrice() - productBean1.getRemainMoney()) * num));
                    format.put("price", MyUtils.formatDouble((productBean1.getPrice()) * num));
                    exchangeMeatList.add(format);
                }
            }
        }
        Logger.d(exchangeMeatList);
        return exchangeMeatList;
    }

    public static Double calculateTotalMoney(List<Object> useExchangeList) {
        double TotalMeatReduceMoney = 0.0;
        for (int i = 0; i < useExchangeList.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(useExchangeList.get(i));
            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            if (productBean.getScale() > 0) {
                if (MyUtils.getProductById(ObjectUtil.getString((HashMap<String, Object>) format, "id")).getCode().length() == 5) {
                    TotalMeatReduceMoney += (MyUtils.formatDouble(ObjectUtil.getDouble(format, "price")) - productBean.getRemainMoney()) * ObjectUtil.getDouble(format, "number");
                } else {
                    TotalMeatReduceMoney += (productBean.getPrice() - productBean.getRemainMoney()) * ObjectUtil.getDouble(format, "number");
                }

            }
        }
        return MyUtils.formatDouble(TotalMeatReduceMoney);
    }

    public static Double calculateTotalWeight(List<Object> useExchangeList) {
        double TotalMeatReduceWeight = 0.0;
        for (int i = 0; i < useExchangeList.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(useExchangeList.get(i));
            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            if (productBean.getScale() > 0) {
                TotalMeatReduceWeight += ObjectUtil.getDouble(format, "meatWeight");
            }
        }

        return MyUtils.formatDouble(TotalMeatReduceWeight);
    }

    public static void setPaymentDetail(OrderDetail orderDetail, Integer type, Double storedBalance, Double whiteBarBalance, TextView title, TextView money) {
        switch (type) {
            case 1:
                title.setText("消费金支付");
                money.setText("消费金支付" + orderDetail.getActualMoney() + "元");
                break;
            case 3:
                title.setText("支付宝支付");
                money.setText("支付宝支付" + orderDetail.getActualMoney() + "元");
                break;
            case 4:
                title.setText("微信支付");
                money.setText("微信支付" + orderDetail.getActualMoney() + "元");
                break;
            case 5:
                title.setText("银行卡支付");
                money.setText("银行卡支付" + orderDetail.getActualMoney() + "元");
                break;
            case 6:
                title.setText("现金支付");
                money.setText("现金支付" + orderDetail.getActualMoney() + "元");
                break;
            case 7:
                title.setText("消费金+支付宝支付");
                money.setText("消费金支付" + storedBalance + "元\n" + "支付宝支付" + MyUtils.formatDouble(orderDetail.getActualMoney() - storedBalance) + "元");
                break;
            case 8:
                title.setText("消费金+微信支付");
                money.setText("消费金支付" + storedBalance + "元\n" + "微信支付" + MyUtils.formatDouble(orderDetail.getActualMoney() - storedBalance) + "元");
                break;
            case 9:
                title.setText("消费金+银行卡支付");
                money.setText("消费金支付" + storedBalance + "元\n" + "银行卡支付" + MyUtils.formatDouble(orderDetail.getActualMoney() - storedBalance) + "元");
                break;
            case 10:
                title.setText("消费金+现金支付");
                money.setText("消费金支付" + storedBalance + "元\n" + "现金支付" + MyUtils.formatDouble(orderDetail.getActualMoney() - storedBalance) + "元");
                break;
            case 11:
                title.setText("白条支付");
                money.setText("白条支付" + orderDetail.getActualMoney() + "元");
                break;
            case 12:
                title.setText("消费金+白条支付");
                money.setText("消费金支付" + storedBalance + "元\n" + "白条支付" + MyUtils.formatDouble(orderDetail.getActualMoney() - storedBalance) + "元");
                break;
            case 13:
                title.setText("白条+支付宝支付");
                money.setText("白条" + whiteBarBalance + "元\n" + "支付宝支付" + MyUtils.formatDouble(orderDetail.getActualMoney() - whiteBarBalance) + "元");
                break;
            case 14:
                title.setText("白条+微信支付");
                money.setText("白条" + whiteBarBalance + "元\n" + "微信支付" + MyUtils.formatDouble(orderDetail.getActualMoney() - whiteBarBalance) + "元");
                break;
            case 15:
                title.setText("白条+银行卡支付");
                money.setText("白条" + whiteBarBalance + "元\n" + "银行卡支付" + MyUtils.formatDouble(orderDetail.getActualMoney() - whiteBarBalance) + "元");
                break;
            case 16:
                title.setText("白条+现金支付");
                money.setText("白条" + whiteBarBalance + "元\n" + "现金支付" + MyUtils.formatDouble(orderDetail.getActualMoney() - whiteBarBalance) + "元");
                break;
            case 17:
                title.setText("消费金+白条+支付宝支付");
                money.setText("消费金" + storedBalance + "元\n" + "白条支付" + whiteBarBalance + "\n支付宝支付" + MyUtils.formatDouble(orderDetail.getActualMoney() - storedBalance - whiteBarBalance) + "元");
                break;
            case 18:
                title.setText("消费金+白条+微信支付");
                money.setText("消费金" + storedBalance + "元\n" + "白条支付" + whiteBarBalance + "\n微信支付" + MyUtils.formatDouble(orderDetail.getActualMoney() - storedBalance - whiteBarBalance) + "元");
                break;
            case 19:
                title.setText("消费金+白条+银行卡支付");
                money.setText("消费金" + storedBalance + "元\n" + "白条支付" + whiteBarBalance + "\n银行卡支付" + MyUtils.formatDouble(orderDetail.getActualMoney() - storedBalance - whiteBarBalance) + "元");
                break;
            case 20:
                title.setText("消费金+白条+现金支付");
                money.setText("消费金" + storedBalance + "元\n" + "白条支付" + whiteBarBalance + "\n现金支付" + MyUtils.formatDouble(orderDetail.getActualMoney() - storedBalance - whiteBarBalance) + "元");
                break;
            case 21:
                title.setText("招商银行信用卡支付");
                money.setText("招行信用卡" + orderDetail.getActualMoney() + "元");
                break;
            case 22:
                title.setText("浦发信用卡支付");
                money.setText("浦发信用卡" + orderDetail.getActualMoney() + "元");
                break;
        }
    }

    public static String setPaymentContent(Integer type, Double actual, Double storedBalance, Double whiteBarBalance) {
        String content = "";
        switch (type) {
            case 1:
                content = "消费金支付" + actual + "元成功？";
                break;
            case 3:
                content = "支付宝支付" + actual + "元成功？";
                break;
            case 4:
                content = "微信支付" + actual + "元成功？";
                break;
            case 5:
                content = "银行卡支付" + actual + "元成功？";
                break;
            case 6:
                content = "现金支付" + actual + "元成功？";
                break;
            case 7:
                content = "支付宝支付" + MyUtils.formatDouble(actual - storedBalance) + "元成功？";
                break;
            case 8:
                content = "微信支付" + MyUtils.formatDouble(actual - storedBalance) + "元成功？";
                break;
            case 9:
                content = "银行卡支付" + MyUtils.formatDouble(actual - storedBalance) + "元成功？";
                break;
            case 10:
                content = "现金支付" + MyUtils.formatDouble(actual - storedBalance) + "元成功？";
                break;
            case 11:
                content = "白条支付" + actual + "元成功？";
                break;
            case 12:
                content = "消费金支付" + storedBalance + "元" + "白条支付" + MyUtils.formatDouble(actual - storedBalance) + "元成功？";
                break;
            case 13:
                content = "支付宝支付" + MyUtils.formatDouble(actual - storedBalance - whiteBarBalance) + "元成功？";
                break;
            case 14:
                content = "微信支付" + MyUtils.formatDouble(actual - storedBalance - whiteBarBalance) + "元成功？";
                break;
            case 15:
                content = "银行卡支付" + MyUtils.formatDouble(actual - storedBalance - whiteBarBalance) + "元成功？";
                break;
            case 16:
                content = "现金支付" + MyUtils.formatDouble(actual - storedBalance - whiteBarBalance) + "元成功？";
                break;
            case 17:
                content = "支付宝支付" + MyUtils.formatDouble(actual - storedBalance - whiteBarBalance) + "元成功？";
                break;
            case 18:
                content = "微信支付" + MyUtils.formatDouble(actual - storedBalance - whiteBarBalance) + "元成功？";
                break;
            case 19:
                content = "银行卡支付" + MyUtils.formatDouble(actual - storedBalance - whiteBarBalance) + "元成功？";
                break;
            case 20:
                content = "现金支付" + MyUtils.formatDouble(actual - storedBalance - whiteBarBalance) + "元成功？";
                break;
            case 21:
                content = "招行信用卡支付余下的" + actual + "元成功？";
                break;
            case 22:
                content = "浦发信用卡支付余下的" + actual + "元成功？";
                break;


        }
        return "确认使用" + content;
    }

    public static List<String> calTotalIds(List<Object> orders) {
        List<String> ids = new ArrayList<>();
        for (Object order : orders) {
            HashMap<String, Object> format = ObjectUtil.format(order);
            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            int number = ObjectUtil.getDouble(format, "number").intValue();
            for (int i = 0; i < number; i++) {
                ids.add(productBean.getObjectId());
            }
        }
        return ids;
    }

    public static List<Double> listToList(List<Object> useExchangeList) {
        List<Double> weights = new ArrayList<>();
        for (int i = 0; i < useExchangeList.size(); i++) {
            weights.add(ObjectUtil.getDouble(ObjectUtil.format(useExchangeList.get(i)), "meatWeight"));
        }
        return weights;
    }

    public static Object listToObject(List<Object> useExchangeList) {
        Map<String, Double> meats = new HashMap<>();
        for (int i = 0; i < useExchangeList.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(useExchangeList.get(i));
            String id = ObjectUtil.getString(format, "id");
            ProductBean productBean = MyUtils.getProductById(id);
            meats.put(productBean.getName(), ObjectUtil.getDouble(format, "meatWeight"));
        }
        return meats;
    }

    public static List<List<String>> getComboList(String comboMenu) {
        List<List<String>> comboList = new ArrayList<>();
        String[] split = comboMenu.split("\\|");
        if (comboMenu.length() > 0) {
            for (int i = 0; i < split.length; i++) {
                List<String> item = Arrays.asList(split[i].split("\\,"));
                comboList.add(item);
            }
        }
        return comboList;

    }

    public static Map<String, Double> managerEscrow(Double actualMoney, int escrow, UserBean userBean) {
        Map<String, Double> escrowDetail = new HashMap<>();
        Double whiteBarBalance = 0.0;
        Double storedBalance = 0.0;
        if (userBean != null) {
            whiteBarBalance = MyUtils.formatDouble(userBean.getBalance());
            storedBalance = MyUtils.formatDouble(userBean.getStored());
        }
        switch (escrow) {
            case 1:
                escrowDetail.put("消费金支付", actualMoney);
                break;
            case 3:
                escrowDetail.put("支付宝支付", actualMoney);
                break;
            case 4:
                escrowDetail.put("微信支付", actualMoney);
                break;
            case 5:
                escrowDetail.put("银联卡支付", actualMoney);
                break;
            case 6:
                escrowDetail.put("现金支付", actualMoney);
                break;
            case 7:

                escrowDetail.put("消费金支付", storedBalance);
                escrowDetail.put("支付宝支付", MyUtils.formatDouble(actualMoney - storedBalance));

                break;
            case 8:
                escrowDetail.put("消费金支付", storedBalance);
                escrowDetail.put("微信支付", MyUtils.formatDouble(actualMoney - storedBalance));
                break;
            case 9:
                escrowDetail.put("消费金支付", storedBalance);
                escrowDetail.put("银联卡支付", MyUtils.formatDouble(actualMoney - storedBalance));
                break;
            case 10:
                escrowDetail.put("消费金支付", storedBalance);
                escrowDetail.put("现金支付", MyUtils.formatDouble(actualMoney - storedBalance));
                break;
            case 11:
                escrowDetail.put("白条支付", actualMoney);
                break;
            case 12:
                escrowDetail.put("消费金支付", storedBalance);
                escrowDetail.put("白条支付", MyUtils.formatDouble(actualMoney - storedBalance));
                break;
            case 13:
                escrowDetail.put("白条支付", whiteBarBalance);
                escrowDetail.put("支付宝支付", MyUtils.formatDouble(actualMoney - whiteBarBalance));
                break;
            case 14:
                escrowDetail.put("白条支付", whiteBarBalance);
                escrowDetail.put("微信支付", MyUtils.formatDouble(actualMoney - whiteBarBalance));
                break;
            case 15:
                escrowDetail.put("白条支付", whiteBarBalance);
                escrowDetail.put("银联卡支付", MyUtils.formatDouble(actualMoney - whiteBarBalance));
                break;
            case 16:
                escrowDetail.put("白条支付", whiteBarBalance);
                escrowDetail.put("现金支付", MyUtils.formatDouble(actualMoney - whiteBarBalance));
                break;
            case 17:
                escrowDetail.put("消费金支付", storedBalance);
                escrowDetail.put("白条支付", whiteBarBalance);
                escrowDetail.put("支付宝支付", MyUtils.formatDouble(actualMoney - whiteBarBalance - storedBalance));
                break;
            case 18:
                escrowDetail.put("消费金支付", storedBalance);
                escrowDetail.put("白条支付", whiteBarBalance);
                escrowDetail.put("微信支付", MyUtils.formatDouble(actualMoney - whiteBarBalance - storedBalance));
                break;
            case 19:
                escrowDetail.put("消费金支付", storedBalance);
                escrowDetail.put("白条支付", whiteBarBalance);
                escrowDetail.put("银联卡支付", MyUtils.formatDouble(actualMoney - whiteBarBalance - storedBalance));
                break;
            case 20:
                escrowDetail.put("消费金支付", storedBalance);
                escrowDetail.put("白条支付", whiteBarBalance);
                escrowDetail.put("现金支付", MyUtils.formatDouble(actualMoney - whiteBarBalance - storedBalance));
                break;
            case 21:
                escrowDetail.put("招商信用卡银行支付", actualMoney);
                break;
            case 22:
                escrowDetail.put("浦发信用卡银行支付", actualMoney);
                break;

        }
        return escrowDetail;
    }

    public static Double calCommodityWeight(String barcode) {
        if (barcode.length()!= 18) {
            List<ProductBean> productBean = getProductBean(barcode);
            if (productBean.size() > 0) {
                return productBean.get(0).getWeight();
            } else {
                return 0.0;
            }

        } else {
            return new Double(Integer.parseInt(barcode.substring(12, 17))) / 1000;
        }
    }
    /**
     * 获取价格
     */
    public static Double getPrice(String barcode) {
        List<ProductBean> productBean = getProductBean(barcode);
        if (productBean.size() > 0) {
            return productBean.get(0).getPrice();
        } else {
            return 0.0;
        }
    }
    public static List<ProductBean> getProductBean(String barcode) {
        RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        List<ProductBean> productBeen = new ArrayList<ProductBean>();
        if (barcode.length() == 13) {
            productBeen = mRealmHleper.queryProductByBarcode(barcode);
        } else if (barcode.length() == 18) {
            productBeen = mRealmHleper.queryProductByBarcode(barcode.substring(2, 7));
        } else if (barcode.length() == 5) {
            productBeen = mRealmHleper.queryProductByBarcode(barcode);
        }else {
            productBeen = mRealmHleper.queryProductByBarcode(barcode);
        }
        return productBeen;
    }

    public static Double calCommodityMoney(String barcode) {
        RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        if (barcode.length() == 18) {
            double finalPrice = 0.0;
            double showprice = Double.parseDouble(barcode.substring(7, 12).substring(0, 5).replaceAll("^[0]+", "")) / 100;
            double price = getPrice(barcode) * (new Double(Integer.parseInt(barcode.substring(12, 17))) / 1000);
            if (price - showprice > 1) {
                if (price / 1000 >= 1) {
                    finalPrice = 1000 * Integer.parseInt((price + "").substring(0, 1)) + (new Double(Integer.parseInt(barcode.substring(7, 12))) / 100);
                } else {
                    finalPrice = showprice;
                }
            } else {
                finalPrice = showprice;
            }
            return MyUtils.formatDouble(finalPrice);
        } else if (barcode.length() == 13||barcode.length()==8) {
            return mRealmHleper.queryProductByBarcode(barcode).get(0).getPrice();
        } else {
            return 0.0;
        }
    }

    public static String calCommodityId(String barcode) {
        RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        return mRealmHleper.queryProductByBarcode(convertCode(barcode)).get(0).getObjectId();
    }

    public static String convertCode(String code) {
        if (code.length() == 13) {
            return code;
        } else if (code.length() == 18) {
            return code.substring(2, 7);
        } else {
            return code;
        }
    }

    public static List<Object> calOtherOder(List<Object> orders, HashMap<String, Object> otherTableOrders) {
        List<Object> finalOrders = orders;
        for (Map.Entry<String, Object> m : otherTableOrders.entrySet()) {
            List<Object> values = (List<Object>) m.getValue();
            for (int i = 0; i < values.size(); i++) {
                finalOrders.add(values.get(i));
            }
        }
        return finalOrders;
    }

    public static int indexOfSerial(List<Object> orders, int k) {
        int number = 0;
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
            int cookSerial = ObjectUtil.getInt(format, "cookSerial");
            if (cookSerial == k) {
                ++number;
            }
        }

        return number;
    }

    public static int indexOfNoDrink(List<Object> orders) {
        int number = 0;
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
            Logger.d(format);
            Logger.d(MyUtils.getProductById(ObjectUtil.getString(format, "id")).getType() != 3);
            Logger.d(MyUtils.getProductById(ObjectUtil.getString(format, "id")).getType() != 4);
            Logger.d(MyUtils.getProductById(ObjectUtil.getString(format, "id")).getType() != 3||MyUtils.getProductById(ObjectUtil.getString(format, "id")).getType() != 4);
            if (MyUtils.getProductById(ObjectUtil.getString(format, "id")).getType() != 3&&MyUtils.getProductById(ObjectUtil.getString(format, "id")).getType() != 4) {
                ++number;
                Logger.d(number);
            }
        }
        return number;
    }

    public static int indexOfDrink(List<Object> orders) {
        int number = 0;
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
            if (MyUtils.getProductById(ObjectUtil.getString(format, "id")).getType() == 3||MyUtils.getProductById(ObjectUtil.getString(format, "id")).getType() == 4) {
                ++number;
            }
        }
        return number;
    }

    /**
     * 确定满减金额
     */
    public static Double calFullReduceMoney(Double actualTotalMoneny) {
        RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        RuleBean ruleBean = mRealmHleper.queryAllRule().get(0);
        RealmList<String> fullReduce = ruleBean.getFullReduce();
        if (fullReduce.size() > 0) {
            int index = -1;
            for (int i = 0; i < fullReduce.size() - 1; i++) {
                if ((Double.parseDouble(fullReduce.get(fullReduce.size() - 1).split("-")[0]) < actualTotalMoneny)) {
                    index = fullReduce.size() - 1;
                }
                if (Double.parseDouble(fullReduce.get(i).split("-")[0]) < actualTotalMoneny && (Double.parseDouble(fullReduce.get(i + 1).split("-")[0]) > actualTotalMoneny)) {
                    index = i;
                }
            }
            if (index != -1) {
                return Double.parseDouble(fullReduce.get(index).split("-")[1]);
            } else {
                return 0.0;
            }


        } else {
            return 0.0;
        }

    }

    public static String calPresenter(AVObject tableAVObject, ProductBean productBean, boolean isSvip) {
        String code = "";
        if (productBean.getGivecode().length() > 0 && MyUtils.getProductById(productBean.getGivecode()) != null) {
            if (productBean.getGiveRule() == 0) {
                code = productBean.getGivecode();
            } else if (productBean.getGiveRule() == 1) {
                if (tableAVObject.getAVObject("user") != null) {
                    code = productBean.getGivecode();
                }
            } else if (productBean.getGiveRule() == 2) {
                if (tableAVObject.getAVObject("user") != null && isSvip) {
                    code = productBean.getGivecode();
                }
            }
        }
        return code;

    }

    public static void saveOperateLog(int i, List<Object> preOrders, AVObject avObject) {
        AVObject operateLog = new AVObject("OperateLog");
        operateLog.put("type", i);//0:下单 1:点单 2:改单 3:退单 4:结账
        operateLog.put("store", 1);
        operateLog.put("orderlist", preOrders);
        operateLog.put("tableNumber", avObject.getString("tableNumber"));
        operateLog.put("operator", AVObject.createWithoutData("_User", SharedHelper.read("cashierId")));
        operateLog.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {

            }
        });

    }

    public static boolean checkIsGive(int type) {
        for (int i = 0; i < CONST.GIVETYPES.length; i++) {
            if (CONST.GIVETYPES[i] == type) {
                return true;
            }
        }
        return false;
    }

    public static String calOtherTable(List<String> selectTableNumbers) {
        String content = "";
        for (int i = 0; i < selectTableNumbers.size(); i++) {
            content += "+" + selectTableNumbers.get(i);
        }
        return content;
    }

    /**
     * 判断是否是超牛会员订单
     */
    public static Boolean isRechargeSvipOrder(AVObject avObject) {
        List<String> commoditys = avObject.getList("commodity");
        if (commoditys.size() == 1) {
            String id = commoditys.get(0);
            if (id.equals(CONST.SVIPSTYLE.DATE_12_MONTH) || id.equals(CONST.SVIPSTYLE.DATE_1_MONTH)) {
                return true;
            }
        }
        return false;
    }


    public static List<ProductBean> getOtherGoods(int index) {
        RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        List<ProductBean> productBeen = mRealmHleper.queryOtherType(index);
        return productBeen;
    }

    public static Double calBlackFiveReduce(boolean useMeat, List<Object> useExchangeList, List<Object> maxExchangeList, List<Object> orderList) {
        Double forzenMoney = 0.0;
        Double exchangeMoney = 0.0;
        if (DateUtil.isBlackFive()) {
            for (int i = 0; i < orderList.size(); i++) {
                HashMap<String, Object> map = (HashMap<String, Object>) orderList.get(i);
                if (MyUtils.getProductById(ObjectUtil.getString(map, "id")).getType() == CONST.ForzeenMeatType) {
                    forzenMoney += ObjectUtil.getDouble(map, "price");
                }
            }
            if (useMeat) {
                for (int i = 0; i < useExchangeList.size(); i++) {
                    HashMap<String, Object> map = (HashMap<String, Object>) useExchangeList.get(i);
                    if (MyUtils.getProductById(ObjectUtil.getString(map, "id")).getType() == CONST.ForzeenMeatType) {
                        exchangeMoney += ObjectUtil.getDouble(map, "price");
                    }
                }
                forzenMoney = (forzenMoney - exchangeMoney) * 0.5;
            } else {
                forzenMoney *= 0.5;
            }
        }
        return MyUtils.formatDouble(forzenMoney);
    }
}
