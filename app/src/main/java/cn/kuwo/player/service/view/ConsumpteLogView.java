package cn.kuwo.player.service.view;

import java.util.List;

import cn.kuwo.player.service.entity.ConsumpteLog;

/**
 * Created by lovely on 2018/7/26
 */
public interface ConsumpteLogView extends View {
    void onSuccess(ConsumpteLog consumpteLogList);
    void onError(String result);
}
