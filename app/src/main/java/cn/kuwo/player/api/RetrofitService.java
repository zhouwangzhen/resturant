package cn.kuwo.player.api;

import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by lovely on 2018/6/26
 */
public interface RetrofitService {
    @FormUrlEncoded
    @POST("services/niu_token/offline_operation/recharge")
    Call<ResponseBody> offlineRecharge(@Field("targetUserId") String targetUserId,
                                       @Field("salesmanId") String salesmanId,
                                       @Field("cashierId") String cashierId,
                                       @Field("amount") Double amount,
                                       @Field("payment") String payment,
                                       @Field("store") int store);

    @FormUrlEncoded
    @POST("services/niu_token/offline_query/user_amount")
    Call<ResponseBody> QueryofflineRecharge(@Field("targetUserId") String targetUserId);

    @FormUrlEncoded
    @POST("services/niu_token/offline_operation/consume")
    Call<ResponseBody> offlineConsume(@Field("targetUserId") String targetUserId,
                                       @Field("salesmanId") String salesmanId,
                                       @Field("cashierId") String cashierId,
                                       @Field("amount") Double amount,
                                      @Field("store") int store);

    @FormUrlEncoded
    @POST("services/niu_token/offline_query/operations")
        Call<ResponseBody> rechargeQuery(@Field("since") long since,
                                         @Field("before") long before,
                                         @Field("op_types") String typeList,
                                         @Field("store") int store,
                                         @Field("should_show_test_user") boolean showTest);
}
