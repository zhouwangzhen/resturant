package cn.kuwo.player.service;

import java.util.Date;
import java.util.List;

import cn.kuwo.player.service.entity.NbRechargeLog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by lovely on 2018/6/26
 */
public interface RetrofitService {
    @FormUrlEncoded
    @POST("services/niu_token/offline_operation/recharge")
    Call<ResponseBody> offlineRecharge(@Field("target_user_id") String targetUserId,
                                       @Field("salesman_id") String salesmanId,
                                       @Field("cashier_id") String cashierId,
                                       @Field("amount") Double amount,
                                       @Field("acctually_paid") Double acctually_paid,
                                       @Field("payment_num") int payment,
                                       @Field("store_num") int store,
                                       @Field("gift_type_num") int gift_type_num);

    @FormUrlEncoded
    @POST("services/niu_token/offline_query/user_amount")
    Call<ResponseBody> QueryofflineRecharge(@Field("target_user_id") String targetUserId);

    @FormUrlEncoded
    @POST("services/niu_token/offline_operation/consume")
    Call<ResponseBody> offlineConsume(@Field("target_user_id") String targetUserId,
                                       @Field("salesman_id") String salesmanId,
                                       @Field("cashier_id") String cashierId,
                                       @Field("amount") Double amount,
                                      @Field("store_num") int store);

    @FormUrlEncoded
    @POST("services/niu_token/offline_query/recharges")
    Observable<List<NbRechargeLog>> rechargeQuery(@Field("since") long since,
                                            @Field("before") long before,
                                            @Field("store_num") int store,
                                            @Field("gift_type_num") int type,
                                            @Field("should_show_test_user") boolean showTest);
}
