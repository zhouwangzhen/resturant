package cn.kuwo.player.service.presenter;

import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import java.util.List;

import cn.kuwo.player.service.manager.DataManager;
import cn.kuwo.player.service.entity.ConsumpteLog;
import cn.kuwo.player.service.view.ConsumpteLogView;
import cn.kuwo.player.service.view.View;
import cn.kuwo.player.util.T;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by lovely on 2018/7/26
 */
public class StoreConsumptePresenter implements Presenter{
    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private ConsumpteLog mConsumpteLog;
    private ConsumpteLogView consumpteLogView;
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
            consumpteLogView=(ConsumpteLogView) view;
    }

    @Override
    public void attachIcoomingIntent(Intent intent) {
    }

    public void getConsumeLog(int store,String userId){
        mCompositeSubscription.add(manager.getConsumeLog(store,userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ConsumpteLog>() {
                    @Override
                    public void onCompleted() {
                        if (consumpteLogView!=null){
                            consumpteLogView.onSuccess(mConsumpteLog);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d(e.getMessage());
                        T.L("请求失败!!!"+e.getMessage());
                    }

                    @Override
                    public void onNext(ConsumpteLog consumpteLog) {
                        mConsumpteLog=consumpteLog;
                    }

                }));
    }
}
