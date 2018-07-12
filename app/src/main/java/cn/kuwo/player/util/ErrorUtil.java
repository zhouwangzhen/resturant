package cn.kuwo.player.util;

import cn.kuwo.player.MyApplication;

/**
 * Created by lovely on 2018/6/23
 */
public class ErrorUtil {
    public static void NETERROR(){
        ToastUtil.showShort(MyApplication.getContextObject(), "网络连接错误");
    }
}
