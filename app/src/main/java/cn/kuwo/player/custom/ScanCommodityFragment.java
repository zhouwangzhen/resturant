package cn.kuwo.player.custom;

import android.content.Intent;
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
import android.widget.TextView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FunctionCallback;
import com.orhanobut.logger.Logger;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.bean.CardBean;
import cn.kuwo.player.util.CameraProvider;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.ToastUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lovely on 2018/6/19
 */
public class ScanCommodityFragment extends DialogFragment {
    private int REQUEST_CODE_SCAN = 111;
    private EditText editScanCode;
    private Button btnCancel,btnScan;
    private Handler mHandler = new Handler();
    private String barcode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_scan_commodity, container);
        initPosition();
        findView(view);
        initData();
        return view;
    }

    private void findView(View view) {
        editScanCode = view.findViewById(R.id.edit_code);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnScan = view.findViewById(R.id.btn_scan);
        editScanCode.setInputType(InputType.TYPE_NULL);
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

    private void initData() {
        if (CameraProvider.hasCamera() && !SharedHelper.readBoolean("useGun")) {
            btnScan.setVisibility(View.VISIBLE);
            btnScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CameraProvider.hasCamera()) {
                        if (MyUtils.getCameraPermission(getContext())) {
                            Intent intent = new Intent(getActivity(), CaptureActivity.class);
                            intent.putExtra(Constant.INTENT_ZXING_CONFIG, MyUtils.caremaSetting());
                            startActivityForResult(intent, REQUEST_CODE_SCAN);
                        }
                    }
                }
            });
        } else {
            btnScan.setVisibility(View.GONE);
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
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

    }

    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {
            editScanCode.setText("");
            barcode = barcode.replace(";", "").replace(";?", "").replace(":", "").replace("?", "");
            if (ProductUtil.getProductBean(barcode).size() > 0) {
                ShowComboMenuFragment showComboMenuFragment = new ShowComboMenuFragment(MyApplication.getContextObject(), ProductUtil.getProductBean(barcode).get(0), false, barcode);
                showComboMenuFragment.show(getActivity().getFragmentManager(), "showcomboMenu");
            } else {
                ToastUtil.showShort(MyApplication.getContextObject(), "扫卡错误,请扫描正确商品条码");
            }
            editScanCode.requestFocus();
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                editScanCode.setText("");
                barcode=data.getStringExtra(Constant.CODED_CONTENT);
                if (ProductUtil.getProductBean(barcode).size() > 0) {
                    ShowComboMenuFragment showComboMenuFragment = new ShowComboMenuFragment(MyApplication.getContextObject(), ProductUtil.getProductBean(barcode).get(0), false, barcode);
                    showComboMenuFragment.show(getActivity().getFragmentManager(), "showcomboMenu");
                } else {
                    ToastUtil.showShort(MyApplication.getContextObject(), "请扫描正确商品条码");
                }
                editScanCode.requestFocus();

            }
        }
    }
}
