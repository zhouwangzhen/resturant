package cn.kuwo.player.service.view;

import cn.kuwo.player.service.entity.NbRechargeLog;

/**
 * Created by lovely on 2018/7/1
 */
public interface NbRechargeLogView extends View {
    void onSuccess(NbRechargeLog nbRechargeLog);
    void onError(String result);
}
