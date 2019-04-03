package cn.kuwo.player.custom;

;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.regex.Pattern;

import cn.kuwo.player.MainActivity;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.fragment.CommodityClassifyFg;
import cn.kuwo.player.fragment.OrderListFg;
import cn.kuwo.player.print.Bill;
import cn.kuwo.player.util.ApiManager;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.T;
import cn.kuwo.player.util.ToastUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lovely on 2018/12/25
 */
public class ShowNbRebateFg extends DialogFragment {
    private TextView order_detail_money, actual_rebate_price;
    private LinearLayout ll_type_rate, ll_type_fix_money;
    private RadioGroup rg_type;
    private EditText rebate_rate, rebate_money, rebate_context;
    private Button btn_ensure_rebate;
    private RadioButton rb_fix, rb_rate;
    private View view;
    private AVObject avObject;
    private QMUITipDialog tipDialog;

    public ShowNbRebateFg(AVObject avObject) {
        this.avObject = avObject;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.fragment_nb_rebate, container);
        findView();
        initViews();
        return view;
    }


    private void findView() {
        tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中")
                .create(false);
        order_detail_money = view.findViewById(R.id.order_detail_money);
        ll_type_rate = view.findViewById(R.id.ll_type_rate);
        ll_type_fix_money = view.findViewById(R.id.ll_type_fix_money);
        rg_type = view.findViewById(R.id.rg_type);
        rebate_rate = view.findViewById(R.id.rebate_rate);
        rebate_money = view.findViewById(R.id.rebate_money);
        btn_ensure_rebate = view.findViewById(R.id.btn_ensure_rebate);
        rebate_context = view.findViewById(R.id.rebate_context);
        rb_fix = view.findViewById(R.id.rb_fix);
        actual_rebate_price = view.findViewById(R.id.actual_rebate_price);
        rb_rate = view.findViewById(R.id.rb_rate);


    }

    private void initViews() {
        order_detail_money.setText(avObject.getDouble("paysum") + "元");
        ll_type_rate.setVisibility(View.GONE);
        rg_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_fix:
                        ll_type_rate.setVisibility(View.GONE);
                        ll_type_fix_money.setVisibility(View.VISIBLE);
                        String rebateMoney = rebate_money.getText().toString().trim();
                        if (isNumber(rebateMoney) && Double.parseDouble(rebateMoney) < avObject.getDouble("paysum")) {
                            actual_rebate_price.setText(rebateMoney);
                        } else {
                            actual_rebate_price.setText("0.0");
                        }
                        break;
                    case R.id.rb_rate:
                        ll_type_rate.setVisibility(View.VISIBLE);
                        ll_type_fix_money.setVisibility(View.GONE);
                        String rebateRate = rebate_rate.getText().toString().trim();
                        if (isNumber(rebateRate) && Double.parseDouble(rebateRate) <= 100) {
                            actual_rebate_price.setText(MyUtils.formatDouble(Double.parseDouble(rebateRate) * avObject.getDouble("paysum") / 100) + "");
                        } else {
                            actual_rebate_price.setText("0.0");
                        }
                        break;
                }
            }
        });
        rg_type.check(R.id.rb_fix);
        rebate_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = rebate_money.getText().toString().trim();
                if (isNumber(text) && Double.parseDouble(text) < avObject.getDouble("paysum")) {
                    actual_rebate_price.setText(text);
                } else {
                    actual_rebate_price.setText("0.0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        rebate_rate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = rebate_rate.getText().toString().trim();
                if (isNumber(text) && Double.parseDouble(text) <= 100) {
                    actual_rebate_price.setText(MyUtils.formatDouble(Double.parseDouble(text) * avObject.getDouble("paysum") / 100) + "");
                } else {
                    actual_rebate_price.setText("0.0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_ensure_rebate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double actual_rebate = MyUtils.formatDouble(Double.parseDouble(actual_rebate_price.getText().toString().trim()));
                if (actual_rebate > 0) {
                    avObject.put("message",avObject.getString("message")+"【牛币返现】");
                    avObject.put("isRebate",true);
                    try {
                        avObject.save();
                    } catch (AVException e) {
                        e.printStackTrace();
                    }
                    tipDialog.show();
                    String reason = "【消费牛币返现】- ";
                    if (rb_rate.isChecked()) {
                        reason += "订单金额的" + MyUtils.formatDouble(Double.parseDouble(rebate_rate.getText().toString().trim()) * avObject.getDouble("paysum") / 100) + "%" + "返现牛币";
                        if(Double.parseDouble(rebate_rate.getText().toString().trim())>10){
                            ToastUtil.showShort(MyApplication.getContextObject(), "最高返现比例为10%");
                            tipDialog.hide();
                            return;
                        }
                    }
                    if (rb_fix.isChecked()) {
                        reason += "固定金额" + MyUtils.formatDouble(Double.parseDouble(rebate_money.getText().toString().trim())) + "个返现牛币";
                        if (Double.parseDouble(rebate_money.getText().toString().trim())>(MyUtils.formatDouble(avObject.getDouble("paysum")-avObject.getDouble("reduce")))){
                            ToastUtil.showShort(MyApplication.getContextObject(), "返现不可超过订单金额");
                            tipDialog.hide();
                            return;
                        }
                        if (Double.parseDouble(rebate_money.getText().toString().trim())>100){
                            ToastUtil.showShort(MyApplication.getContextObject(), "返现不可超过100元");
                            tipDialog.hide();
                            return;
                        }
                    }
                    if (rebate_context.getText().toString().length() > 0) {
                        reason += "-" + rebate_context.getText().toString();
                    }

                    Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().nbCompense(avObject.getAVUser("user").getObjectId(),
                            SharedHelper.read("cashierId"),
                            SharedHelper.read("cashierId"),
                            actual_rebate,
                            0.0,
                            1,
                            2,
                            3,
                            reason);
                    responseBodyCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            tipDialog.hide();
                            if (response.code() == 200 || response.code() == 201) {
                                try {
                                    avObject.save();
                                } catch (AVException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(MyApplication.getContextObject(), "充值成功", Toast.LENGTH_SHORT).show();
                                new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom)
                                        .setTitle("提示")
                                        .setMessage("补偿用户牛币" + actual_rebate_price.getText().toString() + "成功")
                                        .setPositiveButton("确定", null)
                                        .show();
                                getDialog().dismiss();
                                getTargetFragment().onActivityResult(1,1,null);
                            } else {
                                T.show(response);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            tipDialog.hide();
                            T.L(t.getMessage());
                        }
                    });
                } else {
                    ToastUtil.showShort(MyApplication.getContextObject(), "返利不可为0");
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), (int) (dm.widthPixels * 0.5));
            final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            getDialog().getWindow().setAttributes(layoutParams);
        }
    }

    public boolean isNumber(String str) {
        boolean isInt = Pattern.compile("^-?[1-9]\\d*$").matcher(str).find();
        boolean isDouble = Pattern.compile("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$").matcher(str).find();
        return isInt || isDouble;
    }
}
