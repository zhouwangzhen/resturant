package cn.kuwo.player.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by lovely on 2018/6/22
 */
public class USBBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(intent.getAction().equals("android.hardware.usb.action.USB_STATE")){
            if (intent.getExtras().getBoolean("connected")){
                // usb 插入
                Toast.makeText(context, "插入", Toast.LENGTH_LONG).show();
            }else{
                //   usb 拔出
                Toast.makeText(context, "拔出", Toast.LENGTH_LONG).show();
            }
        }
    }
}
