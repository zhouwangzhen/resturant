package cn.kuwo.player.util;

import cn.kuwo.player.service.RetrofitService;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Created by lovely on 2018/6/26
 */
public class ApiManager {
    private RetrofitService retrofitService;
    private static ApiManager sApiManager;

    public static ApiManager getInstance() {
        if (sApiManager == null) {
            synchronized (ApiManager.class) {
                sApiManager = new ApiManager();
            }
        }
        return sApiManager;
    }

    public RetrofitService getRetrofitService() {
        if (retrofitService==null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CONST.APIURL.DOMAIN + CONST.APIURL.ROUTER)
                    .client(ClientUtil.getUnsafeOkHttpClient())
                    .build();
            retrofitService = retrofit.create(RetrofitService.class);
        }
        return this.retrofitService;

    }

}
