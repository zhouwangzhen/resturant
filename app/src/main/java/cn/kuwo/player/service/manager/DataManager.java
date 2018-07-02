package cn.kuwo.player.service.manager;

import android.content.Context;


import cn.kuwo.player.service.RetrofitHelper;
import cn.kuwo.player.service.RetrofitService;
import cn.kuwo.player.service.entity.NbRechargeLog;
import rx.Observable;

/**
 * Created by lovely on 2018/7/1
 */
public class DataManager {
    private RetrofitService mRetrofitService;
    public DataManager(Context context){
        this.mRetrofitService= RetrofitHelper.getInstance(context).getServer();
    }

    public Observable<NbRechargeLog>getNbRechagreLogList(long since,
                                                         long before,
                                                         String typeList,
                                                         int store,
                                                         boolean isShowTest){
        return mRetrofitService.rechargeQuery(since,before,typeList,store,isShowTest);
    }
}
