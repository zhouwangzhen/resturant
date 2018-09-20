package cn.kuwo.player.service;

import java.util.List;

import cn.kuwo.player.service.entity.ConsumpteLog;
import cn.kuwo.player.service.entity.NbRechargeLog;
import cn.kuwo.player.service.entity.SignLog;
import cn.kuwo.player.util.CONST;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
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
    @POST("services/niu_token/offline_operation/recharge")
    Call<ResponseBody> nbCompense(@Field("target_user_id") String targetUserId,
                                  @Field("salesman_id") String salesmanId,
                                  @Field("cashier_id") String cashierId,
                                  @Field("amount") Double amount,
                                  @Field("acctually_paid") Double acctually_paid,
                                  @Field("payment_num") int payment,
                                  @Field("store_num") int store,
                                  @Field("gift_type_num") int gift_type_num,
                                  @Field("gift_reason") String message);

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

    @FormUrlEncoded
    @POST("services/niu_token/offline_query/sign_in_records")
    Observable<List<SignLog>> signQuery(@Field("since") long since,
                                        @Field("before") long before,
                                        @Field("store_num") int store,
                                        @Field("should_show_test_user") boolean showTest);

    @FormUrlEncoded
    @POST("services/user/store_consumption")
    Observable<ConsumpteLog> storeConsumpte(
                                        @Field("store") int store,
                                        @Field("user_id") String userId);

    @FormUrlEncoded
    @POST("services/niu_token_card/enable")
    Call<ResponseBody> mouCardConvert(
            @Field("niu_token_card") String code,
            @Field("marketer_id") String userId,
            @Field("store") int store);
}
