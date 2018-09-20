package cn.kuwo.player.fragment.credit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.GetCallback;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.custom.FlowRadioGroup;
import cn.kuwo.player.util.ApiManager;
import cn.kuwo.player.util.DataUtil;
import cn.kuwo.player.util.LoadingUtil;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.T;
import cn.kuwo.player.util.ToastUtil;
import me.yokeyword.fragmentation.SupportFragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lovely on 2018/8/21
 */
public class CompenseFragment extends SupportFragment {


    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.scan_meatcode)
    EditText edit_code;
    @BindView(R.id.ll_no_user)
    LinearLayout llNoUser;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.user_nb)
    TextView userNb;
    @BindView(R.id.tv_recharge_money)
    TextView tvRechargeMoney;
    @BindView(R.id.rl_recharge)
    RelativeLayout rlRecharge;
    @BindView(R.id.card_recharge_money)
    CardView cardRechargeMoney;
    @BindView(R.id.reason_1)
    RadioButton reason1;
    @BindView(R.id.reason_2)
    RadioButton reason2;
    @BindView(R.id.reason_3)
    RadioButton reason3;
    @BindView(R.id.rg_paystyle)
    FlowRadioGroup rgPaystyle;
    @BindView(R.id.gv_payment)
    CardView gvPayment;
    @BindView(R.id.btn_recharge)
    Button btnRecharge;
    @BindView(R.id.btn_refrsh)
    Button btnRefrsh;
    @BindView(R.id.reset_data)
    Button resetData;
    Unbinder unbinder;
    private String userId = "";
    private String marketId = "";
    private String username = "";
    private String marketName = "";
    private Double rechargeMoney = 0.0;
    private Double paySumMoney = 20.0;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private String editString = "";
    private View view;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(getContext(), "网络错误,请检查网络连接", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    Toast.makeText(getContext(), msg.getData().getString("error"), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {
            LoadingUtil.show(getContext(), "加载中");
            edit_code.setText("");
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("payCode", editString);
            AVCloud.callFunctionInBackground("payCodeGetUser", parameters, new FunctionCallback<Map<String, Object>>() {

                @Override
                public void done(final Map<String, Object> stringObjectMap, AVException e) {
                    if (e == null) {
                        userId = (String) stringObjectMap.get("objectId");
                        Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().QueryofflineRecharge(userId);
                        responseBodyCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                LoadingUtil.hide();
                                if (response.code() == 200 || response.code() == 201) {
                                    Double nb = 0.0;
                                    try {
                                        String responseText = DataUtil.JSONTokener(response.body().string());
                                        JSONObject jsonObject = new JSONObject(responseText);
                                        nb = jsonObject.getDouble("amount");
                                        username = stringObjectMap.get("username").toString();
                                        llNoUser.setVisibility(View.GONE);
                                        userNb.setText("牛币:" + nb);
                                        tvTel.setText("手机号:" + username);
                                    } catch (Exception e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                LoadingUtil.hide();
                            }
                        });

                    } else {
                        LoadingUtil.hide();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            });


        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    public int getLayoutId() {
        return R.layout.fragment_nb_compense;
    }

    private void initView(View view) {
        title.setText("牛币补偿");
        edit_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (delayRun != null) {
                        mHandler.removeCallbacks(delayRun);
                    }
                    editString = s.toString().trim();
                    mHandler.postDelayed(delayRun, 200);
                }
            }
        });
    }

    private void showSimpleBottomSheetList() {
        new QMUIBottomSheet.BottomListSheetBuilder(getActivity())
                .addItem("20")
                .addItem("50")
                .addItem("60")
                .addItem("100")
                .addItem("150")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        paySumMoney = Double.parseDouble(tag);
                        tvRechargeMoney.setText(paySumMoney + "个牛币");
                    }
                })
                .build()
                .show();
    }

    public static CompenseFragment newInstance() {
        CompenseFragment compenseFragment = new CompenseFragment();
        return compenseFragment;
    }

    private void toRechargeEnsure() {
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle("支付信息确认")
                .setMessage("确定充值" + paySumMoney + "牛币？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(0, "确定", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        LoadingUtil.show(getContext(), "加载中");
                        Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().nbCompense(userId,
                                new SharedHelper().read("cashierId"),
                                new SharedHelper().read("cashierId"),
                                paySumMoney,
                                0.0,
                                1,
                                2,
                                1,
                                ((RadioButton) view.findViewById(rgPaystyle.getCheckedRadioButtonId())).getText().toString());
                        responseBodyCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                LoadingUtil.hide();
                                if (response.code() == 200 || response.code() == 201) {
                                    Toast.makeText(MyApplication.getContextObject(), "充值成功", Toast.LENGTH_SHORT).show();
                                    cardRechargeMoney.setVisibility(View.INVISIBLE);
                                    gvPayment.setVisibility(View.INVISIBLE);
                                    btnRecharge.setVisibility(View.INVISIBLE);
                                    freshUserInfo();
                                } else {
                                    T.show(response);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                LoadingUtil.hide();
                                T.L(t.getMessage());
                            }
                        });
                    }
                })
                .create(mCurrentDialogStyle).show();
    }


    @OnClick({R.id.tv_back, R.id.rl_recharge, R.id.btn_recharge, R.id.btn_refrsh, R.id.reset_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                pop();
                break;
            case R.id.rl_recharge:
                showSimpleBottomSheetList();
                break;
            case R.id.btn_recharge:
                toRechargeEnsure();
                break;
            case R.id.btn_refrsh:
                freshUserInfo();
                break;
            case R.id.reset_data:
                clearInfo();
                break;
        }
    }

    private void freshUserInfo() {
        LoadingUtil.show(getContext(), "加载中");
        AVQuery<AVObject> user = new AVQuery<>("_User");
        user.getInBackground(userId, new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().QueryofflineRecharge(avObject.getObjectId());
                    responseBodyCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            LoadingUtil.hide();
                            if (response.code() == 200 || response.code() == 201) {
                                llNoUser.setVisibility(View.GONE);
                                tvTel.setText("用户手机号:" + avObject.getString("username"));
                                try {
                                    String responseText = DataUtil.JSONTokener(response.body().string());
                                    JSONObject jsonObject = null;
                                    jsonObject = new JSONObject(responseText);
                                    Double nb = jsonObject.getDouble("amount");
                                    userNb.setText("牛币:" + nb);
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            LoadingUtil.hide();
                        }
                    });

                } else {
                    LoadingUtil.hide();
                    ToastUtil.showShort(MyApplication.getContextObject(), "网络错误");
                }
            }
        });
    }

    private void clearInfo() {
        cardRechargeMoney.setVisibility(View.VISIBLE);
        gvPayment.setVisibility(View.VISIBLE);
        btnRecharge.setVisibility(View.VISIBLE);
        llNoUser.setVisibility(View.VISIBLE);
        userId = "";
        marketId = "";
        username = "";
        paySumMoney = 20.0;
        tvRechargeMoney.setText("20个牛币");
        rgPaystyle.check(R.id.reason_1);
    }

}

