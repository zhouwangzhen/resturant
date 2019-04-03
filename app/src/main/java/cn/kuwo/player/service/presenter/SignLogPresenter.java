package cn.kuwo.player.service.presenter;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import cn.kuwo.player.service.entity.NbRechargeLog;
import cn.kuwo.player.service.entity.SignLog;
import cn.kuwo.player.service.manager.DataManager;
import cn.kuwo.player.service.view.SignLogView;
import cn.kuwo.player.service.view.View;
import cn.kuwo.player.util.T;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lovely on 2018/7/24
 */
public class SignLogPresenter implements Presenter {
    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private List<SignLog> mSignLogs;
    private SignLogView mSignLogView;
    @Override
    public void onCreate() {
        manager=new DataManager(mContext);
        mCompositeSubscription=new CompositeSubscription();
    }
    public SignLogPresenter(Context mContext){
        this.mContext=mContext;
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
        mSignLogView=(SignLogView)view;
    }

    @Override
    public void attachIcoomingIntent(Intent intent) {

    }
    public void getSignLog(long since,
                                 long before,
                                 int store,
                                 boolean isShowTest){
        mCompositeSubscription.add(manager.getSignLogList(since,before,store,isShowTest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<SignLog>>() {
                    @Override
                    public void onCompleted() {
                        if (mSignLogView!=null){
                            mSignLogView.onSuccess(mSignLogs);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        T.L("请求失败!!!"+e.getMessage());
                    }

                    @Override
                    public void onNext(List<SignLog> signLogs) {
                        mSignLogs=signLogs;
                    }

                }));
    }

}
