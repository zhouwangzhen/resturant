package cn.kuwo.player.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by lovely on 2018/10/19
 */
public class IntegrateUtil {
    public static List<Object> integrateKitcherOrder(List<Object> oldOrders, String style) {
        List<Object> orders= null;
        try {
            orders = ObjectUtil.deepCopy(oldOrders);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        List<Object> mergeOrders = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
            boolean isualified = false;
            if (style.equals("kitchen")) {
                isualified = ProductUtil.isCookCommodity(format);
            } else if (style.equals("water")) {
                isualified = MyUtils.getProductById(ObjectUtil.getString(format, "id")).getType() == 3 || MyUtils.getProductById(ObjectUtil.getString(format, "id")).getType() == 4;
            }
            if (isualified) {
                String id = ObjectUtil.getString(format, "id");
                boolean hasCommodity = false;
                Object hasOrder = null;
                for (Object mergeOrder : mergeOrders) {
                    HashMap<String, Object> hasFormat = ObjectUtil.format(mergeOrder);
                    String hasId = ObjectUtil.getString(hasFormat, "id");
                    if (id.equals(hasId)) {
                        hasCommodity = true;
                        hasOrder = hasFormat;
                    }
                }
                if (hasCommodity) {
                    HashMap<String, Object> hashFormat = ObjectUtil.format(hasOrder);
                    hashFormat.put("price", ObjectUtil.getDouble(hashFormat, "price") + ObjectUtil.getDouble(format, "price"));
                    hashFormat.put("nb", ObjectUtil.getDouble(hashFormat, "nb") + ObjectUtil.getDouble(format, "nb"));
                    hashFormat.put("comment", (ObjectUtil.getString(hashFormat, "comment") + ObjectUtil.getString(hashFormat, "comment")).length() > 0 ? ObjectUtil.getString(hashFormat, "comment") + ObjectUtil.getString(format, "comment") : "");
                    hashFormat.put("presenter", (ObjectUtil.getString(hashFormat, "presenter") + ObjectUtil.getString(hashFormat, "presenter")).length() > 0 ? ObjectUtil.getString(hashFormat, "presenter") + ObjectUtil.getString(format, "presenter") : "");
                    String content = "";
                    if (ObjectUtil.getString(hashFormat, "sideDishCommodity").length() > 0) {
                        if (ObjectUtil.getString(format, "sideDishCommodity").length() > 0) {
                            content = ObjectUtil.getString(hashFormat, "sideDishCommodity") + "*" + ObjectUtil.getDouble(hashFormat, "number") + "+" + ObjectUtil.getString(format, "sideDishCommodity") + "*" + ObjectUtil.getDouble(format, "number");
                        } else {
                            content = ObjectUtil.getString(hashFormat, "sideDishCommodity") + "*" + ObjectUtil.getDouble(hashFormat, "number");
                        }
                    } else {
                        if (ObjectUtil.getString(format, "sideDishCommodity").length() > 0) {
                            content = ObjectUtil.getString(format, "sideDishCommodity") + "*" + ObjectUtil.getDouble(format, "number");
                        } else {
                            content = "";
                        }
                    }
                    String comment = "";
                    if (ObjectUtil.getString(hashFormat, "comment").length() > 0) {
                        if (ObjectUtil.getString(format, "comment").length() > 0) {
                            comment = ObjectUtil.getString(hashFormat, "comment") + "*" + ObjectUtil.getDouble(hashFormat, "number") + "+" + ObjectUtil.getString(format, "comment") + "*" + ObjectUtil.getDouble(format, "number");
                        } else {
                            comment = ObjectUtil.getString(hashFormat, "comment") + "*" + ObjectUtil.getDouble(hashFormat, "number");
                        }
                    } else {
                        if (ObjectUtil.getString(format, "comment").length() > 0) {
                            comment = ObjectUtil.getString(format, "comment") + "*" + ObjectUtil.getDouble(format, "number");
                        } else {
                            comment = "";
                        }
                    }
                    hashFormat.put("comment", comment);
                    hashFormat.put("sideDishCommodity", content);
                    hashFormat.put("number", ObjectUtil.getDouble(hashFormat, "number") + ObjectUtil.getDouble(format, "number"));
                } else {
                    mergeOrders.add(format);
                }
            }
        }
        return mergeOrders;
    }

