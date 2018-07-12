package cn.kuwo.player.util;

/**
 * Created by lovely on 2018/6/15
 */
public class RechargeUtil {
    public static Double findRealMoney(Double money){
        Double pay = Math.abs(money);
        if (pay == 550) {
            pay = 500.0;
        } else if (pay == 2500) {
            pay = 2000.0;
        } else if (pay == 8000) {
            pay = 6000.0;
        } else if (pay == 15000) {
            pay = 10000.0;
        }
        return pay;
    }
}
