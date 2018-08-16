package cn.kuwo.player.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.nio.file.Path;

import butterknife.ButterKnife;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.util.AppUtils;

public abstract class BaseActivity extends AppCompatActivity {
    protected abstract int getContentViewId();
    QMUITipDialog tipDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        tipDialog = new QMUITipDialog.Builder(this)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中")
                .create();
        initData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (getSupportFragmentManager()
                    .getBackStackEntryCount() == 1) {
                moveTaskToBack(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    public abstract void initData();

    public void showLongToast(String content) {
        Toast.makeText(MyApplication.getContextObject(), content, Toast.LENGTH_LONG).show();
    }

    public void showShortToast(String content) {
        Toast.makeText(MyApplication.getContextObject(), content, Toast.LENGTH_SHORT).show();
    }

    public String TextToString(View tv) {
        if (tv instanceof EditText) {
            return ((EditText) tv).getText().toString().trim();
        } else if (tv instanceof TextView) {
            return ((TextView) tv).getText().toString().trim();
        } else {
            return "字符转换错误";
        }

    }
    public void showDialog() {
        tipDialog.show();
    }

    public void hideDialog() {
        try {
            if (tipDialog != null) {
                tipDialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public Float getDensity() {
        try {
            return AppUtils.getScreenDensity(MyApplication.getContextObject());
        }catch (Exception e){
            return 2f;
        }

    }
}
