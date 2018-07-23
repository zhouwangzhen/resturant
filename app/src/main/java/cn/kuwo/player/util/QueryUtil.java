package cn.kuwo.player.util;


import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lovely on 2018/6/22
 */
public class QueryUtil {
    public static int findMachine(List<Object> orders) {
        int number = 0;
        for (Object o : orders) {
            HashMap<String, Object> map = (HashMap<String, Object>) o;
            if (Arrays.asList(CONST.COUNTIDS).contains(ObjectUtil.getString(map, "id"))){
                number += ObjectUtil.getDouble(map, "number").intValue();
            }
        }
        return number;
    }
    public static int findCookMeatNumber(List<Object> orders) {
        int number = 0;
        for (Object o : orders) {
            HashMap<String, Object> map = (HashMap<String, Object>) o;
            if (!ObjectUtil.getString(map, "cookStyle").equals("")) {
                number += ObjectUtil.getDouble(map, "number").intValue();
            }
        }
        return number;
    }
}
