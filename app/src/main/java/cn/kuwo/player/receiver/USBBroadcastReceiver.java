package cn.kuwo.player.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.ToastUtil;

/**
 * Created by lovely on 2018/6/22
 */
public class USBBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())){
            ToastUtil.showLong(MyApplication.getContextObject(),"扫描枪连接成功");
            SharedHelper.saveBoolean("useGun",true);
        }
        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())){
            ToastUtil.showLong(MyApplication.getContextObject(),"扫描枪断开连接");
            SharedHelper.saveBoolean("useGun",false);
        }
    }
}
