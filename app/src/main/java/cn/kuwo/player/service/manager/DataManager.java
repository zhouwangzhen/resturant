package cn.kuwo.player.service.manager;

import android.content.Context;


import java.util.List;

import cn.kuwo.player.service.RetrofitHelper;
import cn.kuwo.player.service.RetrofitService;
import cn.kuwo.player.service.entity.ConsumpteLog;
import cn.kuwo.player.service.entity.NbRechargeLog;
import cn.kuwo.player.service.entity.SignLog;
import rx.Observable;

/**
 * Created by lovely on 2018/7/1
 */
public class DataManager {
    private RetrofitService mRetrofitService;

    public DataManager(Context context) {
        this.mRetrofitService = RetrofitHelper.getInstance(context).getServer();
    }

    public Observable<List<NbRechargeLog>> getNbRechagreLogList(long since,
                                                                long before,
                                                                int store,
                                                                int type,
                                                                boolean isShowTest) {
        return mRetrofitService.rechargeQuery(since, before, store, type, isShowTest);
    }


    public Observable<List<SignLog>> getSignLogList(long since,
                                                    long before,
                                                    int store,
                                                    boolean isShowTest) {
        return mRetrofitService.signQuery(since, before, store, isShowTest);
    }

    public Observable<ConsumpteLog> getConsumeLog(
            int store,
            String userId) {
        return mRetrofitService.storeConsumpte(store, userId);
    }
}
