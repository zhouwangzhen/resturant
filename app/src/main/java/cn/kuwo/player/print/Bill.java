package cn.kuwo.player.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.avos.avoscloud.AVObject;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.bean.MeatBean;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.UserBean;
import cn.kuwo.player.event.OrderDetail;
import cn.kuwo.player.event.PrintEvent;
import cn.kuwo.player.event.ProgressEvent;
import cn.kuwo.player.event.SuccessEvent;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.PayInfoUtil;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.ToastUtil;

public class Bill {

    private static Context mContext;
    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(mContext, "IP连接失败", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 下单小票
     * type 0,正常 1,收银的坏了  厨房的好使 2,收银的好使 厨房的坏了
     */
    public static void printCateringFore(final List<Object> orders,
                                         final AVObject tableAVObject,
                                         final int type) {
        new Thread() {
            public void run() {
                mContext = MyApplication.getContextObject();
                SharedHelper sharedHelper = new SharedHelper(mContext);
                try {
                    if (type == 0 || type == -1) {
                        String url = SharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                        Pos pos;
                        final DecimalFormat df = new DecimalFormat("######0.00");
                        pos = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                        pos.initPos();
                        pos.printLocation(1);
                        pos.bold(true);
                        pos.printText("点单(客户联)");
                        pos.bold(false);
                        pos.printLocation(0);
                        pos.printTextNewLine("----------------------------------------------");
                        pos.printTextNewLine("操作时间:" + MyUtils.dateFormat(new Date()));
                        pos.printTextNewLine("----------------------------------------------");
                        pos.printTwoColumn("台 号:" + tableAVObject.getString("tableNumber"), "人数:" + tableAVObject.getInt("customer"));
                        pos.printLine(1);
                        pos.printTextNewLine("----------------------------------------------");
                        pos.printLine(1);
                        pos.printText("品名");
                        pos.printLocation(20, 1);
                        pos.printText("数量");
                        pos.printLocation(90, 1);
                        pos.printWordSpace(1);
                        pos.printText("单价");
                        pos.printWordSpace(2);
                        pos.printText("金额");
                        pos.printTextNewLine("----------------------------------------------");
                        for (int i = 0; i < orders.size(); i++) {
                            HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
                            pos.printTextNewLine(MyUtils.getProductById(ObjectUtil.getString(format, "id")).getName());
                            pos.printText("");
                            pos.printLocation(20, 1);
                            pos.printText(ObjectUtil.getDouble(format, "number") + "");
                            pos.printLocation(80, 1);
                            pos.printWordSpace(1);
                            pos.printText(MyUtils.getProductById(ObjectUtil.getString(format, "id")).getPrice() + "");
                            pos.printWordSpace(2);
                            pos.printText(MyUtils.formatDouble(MyUtils.getProductById(ObjectUtil.getString(format, "id")).getPrice() * ObjectUtil.getDouble(format, "number")) + "");
                            pos.printLine(1);
                            try {
                                if (ObjectUtil.getList(format, "comboList").size() > 0) {
                                    List<String> comboList = ObjectUtil.getList(format, "comboList");
                                    for (int j = 0; j < comboList.size(); j++) {
                                        pos.printLine();
                                        pos.printWordSpace(1);
                                        pos.printText(comboList.get(j));
                                        pos.printLine();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (ObjectUtil.getString(format, "presenter").length() > 0) {
                                pos.printLine();
                                pos.printWordSpace(1);
                                pos.printText("赠送菜品:" + MyUtils.getProductById(ObjectUtil.getString(format, "presenter")).getName());
                                pos.printLine();
                            }
                            if (ObjectUtil.getString(format, "comment") != "" && ObjectUtil.getString(format, "comment").trim().length() > 0) {
                                pos.printText("(备注:" + ObjectUtil.getString(format, "comment") + ")");
                            }
                        }
                        pos.printLine(2);
                        pos.feedAndCut();
                        pos.closeIOAndSocket();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new SuccessEvent(-1, tableAVObject.getString("tableNumber") + "桌下单收银小票机连接失败", orders, tableAVObject));
                }
                try {
                    if (type == 0 || type == -2) {
                        if (ProductUtil.indexOfNoDrink(orders) > 0) {
                            String url_kitchen = SharedHelper.read("ip1_kitchen") + "." + SharedHelper.read("ip2_kitchen") + "." + SharedHelper.read("ip3_kitchen") + "." + SharedHelper.read("ip4_kitchen");
                            Pos pos1;
                            pos1 = new Pos(url_kitchen, 9100, "GBK");    //第一个参数是打印机网口IP
                            pos1.initPos();
                            pos1.printLine(1);
                            pos1.fontSizeSetBig(3);
                            pos1.printCenter();
                            pos1.printText("厨房订单(主联)");
                            pos1.printLine(1);
                            pos1.printLocation(2);
                            pos1.fontSizeSetBig(3);
                            pos1.printText(tableAVObject.getString("tableNumber") + "桌");
                            pos1.printLine(1);
                            pos1.fontSizeSetBig(1);
                            pos1.printLocation(0);
                            pos1.printText("点单");
                            pos1.printTextNewLine("-----------------------------------------------");
                            pos1.printLine(1);
                            for (int k = -1; k < 6; k++) {
                                pos1.bold(true);
                                int serial = ProductUtil.indexOfSerial(orders, k);
                                Logger.d(serial);
                                if (k == 1 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第一道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);
                                } else if (k == 2 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第二道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);
                                } else if (k == 3 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第三道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);
                                } else if (k == 4 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第四道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);

                                } else if (k == 5 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第五道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);
                                }
                                pos1.bold(false);
                                for (int i = 0; i < orders.size(); i++) {
                                    pos1.printLocation(0);
                                    HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
                                    int serialNumber = ProductUtil.indexOfSerial(orders, k);
                                    ProductUtil.indexOfSerial(orders, k);
                                    int number = 0;
                                    if (ObjectUtil.getInt(format, "cookSerial") == k && serialNumber > 0 && MyUtils.getProductById(ObjectUtil.getString(format, "id")).getType() != 5) {
                                        pos1.printLocation(0);
                                        pos1.printTextNewLine(MyUtils.getProductById(ObjectUtil.getString(format, "id")).getName());
                                        pos1.printLine(1);
                                        pos1.printText("");
                                        pos1.printLocation(20, 1);
                                        pos1.printText("");
                                        pos1.printLocation(80, 1);
                                        pos1.printWordSpace(1);
                                        pos1.printText("");
                                        pos1.printWordSpace(2);
                                        pos1.printText("   x" + ObjectUtil.getDouble(format, "number") + "份");
                                        pos1.printLine(1);
                                        Logger.d(ObjectUtil.getList(format, "comboList").size());
                                        if (ObjectUtil.getList(format, "comboList").size() > 0) {
                                            List<String> comboList = ObjectUtil.getList(format, "comboList");
                                            for (int j = 0; j < comboList.size(); j++) {
                                                pos1.printLine();
                                                pos1.printWordSpace(1);
                                                pos1.printText(comboList.get(j));
                                                pos1.printLine();
                                            }
                                        }
                                        if (ObjectUtil.getString(format, "presenter").length() > 0) {
                                            pos1.printLine();
                                            pos1.printWordSpace(1);
                                            pos1.printText("赠送菜品:" + MyUtils.getProductById(ObjectUtil.getString(format, "presenter")).getName());
                                            pos1.printLine();
                                        }
                                        if (ObjectUtil.getString(format, "comment") != "" && ObjectUtil.getString(format, "comment").trim().length() > 0) {
                                            pos1.printText("(备注:" + ObjectUtil.getString(format, "comment") + ")");
                                            pos1.printLine(1);
                                        }
                                        if (number > 0 && number != serialNumber) {
                                            pos1.printTextNewLine("-----------------------------------------------");
                                        }
                                        ++number;

                                        pos1.printLine(1);
                                    }
                                }
                            }
                            pos1.printLine(3);
                            pos1.feedAndCut();
                            pos1.closeIOAndSocket();
                        }

                        if (ProductUtil.indexOfNoDrink(orders) > 0) {
                            String url_kitchen = SharedHelper.read("ip1_kitchen") + "." + SharedHelper.read("ip2_kitchen") + "." + SharedHelper.read("ip3_kitchen") + "." + SharedHelper.read("ip4_kitchen");
                            Pos pos1;
                            pos1 = new Pos(url_kitchen, 9100, "GBK");    //第一个参数是打印机网口IP
                            pos1.initPos();
                            pos1.printLine(1);
                            pos1.fontSizeSetBig(3);
                            pos1.printCenter();
                            pos1.printText("厨房订单(副联)");
                            pos1.fontSizeSetBig(1);
                            pos1.printLine(1);
                            pos1.printLocation(2);
                            pos1.fontSizeSetBig(3);
                            pos1.printText(tableAVObject.getString("tableNumber") + "桌");
                            pos1.printLine(1);
                            pos1.fontSizeSetBig(1);
                            pos1.printLocation(0);
                            pos1.printText("点单");
                            pos1.printTextNewLine("----------------------------------------------");
                            pos1.printLine(1);
                            for (int k = -1; k < 6; k++) {
                                pos1.bold(true);
                                int serial = ProductUtil.indexOfSerial(orders, k);
                                Logger.d(serial);
                                if (k == 1 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第一道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);
                                } else if (k == 2 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第二道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);
                                } else if (k == 3 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第三道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);
                                } else if (k == 4 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第四道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);

                                } else if (k == 5 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第五道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);
                                }
                                pos1.bold(false);
                                for (int i = 0; i < orders.size(); i++) {
                                    pos1.printLocation(0);
                                    HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
                                    int serialNumber = ProductUtil.indexOfSerial(orders, k);
                                    ProductUtil.indexOfSerial(orders, k);
                                    int number = 0;
                                    if (ObjectUtil.getInt(format, "cookSerial") == k && serialNumber > 0 && MyUtils.getProductById(ObjectUtil.getString(format, "id")).getType() != 5) {
                                        pos1.printLocation(0);
                                        pos1.printTextNewLine(MyUtils.getProductById(ObjectUtil.getString(format, "id")).getName());
                                        pos1.printLine(1);
                                        pos1.printText("");
                                        pos1.printLocation(20, 1);
                                        pos1.printText("");
                                        pos1.printLocation(80, 1);
                                        pos1.printWordSpace(1);
                                        pos1.printText("");
                                        pos1.printWordSpace(2);
                                        pos1.printText("   x" + ObjectUtil.getDouble(format, "number") + "份");
                                        pos1.printLine(1);
                                        Logger.d(ObjectUtil.getList(format, "comboList").size());
                                        if (ObjectUtil.getList(format, "comboList").size() > 0) {
                                            List<String> comboList = ObjectUtil.getList(format, "comboList");
                                            for (int j = 0; j < comboList.size(); j++) {
                                                pos1.printLine();
                                                pos1.printWordSpace(1);
                                                pos1.printText(comboList.get(j));
                                                pos1.printLine();
                                            }
                                        }
                                        if (ObjectUtil.getString(format, "presenter").length() > 0) {
                                            pos1.printLine();
                                            pos1.printWordSpace(1);
                                            pos1.printText("赠送菜品:" + MyUtils.getProductById(ObjectUtil.getString(format, "presenter")).getName());
                                            pos1.printLine();
                                        }
                                        if (ObjectUtil.getString(format, "comment") != "" && ObjectUtil.getString(format, "comment").trim().length() > 0) {
                                            pos1.printText("(备注:" + ObjectUtil.getString(format, "comment") + ")");
                                            pos1.printLine(1);
                                        }
                                        if (number > 0 && number != serialNumber) {
                                            pos1.printTextNewLine("-----------------------------------------------");
                                        }
                                        ++number;

                                    }
                                }
                            }
                            pos1.printLine(2);
                            pos1.feedAndCut();
                            pos1.closeIOAndSocket();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.d(e.getMessage());
                    EventBus.getDefault().post(new SuccessEvent(-2, tableAVObject.getString("tableNumber") + "桌下单厨房小票机连接失败", orders, tableAVObject));

                }
                try {
                    if (type == 0 || type == -3) {
                        if (ProductUtil.indexOfDrink(orders) > 0) {
                            String url_kitchen = SharedHelper.read("ip1_drink") + "." + SharedHelper.read("ip2_drink") + "." + SharedHelper.read("ip3_drink") + "." + SharedHelper.read("ip4_drink");
                            Pos pos2;
                            pos2 = new Pos(url_kitchen, 9100, "GBK");    //第一个参数是打印机网口IP
                            pos2.initPos();
                            pos2.printLine(1);
                            pos2.fontSizeSetBig(3);
                            pos2.printCenter();
                            pos2.printText("水吧订单");
                            pos2.fontSizeSetBig(1);
                            pos2.printLine(1);
                            pos2.printLocation(2);
                            pos2.fontSizeSetBig(3);
                            pos2.printText(tableAVObject.getString("tableNumber") + "桌");
                            pos2.printLine(1);
                            pos2.fontSizeSetBig(1);
                            pos2.printLocation(0);
                            pos2.printText("点单");
                            pos2.printTextNewLine("-----------------------------------------------");
                            pos2.printLine(1);

                            for (int i = 0; i < orders.size(); i++) {
                                pos2.printLocation(0);
                                HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
                                if (MyUtils.getProductById(ObjectUtil.getString(format, "id")).getType() == 5) {
                                    pos2.printLocation(0);
                                    pos2.printTextNewLine(MyUtils.getProductById(ObjectUtil.getString(format, "id")).getName());
                                    pos2.printLine(1);
                                    pos2.printText("");
                                    pos2.printLocation(20, 1);
                                    pos2.printText("");
                                    pos2.printLocation(80, 1);
                                    pos2.printWordSpace(1);
                                    pos2.printText("");
                                    pos2.printWordSpace(2);
                                    pos2.printText("   x" + ObjectUtil.getDouble(format, "number") + "份");
                                    pos2.printLine(1);
                                    Logger.d(ObjectUtil.getList(format, "comboList").size());
                                    if (ObjectUtil.getList(format, "comboList").size() > 0) {
                                        List<String> comboList = ObjectUtil.getList(format, "comboList");
                                        for (int j = 0; j < comboList.size(); j++) {
                                            pos2.printLine();
                                            pos2.printWordSpace(1);
                                            pos2.printText(comboList.get(j));
                                            pos2.printLine();
                                        }
                                    }
                                    if (ObjectUtil.getString(format, "presenter").length() > 0) {
                                        pos2.printLine();
                                        pos2.printWordSpace(1);
                                        pos2.printText("赠送菜品:" + MyUtils.getProductById(ObjectUtil.getString(format, "presenter")).getName());
                                        pos2.printLine();
                                    }
                                    if (ObjectUtil.getString(format, "comment") != "" && ObjectUtil.getString(format, "comment").trim().length() > 0) {
                                        pos2.printText("(备注:" + ObjectUtil.getString(format, "comment") + ")");
                                        pos2.printLine(1);
                                    }
                                    pos2.printLine(1);
                                }
                            }

                            pos2.printLine(3);
                            pos2.feedAndCut();
                            pos2.closeIOAndSocket();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.d(e.getMessage());
                    EventBus.getDefault().post(new SuccessEvent(-3, tableAVObject.getString("tableNumber") + "桌下单水吧小票机连接失败", orders, tableAVObject));

                }
                try {
                    if (type == 0 || type == -4) {
                        if (ProductUtil.indexOfNoDrink(orders) > 0) {
                            String url_kitchen = SharedHelper.read("ip1_cool") + "." + SharedHelper.read("ip2_cool") + "." + SharedHelper.read("ip3_cool") + "." + SharedHelper.read("ip4_cool");
                            Pos pos1;
                            pos1 = new Pos(url_kitchen, 9100, "GBK");    //第一个参数是打印机网口IP
                            pos1.initPos();
                            pos1.printLine(1);
                            pos1.fontSizeSetBig(3);
                            pos1.printCenter();
                            pos1.printText("冷菜间订单");
                            pos1.fontSizeSetBig(1);
                            pos1.printLine(1);
                            pos1.printLocation(2);
                            pos1.fontSizeSetBig(3);
                            pos1.printText(tableAVObject.getString("tableNumber") + "桌");
                            pos1.printLine(1);
                            pos1.fontSizeSetBig(1);
                            pos1.printLocation(0);
                            pos1.printText("点单");
                            pos1.printTextNewLine("-----------------------------------------------");
                            pos1.printLine(1);
                            for (int k = -1; k < 6; k++) {
                                pos1.bold(true);
                                int serial = ProductUtil.indexOfSerial(orders, k);
                                if (k == 1 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第一道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);
                                } else if (k == 2 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第二道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);
                                } else if (k == 3 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第三道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);
                                } else if (k == 4 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第四道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);

                                } else if (k == 5 && serial > 0) {
                                    pos1.printText("-----------------------------------------------");
                                    pos1.printLine(1);
                                    pos1.printLocation(1);
                                    pos1.bold(true);
                                    pos1.printText("第五道菜" + "(" + serial + "种)");
                                    pos1.printLine(1);
                                    pos1.bold(false);
                                }
                                pos1.bold(false);
                                for (int i = 0; i < orders.size(); i++) {
                                    pos1.printLocation(0);
                                    HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
                                    int serialNumber = ProductUtil.indexOfSerial(orders, k);
                                    ProductUtil.indexOfSerial(orders, k);
                                    int number = 0;
                                    if (ObjectUtil.getInt(format, "cookSerial") == k && serialNumber > 0 && MyUtils.getProductById(ObjectUtil.getString(format, "id")).getType() != 5) {
                                        pos1.printLocation(0);
                                        pos1.printTextNewLine(MyUtils.getProductById(ObjectUtil.getString(format, "id")).getName());
                                        pos1.printLine(1);
                                        pos1.printText("");
                                        pos1.printLocation(20, 1);
                                        pos1.printText("");
                                        pos1.printLocation(80, 1);
                                        pos1.printWordSpace(1);
                                        pos1.printText("");
                                        pos1.printWordSpace(2);
                                        pos1.printText("   x" + ObjectUtil.getDouble(format, "number") + "份");
                                        pos1.printLine(1);
                                        Logger.d(ObjectUtil.getList(format, "comboList"));
                                        if (format.containsKey("comboList") && ObjectUtil.getList(format, "comboList").size() > 0) {
                                            List<String> comboList = ObjectUtil.getList(format, "comboList");
                                            for (int j = 0; j < comboList.size(); j++) {
                                                pos1.printLine();
                                                pos1.printWordSpace(1);
                                                pos1.printText(comboList.get(j));
                                                pos1.printLine();
                                            }
                                        }
                                        if (ObjectUtil.getString(format, "presenter").length() > 0) {
                                            pos1.printLine();
                                            pos1.printWordSpace(1);
                                            pos1.printText("赠送菜品:" + MyUtils.getProductById(ObjectUtil.getString(format, "presenter")).getName());
                                            pos1.printLine();
                                        }
                                        if (ObjectUtil.getString(format, "comment") != "" && ObjectUtil.getString(format, "comment").trim().length() > 0) {
                                            pos1.printText("(备注:" + ObjectUtil.getString(format, "comment") + ")");
                                            pos1.printLine(1);
                                        }
                                        if (number > 0 && number != serialNumber) {
                                            pos1.printTextNewLine("-----------------------------------------------");
                                        }
                                        ++number;

                                        pos1.printLine(1);
                                    }
                                }
                            }
                            pos1.printLine(3);
                            pos1.feedAndCut();
                            pos1.closeIOAndSocket();
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.d(e.getMessage());
                    EventBus.getDefault().post(new SuccessEvent(-4, tableAVObject.getString("tableNumber") + "桌下单冷菜间小票机连接失败", orders, tableAVObject));

                }
            }

        }.start();
    }

    public static void resturateGenerate(final Context context,
                                         final AVObject tableAvObject,
                                         final String payStyleContent,
                                         final String userId,
                                         Double stored,
                                         Double balance,
                                         final Double originMoney,
                                         final Double finalMoney,
                                         final UserBean userBean,
                                         final int payment,
                                         final Double couponMoney,
                                         final String couponContent,
                                         final List<MeatBean> ableExchangeMeatList,
                                         final Double cartMeatsMoney) {
        new Thread() {
            public void run() {
                try {

                    List<ProductBean> productBeans = new ArrayList<>();
                    List<Double> productNumbers = new ArrayList<>();
                    List<String> productComments = new ArrayList<>();
                    mContext = context;
                    List<String> commoditys = (List<String>) tableAvObject.getList("commodity");
                    for (int i = 0; i < commoditys.size(); i++) {
                        ProductBean product = MyUtils.getProductById(commoditys.get(i));
                        productBeans.add(product);
                        productNumbers.add(Double.valueOf(tableAvObject.getList("commodityNumber").get(0).toString()));
                        productComments.add(((List<String>) tableAvObject.getList("comments")).get(i));
                    }
                    SharedHelper sharedHelper = new SharedHelper(context);
                    String url = SharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                    Pos pos;
                    final DecimalFormat df = new DecimalFormat("######0.00");
                    pos = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                    pos.initPos();
                    pos.printLocation(1);
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_papa);
                    pos.printBitmap(bitmap);
                    pos.printLocation(1);
                    pos.printText("(客户联)");
                    pos.printText("已结账");
                    pos.printLocation(0);
                    pos.printLine(2);
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printTextNewLine("开单时间:" + MyUtils.dateFormat(tableAvObject.getDate("startedAt")));
                    pos.printTextNewLine("操作时间:" + MyUtils.dateFormat(new Date()));
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printTextNewLine("台 号:" + tableAvObject.getString("tableNumber"));
                    pos.printTextNewLine("人数:" + tableAvObject.getInt("customer"));
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printLine(1);
                    pos.printText("品名");
                    pos.printLocation(20, 1);
                    pos.printText("数量");
                    pos.printLocation(90, 1);
                    pos.printWordSpace(1);
                    pos.printText("单价");
                    pos.printWordSpace(2);
                    pos.printText("金额");
                    pos.printTextNewLine("----------------------------------------------");
                    for (int i = 0; i < productBeans.size(); i++) {
                        pos.printLine(1);
                        pos.printTextNewLine(productBeans.get(i).getName());
                        pos.printLine(1);
                        pos.printText("");
                        pos.printLocation(20, 1);
                        pos.printText(productNumbers.get(i) + "");
                        pos.printLocation(80, 1);
                        pos.printWordSpace(1);
                        pos.printText(productBeans.get(i).getPrice() + "");
                        pos.printWordSpace(2);
                        pos.printText(MyUtils.formatDouble(productBeans.get(i).getPrice() * productNumbers.get(i)) + "");
                        pos.printLine(1);
                    }
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printLine(1);
                    pos.printLocation(0);
                    pos.printTwoColumn("原价合计 :", originMoney + "");
                    pos.printLine(1);
                    if (couponMoney > 0) {
                        pos.printTwoColumn(couponContent + ":", "-" + couponMoney + "");
                        pos.printLine(1);
                    }
                    if (cartMeatsMoney > 0) {
                        pos.printTwoColumn("超牛牛肉抵扣金额:", "-" + cartMeatsMoney + "");
                        pos.printLine(1);
                    }

                    pos.printTwoColumn("优惠合计 :", MyUtils.formatDouble(originMoney - finalMoney) + "");
                    pos.printLine(1);
                    pos.printTwoColumn("应收合计 :", finalMoney + "");
                    pos.printLine(1);
                    pos.printTextNewLine("--------------------支付方式--------------------");
                    pos.printLine(1);
                    pos.printTwoColumn(payStyleContent, finalMoney + "");
                    pos.printLine(1);
                    if (ableExchangeMeatList.size() > 0) {
                        pos.printTextNewLine("--------------------牛肉抵扣--------------------");
                        pos.printLine(1);
                        pos.printText("品名");
                        pos.printLocation(20, 1);
                        pos.printText("数量");
                        pos.printLocation(30, 1);
                        pos.printWordSpace(2);
                        pos.printText("重量");
                        pos.printWordSpace(1);
                        pos.printText("金额");
                        pos.printLine(1);
                        for (int i = 0; i < ableExchangeMeatList.size(); i++) {
                            MeatBean meatBean = ableExchangeMeatList.get(i);
                            pos.printLine(1);
                            pos.printText(meatBean.getName());
                            pos.printLocation(20, 1);
                            pos.printText("1");
                            pos.printLocation(90, 1);
                            pos.printWordSpace(2);
                            pos.printText(meatBean.getWeight() + "kg");
                            pos.printWordSpace(1);
                            pos.printText(meatBean.getPrice() + "");
                            pos.printLine(1);
                        }
                    }
                    if (userId.length() > 0) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        pos.printText("会员手机号：" + userBean.getUsername().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
                        pos.printLine(1);
                        pos.printText("本次积分：" + MyUtils.formatDouble(Math.round(Double.valueOf(finalMoney))));
                        pos.printLine(1);
                        pos.printText("累计积分：" + MyUtils.formatDouble(Math.round(Double.valueOf(finalMoney)) + userBean.getCredit()));
                        pos.printLine(1);
                        if (payment == 1) {
                            if (finalMoney <= userBean.getStored()) {
                                pos.printText("消费金余额：" + MyUtils.formatDouble(userBean.getStored() - finalMoney));
                                pos.printLine(1);
                                pos.printText("白条余额：" + Math.round(Double.valueOf(userBean.getBalance())));
                            } else {
                                pos.printText("消费金余额：0.0");
                                pos.printLine(1);
                                pos.printText("白条余额：" + Math.round(Double.valueOf(userBean.getBalance() + userBean.getStored() - finalMoney)));
                            }
                        } else if (payment == 2) {
                            pos.printText("消费金余额：" + MyUtils.formatDouble(userBean.getStored()));
                            pos.printLine(1);
                            pos.printText("白条余额：" + Math.round(Double.valueOf(userBean.getBalance())));
                        } else if (payment == 3) {
                            pos.printText("消费金余额：0.0");
                            pos.printLine(1);
                            pos.printText("白条余额：0.0");


                        }

                        pos.printLine(1);
                    }
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLocation(1);
                    pos.printLine(1);
                    pos.printText("地址：上海市徐汇区龙华路2520号");
                    pos.printLine(2);
                    pos.printText("电话：021-54566808");
                    pos.printLine(2);
                    pos.printText("祝您生活越来越牛!");
                    pos.printLine(2);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();


                    Pos pos1;
                    pos1 = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                    pos1.initPos();
                    pos1.printLine(2);
                    pos1.printLocation(1);
                    Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_papa);
                    pos1.printBitmap(bitmap1);
                    pos1.printLocation(0);
                    pos1.printText("(商户联)");
                    pos1.printText("已结账");
                    pos1.printLocation(0);
                    pos1.printLine(2);
                    pos1.printTextNewLine("----------------------------------------------");
                    pos1.printTextNewLine("开单时间:" + MyUtils.dateFormat(tableAvObject.getDate("startedAt")));
                    pos1.printTextNewLine("操作时间:" + MyUtils.dateFormat(new Date()));
                    pos1.printTextNewLine("----------------------------------------------");
                    pos1.printTextNewLine("台 号:" + tableAvObject.getString("tableNumber"));
                    pos1.printTextNewLine("人数:" + tableAvObject.getInt("customer"));
                    pos1.printLine(1);
                    pos1.printTextNewLine("----------------------------------------------");
                    pos1.printLine(1);
                    pos1.printText("品名");
                    pos1.printLocation(20, 1);
                    pos1.printText("数量");
                    pos1.printLocation(90, 1);
                    pos1.printWordSpace(1);
                    pos1.printText("单价");
                    pos1.printWordSpace(2);
                    pos1.printText("金额");
                    pos1.printTextNewLine("----------------------------------------------");
                    for (int i = 0; i < productBeans.size(); i++) {
                        pos1.printLine(1);
                        pos1.printTextNewLine(productBeans.get(i).getName());
                        pos1.printLine(1);
                        pos1.printText("");
                        pos1.printLocation(20, 1);
                        pos1.printText(productNumbers.get(i) + "");
                        pos1.printLocation(80, 1);
                        pos1.printWordSpace(1);
                        pos1.printText(productBeans.get(i).getPrice() + "");
                        pos1.printWordSpace(2);
                        pos1.printText(MyUtils.formatDouble(productBeans.get(i).getPrice() * productNumbers.get(i)) + "");
                        pos1.printLine(1);
                    }
                    pos1.printTextNewLine("----------------------------------------------");
                    pos1.printLine(1);
                    pos1.printLocation(0);
                    pos1.printTwoColumn("原价合计 :", originMoney + "");
                    pos1.printLine(1);
                    if (couponMoney > 0) {
                        pos1.printTwoColumn(couponContent + " :", "-" + couponMoney + "");
                        pos1.printLine(1);
                    }
                    if (cartMeatsMoney > 0) {
                        pos1.printTwoColumn("超牛牛肉抵扣金额:", "-" + cartMeatsMoney + "");
                        pos1.printLine(1);
                    }
                    pos1.printTwoColumn("优惠合计 :", MyUtils.formatDouble(originMoney - finalMoney) + "");
                    pos1.printLine(1);
                    pos1.printTwoColumn("应收合计 :", finalMoney + "");
                    pos1.printLine(1);
                    pos1.printTextNewLine("--------------------支付方式--------------------");
                    pos1.printLine(1);
                    pos1.printTwoColumn(payStyleContent, finalMoney + "");
                    pos1.printLine(1);
                    if (ableExchangeMeatList.size() > 0) {
                        pos1.printTextNewLine("--------------------牛肉抵扣--------------------");
                        pos1.printLine(1);
                        pos1.printText("品名");
                        pos1.printLocation(20, 1);
                        pos1.printText("数量");
                        pos1.printLocation(30, 1);
                        pos1.printWordSpace(1);
                        pos1.printText("重量");
                        pos1.printWordSpace(2);
                        pos1.printText("金额");
                        pos1.printLine(1);
                        for (int i = 0; i < ableExchangeMeatList.size(); i++) {
                            MeatBean meatBean = ableExchangeMeatList.get(i);
                            pos1.printLine(1);
                            pos1.printText(meatBean.getName());
                            pos1.printLocation(20, 1);
                            pos1.printText("1");
                            pos1.printLocation(90, 1);
                            pos1.printWordSpace(2);
                            pos1.printText(meatBean.getWeight() + "kg");
                            pos1.printWordSpace(1);
                            pos1.printText(meatBean.getPrice() + "");
                            pos1.printLine(1);
                        }
                    }
                    if (userId.length() > 0) {
                        pos1.printTextNewLine("------------------------------------------------");
                        pos1.printLine(1);
                        pos1.printText("会员手机号：" + userBean.getUsername().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
                        pos1.printLine(1);
                        pos1.printText("本次积分：" + Math.round(Double.valueOf(finalMoney)));
                        pos1.printLine(1);
                        pos1.printText("累计积分：" + MyUtils.formatDouble(Math.round(Double.valueOf(finalMoney)) + userBean.getCredit()));
                        pos1.printLine(1);
                        if (payment == 1) {
                            if (finalMoney <= userBean.getStored()) {
                                pos1.printText("消费金余额：" + MyUtils.formatDouble(userBean.getStored() - finalMoney));
                                pos1.printLine(1);
                                pos1.printText("白条余额：" + Math.round(Double.valueOf(userBean.getBalance())));
                            } else {
                                pos1.printText("消费金余额：0.0");
                                pos1.printLine(1);
                                pos1.printText("白条余额：" + Math.round(Double.valueOf(userBean.getBalance() + userBean.getStored() - finalMoney)));
                            }
                        } else if (payment == 2) {
                            pos1.printText("消费金余额：" + MyUtils.formatDouble(userBean.getStored()));
                            pos1.printLine(1);
                            pos1.printText("白条余额：" + Math.round(Double.valueOf(userBean.getBalance())));
                        } else if (payment == 3) {
                            pos1.printText("消费金余额：0.0");
                            pos1.printLine(1);
                            pos1.printText("白条余额：0.0");


                        }

                        pos1.printLine(1);
                    }
                    pos1.printTextNewLine("----------------------------------------------");
                    pos1.printLocation(1);
                    pos1.printLine(1);
                    pos1.printText("地址：上海市徐汇区龙华路2520号");
                    pos1.printLine(2);
                    pos1.printText("电话：021-54566808");
                    pos1.printLine(2);
                    pos1.printText("祝您生活越来越牛!");
                    pos1.printLine(2);
                    pos1.feedAndCut();
                    pos1.closeIOAndSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                    Logger.d("打印失败");
                }
            }

        }.start();

    }

    /**
     * 获取现在的时间
     */
    public static String getNowDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 打印结账账单
     */
    public static void printSettleBill(final Context context,
                                       final OrderDetail orderDetail,
                                       final JSONObject jsonReduce,
                                       final int escrow,
                                       final String finalTableNumber) {
        new Thread() {
            @Override
            public void run() {
                try {
                    AVObject tableAVObject = orderDetail.getAvObject();
                    List<Object> orders = orderDetail.getFinalOrders();
                    Double totalMoney = orderDetail.getTotalMoney();
                    Double actualMoney = orderDetail.getActualMoney();
                    mContext = context;
                    SharedHelper sharedHelper = new SharedHelper(context);
                    String url = sharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                    Pos pos;
                    pos = new Pos(url, 9100, "GBK");
                    pos.initPos();
                    pos.printLocation(1);
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
                    pos.printBitmap(bitmap);
                    pos.bold(true);
                    pos.printLine(1);
                    pos.printText("用户联");
                    pos.printLine(1);
                    pos.bold(false);
                    pos.printLine(1);
                    pos.printLocation(0);
                    pos.printText("桌号:" + finalTableNumber);
                    pos.printLine(1);
                    pos.printText("人数:" + tableAVObject.getInt("customer"));
                    pos.printLine(1);
                    if (tableAVObject.getDate("startedAt") != null) {
                        pos.printTextNewLine("开单时间:" + MyUtils.dateFormat(tableAVObject.getDate("startedAt")));
                    } else {
                        pos.printTextNewLine("开单时间:" + MyUtils.dateFormat(new Date()));
                    }

                    pos.printLine(1);
                    pos.printText("结账时间:" + getNowDate());
                    pos.printLine(1);
                    pos.printTwoColumn("收银员:" + sharedHelper.read("cashierName"), "服务人员:" + sharedHelper.read("cashierName"));
                    pos.printLine(1);
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLine(1);
                    pos.printText("品名");
                    pos.printLocation(20, 1);
                    pos.printText("数量");
                    pos.printLocation(90, 1);
                    pos.printWordSpace(1);
                    pos.printText("  ");
                    pos.printWordSpace(2);
                    pos.printText("金额");
                    pos.printLine(1);

                    pos.printTextNewLine("------------------------------------------------");
                    for (int i = 0; i < orders.size(); i++) {
                        HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
                        ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                        pos.printLine(1);
                        pos.printTextNewLine(productBean.getName());
                        pos.printLine(1);
                        pos.printText("  ");
                        pos.printLocation(20, 1);
                        pos.printText(ObjectUtil.getDouble(format, "number") + "");
                        pos.printLocation(90, 1);
                        pos.printWordSpace(1);
                        pos.printText("  ");
                        pos.printWordSpace(2);
                        pos.printText(MyUtils.formatDouble(ObjectUtil.getDouble(format, "number") * productBean.getPrice()) + "");
                        pos.printLine(1);
                    }
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLocation(0);
                    pos.printLine(1);
                    pos.printTwoColumn("原价合计 :", MyUtils.formatDouble(totalMoney) + "");
                    pos.printLine(1);
                    if (MyUtils.formatDouble(totalMoney - actualMoney) > 0) {
                        pos.printTwoColumn("优惠金额 :", MyUtils.formatDouble(totalMoney - actualMoney) + "");
                    }
                    pos.printLine(1);
                    pos.printTwoColumn("实付金额 :", MyUtils.formatDouble(actualMoney) + "");
                    pos.printLine(1);
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLine(1);
                    Map<String, Double> secrowMap = PayInfoUtil.managerEscrowByRest(actualMoney, escrow, orderDetail.getAvObject());
                    for (String key : secrowMap.keySet()) {
                        pos.printTwoColumn(key + " :", secrowMap.get(key) + "");
                        pos.printLine(1);
                    }

                    pos.printLine(2);
                    if (jsonReduce != null && jsonReduce.length() > 0) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        Iterator iterator = jsonReduce.keys();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            Double value = jsonReduce.getDouble(key);
                            pos.printTwoColumn(key + " :", value + "");
                            pos.printLine(1);
                        }
                        pos.printLine(2);
                    }
                    if (orderDetail.getChooseReduce() && orderDetail.getUseExchangeList().size() > 0) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        pos.printLocation(1);
                        pos.printText("我的牛肉抵扣详情");
                        pos.printLine();
                        pos.printLocation(0);
                        pos.printFourColumn("品名", "数量", "  抵扣重量", "抵扣金额");
                        pos.printLine(1);
                        Double actualReduce = 0.0;
                        for (int i = 0; i < orderDetail.getUseExchangeList().size(); i++) {
                            Object o = orderDetail.getUseExchangeList().get(i);
                            HashMap<String, Object> format = ObjectUtil.format(o);
                            pos.printTextNewLine(ObjectUtil.getString(format, "name"));
                            pos.printLine(1);
                            Double reduce = MyUtils.formatDouble(ObjectUtil.getDouble(format, "reduceMoeny"));
                            actualReduce += reduce;
                            pos.printFourColumn("    ", ObjectUtil.getDouble(format, "number") + "份", "   " + ObjectUtil.getDouble(format, "meatWeight") + "kg", MyUtils.formatDouble(reduce) + "");
                            pos.printLine(1);
                        }
                        pos.printFourColumn("总计", "    ", "    " + orderDetail.getMyReduceWeight() + "kg", MyUtils.formatDouble(actualReduce) + "");
                        pos.printLine(2);
                    }
                    if (orderDetail.getSvipMaxExchangeList().size() > 0) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        pos.printLocation(1);
                        pos.printText("超牛牛肉充足抵扣详情");
                        pos.printLine();
                        pos.printLocation(0);
                        pos.printFourColumn("品名", "数量", "  抵扣重量", "抵扣金额");
                        pos.printLine(1);
                        for (int i = 0; i < orderDetail.getSvipMaxExchangeList().size(); i++) {
                            Object o = orderDetail.getSvipMaxExchangeList().get(i);
                            HashMap<String, Object> format = ObjectUtil.format(o);
                            pos.printTextNewLine(ObjectUtil.getString(format, "name"));
                            pos.printLine(1);
                            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                            Double reduce = MyUtils.formatDouble((productBean.getPrice() - productBean.getRemainMoney()) * ObjectUtil.getDouble(format, "number"));
                            pos.printFourColumn("    ", ObjectUtil.getDouble(format, "number") + "份", "   " + ObjectUtil.getDouble(format, "meatWeight") + "kg", reduce + "");
                            pos.printLine(1);
                        }
                        pos.printFourColumn("总计", "    ", "    " + orderDetail.getMaxReduceWeight() + "kg", orderDetail.getMaxReduceMoney() + "");
                        pos.printLine(2);
                    }
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLocation(1);
                    pos.printLine(1);
                    pos.printText("地址:" + CONST.ADDRESS);
                    pos.printLine(2);
                    pos.printText("电话:" + CONST.TEL);
                    pos.printLine(2);
                    pos.printText("祝您生活越来越牛!");
                    pos.printLine(4);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();

                    String url1 = sharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                    Pos pos1;
                    pos1 = new Pos(url1, 9100, "GBK");
                    pos1.initPos();
                    pos1.printLocation(1);
                    Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
                    pos1.printBitmap(bitmap1);
                    pos1.bold(true);
                    pos1.printLine(1);
                    pos1.printText("商户联");
                    pos1.printLine(1);
                    pos1.bold(false);
                    pos1.printLine(1);
                    pos1.printLocation(0);
                    pos1.printText("桌号:" + finalTableNumber);
                    pos1.printLine(1);
                    pos1.printText("人数:" + tableAVObject.getInt("customer"));
                    pos1.printLine(1);
                    if (tableAVObject.getDate("startedAt") != null) {
                        pos1.printTextNewLine("开单时间:" + MyUtils.dateFormat(tableAVObject.getDate("startedAt")));
                    } else {
                        pos1.printTextNewLine("开单时间:" + MyUtils.dateFormat(new Date()));
                    }
                    pos1.printLine(1);
                    pos1.printText("结账时间:" + getNowDate());
                    pos1.printLine(1);
                    pos1.printTwoColumn("收银员:" + sharedHelper.read("cashierName"), "服务人员:" + sharedHelper.read("cashierName"));
                    pos1.printLine(1);
                    pos1.printTextNewLine("------------------------------------------------");
                    pos1.printLine(1);
                    pos1.printText("品名");
                    pos1.printLocation(20, 1);
                    pos1.printText("数量");
                    pos1.printLocation(90, 1);
                    pos1.printWordSpace(1);
                    pos1.printText("  ");
                    pos1.printWordSpace(2);
                    pos1.printText("金额");
                    pos1.printLine(1);

                    pos1.printTextNewLine("------------------------------------------------");
                    for (int i = 0; i < orders.size(); i++) {
                        HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
                        ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                        pos1.printLine(1);
                        pos1.printTextNewLine(productBean.getName());
                        pos1.printLine(1);
                        pos1.printText("  ");
                        pos1.printLocation(20, 1);
                        pos1.printText(ObjectUtil.getDouble(format, "number") + "");
                        pos1.printLocation(90, 1);
                        pos1.printWordSpace(1);
                        pos1.printText("  ");
                        pos1.printWordSpace(2);
                        pos1.printText(MyUtils.formatDouble(ObjectUtil.getDouble(format, "number") * productBean.getPrice()) + "");
                        pos1.printLine(1);
                    }
                    pos1.printTextNewLine("------------------------------------------------");
                    pos1.printLocation(0);
                    pos1.printLine(1);
                    pos1.printTwoColumn("原价合计 :", MyUtils.formatDouble(totalMoney) + "");
                    pos1.printLine(1);
                    if (MyUtils.formatDouble(totalMoney - actualMoney) > 0) {
                        pos1.printTwoColumn("优惠金额 :", MyUtils.formatDouble(totalMoney - actualMoney) + "");
                    }
                    pos1.printLine(1);
                    pos1.printTwoColumn("实付金额 :", MyUtils.formatDouble(actualMoney) + "");
                    pos1.printLine(1);
                    pos1.printTextNewLine("------------------------------------------------");
                    pos1.printLine(1);
                    Map<String, Double> secrowMap1 = PayInfoUtil.managerEscrowByRest(actualMoney, escrow, orderDetail.getAvObject());
                    for (String key : secrowMap1.keySet()) {
                        pos1.printTwoColumn(key + " :", secrowMap1.get(key) + "");
                        pos1.printLine(1);
                    }

                    pos1.printLine(2);
                    if (jsonReduce != null && jsonReduce.length() > 0) {
                        pos1.printTextNewLine("------------------------------------------------");
                        pos1.printLine(1);
                        Iterator iterator = jsonReduce.keys();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            Double value = jsonReduce.getDouble(key);
                            pos1.printTwoColumn(key + " :", value + "");
                            pos1.printLine(1);
                        }
                        pos1.printLine(2);
                    }
                    if (orderDetail.getChooseReduce() && orderDetail.getUseExchangeList().size() > 0) {
                        pos1.printTextNewLine("------------------------------------------------");
                        pos1.printLine(1);
                        pos1.printLocation(1);
                        pos1.printText("我的牛肉抵扣详情");
                        pos1.printLine();
                        pos1.printLocation(0);
                        pos1.printFourColumn("品名", "数量", "  抵扣重量", "抵扣金额");
                        pos1.printLine(1);
                        Double actualReduce = 0.0;
                        for (int i = 0; i < orderDetail.getUseExchangeList().size(); i++) {
                            Object o = orderDetail.getUseExchangeList().get(i);
                            HashMap<String, Object> format = ObjectUtil.format(o);
                            pos1.printTextNewLine(ObjectUtil.getString(format, "name"));
                            pos1.printLine(1);
                            Double reduce = MyUtils.formatDouble(ObjectUtil.getDouble(format, "reduceMoeny"));
                            actualReduce += reduce;
                            pos1.printFourColumn("    ", ObjectUtil.getDouble(format, "number") + "份", "   " + ObjectUtil.getDouble(format, "meatWeight") + "kg", MyUtils.formatDouble(reduce) + "");
                            pos1.printLine(1);
                        }
                        pos1.printFourColumn("总计", "    ", "    " + orderDetail.getMyReduceWeight() + "kg", MyUtils.formatDouble(actualReduce) + "");
                        pos1.printLine(2);
                    }
                    if (orderDetail.getSvipMaxExchangeList().size() > 0) {
                        pos1.printTextNewLine("------------------------------------------------");
                        pos1.printLine(1);
                        pos1.printLocation(1);
                        pos1.printText("超牛牛肉充足抵扣详情");
                        pos1.printLine();
                        pos1.printLocation(0);
                        pos1.printFourColumn("品名", "数量", "  抵扣重量", "抵扣金额");
                        pos1.printLine(1);
                        Double maxReduce = 0.0;
                        for (int i = 0; i < orderDetail.getSvipMaxExchangeList().size(); i++) {
                            Object o = orderDetail.getSvipMaxExchangeList().get(i);
                            HashMap<String, Object> format = ObjectUtil.format(o);
                            pos1.printTextNewLine(ObjectUtil.getString(format, "name"));
                            pos1.printLine(1);
                            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                            Double reduce = MyUtils.formatDouble(ObjectUtil.getDouble(format, "reduceMoeny"));
                            maxReduce += reduce;
                            pos1.printFourColumn("    ", ObjectUtil.getDouble(format, "number") + "份", "   " + ObjectUtil.getDouble(format, "meatWeight") + "kg", MyUtils.formatDouble(reduce) + "");
                            pos1.printLine(1);
                        }
                        pos1.printFourColumn("总计", "    ", "    " + orderDetail.getMaxReduceWeight() + "kg", MyUtils.formatDouble(maxReduce) + "");
                        pos1.printLine(2);
                    }
                    pos1.printTextNewLine("------------------------------------------------");
                    pos1.printLine(3);
                    pos1.printLocation(1);
                    pos1.printLine(1);
                    pos1.printText("地址:" + CONST.ADDRESS);
                    pos1.printLine(2);
                    pos1.printText("电话:" + CONST.TEL);
                    pos1.printLine(2);
                    pos1.printText("祝您生活越来越牛!");
                    pos1.printLine(4);
                    pos1.feedAndCut();
                    pos1.closeIOAndSocket();
                    EventBus.getDefault().post(new PrintEvent(0, "打印机连接成功"));
                } catch (Exception e) {
                    Logger.d(e.getMessage());
                    EventBus.getDefault().post(new PrintEvent(-1, "打印机连接失败" + e.getMessage(), orderDetail, jsonReduce, escrow, finalTableNumber));
                }

            }
        }.start();
    }

    public static void printRechargeSvip(final String tel,
                                         final int escrow,
                                         final String chargeStyle,
                                         final Double payMoeny,
                                         final Double reduceMoney) {
        new Thread() {
            public void run() {
                try {
                    mContext = MyApplication.getContextObject();
                    String url = SharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                    Pos pos;
                    final DecimalFormat df = new DecimalFormat("######0.00");
                    pos = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                    pos.initPos();
                    pos.printLocation(1);
                    Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo_papa);
                    pos.printBitmap(bitmap);
                    pos.printLine(5);
                    pos.printLocation(1);
                    pos.printText("(客户联)");
                    pos.printLine(1);
                    pos.printText("超牛会员充值");
                    pos.printLine(1);
                    pos.printLocation(0);
                    pos.printLine(1);
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printLine(1);
                    String rechargeContent = "";
                    switch (chargeStyle) {
                        case CONST.SVIPSTYLE.DATE_1_MONTH:
                            rechargeContent = "超牛会员体验(1个月)";
                            break;
                        case CONST.SVIPSTYLE.DATE_11_MONTH:
                            rechargeContent = "超牛会员续费(11个月)";
                            break;

                        case CONST.SVIPSTYLE.DATE_12_MONTH:
                            rechargeContent = "超牛会员年费(12个月)";
                            break;
                    }
                    pos.printTwoColumn("充值类型 :", rechargeContent);
                    pos.printLine(1);
                    pos.printTwoColumn("充值手机号 :", tel.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2") + "元");
                    pos.printLine(1);
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printLine(1);
                    pos.printTwoColumn("订单原价 :", payMoeny + "");
                    pos.printLine(1);
                    if (reduceMoney > 0) {
                        pos.printLine(1);
                        pos.printTwoColumn("预购优惠券减200:", "-" + reduceMoney + "");
                        pos.printLine(1);
                    }
                    pos.printLine(1);
                    pos.printTwoColumn("实付金额 :", MyUtils.formatDouble(payMoeny - reduceMoney) + "");
                    pos.printLine(1);
                    String payContent = "";
                    switch (escrow) {
                        case 11:
                            payContent = "白条支付";
                            break;
                        case 3:
                            payContent = "支付宝支付";
                            break;
                        case 4:
                            payContent = "微信支付";
                            break;
                        case 5:
                            payContent = "银联支付";
                            break;
                        case 6:
                            payContent = "现金支付";
                            break;
                    }
                    pos.printTwoColumn("支付方式 :", payContent);
                    pos.printLine(1);
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printLocation(1);
                    pos.printLine(1);
                    pos.printText("地址：上海市徐汇区龙华路2520号");
                    pos.printLine(2);
                    pos.printText("电话：021-54566808");
                    pos.printLine(2);
                    pos.printText("祝您生活越来越牛!");
                    pos.printLine(2);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();
                } catch (IOException e) {
                    EventBus.getDefault().post(new ProgressEvent(-1, "打印充值会员小票失败"));
                    e.printStackTrace();
                    Logger.d("打印失败");
                }
            }

        }.start();

    }

    /**
     * 打印总账单
     */
    public static void printTotalBill(final Context context,
                                      final HashMap<String, Object> ordersDetail,
                                      final Date orderDate) {
        new Thread() {
            @Override
            public void run() {
                try {
                    mContext = context;
                    SharedHelper sharedHelper = new SharedHelper(context);
                    String url = sharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                    Pos pos;
                    pos = new Pos(url, 9100, "GBK");
                    pos.initPos();
                    pos.printLocation(1);
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
                    pos.printBitmap(bitmap);
                    pos.bold(true);
                    pos.printLine(1);
                    pos.printText("账单汇总");
                    pos.printLine(1);
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLine(1);
                    pos.printTwoColumn("总营业金额", ordersDetail.get("totalMoney").toString());
                    pos.printTwoColumn("线上收款金额", ordersDetail.get("onlineMoney").toString());
                    pos.printTwoColumn("线下收款金额", ordersDetail.get("offlineMoney").toString());
                    pos.printTwoColumn("会员数", ordersDetail.get("member").toString());
                    pos.printTwoColumn("非会员数", ordersDetail.get("noMember").toString());
                    pos.printTwoColumn("餐饮单数", ordersDetail.get("retailNumber").toString());
                    pos.printTwoColumn("零售单数", ordersDetail.get("restaurarntNumber").toString());
                    pos.printTwoColumn("抵扣牛肉重量", ordersDetail.get("reduceWeight").toString());
                    pos.printTextNewLine("------------------------------------------------");
                    HashMap<String, Double> weights = (HashMap<String, Double>) ordersDetail.get("weights");
                    pos.printLocation(0);
                    List<Map.Entry<String, Double>> numbersList = (List<Map.Entry<String, Double>>) ordersDetail.get("numbers");
                    for (Map.Entry<String, Double> mapping : numbersList) {
                        pos.printTextNewLine(mapping.getKey());
                        pos.printLine(1);
                        pos.printTwoColumn("", mapping.getValue() + "份");
                        pos.printLine(1);
                        if (weights.containsKey(mapping.getKey())) {
                            pos.printTwoColumn("", weights.get(mapping.getValue()) + "kg");
                            pos.printLine(1);
                        }
                    }
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLine(1);
                    HashMap<String, Integer> offlineCoupon = (HashMap<String, Integer>) ordersDetail.get("offlineCoupon");
                    HashMap<String, Integer> onlineCoupon = (HashMap<String, Integer>) ordersDetail.get("onlineCoupon");
                    for (String key : offlineCoupon.keySet()) {
                        pos.printTwoColumn(key, offlineCoupon.get(key) + "张");
                        pos.printLine(1);
                    }
                    pos.printTextNewLine("------------------------------------------------");
                    for (String key : onlineCoupon.keySet()) {
                        pos.printTwoColumn(key, onlineCoupon.get(key) + "张");
                        pos.printLine(1);
                    }
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLine(1);
                    pos.printTextNewLine("订单时间:" + MyUtils.dateFormatShort(orderDate) + " 00:00:00 ~ " + MyUtils.dateFormatShort(orderDate) + " 24:00:00");
                    pos.printLine(1);
                    pos.printTextNewLine("打印时间:" + MyUtils.dateFormat(new Date()));
                    pos.printLine(1);
                    pos.printTextNewLine("打印人:" + sharedHelper.read("cashierName"));
                    pos.printLine(4);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 打开钱箱
     */
    public static void openMoneyBox(final Context context) {
        new Thread() {
            @Override
            public void run() {
                mContext = context;
                String url = SharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                Pos pos;
                try {
                    pos = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                    pos.initPos();
                    pos.openCashbox();
                    pos.closeIOAndSocket();
                } catch (IOException e) {
                    Logger.d(e.getMessage());
                    ToastUtil.showShort(context, "连接失败");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 打印零售账单
     */
    public static void printRetailBill(final Context context,
                                       final OrderDetail orderDetail,
                                       final JSONObject jsonReduce,
                                       final int escrow,
                                       UserBean userBean) {
        new Thread() {
            @Override
            public void run() {
                try {

                    Double totalMoney = orderDetail.getTotalMoney();
                    Double actualMoney = orderDetail.getActualMoney();
                    mContext = context;
                    SharedHelper sharedHelper = new SharedHelper(context);
                    String url = sharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                    Pos pos;
                    pos = new Pos(url, 9100, "GBK");
                    pos.initPos();
                    pos.printLocation(1);
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
                    pos.printBitmap(bitmap);
                    pos.bold(true);
                    pos.printLine(1);
                    pos.printText("用户联");
                    pos.printLine(1);
                    pos.bold(false);
                    pos.printLine(1);
                    pos.printLocation(0);
                    pos.printTextNewLine("下单时间:" + MyUtils.dateFormat(new Date()));
                    pos.printLine(1);
                    pos.printText("结账时间:" + getNowDate());
                    pos.printLine(1);
                    pos.printTwoColumn("收银员:" + sharedHelper.read("cashierName"), "服务人员:" + sharedHelper.read("cashierName"));
                    pos.printLine(1);
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLine(1);
                    pos.printText("品名");
                    pos.printLocation(20, 1);
                    pos.printText("数量");
                    pos.printLocation(90, 1);
                    pos.printWordSpace(1);
                    pos.printText("重量");
                    pos.printWordSpace(2);
                    pos.printText("金额");
                    pos.printLine(1);

                    pos.printTextNewLine("------------------------------------------------");
                    List<Object> orders = orderDetail.getOrders();
                    for (int i = 0; i < orders.size(); i++) {
                        HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
                        ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                        pos.printLine(1);
                        pos.printTextNewLine(productBean.getName());
                        pos.printLine(1);
                        pos.printText("  ");
                        pos.printLocation(20, 1);
                        pos.printText(ObjectUtil.getDouble(format, "number") + "");
                        pos.printLocation(90, 1);
                        pos.printWordSpace(1);
                        if (ObjectUtil.getDouble(format, "weight") > 20) {
                            pos.printText(ObjectUtil.getDouble(format, "weight") + "ml");
                        } else {
                            pos.printText(ObjectUtil.getDouble(format, "weight") + "kg");
                        }

                        pos.printWordSpace(2);
                        pos.printText(MyUtils.formatDouble(ObjectUtil.getDouble(format, "price")) + "");
                        pos.printLine(1);
                    }
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLocation(0);
                    pos.printLine(1);
                    pos.printTwoColumn("原价合计 :", MyUtils.formatDouble(totalMoney) + "");
                    pos.printLine(1);
                    if (MyUtils.formatDouble(totalMoney - actualMoney) > 0) {
                        pos.printTwoColumn("优惠金额 :", MyUtils.formatDouble(totalMoney - actualMoney) + "");
                    }
                    pos.printLine(1);
                    pos.printTwoColumn("实付金额 :", MyUtils.formatDouble(actualMoney) + "");
                    pos.printLine(1);
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLine(1);
                    Map<String, Double> secrowMap = ProductUtil.managerEscrow(actualMoney, escrow, orderDetail.getUserBean());
                    for (String key : secrowMap.keySet()) {
                        pos.printTwoColumn(key + " :", secrowMap.get(key) + "");
                        pos.printLine(1);
                    }

                    pos.printLine(2);
                    if (jsonReduce != null && jsonReduce.length() > 0) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        Iterator iterator = jsonReduce.keys();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            Double value = jsonReduce.getDouble(key);
                            pos.printTwoColumn(key + " :", value + "");
                            pos.printLine(1);
                        }
                        pos.printLine(2);
                    }
                    if (orderDetail.getChooseReduce() && orderDetail.getUseExchangeList().size() > 0) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        pos.printLocation(1);
                        pos.printText("我的牛肉抵扣详情");
                        pos.printLine();
                        pos.printLocation(0);
                        pos.printFourColumn("品名", "重量", "  抵扣重量", "抵扣金额");
                        pos.printLine(1);
                        for (int i = 0; i < orderDetail.getUseExchangeList().size(); i++) {
                            Object o = orderDetail.getUseExchangeList().get(i);
                            HashMap<String, Object> format = ObjectUtil.format(o);
                            pos.printTextNewLine(ObjectUtil.getString(format, "name"));
                            pos.printLine(1);
                            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                            Double reduce = MyUtils.formatDouble((productBean.getPrice() - productBean.getRemainMoney()) * ObjectUtil.getDouble(format, "number"));
                            pos.printFourColumn("    ", MyUtils.formatDouble(ObjectUtil.getDouble(format, "weight")) + "", "   " + ObjectUtil.getDouble(format, "meatWeight") + "kg", MyUtils.formatDouble(ObjectUtil.getDouble(format, "price")) + "");
                            pos.printLine(1);
                        }
                        pos.printFourColumn("总计", "    ", "    " + orderDetail.getMyReduceWeight() + "kg", orderDetail.getMaxReduceMoney() + "");
                        pos.printLine(2);
                    }
                    if (orderDetail.getSvipMaxExchangeList().size() > 0) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        pos.printLocation(1);
                        pos.printText("超牛牛肉充足抵扣详情");
                        pos.printLine();
                        pos.printLocation(0);
                        pos.printFourColumn("品名", "重量", "  抵扣重量", "抵扣金额");
                        pos.printLine(1);
                        for (int i = 0; i < orderDetail.getSvipMaxExchangeList().size(); i++) {
                            Object o = orderDetail.getSvipMaxExchangeList().get(i);
                            HashMap<String, Object> format = ObjectUtil.format(o);
                            pos.printTextNewLine(ObjectUtil.getString(format, "name"));
                            pos.printLine(1);
                            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                            Double reduce = MyUtils.formatDouble((productBean.getPrice() - productBean.getRemainMoney()) * ObjectUtil.getDouble(format, "number"));
                            pos.printFourColumn("    ", MyUtils.formatDouble(ObjectUtil.getDouble(format, "weight")) + "", "   " + ObjectUtil.getDouble(format, "meatWeight") + "kg", MyUtils.formatDouble(ObjectUtil.getDouble(format, "price")) + "");
                            pos.printLine(1);
                        }
                        pos.printFourColumn("总计", "    ", "    " + orderDetail.getMaxReduceWeight() + "kg", orderDetail.getMaxReduceMoney() + "");
                        pos.printLine(2);
                    }
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLocation(1);
                    pos.printLine(1);
                    pos.printText("地址：上海市徐汇区龙华路2520号");
                    pos.printLine(2);
                    pos.printText("电话：021-54566808");
                    pos.printLine(2);
                    pos.printText("祝您生活越来越牛!");
                    pos.printLine(4);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();

                    String url1 = sharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                    Pos pos1;
                    pos1 = new Pos(url1, 9100, "GBK");
                    pos1.initPos();
                    pos1.printLocation(1);
                    Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
                    pos1.printBitmap(bitmap1);
                    pos1.bold(true);
                    pos1.printLine(1);
                    pos1.printText("商户联");
                    pos1.printLine(1);
                    pos1.bold(false);
                    pos1.printLine(1);
                    pos1.printLocation(0);
                    pos1.printTextNewLine("开单时间:" + MyUtils.dateFormat(new Date()));
                    pos1.printLine(1);
                    pos1.printText("结账时间:" + getNowDate());
                    pos1.printLine(1);
                    pos1.printTwoColumn("收银员:" + sharedHelper.read("cashierName"), "服务人员:" + sharedHelper.read("cashierName"));
                    pos1.printLine(1);
                    pos1.printTextNewLine("------------------------------------------------");
                    pos1.printLine(1);
                    pos1.printText("品名");
                    pos1.printLocation(20, 1);
                    pos1.printText("数量");
                    pos1.printLocation(90, 1);
                    pos1.printWordSpace(1);
                    pos1.printText("  ");
                    pos1.printWordSpace(2);
                    pos1.printText("金额");
                    pos1.printLine(1);

                    pos1.printTextNewLine("------------------------------------------------");
                    for (int i = 0; i < orders.size(); i++) {
                        HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
                        ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                        pos1.printLine(1);
                        pos1.printTextNewLine(productBean.getName());
                        pos1.printLine(1);
                        pos1.printText("  ");
                        pos1.printLocation(20, 1);
                        pos1.printText(ObjectUtil.getDouble(format, "number") + "");
                        pos1.printLocation(90, 1);
                        pos1.printWordSpace(1);
                        if (ObjectUtil.getDouble(format, "weight") > 20) {
                            pos1.printText(ObjectUtil.getDouble(format, "weight") + "ml");
                        } else {
                            pos1.printText(ObjectUtil.getDouble(format, "weight") + "kg");
                        }
                        pos1.printWordSpace(2);
                        pos1.printText(MyUtils.formatDouble(ObjectUtil.getDouble(format, "price")) + "");
                        pos1.printLine(1);
                    }
                    pos1.printTextNewLine("------------------------------------------------");
                    pos1.printLocation(0);
                    pos1.printLine(1);
                    pos1.printTwoColumn("原价合计 :", MyUtils.formatDouble(totalMoney) + "");
                    pos1.printLine(1);
                    if (MyUtils.formatDouble(totalMoney - actualMoney) > 0) {
                        pos1.printTwoColumn("优惠金额 :", MyUtils.formatDouble(totalMoney - actualMoney) + "");
                    }
                    pos1.printLine(1);
                    pos1.printTwoColumn("实付金额 :", MyUtils.formatDouble(actualMoney) + "");
                    pos1.printLine(1);
                    pos1.printTextNewLine("------------------------------------------------");
                    pos1.printLine(1);
                    Map<String, Double> secrowMap1 = ProductUtil.managerEscrow(actualMoney, escrow, orderDetail.getUserBean());
                    for (String key : secrowMap1.keySet()) {
                        pos1.printTwoColumn(key + " :", secrowMap1.get(key) + "");
                        pos1.printLine(1);
                    }

                    pos1.printLine(2);
                    if (jsonReduce != null && jsonReduce.length() > 0) {
                        pos1.printTextNewLine("------------------------------------------------");
                        pos1.printLine(1);
                        Iterator iterator = jsonReduce.keys();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            Double value = jsonReduce.getDouble(key);
                            pos1.printTwoColumn(key + " :", value + "");
                            pos1.printLine(1);
                        }
                        pos1.printLine(2);
                    }
                    if (orderDetail.getChooseReduce() && orderDetail.getUseExchangeList().size() > 0) {
                        pos1.printTextNewLine("------------------------------------------------");
                        pos1.printLine(1);
                        pos1.printLocation(1);
                        pos1.printText("我的牛肉抵扣详情");
                        pos1.printLine();
                        pos1.printLocation(0);
                        pos1.printFourColumn("品名", "重量", "  抵扣重量", "抵扣金额");
                        pos1.printLine(1);
                        for (int i = 0; i < orderDetail.getUseExchangeList().size(); i++) {
                            Object o = orderDetail.getUseExchangeList().get(i);
                            HashMap<String, Object> format = ObjectUtil.format(o);
                            pos1.printTextNewLine(ObjectUtil.getString(format, "name"));
                            pos1.printLine(1);
                            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                            pos1.printFourColumn("    ", MyUtils.formatDouble(ObjectUtil.getDouble(format, "weight")) + "", "   " + ObjectUtil.getDouble(format, "meatWeight") + "kg", MyUtils.formatDouble(ObjectUtil.getDouble(format, "price")) + "");
                            pos1.printLine(1);
                        }
                        pos1.printFourColumn("总计", "    ", "    " + orderDetail.getMyReduceWeight(), orderDetail.getMaxReduceMoney() + "");
                        pos1.printLine(2);
                    }
                    if (orderDetail.getSvipMaxExchangeList().size() > 0) {
                        pos1.printTextNewLine("------------------------------------------------");
                        pos1.printLine(1);
                        pos1.printLocation(1);
                        pos1.printText("超牛牛肉充足抵扣详情");
                        pos1.printLine();
                        pos1.printLocation(0);
                        pos1.printFourColumn("品名", "重量", "  抵扣重量", "抵扣金额");
                        pos1.printLine(1);
                        for (int i = 0; i < orderDetail.getSvipMaxExchangeList().size(); i++) {
                            Object o = orderDetail.getSvipMaxExchangeList().get(i);
                            HashMap<String, Object> format = ObjectUtil.format(o);
                            pos1.printTextNewLine(ObjectUtil.getString(format, "name"));
                            pos1.printLine(1);
                            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                            pos1.printFourColumn("    ", MyUtils.formatDouble(ObjectUtil.getDouble(format, "weight")) + "", "   " + ObjectUtil.getDouble(format, "meatWeight") + "kg", MyUtils.formatDouble(ObjectUtil.getDouble(format, "price")) + "");
                            pos1.printLine(1);
                        }
                        pos1.printFourColumn("总计", "    ", "    " + orderDetail.getMaxReduceWeight() + "kg", orderDetail.getMaxReduceMoney() + "");
                        pos1.printLine(2);
                    }
                    pos1.printTextNewLine("------------------------------------------------");
                    pos1.printLine(3);
                    pos1.printLocation(1);
                    pos1.printLine(1);
                    pos1.printText("地址：上海市徐汇区龙华路2520号");
                    pos1.printLine(2);
                    pos1.printText("电话：021-54566808");
                    pos1.printLine(2);
                    pos1.printText("祝您生活越来越牛!");
                    pos1.printLine(4);
                    pos1.feedAndCut();
                    pos1.closeIOAndSocket();
                    EventBus.getDefault().post(new PrintEvent(0, "打印机连接成功"));
                } catch (Exception e) {
                    Logger.d(e.getMessage());
                    EventBus.getDefault().post(new PrintEvent(1, "打印机连接失败" + e.getMessage()));
                }

            }
        }.start();
    }

    /**
     * 超牛充值账单
     */
    public static void printSvipBill(final String svipStyle,
                                     final Double money,
                                     final Double reduce,
                                     final Double finalMoney,
                                     final int escrow,
                                     final AVObject avObject) {
        new Thread() {
            @Override
            public void run() {
                try {
                    mContext = MyApplication.getContextObject();
                    String url = SharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                    Pos pos;
                    pos = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                    pos.initPos();
                    pos.printLocation(1);
                    pos.bold(true);
                    pos.printText("超牛充值");
                    pos.bold(false);
                    pos.printLine(1);
                    pos.printLocation(0);
                    pos.printTextNewLine("操作人:" + (avObject.getAVObject("cashier") != null ? avObject.getAVObject("cashier").getString("realName") : avObject.getAVObject("cashier").getString("nickName")));
                    pos.printTextNewLine("负责人:" + (avObject.getAVObject("market") != null ? avObject.getAVObject("market").getString("realName") : avObject.getAVObject("market").getString("nickName")));
                    pos.printTextNewLine("时间:" + MyUtils.dateFormat(avObject.getCreatedAt()));
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printLine(1);
                    pos.printLocation(0);
                    pos.printTwoColumn("充值类型:", svipStyle);
                    pos.printLine(1);
                    pos.printTwoColumn("充值金额:", money + "元");
                    pos.printLine(1);
                    if (reduce > 0) {
                        pos.printTwoColumn("优惠金额:", reduce + "元");
                        pos.printLine(1);
                    }
                    pos.printTwoColumn("实付金额:", finalMoney + "元");
                    pos.printLine(1);
                    String secrowContent = "";
                    if (escrow == 11) {
                        secrowContent = "白条支付";
                    } else if (escrow == 3) {
                        secrowContent = "支付宝支付";
                    } else if (escrow == 4) {
                        secrowContent = "微信支付";
                    } else if (escrow == 5) {
                        secrowContent = "银行卡支付";
                    } else if (escrow == 6) {
                        secrowContent = "现金支付";
                    }
                    pos.printTwoColumn("支付方式:", secrowContent);
                    pos.printLine(1);
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printLocation(1);
                    pos.printLine(1);
                    pos.printText("地址:" + CONST.ADDRESS);
                    pos.printLine(2);
                    pos.printText("电话:" + CONST.TEL);
                    pos.printLine(2);
                    pos.printText("祝您生活越来越牛!");
                    pos.printLine(4);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();

                    Pos pos1;
                    pos1 = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                    pos1.initPos();
                    pos1.printLocation(1);
                    pos1.bold(true);
                    pos1.printText("超牛充值");
                    pos1.bold(false);
                    pos1.printLine(1);
                    pos1.printTextNewLine("----------------------------------------------");
                    pos1.printLine(1);
                    pos1.printLocation(0);
                    pos1.printTwoColumn("充值类型:", svipStyle);
                    pos1.printLine(1);
                    pos1.printTwoColumn("充值金额:", money + "元");
                    pos1.printLine(1);
                    if (reduce > 0) {
                        pos1.printTwoColumn("优惠金额:", reduce + "元");
                        pos1.printLine(1);
                    }
                    pos1.printTwoColumn("实付金额:", finalMoney + "元");
                    pos1.printLine(1);
                    secrowContent = "";
                    if (escrow == 11) {
                        secrowContent = "白条支付";
                    } else if (escrow == 3) {
                        secrowContent = "支付宝支付";
                    } else if (escrow == 4) {
                        secrowContent = "微信支付";
                    } else if (escrow == 5) {
                        secrowContent = "银行卡支付";
                    } else if (escrow == 6) {
                        secrowContent = "现金支付";
                    }
                    pos1.printTwoColumn("支付方式:", secrowContent);
                    pos1.printLine(1);
                    pos1.printTextNewLine("----------------------------------------------");
                    pos1.printLocation(1);
                    pos1.printLine(1);
                    pos1.printText("地址:" + CONST.ADDRESS);
                    pos1.printLine(1);
                    pos1.printText("电话:" + CONST.TEL);
                    pos1.printLine(1);
                    pos1.printText("祝您生活越来越牛!");
                    pos1.printLine(4);
                    pos1.feedAndCut();
                    pos1.closeIOAndSocket();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 补打订单
     */
    public static void reprintBill(final Context context,
                                   final AVObject avObject) {
        new Thread() {
            @Override
            public void run() {
                try {
                    List<Object> orders = avObject.getList("commodityDetail");
                    JSONObject jsonReduce = avObject.getJSONObject("reduceDetail");
                    List<Object> maxMeatDeduct = (List<Object>) avObject.getList("maxMeatDeduct");
                    List<Object> realMeatDeduct = (List<Object>) avObject.getList("realMeatDeduct");
                    mContext = context;
                    SharedHelper sharedHelper = new SharedHelper(context);
                    String url = sharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                    Pos pos;
                    pos = new Pos(url, 9100, "GBK");
                    pos.initPos();
                    pos.printLocation(1);
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
                    pos.printBitmap(bitmap);
                    pos.bold(true);
                    pos.printLine(1);
                    pos.printText("补打联");
                    pos.printLine(1);
                    pos.bold(false);
                    pos.printLine(1);
                    pos.printLocation(0);
                    pos.printText("桌号:" + avObject.getString("tableNumber"));
                    pos.printLine(1);
                    pos.printText("人数:" + avObject.getInt("customer"));
                    pos.printLine(1);
                    if (avObject.getDate("startedAt") != null) {
                        pos.printTextNewLine("开单时间:" + MyUtils.dateFormat(avObject.getDate("startedAt")));
                    } else {
                        pos.printTextNewLine("开单时间:" + MyUtils.dateFormat(new Date()));
                    }
                    pos.printLine(1);
                    pos.printText("结账时间:" + getNowDate());
                    pos.printLine(1);
                    pos.printTwoColumn("收银员:" + sharedHelper.read("cashierName"), "服务人员:" + sharedHelper.read("cashierName"));
                    pos.printLine(1);
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLine(1);
                    pos.printText("品名");
                    pos.printLocation(20, 1);
                    pos.printText("数量");
                    pos.printLocation(90, 1);
                    pos.printWordSpace(1);
                    pos.printText("  ");
                    pos.printWordSpace(2);
                    pos.printText("金额");
                    pos.printLine(1);
                    pos.printTextNewLine("------------------------------------------------");
                    for (int i = 0; i < orders.size(); i++) {
                        HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
                        ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                        pos.printLine(1);
                        pos.printTextNewLine(productBean.getName());
                        pos.printLine(1);
                        pos.printText("  ");
                        pos.printLocation(20, 1);
                        pos.printText(ObjectUtil.getDouble(format, "number") + "");
                        pos.printLocation(90, 1);
                        pos.printWordSpace(1);
                        pos.printText("  ");
                        pos.printWordSpace(2);
                        pos.printText(MyUtils.formatDouble(ObjectUtil.getDouble(format, "price")) + "");
                        pos.printLine(1);
                    }
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLocation(0);
                    pos.printLine(1);
                    pos.printTwoColumn("原价合计 :", MyUtils.formatDouble(avObject.getDouble("paysum")) + "");
                    pos.printLine(1);
                    if (MyUtils.formatDouble(avObject.getDouble("reduce")) > 0) {
                        pos.printTwoColumn("优惠金额 :", MyUtils.formatDouble(avObject.getDouble("reduce")) + "");
                    }
                    pos.printLine(1);
                    pos.printTwoColumn("实付金额 :", MyUtils.formatDouble(avObject.getDouble("paysum") - avObject.getDouble("reduce")) + "");
                    pos.printLine(1);
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLine(2);
                    if (jsonReduce != null && jsonReduce.length() > 0) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        Iterator iterator = jsonReduce.keys();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            Double value = jsonReduce.getDouble(key);
                            pos.printTwoColumn(key + " :", value + "");
                            pos.printLine(1);
                        }
                        pos.printLine(2);
                        pos.printTextNewLine("------------------------------------------------");
                    }
                    Map<String, Double> escrowDetail = avObject.getMap("escrowDetail");
                    for (String key : escrowDetail.keySet()) {
                        pos.printTwoColumn(key + " :", escrowDetail.get(key) + "");
                        pos.printLine(1);
                    }
                    if (realMeatDeduct != null && realMeatDeduct.size() > 0) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        pos.printLocation(1);
                        pos.printText("我的牛肉抵扣详情");
                        pos.printLine();
                        pos.printLocation(0);
                        pos.printFourColumn("品名", "数量", "  抵扣重量", "抵扣金额");
                        pos.printLine(1);
                        double myReduceWeight = 0.0;
                        double myReduceMoney = 0.0;
                        for (int i = 0; i < realMeatDeduct.size(); i++) {
                            Object o = realMeatDeduct.get(i);
                            HashMap<String, Object> format = ObjectUtil.format(o);
                            pos.printTextNewLine(ObjectUtil.getString(format, "name"));
                            pos.printLine(1);
                            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                            Double reduce = MyUtils.formatDouble(ObjectUtil.getDouble(format, "reduceMoeny"));
                            myReduceWeight += ObjectUtil.getDouble(format, "meatWeight");
                            myReduceMoney += reduce;
                            pos.printFourColumn("    ", ObjectUtil.getDouble(format, "number") + "份", "   " + ObjectUtil.getDouble(format, "meatWeight") + "kg", reduce + "");
                            pos.printLine(1);
                        }
                        pos.printFourColumn("总计", "    ", "    " + MyUtils.formatDouble(myReduceWeight) + "kg", MyUtils.formatDouble(myReduceMoney) + "");
                        pos.printLine(2);
                    }
                    if (maxMeatDeduct != null && maxMeatDeduct.size() > 0) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        pos.printLocation(1);
                        pos.printText("超牛会员牛肉充足抵扣详情");
                        pos.printLine();
                        pos.printLocation(0);
                        pos.printFourColumn("品名", "数量", "  抵扣重量", "抵扣金额");
                        pos.printLine(1);
                        double myReduceWeight = 0.0;
                        double myReduceMoney = 0.0;
                        for (int i = 0; i < maxMeatDeduct.size(); i++) {
                            Object o = maxMeatDeduct.get(i);
                            HashMap<String, Object> format = ObjectUtil.format(o);
                            pos.printTextNewLine(ObjectUtil.getString(format, "name"));
                            pos.printLine(1);
                            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                            Double reduce = MyUtils.formatDouble(ObjectUtil.getDouble(format, "reduceMoeny"));
                            myReduceWeight += ObjectUtil.getDouble(format, "meatWeight");
                            myReduceMoney += reduce;
                            pos.printFourColumn("    ", ObjectUtil.getDouble(format, "number") + "份", "   " + ObjectUtil.getDouble(format, "meatWeight") + "kg", reduce + "");
                            pos.printLine(1);
                        }
                        pos.printFourColumn("总计", "    ", "    " + MyUtils.formatDouble(myReduceWeight) + "kg", MyUtils.formatDouble(myReduceMoney) + "");
                        pos.printLine(2);
                    }
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLocation(1);
                    pos.printLine(1);
                    pos.printText("地址:" + CONST.ADDRESS);
                    pos.printLine(2);
                    pos.printText("电话:" + CONST.TEL);
                    pos.printLine(2);
                    pos.printText("祝您生活越来越牛!");
                    pos.printLine(4);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }.start();
    }

    /**
     * 充值订单
     */
    public static void printRechargeStored(final Context context,
                                           final String username,
                                           final int rechargeMoney,
                                           final int escrow,
                                           final String cashierName,
                                           final String marketName) {
        new Thread() {
            @Override
            public void run() {
                try {
                    SharedHelper sharedHelper = new SharedHelper(context);
                    String url = SharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                    Pos pos;
                    pos = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP

                    //初始化打印机
                    pos.initPos();
                    pos.printLocation(1);
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
                    pos.printBitmap(bitmap);
                    pos.bold(true);
                    pos.printLargeText("BEEF X-用户联");
                    pos.printLine(1);
                    pos.bold(false);
                    pos.printLine(1);
                    pos.printLocation(0);
                    pos.printText("时间:" + MyUtils.dateFormat(new Date()));
                    pos.printLine(1);
                    pos.printText("服务员:" + cashierName);
                    pos.printLine(1);
                    pos.printText("销售:" + marketName);
                    pos.printLine(1);
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printLocation(0);
                    pos.printLine(1);
                    pos.printTwoColumn("电话号:", username.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
                    pos.printLine(1);
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printLocation(0);
                    pos.printLine(1);
                    pos.printTwoColumn("消费金充值:", rechargeMoney + "元");
                    pos.printLine(1);
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printLocation(0);
                    pos.printLine(1);
                    String payContent = "";
                    switch (escrow) {
                        case 3:
                            payContent = "支付宝支付";
                            break;
                        case 4:
                            payContent = "微信支付";
                            break;
                        case 5:
                            payContent = "银行卡支付";
                            break;
                        case 6:
                            payContent = "现金支付";
                            break;
                    }
                    pos.printTwoColumn(payContent + ":", rechargeMoney + "元");
                    pos.printLine(1);
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLocation(1);
                    pos.printLine(1);
                    pos.printText("地址:" + CONST.ADDRESS);
                    pos.printLine(2);
                    pos.printText("电话:" + CONST.TEL);
                    pos.printLine(2);
                    pos.printText("祝您生活越来越牛!");
                    pos.printLine(4);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();
                } catch (Exception e) {

                }


            }
        }.start();

    }

    public static void printPreOrder(final Context context, final AVObject tableAVObject, final Double originTotalMoneny, final Double actualTotalMoneny, final HashMap<String, Double> reduceMap, final List<Object> useExchangeList, final List<Object> canExchangeList) {
        new Thread() {
            @Override
            public void run() {
                mContext = context;
                SharedHelper sharedHelper = new SharedHelper(context);
                try {
                    String url = SharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                    Pos pos;
                    final DecimalFormat df = new DecimalFormat("######0.00");
                    pos = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                    pos.initPos();
                    pos.printLocation(1);
                    pos.bold(true);
                    pos.printText("点单(客户联)");
                    pos.bold(false);
                    pos.printLocation(0);
                    pos.printTextNewLine("----------------------------------------------");
                    if (tableAVObject.getDate("startedAt") != null) {
                        pos.printTextNewLine("开单时间:" + MyUtils.dateFormat(tableAVObject.getDate("startedAt")));
                    } else {
                        pos.printTextNewLine("开单时间:" + MyUtils.dateFormat(new Date()));
                    }
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printTextNewLine("台 号:" + tableAVObject.getString("tableNumber"));
                    pos.printTextNewLine("人数:" + tableAVObject.getInt("customer"));
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printLine(1);
                    pos.printText("品名");
                    pos.printLocation(20, 1);
                    pos.printText("数量");
                    pos.printLocation(90, 1);
                    pos.printWordSpace(1);
                    pos.printText("单价");
                    pos.printWordSpace(2);
                    pos.printText("金额");
                    List orders = tableAVObject.getList("order");
                    pos.printTextNewLine("----------------------------------------------");
                    for (int i = 0; i < orders.size(); i++) {
                        HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
                        ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                        pos.printTextNewLine(productBean.getName());
                        pos.printLine(1);
                        pos.printText("  ");
                        pos.printLocation(20, 1);
                        pos.printText(ObjectUtil.getDouble(format, "number") + "");
                        pos.printLocation(90, 1);
                        pos.printWordSpace(1);
                        pos.printText(productBean.getPrice() + "");
                        pos.printWordSpace(2);
                        pos.printText(MyUtils.formatDouble(ObjectUtil.getDouble(format, "number") * productBean.getPrice()) + "");
                    }
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLocation(0);
                    pos.printLine(1);
                    pos.printTwoColumn("原价合计 :", MyUtils.formatDouble(originTotalMoneny) + "");
                    pos.printLine(1);
                    if (MyUtils.formatDouble(originTotalMoneny - actualTotalMoneny) > 0) {
                        pos.printTwoColumn("优惠金额 :", MyUtils.formatDouble(originTotalMoneny - actualTotalMoneny) + "");
                    }
                    pos.printLine(1);
                    pos.printTwoColumn("实付金额 :", MyUtils.formatDouble(actualTotalMoneny) + "");
                    if (reduceMap != null && !reduceMap.isEmpty()) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        Set<String> keySet = reduceMap.keySet();
                        //有了Set集合就可以获取其迭代器，取值
                        Iterator<String> iterator = keySet.iterator();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            Double value = reduceMap.get(key);
                            pos.printTwoColumn(key + " :", value + "");
                        }
                    }
                    if (useExchangeList.size() > 0) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        pos.printLocation(1);
                        pos.printText("我的牛肉可抵扣详情");
                        pos.printLine();
                        pos.printLocation(0);
                        pos.printFourColumn("品名", "数量", "  抵扣重量", "抵扣金额");
                        pos.printLine(1);
                        Double actualReduce = 0.0;
                        Double actualWeight = 0.0;
                        for (int i = 0; i < useExchangeList.size(); i++) {
                            Object o = useExchangeList.get(i);
                            HashMap<String, Object> format = ObjectUtil.format(o);
                            pos.printTextNewLine(ObjectUtil.getString(format, "name"));
                            pos.printLine(1);
                            Double reduce = MyUtils.formatDouble(ObjectUtil.getDouble(format, "reduceMoeny"));
                            actualReduce += reduce;
                            actualWeight += ObjectUtil.getDouble(format, "meatWeight");
                            pos.printFourColumn("    ", ObjectUtil.getDouble(format, "number") + "份", "   " + ObjectUtil.getDouble(format, "meatWeight") + "kg", MyUtils.formatDouble(reduce) + "");
                            pos.printLine(1);
                        }
                        pos.printFourColumn("总计", "    ", "    " + MyUtils.formatDouble(actualWeight) + "kg", MyUtils.formatDouble(actualReduce) + "");
                        pos.printLine(1);
                    }
                    if (canExchangeList.size() > 0) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        pos.printLocation(1);
                        pos.printText("超牛牛肉充足可抵扣详情");
                        pos.printLine();
                        pos.printLocation(0);
                        pos.printFourColumn("品名", "数量", "  抵扣重量", "抵扣金额");
                        pos.printLine(1);
                        Double actualReduce = 0.0;
                        Double actualWeight = 0.0;
                        for (int i = 0; i < canExchangeList.size(); i++) {
                            Object o = canExchangeList.get(i);
                            HashMap<String, Object> format = ObjectUtil.format(o);
                            pos.printTextNewLine(ObjectUtil.getString(format, "name"));
                            pos.printLine(1);
                            Double reduce = MyUtils.formatDouble(ObjectUtil.getDouble(format, "reduceMoeny"));
                            actualReduce += reduce;
                            actualWeight += ObjectUtil.getDouble(format, "meatWeight");
                            pos.printFourColumn("    ", ObjectUtil.getDouble(format, "number") + "份", "   " + ObjectUtil.getDouble(format, "meatWeight") + "kg", MyUtils.formatDouble(reduce) + "");
                            pos.printLine(1);
                        }
                        pos.printFourColumn("总计", "    ", "    " + MyUtils.formatDouble(actualWeight) + "kg", MyUtils.formatDouble(actualReduce) + "");
                        pos.printLine(1);
                    }
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLocation(1);
                    pos.printLine(1);
                    pos.printText("地址:" + CONST.ADDRESS);
                    pos.printLine(1);
                    pos.printText("电话:" + CONST.TEL);
                    pos.printLine(1);
                    pos.printText("祝您生活越来越牛!");
                    pos.printLine(2);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();
                } catch (Exception e) {
                    Logger.d(e.getMessage());
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void reprintSvipBill(final AVObject avObject) {
        new Thread() {
            @Override
            public void run() {
                try {
                    final String svipStyle = MyUtils.getProductById(avObject.getList("commodity").get(0).toString()).getName();
                    final Double money = MyUtils.formatDouble(avObject.getDouble("paysum"));
                    final Double reduce = MyUtils.formatDouble(avObject.getDouble("reduce"));
                    final Double finalMoney = MyUtils.formatDouble(avObject.getDouble("paysum") - avObject.getDouble("reduce"));
                    final int escrow = avObject.getInt("escrow");
                    mContext = MyApplication.getContextObject();
                    String url = SharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                    Pos pos;
                    pos = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                    pos.initPos();
                    pos.printLocation(1);
                    pos.bold(true);
                    pos.printText("超牛充值");
                    pos.bold(false);
                    pos.printLine(1);
                    pos.printLocation(0);
                    pos.printTextNewLine("操作人:" + (avObject.getAVObject("cashier") != null ? avObject.getAVObject("cashier").getString("realName") : avObject.getAVObject("cashier").getString("nickName")));
                    pos.printTextNewLine("负责人:" + (avObject.getAVObject("market") != null ? avObject.getAVObject("market").getString("realName") : avObject.getAVObject("market").getString("nickName")));
                    pos.printTextNewLine("时间:" + MyUtils.dateFormat(avObject.getCreatedAt()));
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printLine(1);
                    pos.printLocation(0);
                    pos.printTwoColumn("充值用户:", avObject.getAVObject("user").getString("username"));
                    pos.printLine(1);
                    pos.printTwoColumn("充值类型:", svipStyle);
                    pos.printLine(1);
                    pos.printTwoColumn("充值金额:", money + "元");
                    pos.printLine(1);
                    if (reduce > 0) {
                        pos.printTwoColumn("优惠金额:", reduce + "元");
                        pos.printLine(1);
                    }
                    pos.printTwoColumn("实付金额:", finalMoney + "元");
                    pos.printLine(1);
                    String secrowContent = "";
                    if (escrow == 11) {
                        secrowContent = "白条支付";
                    } else if (escrow == 3) {
                        secrowContent = "支付宝支付";
                    } else if (escrow == 4) {
                        secrowContent = "微信支付";
                    } else if (escrow == 5) {
                        secrowContent = "银行卡支付";
                    } else if (escrow == 6) {
                        secrowContent = "现金支付";
                    }
                    pos.printTwoColumn("支付方式:", secrowContent);
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printLocation(1);
                    pos.printLine(1);
                    pos.printText("地址:" + CONST.ADDRESS);
                    pos.printLine(1);
                    pos.printText("电话:" + CONST.TEL);
                    pos.printLine(1);
                    pos.printText("祝您生活越来越牛!");
                    pos.printLine(4);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public static void printPreOrderRest(final Context context, final Double originTotalMoneny, final Double actualTotalMoneny, final LinkedHashMap<String, Double> reduceMap, final List<Object> useExchangeList, final List<Object> canExchangeList,final List<Object> orders) {
        new Thread() {
            @Override
            public void run() {
                mContext = context;
                SharedHelper sharedHelper = new SharedHelper(context);
                try {
                    String url = SharedHelper.read("ip1") + "." + SharedHelper.read("ip2") + "." + SharedHelper.read("ip3") + "." + SharedHelper.read("ip4");
                    Pos pos;
                    final DecimalFormat df = new DecimalFormat("######0.00");
                    pos = new Pos(url, 9100, "GBK");    //第一个参数是打印机网口IP
                    pos.initPos();
                    pos.printLocation(1);
                    pos.bold(true);
                    pos.printText("点单(客户联)");
                    pos.bold(false);
                    pos.printLocation(0);
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printTextNewLine("下单时间:" + MyUtils.dateFormat(new Date()));
                    pos.printTextNewLine("----------------------------------------------");
                    pos.printLine(1);
                    pos.printText("品名");
                    pos.printLocation(20, 1);
                    pos.printText("数量");
                    pos.printLocation(90, 1);
                    pos.printWordSpace(1);
                    pos.printText("单价");
                    pos.printWordSpace(2);
                    pos.printText("金额");
                    pos.printTextNewLine("----------------------------------------------");
                    for (int i = 0; i < orders.size(); i++) {
                        HashMap<String, Object> format = ObjectUtil.format(orders.get(i));
                        ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                        pos.printTextNewLine(productBean.getName());
                        pos.printLine(1);
                        pos.printText("  ");
                        pos.printLocation(20, 1);
                        pos.printText(ObjectUtil.getDouble(format, "number") + "");
                        pos.printLocation(90, 1);
                        pos.printWordSpace(1);
                        pos.printText(productBean.getPrice() + "");
                        pos.printWordSpace(2);
                        pos.printText(MyUtils.formatDouble(ObjectUtil.getDouble(format, "number") * productBean.getPrice()) + "");
                    }
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLocation(0);
                    pos.printLine(1);
                    pos.printTwoColumn("原价合计 :", MyUtils.formatDouble(originTotalMoneny) + "");
                    pos.printLine(1);
                    if (MyUtils.formatDouble(originTotalMoneny - actualTotalMoneny) > 0) {
                        pos.printTwoColumn("优惠金额 :", MyUtils.formatDouble(originTotalMoneny - actualTotalMoneny) + "");
                    }
                    pos.printLine(1);
                    pos.printTwoColumn("实付金额 :", MyUtils.formatDouble(actualTotalMoneny) + "");
                    if (reduceMap != null && !reduceMap.isEmpty()) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        Set<String> keySet = reduceMap.keySet();
                        //有了Set集合就可以获取其迭代器，取值
                        Iterator<String> iterator = keySet.iterator();
                        while (iterator.hasNext()) {
                            String key = (String) iterator.next();
                            Double value = reduceMap.get(key);
                            pos.printTwoColumn(key + " :", value + "");
                        }
                    }
                    if (useExchangeList.size() > 0) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        pos.printLocation(1);
                        pos.printText("我的牛肉可抵扣详情");
                        pos.printLine();
                        pos.printLocation(0);
                        pos.printFourColumn("品名", "数量", "  抵扣重量", "抵扣金额");
                        pos.printLine(1);
                        Double actualReduce = 0.0;
                        Double actualWeight = 0.0;
                        for (int i = 0; i < useExchangeList.size(); i++) {
                            Object o = useExchangeList.get(i);
                            HashMap<String, Object> format = ObjectUtil.format(o);
                            pos.printTextNewLine(ObjectUtil.getString(format, "name"));
                            pos.printLine(1);
                            Double reduce = MyUtils.formatDouble(ObjectUtil.getDouble(format, "reduceMoeny"));
                            actualReduce += reduce;
                            actualWeight += ObjectUtil.getDouble(format, "meatWeight");
                            pos.printFourColumn("    ", ObjectUtil.getDouble(format, "number") + "份", "   " + ObjectUtil.getDouble(format, "meatWeight") + "kg", MyUtils.formatDouble(reduce) + "");
                            pos.printLine(1);
                        }
                        pos.printFourColumn("总计", "    ", "    " + MyUtils.formatDouble(actualWeight) + "kg", MyUtils.formatDouble(actualReduce) + "");
                        pos.printLine(1);
                    }
                    if (canExchangeList.size() > 0) {
                        pos.printTextNewLine("------------------------------------------------");
                        pos.printLine(1);
                        pos.printLocation(1);
                        pos.printText("超牛牛肉充足可抵扣详情");
                        pos.printLine();
                        pos.printLocation(0);
                        pos.printFourColumn("品名", "数量", "  抵扣重量", "抵扣金额");
                        pos.printLine(1);
                        Double actualReduce = 0.0;
                        Double actualWeight = 0.0;
                        for (int i = 0; i < canExchangeList.size(); i++) {
                            Object o = canExchangeList.get(i);
                            HashMap<String, Object> format = ObjectUtil.format(o);
                            pos.printTextNewLine(ObjectUtil.getString(format, "name"));
                            pos.printLine(1);
                            Double reduce = MyUtils.formatDouble(ObjectUtil.getDouble(format, "reduceMoeny"));
                            actualReduce += reduce;
                            actualWeight += ObjectUtil.getDouble(format, "meatWeight");
                            pos.printFourColumn("    ", ObjectUtil.getDouble(format, "number") + "份", "   " + ObjectUtil.getDouble(format, "meatWeight") + "kg", MyUtils.formatDouble(reduce) + "");
                            pos.printLine(1);
                        }
                        pos.printFourColumn("总计", "    ", "    " + MyUtils.formatDouble(actualWeight) + "kg", MyUtils.formatDouble(actualReduce) + "");
                        pos.printLine(1);
                    }
                    pos.printTextNewLine("------------------------------------------------");
                    pos.printLocation(1);
                    pos.printLine(1);
                    pos.printText("地址:" + CONST.ADDRESS);
                    pos.printLine(1);
                    pos.printText("电话:" + CONST.TEL);
                    pos.printLine(1);
                    pos.printText("祝您生活越来越牛!");
                    pos.printLine(2);
                    pos.feedAndCut();
                    pos.closeIOAndSocket();
                } catch (Exception e) {
                    Logger.d(e.getMessage());
                    e.printStackTrace();
                }
            }
        }.start();
    }
}