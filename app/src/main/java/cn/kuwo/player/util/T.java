package cn.kuwo.player.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.logging.Logger;

import cn.kuwo.player.MyApplication;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by lovely on 2018/6/27
 */
public class T {
    public static void show(Response<ResponseBody> response){
        try {
            JSONObject jsonObject = new JSONObject(response.errorBody().string());
            ToastUtil.showLong(MyApplication.getContextObject(),"错误码:"+response.code()+","+jsonObject.getString("message"));
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showLong(MyApplication.getContextObject(),"错误码:"+response.code()+"网络连接错误");
        }

    }
    public static void L(String message){
        ToastUtil.showLong(MyApplication.getContextObject(),message);
    }
}
