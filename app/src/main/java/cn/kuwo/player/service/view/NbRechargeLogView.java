package cn.kuwo.player.service.view;

import java.util.List;

import cn.kuwo.player.service.entity.NbRechargeLog;

/**
 * Created by lovely on 2018/7/1
 */
public interface NbRechargeLogView extends View {
    void onSuccess(List<NbRechargeLog> nbRechargeLogs);
    void onError(String result);
}
