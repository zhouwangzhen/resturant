package cn.kuwo.player.util;

import com.avos.avoscloud.AVObject;

import java.util.HashMap;
import java.util.Map;

public class PayInfoUtil {
    /**
     * 结账账单时计算付款详情
     */
    public static Map<String,Double> managerEscrowByRest(Double actualMoney, int escrow, AVObject avObject) {
        Map<String, Double> escrowDetail = new HashMap<>();
        Double whiteBarBalance=0.0;
        Double storedBalance=0.0;
        AVObject user = avObject.getAVObject("user");
        if (user!=null) {
            whiteBarBalance = MyUtils.formatDouble(MyUtils.formatDouble(user.getDouble("gold")) - MyUtils.formatDouble(user.getDouble("arrears")));
            storedBalance = MyUtils.formatDouble(user.getDouble("stored"));
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
            case 26:
                escrowDetail.put("互动吧支付", actualMoney);
                break;

        }
        return escrowDetail;
    }
}
