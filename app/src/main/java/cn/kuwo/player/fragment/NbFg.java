package cn.kuwo.player.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.bean.UserBean;
import cn.kuwo.player.custom.FlowRadioGroup;
import cn.kuwo.player.custom.ScanUserFragment;
import cn.kuwo.player.print.Bill;
import cn.kuwo.player.util.ApiManager;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.CameraProvider;
import cn.kuwo.player.util.DataUtil;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.T;
import cn.kuwo.player.util.ToastUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lovely on 2018/6/27
 */
public class NbFg extends BaseFragment {
    private static String ARG_PARAM = "userId";
    @BindView(R.id.btn_scan_user)
    Button btnScanUser;
    @BindView(R.id.ll_no_user)
    LinearLayout llNoUser;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.tv_stored)
    TextView tvStored;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.tv_meatweight)
    TextView tvMeatweight;
    @BindView(R.id.tv_is_buy)
    TextView tvIsBuy;
    @BindView(R.id.tv_is_svip)
    TextView tvIsSvip;
    @BindView(R.id.tv_recharge_money)
    TextView tvRechargeMoney;
    @BindView(R.id.rl_recharge)
    RelativeLayout rlRecharge;
    @BindView(R.id.card_recharge_money)
    CardView cardRechargeMoney;
    @BindView(R.id.pay_ali)
    RadioButton payAli;
    @BindView(R.id.pay_wx)
    RadioButton payWx;
    @BindView(R.id.pay_card)
    RadioButton payCard;
    @BindView(R.id.pay_cash)
    RadioButton payCash;
    @BindView(R.id.rg_paystyle)
    FlowRadioGroup rgPaystyle;
    @BindView(R.id.btn_recharge)
    Button btnRecharge;
    @BindView(R.id.btn_refrsh)
    Button btnRefrsh;
    @BindView(R.id.reset_data)
    Button resetData;
    Unbinder unbinder;
    @BindView(R.id.gv_payment)
    CardView gvPayment;
    Unbinder unbinder1;
    @BindView(R.id.user_nb)
    TextView userNb;
    Unbinder unbinder2;
    @BindView(R.id.tv_paymoney)
    TextView tvPaymoney;
    @BindView(R.id.gv_paymoney)
    CardView gvPaymoney;
    private Activity mActivity;
    private String mParam;
    private String userId = "";
    private String marketId = "";
    private String username = "";
    private String marketName = "";
    private int escrow = 3;
    private int REQUEST_CODE_SCAN = 111;
    private int REQUEST_CODE_SCAN_USER = 112;
    private Double rechargeMoney = 2000.0;
    private Double paySumMoney = 2000.0;
    //选择充值列表
    private Double[] chooseList = new Double[]{500.0, 2000.0, 5000.0, 10000.0};
    //实际充值列表
    private Double[] rechargeList = new Double[]{500.0, 2000.0, 5000.0, 10000.0};
    //实际付款金额
    private Double[] payList = new Double[]{550.0, 2200.0, 5500.0, 11000.0};
    private int ChooseIndex = 0;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fg_nb;
    }

    @Override
    public void initData() {
        setUserInfo();
        rechargeMoney = chooseList[ChooseIndex];
        paySumMoney = payList[ChooseIndex];
        tvRechargeMoney.setText(rechargeMoney + "个牛币");
        tvPaymoney.setText("需要支付的金额" + paySumMoney + "元");
        rgPaystyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.pay_ali:
                        escrow = 3;
                        break;
                    case R.id.pay_wx:
                        escrow = 4;
                        break;
                    case R.id.pay_card:
                        escrow = 5;
                        break;
                    case R.id.pay_cash:
                        escrow = 6;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void setUserInfo() {
        if (!mParam.equals("")) {
            userId = mParam;
            freshUserInfo();
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mParam = getArguments().getString(ARG_PARAM);  //获取参数
    }

    public static NbFg newInstance(String str) {
        NbFg nbFg = new NbFg();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM, str);
        nbFg.setArguments(bundle);
        return nbFg;
    }

    @OnClick({R.id.btn_scan_user, R.id.rl_recharge, R.id.btn_recharge, R.id.btn_refrsh, R.id.reset_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scan_user:
                if (CameraProvider.hasCamera() && !SharedHelper.readBoolean("useGun")) {
                    if (MyUtils.getCameraPermission(MyApplication.getContextObject())) {
                        Intent intent = new Intent(getActivity(), CaptureActivity.class);
                        intent.putExtra(Constant.INTENT_ZXING_CONFIG, MyUtils.caremaSetting());
                        startActivityForResult(intent, REQUEST_CODE_SCAN);
                    }
                } else {
                    ScanUserFragment scanUserFragment = new ScanUserFragment(1);
                    scanUserFragment.show(getFragmentManager(), "scanuser");
                }
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
        showDialog();
        AVQuery<AVObject> user = new AVQuery<>("_User");
        user.getInBackground(userId, new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    Map<String, Object> parameter = new HashMap<String, Object>();
                    parameter.put("userID", avObject.getObjectId());
                    AVCloud.callFunctionInBackground("svip", parameter, new FunctionCallback<Map<String, Object>>() {
                        @Override
                        public void done(final Map<String, Object> objectMap, AVException e) {
                            if (e == null) {
                                Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().QueryofflineRecharge(avObject.getObjectId());
                                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.code() == 200 || response.code() == 201) {
                                            llNoUser.setVisibility(View.GONE);
                                            tvTel.setText("用户手机号:" + avObject.getString("username"));
                                            Double storedBalance = MyUtils.formatDouble(avObject.getDouble("stored"));
                                            tvStored.setText("消费金:" + storedBalance);
                                            tvBalance.setText("白条:" + MyUtils.formatDouble(avObject.getDouble("gold") - avObject.getDouble("arrears")));
                                            tvMeatweight.setText("牛肉额度:" + objectMap.get("meatWeight").toString() + "kg");
                                            if ((Boolean) objectMap.get("alreadySVIP")) {
                                                tvIsBuy.setText("是否购买过超牛会员:已购买过");
                                            } else {
                                                tvIsBuy.setText("是否购买过超牛会员:未购买过");
                                            }
                                            if ((Boolean) objectMap.get("svip")) {
                                                tvIsSvip.setText("会员类型:超牛会员");
                                            } else {
                                                tvIsSvip.setText("会员类型:普通会员");
                                            }

                                            try {
                                                String responseText = DataUtil.JSONTokener(response.body().string());
                                                JSONObject jsonObject = null;
                                                jsonObject = new JSONObject(responseText);
                                                Double nb = jsonObject.getDouble("amount");
                                                userNb.setText("牛币:" + nb);
                                            } catch (Exception e1) {
                                                e1.printStackTrace();
                                            }

                                            hideDialog();
                                        } else {
                                            hideDialog();
                                            T.show(response);
                                            tvTel.setText("用户手机号:" + avObject.getString("username"));
                                            Double storedBalance = MyUtils.formatDouble(avObject.getDouble("stored"));
                                            tvStored.setText("消费金:" + storedBalance);
                                            tvBalance.setText("白条:" + MyUtils.formatDouble(avObject.getDouble("gold") - avObject.getDouble("arrears")));
                                            tvMeatweight.setText("牛肉额度:" + objectMap.get("meatWeight").toString() + "kg");
                                            if ((Boolean) objectMap.get("alreadySVIP")) {
                                                tvIsBuy.setText("是否购买过超牛会员:已购买过");
                                            } else {
                                                tvIsBuy.setText("是否购买过超牛会员:未购买过");
                                            }
                                            if ((Boolean) objectMap.get("svip")) {
                                                tvIsSvip.setText("会员类型:超牛会员");
                                            } else {
                                                tvIsSvip.setText("会员类型:普通会员");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });

                            } else {
                                hideDialog();
                                ToastUtil.showShort(MyApplication.getContextObject(), "网络错误");
                            }
                        }
                    });
                } else {
                    hideDialog();
                    ToastUtil.showShort(MyApplication.getContextObject(), "网络错误");
                }
            }
        });
    }

    private void clearInfo() {
        cardRechargeMoney.setVisibility(View.VISIBLE);
        gvPaymoney.setVisibility(View.VISIBLE);
        gvPayment.setVisibility(View.VISIBLE);
        btnRecharge.setVisibility(View.VISIBLE);
        llNoUser.setVisibility(View.VISIBLE);
        userId = "";
        marketId = "";
        username = "";
        escrow = 3;
        ChooseIndex=0;
        rechargeMoney = chooseList[ChooseIndex];
        paySumMoney = payList[ChooseIndex];
        tvRechargeMoney.setText(rechargeMoney + "个牛币");
        tvPaymoney.setText("需要支付的金额" + paySumMoney + "元");
        rgPaystyle.check(R.id.pay_ali);
    }

    private void toRechargeEnsure() {
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle("支付信息确认")
                .setMessage("确定收款" + paySumMoney + "元成功？")
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
                        if (CameraProvider.hasCamera() && !SharedHelper.readBoolean("useGun")) {
                            if (MyUtils.getCameraPermission(MyApplication.getContextObject())) {
                                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                                intent.putExtra(Constant.INTENT_ZXING_CONFIG, MyUtils.caremaSetting());
                                startActivityForResult(intent, REQUEST_CODE_SCAN_USER);
                            }
                        } else {
                            ScanUserFragment scanUserFragment = new ScanUserFragment(2);
                            scanUserFragment.show(getFragmentManager(), "scanuser");
                        }
                    }
                })
                .create(mCurrentDialogStyle).show();
    }

    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                showDialog();
                String code = data.getStringExtra(Constant.CODED_CONTENT);
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("payCode", code.trim());
                AVCloud.callFunctionInBackground("payCodeGetUser", parameters, new FunctionCallback<Map<String, Object>>() {
                    @Override
                    public void done(final Map<String, Object> object, AVException e) {
                        if (e == null) {
                            Map<String, Object> parameter = new HashMap<String, Object>();
                            parameter.put("userID", object.get("objectId").toString());
                            AVCloud.callFunctionInBackground("svip", parameter, new FunctionCallback<Map<String, Object>>() {
                                @Override
                                public void done(final Map<String, Object> objectMap, AVException e) {
                                    Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().QueryofflineRecharge(object.get("objectId").toString());
                                    responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if (response.code() == 200 || response.code() == 201) {
                                                hideDialog();
                                                try {
                                                    String responseText = DataUtil.JSONTokener(response.body().string());
                                                    JSONObject jsonObject = new JSONObject(responseText);
                                                    Double nb = jsonObject.getDouble("amount");
                                                    userNb.setText("牛币:" + nb);
                                                    llNoUser.setVisibility(View.GONE);
                                                    username = object.get("username").toString();
                                                    tvTel.setText("用户手机号:" + object.get("username").toString());
                                                    Double storedBalance = MyUtils.formatDouble(Double.parseDouble(object.get("stored").toString()));
                                                    tvStored.setText("消费金:" + storedBalance);
                                                    tvBalance.setText("白条:" + MyUtils.formatDouble(Double.parseDouble(object.get("gold").toString()) - Double.parseDouble(object.get("arrears").toString())));
                                                    tvMeatweight.setText("牛肉额度:" + objectMap.get("meatWeight").toString() + "kg");
                                                    userId = object.get("objectId").toString();
                                                    if ((Boolean) objectMap.get("alreadySVIP")) {
                                                        tvIsBuy.setText("是否购买过超牛会员:已购买过");
                                                    } else {
                                                        tvIsBuy.setText("是否购买过超牛会员:未购买过");
                                                    }
                                                    if ((Boolean) objectMap.get("svip")) {
                                                        tvIsSvip.setText("会员类型:超牛会员");
                                                    } else {
                                                        tvIsSvip.setText("会员类型:普通会员");
                                                    }
                                                } catch (Exception e1) {
                                                    e1.printStackTrace();
                                                }

                                            } else {
                                                hideDialog();
                                                T.show(response);
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            hideDialog();
                                            ToastUtil.showShort(getContext(), t.getMessage() + "网络异常,请刷新重试");
                                        }
                                    });

                                }
                            });
                        } else {
                            hideDialog();
                            ToastUtil.showShort(getContext(), e.getMessage());
                        }
                    }
                });

            }
        } else if (requestCode == REQUEST_CODE_SCAN_USER && resultCode == RESULT_OK) {
            if (data != null) {
                showDialog();
                String code = data.getStringExtra(Constant.CODED_CONTENT);
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("payCode", code.trim());
                AVCloud.callFunctionInBackground("payCodeGetUser", parameters, new FunctionCallback<Map<String, Object>>() {
                    @Override
                    public void done(Map<String, Object> objectMap, AVException e) {
                        if (e == null) {
                            if (Integer.parseInt(objectMap.get("clerk").toString()) > 0 || (Boolean) objectMap.get("Test")) {
                                marketId = objectMap.get("objectId").toString();
                                marketName = objectMap.get("realName") == null ? objectMap.get("nickName").toString() : objectMap.get("realName").toString();
                                toRecharge();
                            } else {
                                hideDialog();
                                ToastUtil.showShort(MyApplication.getContextObject(), "非销售账号,请扫描销售账号");
                            }
                        }

                    }
                });
            }
        }
    }

    private void toRecharge() {
        showDialog();
        int payment = 1;
        if (escrow == 3) {
            payment = 1;
        } else if (escrow == 4) {
            payment = 2;
        } else if (escrow == 5) {
            payment = 3;
        } else if (escrow == 6) {
            payment = 4;
        }
        Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().offlineRecharge(userId,
                marketId,
                SharedHelper.read("cashierId"),
                rechargeList[ChooseIndex],
                paySumMoney,
                payment,
                2,
                0);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideDialog();
                if (response.code() == 200 || response.code() == 201) {
                    Toast.makeText(MyApplication.getContextObject(), "充值成功", Toast.LENGTH_SHORT).show();
                    cardRechargeMoney.setVisibility(View.INVISIBLE);
                    gvPaymoney.setVisibility(View.INVISIBLE);
                    gvPayment.setVisibility(View.INVISIBLE);
                    btnRecharge.setVisibility(View.INVISIBLE);
                    Bill.printNb(MyApplication.getContextObject(),
                            username,
                            rechargeList[ChooseIndex],
                            escrow,
                            SharedHelper.read("cashierName"),
                            marketName,
                            paySumMoney);
                    freshUserInfo();
                } else {
                    T.show(response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideDialog();
                T.L(t.getMessage());
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UserMessgae(UserBean userBean) {
        if (userBean.getCallbackCode() == CONST.UserCode.SCANCUSTOMER) {
            llNoUser.setVisibility(View.GONE);
            username = userBean.getUsername();
            tvTel.setText("用户手机号:" + userBean.getUsername());
            tvStored.setText("消费金:" + userBean.getStored());
            tvBalance.setText("白条:" + userBean.getBalance());
            tvMeatweight.setText("牛肉额度:" + userBean.getMeatWeight() + "kg");
            userId = userBean.getId();
            if ((Boolean) userBean.getAlreadySvip()) {
                tvIsBuy.setText("是否购买过超牛会员:已购买过");
            } else {
                tvIsBuy.setText("是否购买过超牛会员:未购买过");
            }
            if (userBean.getSvip()) {
                tvIsSvip.setText("会员类型:超牛会员");
            } else {
                tvIsSvip.setText("会员类型:普通会员");
            }
            userNb.setText("牛币:" + userBean.getNb());
        } else if (userBean.getCallbackCode() == CONST.UserCode.SCANUSER) {
            if (userBean.getClerk() > 0 || userBean.getTest()) {
                marketId = userBean.getId();
                marketName = userBean.getRealName();
                toRecharge();
            } else {
                ToastUtil.showShort(MyApplication.getContextObject(), "非销售账号,请扫描销售账号");
            }
        }
    }

    private void showSimpleBottomSheetList() {
        QMUIBottomSheet.BottomListSheetBuilder bottomListSheetBuilder = new QMUIBottomSheet.BottomListSheetBuilder(getActivity());
        for (int i=0;i<chooseList.length;i++){
            bottomListSheetBuilder.addItem("充值"+chooseList[i]+"个牛币");
        }
        bottomListSheetBuilder.setTitle("选择充值牛币的数量")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        ChooseIndex=position;
                        rechargeMoney = chooseList[ChooseIndex];
                        paySumMoney = payList[ChooseIndex];
                        tvRechargeMoney.setText(rechargeMoney+"个牛币");
                        tvPaymoney.setText("需要支付的金额"+paySumMoney+"元");
                    }
                })
                .build()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder2 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder2.unbind();
    }
}
