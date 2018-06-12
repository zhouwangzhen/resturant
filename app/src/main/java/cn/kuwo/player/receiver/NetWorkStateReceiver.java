package cn.kuwo.player.receiver;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.bean.NetBean;
import cn.kuwo.player.custom.CommomDialog;
import cn.kuwo.player.custom.ShowNoNetFragment;
import cn.kuwo.player.util.NetUtils;
import cn.kuwo.player.util.ToastUtil;

public class NetWorkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {

        System.out.println("网络状态发生变化");
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetworkInfo.isConnected()) {
                Toast.makeText(context, "WIFI已连接", Toast.LENGTH_SHORT).show();
            } else if (!wifiNetworkInfo.isConnected()) {
                Toast.makeText(context, "WIFI已断开", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (NetUtils.isNetConnected(context)) {
                EventBus.getDefault().post(new NetBean(0));
            } else {
                EventBus.getDefault().post(new NetBean(-1));
            }

        }
    }

}
