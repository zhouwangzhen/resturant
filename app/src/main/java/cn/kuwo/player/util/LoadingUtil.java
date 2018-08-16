package cn.kuwo.player.util;

import android.content.Context;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;


/**
 * Created by lovely on 2018/7/27
 */
public class LoadingUtil {
    public static QMUITipDialog tipDialog;
    public static void show(Context context,String content){
        try {
            if (tipDialog == null) {
                tipDialog = new QMUITipDialog.Builder(context)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord(content)
                        .create(false);
            }
            tipDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void hide(){
        try {
            if (tipDialog != null && tipDialog.isShowing()) {
                tipDialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
