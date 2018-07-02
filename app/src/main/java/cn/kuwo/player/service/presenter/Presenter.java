package cn.kuwo.player.service.presenter;

import android.content.Intent;

import cn.kuwo.player.service.view.View;

/**
 * Created by lovely on 2018/7/1
 */
public interface Presenter {
    void onCreate();
    void onStart();
    void onStop();
    void pause();
    void attachView(View view);
    void attachIcoomingIntent(Intent intent);

}
