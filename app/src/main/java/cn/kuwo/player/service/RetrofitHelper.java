package cn.kuwo.player.service;

import android.content.Context;

import com.google.gson.GsonBuilder;

import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.ClientUtil;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lovely on 2018/7/1
 */
public class RetrofitHelper {
    private Context mContext;
    GsonConverterFactory factory=GsonConverterFactory.create(new GsonBuilder().create());
    private static RetrofitHelper instance=null;
    private Retrofit mRetrofit=null;

    public RetrofitHelper(Context context) {
        mContext=context;
        init();
    }

    private void init() {
        resetApp();
    }

    private void resetApp() {
        mRetrofit=new Retrofit.Builder()
                .baseUrl(CONST.APIURL.DOMAIN + CONST.APIURL.ROUTER)
                .client(ClientUtil.getUnsafeOkHttpClient())
                .addConverterFactory(factory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }
    public RetrofitService getServer(){
        return mRetrofit.create(RetrofitService.class);
    }
    public static  RetrofitHelper getInstance(Context context){
        if (instance==null){
            instance=new RetrofitHelper(context);
        }
        return instance;
    }
}
