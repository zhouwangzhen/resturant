package cn.kuwo.player.custom;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.bean.CardBean;
import cn.kuwo.player.util.ToastUtil;

public class ScanCardFragment extends DialogFragment {
    private EditText editScanCode;
    private Button btnCancel;
    private Handler mHandler = new Handler();
    private String barcode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_scan_card, container);
        initPosition();
        findView(view);
        initData();
        return view;
    }

    private void initPosition() {
        getDialog().getWindow().setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.dimAmount = 0.6f;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(lp);
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
    }

    private void findView(View view) {
        editScanCode = view.findViewById(R.id.edit_code);
        btnCancel = view.findViewById(R.id.btn_cancel);
        editScanCode.setInputType(InputType.TYPE_NULL);
    }

    private void initData() {

        editScanCode.requestFocus();
        editScanCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (delayRun != null) {
                        mHandler.removeCallbacks(delayRun);
                    }
                    barcode = s.toString().trim();
                    mHandler.postDelayed(delayRun, 300);
                }
            }

        });
        editScanCode.setText("1000");

    }

    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {
            editScanCode.setText("");
            barcode = barcode.replace(";", "").replace(";?", "").replace(":", "").replace("?", "");
            if (barcode.length() == 4) {
                AVQuery<AVObject> power = new AVQuery<>("Power");
                power.whereEqualTo("card", barcode);
                power.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
                            if (list.size() > 0) {
                                ToastUtil.showShort(MyApplication.getContextObject(), "此卡已经绑定用户,不可重复绑定");
                            } else {
                                getDialog().dismiss();
                                EventBus.getDefault().post(new CardBean(barcode));
                            }
                        } else {
                            ToastUtil.showShort(MyApplication.getContextObject(), "网络超时,请重试");
                        }
                    }
                });
            } else {
                ToastUtil.showShort(MyApplication.getContextObject(), "扫卡错误,请扫描正确的会员卡");
            }
            editScanCode.requestFocus();
        }
    };
}
