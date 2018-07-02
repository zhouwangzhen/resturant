package cn.kuwo.player.service.presenter;

import android.content.Context;
import android.content.Intent;

import cn.kuwo.player.service.entity.NbRechargeLog;
import cn.kuwo.player.service.manager.DataManager;
import cn.kuwo.player.service.view.NbRechargeLogView;
import cn.kuwo.player.service.view.View;
import cn.kuwo.player.util.T;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lovely on 2018/7/1
 */
public class NbRechargeLogPresenter implements Presenter {
    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private NbRechargeLog mNbRechargeLog;
    private NbRechargeLogView mNbRechargeLogView;

    public NbRechargeLogPresenter(Context mContext){
        this.mContext=mContext;
    }
    @Override
    public void onCreate() {
        manager=new DataManager(mContext);
        mCompositeSubscription=new CompositeSubscription();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
        if (mCompositeSubscription.hasSubscriptions()){
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void attachView(View view) {
        mNbRechargeLogView= (NbRechargeLogView) view;
    }

    @Override
    public void attachIcoomingIntent(Intent intent) {
    }

    public void getNbRechagreLog(long since,
                                 long before,
                                 String typeList,
                                 int store,
                                 boolean isShowTest){
        mCompositeSubscription.add(manager.getNbRechagreLogList(since,before,typeList,store,isShowTest)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<NbRechargeLog>() {
            @Override
            public void onCompleted() {
                if (mNbRechargeLogView!=null){
                    mNbRechargeLogView.onSuccess(mNbRechargeLog);
                }
            }

            @Override
            public void onError(Throwable e) {
             T.L("请求失败!!!");
            }

            @Override
            public void onNext(NbRechargeLog nbRechargeLog) {
                mNbRechargeLog=nbRechargeLog;
            }
        }));
    }
}