    public static List<Object> integratePreOrder(List<Object> oldOrders) {
        List<Object> orders= null;
        try {
            orders = ObjectUtil.deepCopy(oldOrders);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        List<Object> mergeOrders = new ArrayList<>();
        for (int i = 0; i < orders.size(); i++) {
            HashMap<String, Object> format = ObjectUtil.format(orders.get(i));

            String id = ObjectUtil.getString(format, "id");
            boolean hasCommodity = false;
            Object hasOrder = null;
            for (Object mergeOrder : mergeOrders) {
                HashMap<String, Object> hasFormat = ObjectUtil.format(mergeOrder);
                String hasId = ObjectUtil.getString(hasFormat, "id");
                if (id.equals(hasId)) {
                    hasCommodity = true;
                    hasOrder = hasFormat;
                }
            }
            if (hasCommodity) {
                HashMap<String, Object> hashFormat = ObjectUtil.format(hasOrder);
                hashFormat.put("price", ObjectUtil.getDouble(hashFormat, "price") + ObjectUtil.getDouble(format, "price"));
                hashFormat.put("nb", ObjectUtil.getDouble(hashFormat, "nb") + ObjectUtil.getDouble(format, "nb"));
                hashFormat.put("comment", (ObjectUtil.getString(hashFormat, "comment") + ObjectUtil.getString(hashFormat, "comment")).length() > 0 ? ObjectUtil.getString(hashFormat, "comment") + ObjectUtil.getString(format, "comment") : "");
                hashFormat.put("presenter", (ObjectUtil.getString(hashFormat, "presenter") + ObjectUtil.getString(hashFormat, "presenter")).length() > 0 ? ObjectUtil.getString(hashFormat, "presenter") + ObjectUtil.getString(format, "presenter") : "");
                String content = "";
                if (ObjectUtil.getString(hashFormat, "sideDishCommodity").length() > 0) {
                    if (ObjectUtil.getString(format, "sideDishCommodity").length() > 0) {
                        content = ObjectUtil.getString(hashFormat, "sideDishCommodity") + "*" + ObjectUtil.getDouble(hashFormat, "number") + "+" + ObjectUtil.getString(format, "sideDishCommodity") + "*" + ObjectUtil.getDouble(format, "number");
                    } else {
                        content = ObjectUtil.getString(hashFormat, "sideDishCommodity") + "*" + ObjectUtil.getDouble(hashFormat, "number");
                    }
                } else {
                    if (ObjectUtil.getString(format, "sideDishCommodity").length() > 0) {
                        content = ObjectUtil.getString(format, "sideDishCommodity") + "*" + ObjectUtil.getDouble(format, "number");
                    } else {
                        content = "";
                    }
                }
                String comment = "";
                if (ObjectUtil.getString(hashFormat, "comment").length() > 0) {
                    if (ObjectUtil.getString(format, "comment").length() > 0) {
                        comment = ObjectUtil.getString(hashFormat, "comment") + "*" + ObjectUtil.getDouble(hashFormat, "number") + "+" + ObjectUtil.getString(format, "comment") + "*" + ObjectUtil.getDouble(format, "number");
                    } else {
                        comment = ObjectUtil.getString(hashFormat, "comment") + "*" + ObjectUtil.getDouble(hashFormat, "number");
                    }
                } else {
                    if (ObjectUtil.getString(format, "comment").length() > 0) {
                        content = ObjectUtil.getString(format, "comment") + "*" + ObjectUtil.getDouble(format, "number");
                    } else {
                        comment = "";
                    }
                }
                hashFormat.put("comment", comment);
                hashFormat.put("sideDishCommodity", content);
                hashFormat.put("number", ObjectUtil.getDouble(hashFormat, "number") + ObjectUtil.getDouble(format, "number"));
            } else {
                mergeOrders.add(format);
            }
        }
        return mergeOrders;
    }
}
