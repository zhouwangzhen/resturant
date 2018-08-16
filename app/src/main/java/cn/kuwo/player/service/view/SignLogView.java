package cn.kuwo.player.service.view;

import java.util.List;

import cn.kuwo.player.service.entity.SignLog;

/**
 * Created by lovely on 2018/7/24
 */
public interface SignLogView extends View {
    void onSuccess(List<SignLog> nbRechargeLogs);
    void onError(String result);
}
