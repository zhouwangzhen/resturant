package cn.kuwo.player.util;

import com.orhanobut.logger.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.RetailBean;

public class ObjectUtil {
    public static HashMap<String, Object> format(Object o) {
        HashMap<String, Object> stringObjectHashMap = (HashMap<String, Object>) o;
        return stringObjectHashMap;
    }
    public static String getString(HashMap<String, Object> o,String param){

        try{
            return o.get(param).toString();
        }catch (Exception e){
            return "";
        }
    }
    public static Double getDouble(HashMap<String, Object> o,String param){
       try{
           return MyUtils.formatDouble((Double) o.get(param));
       }catch (Exception e){
           return MyUtils.formatDouble(Double.parseDouble(o.get(param)+""));
       }

    }
    public static int getInt(HashMap<String, Object> o,String param){
        try{
            return (int) o.get(param);
        }catch (Exception e){
            return 0;
        }

    }
    public static List<String> getList(HashMap<String, Object> o, String param){
        try{
            return  (List<String>) o.get(param);
        }catch (Exception e){
            e.printStackTrace();
            Logger.d(e.getMessage());
            return new ArrayList<>();
        }
    }
    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }
    public static <T> T cloneTo(T src) throws RuntimeException {
        ByteArrayOutputStream memoryBuffer = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        T dist = null;
        try {
            out = new ObjectOutputStream(memoryBuffer);
            out.writeObject(src);
            out.flush();
            in = new ObjectInputStream(new ByteArrayInputStream(memoryBuffer.toByteArray()));
            dist = (T) in.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null)
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            if (in != null)
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
        return dist;
    }

    public static List<Object> toObject(List<String> ids, ArrayList<Double> prices, ArrayList<Double> weights) {
        List<Object> orders=new ArrayList<>();
        for (int i=0;i<ids.size();i++) {
            HashMap<String, Object> params = new HashMap<>();
            ProductBean productBean = MyUtils.getProductById(ids.get(i));
            params.put("name", productBean.getName());
            params.put("id", productBean.getObjectId());
            params.put("price", MyUtils.formatDouble(prices.get(i)));
            params.put("weight", MyUtils.formatDouble(weights.get(i)));
            params.put("number", 1);
            if (productBean.getType()==6||productBean.getType()==7){
                params.put("nb", MyUtils.formatDouble(prices.get(i)*CONST.NB.MEATDiSCOUNT));
            }else if(productBean.getType()==9){
                params.put("nb", MyUtils.formatDouble(prices.get(i)));
            }else{
                params.put("nb", MyUtils.formatDouble(prices.get(i)*CONST.NB.OTHERDISCOUNT));
            }
            orders.add(params);
        }
        return orders;
    }
}
