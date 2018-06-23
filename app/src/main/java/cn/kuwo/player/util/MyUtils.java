package cn.kuwo.player.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.RuleBean;
import cn.kuwo.player.bean.TypeBean;


public class MyUtils {
    private static final int TAKE_PHOTO_REQUEST_CODE = 1;

    /*
    TextView转String
     */
    public static String TextToString(View tv) {
        if (tv instanceof EditText) {
            return ((EditText) tv).getText().toString().trim();
        } else if (tv instanceof TextView) {
            return ((TextView) tv).getText().toString().trim();
        } else {
            return "字符转换错误";
        }

    }

    public static int getVersionCode(Context context) {

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            int versionCode = pi.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * 检查是否有摄像头权限,没有请求权限
     */
    public static boolean getCameraPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.CAMERA},
                    TAKE_PHOTO_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }


    /**
     * 格式化double类型 保留2位小数
     */
    public static Double formatDouble(double number) {
        final DecimalFormat df = new DecimalFormat("######0.00");
        return Double.parseDouble(df.format(number));
    }

    public static List<ProductBean> getTypeCommodity(int type) {
        RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        List<ProductBean> productBeans = mRealmHleper.queryStoreCommodity(type);
        return productBeans;
    }

    /**
     * 获取所有品类的类型
     */
    public static List<TypeBean> getCommdityType() {
        RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        List<TypeBean> typeBeans = mRealmHleper.queryCommodityTypes();
        return typeBeans;
    }


    /**
     * 开启摄像头参数设置
     */
    public static ZxingConfig caremaSetting() {
        ZxingConfig config = new ZxingConfig();
        config.setShowbottomLayout(true);//底部布局（包括闪光灯和相册）
        config.setPlayBeep(false);//是否播放提示音
        config.setShake(true);//是否震动
        config.setShowAlbum(false);//是否显示相册
        config.setShowFlashLight(true);//是否显示闪光灯
        return config;
    }

    public static ProductBean getProductById(String id) {
        RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        ProductBean productBean = mRealmHleper.queryCommodityById(id);
        return productBean;
    }

    /**
     * 计算商品的总价 通过商品进行查询
     */
    public static double calculateTotal(List<ProductBean> preProductBeans, List<Double> preProductNumbers, List<ProductBean> productBeans, List<Double> productNumbers) {
        double total = 0.0;
        for (int i = 0; i < preProductBeans.size(); i++) {
            total += preProductBeans.get(i).getPrice() * preProductNumbers.get(i);
        }
        for (int i = 0; i < productBeans.size(); i++) {
            total += (Double) (productBeans.get(i).getPrice() * productNumbers.get(i));
        }
        return formatDouble(total);
    }

    /**
     * 计算商品的总价 通过商品的id
     */
    public static double calculateTotal(List<String> productIds, List<Object> productNumbers) {
        double total = 0.0;
        for (int i = 0; i < productIds.size(); i++) {
            total += MyUtils.getProductById(productIds.get(i)).getPrice() * Double.valueOf(productNumbers.get(i).toString());
        }
        return formatDouble(total);
    }

    /**
     * 格式化时间
     */
    public static String dateFormat(Date date) {
        SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
        return timeformat.format(date);
    }
    public static String dateFormat1(Date date) {
        SimpleDateFormat timeformat = new SimpleDateFormat("HH:MM:SS");
        return timeformat.format(date);
    }
    public static String dateFormatShort(Date date) {
        SimpleDateFormat timeformat = new SimpleDateFormat("MM-dd");
        return timeformat.format(date);
    }

    public static Double getDayRate() {
        RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        RuleBean ruleBean = mRealmHleper.queryAllRule().get(0);
        return ruleBean.getAllDiscount();
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static List<ProductBean> getProductBean(String barcode) {
        RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        List<ProductBean> productBeen = new ArrayList<ProductBean>();
        if (barcode.length() == 13) {
            productBeen = mRealmHleper.queryProductByBarcode(barcode);
        } else if (barcode.length() == 18) {
            productBeen = mRealmHleper.queryProductByBarcode(barcode.substring(2, 7));
        } else if (barcode.length() == 5||barcode.length() == 8) {
            productBeen = mRealmHleper.queryProductByBarcode(barcode);
        }
        return productBeen;
    }

    public static Double totalPrice(List<Double> prices) {
        Double totalPrice = 0.0;
        for (Double price : prices) {
            totalPrice += price;
        }
        return formatDouble(totalPrice);
    }
    public static boolean isDoubleOrFloat(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }
    public static String filter(String character)
    {
        character = character.replaceAll("[^(0-9\\u4e00-\\u9fa5)]", "");
        return character;
    }
}
