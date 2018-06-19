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
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
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
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.CameraProvider;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.ToastUtil;

import static android.app.Activity.RESULT_OK;

public class StoredFg extends BaseFragment {
    private static String ARG_PARAM = "param_key";
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
    @BindView(R.id.recharge_content)
    TextView rechargeContent;
    Unbinder unbinder;
    @BindView(R.id.gv_payment)
    CardView gvPayment;
    Unbinder unbinder1;
    private Activity mActivity;
    private String mParam;
    private String userId = "";
    private String marketId = "";
    private String username = "";
    private String marketName="";
    private int escrow = 3;
    private int REQUEST_CODE_SCAN = 111;
    private int REQUEST_CODE_SCAN_USER = 112;
    private int rechargeMoney = 500;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fg_stored;
    }

    @Override
    public void initData() {
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
                }
            }
        });
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mParam = getArguments().getString(ARG_PARAM);  //获取参数
    }

    public static StoredFg newInstance(String str) {
        StoredFg storedFg = new StoredFg();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM, str);
        storedFg.setArguments(bundle);
        return storedFg;
    }


    @OnClick({R.id.btn_scan_user, R.id.rl_recharge, R.id.btn_recharge, R.id.btn_refrsh, R.id.reset_data})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scan_user:
                if (CameraProvider.hasCamera()) {
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
                        public void done(Map<String, Object> objectMap, AVException e) {
                            if (e == null) {
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
                                hideDialog();
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
        gvPayment.setVisibility(View.VISIBLE);
        btnRecharge.setVisibility(View.VISIBLE);
        llNoUser.setVisibility(View.VISIBLE);
        userId="";
        marketId="";
        username="";
        escrow=3;
        rechargeMoney=500;
        tvRechargeMoney.setText("500元");
        rechargeContent.setText("充值500得550");
        rgPaystyle.check(R.id.pay_ali);
    }

    private void toRechargeEnsure() {
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle("支付信息确认")
                .setMessage("确定收款" + rechargeMoney + "元成功？")
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
                        if (CameraProvider.hasCamera()) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                                public void done(Map<String, Object> objectMap, AVException e) {
                                    hideDialog();
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
                            if (Integer.parseInt(objectMap.get("clerk").toString()) > 0 || (Boolean) objectMap.get("test")) {
                                marketId = objectMap.get("objectId").toString();
                                marketName= objectMap.get("realName") == null ? objectMap.get("nickName").toString() : objectMap.get("realName").toString();
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
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("username", username);
        parameters.put("sum", rechargeMoney);
        parameters.put("cashier", AVObject.createWithoutData("_User", SharedHelper.read("cashierId")));
        parameters.put("market", marketId);
        parameters.put("key", "eNn59AK231DgUuVu");
        parameters.put("phoneRecharge", false);
        parameters.put("escrow", escrow);
        parameters.put("store",1);
        AVCloud.callFunctionInBackground("usernamePrepaid", parameters, new FunctionCallback<String>() {
            @Override
            public void done(String s, AVException e) {
                if (e == null) {
                    hideDialog();
                    Toast.makeText(MyApplication.getContextObject(), "充值成功", Toast.LENGTH_SHORT).show();
                    cardRechargeMoney.setVisibility(View.INVISIBLE);
                    gvPayment.setVisibility(View.INVISIBLE);
                    btnRecharge.setVisibility(View.INVISIBLE);
                    Bill.printRechargeStored(
                            MyApplication.getContextObject(),
                            username,
                            rechargeMoney,
                            escrow,
                            SharedHelper.read("cashierName"),
                            marketName
                    );
                    freshUserInfo();
                }else{
                    hideDialog();
                    ToastUtil.showShort(MyApplication.getContextObject(), "网络繁忙"+e.getMessage());
                }
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
        } else if (userBean.getCallbackCode() == CONST.UserCode.SCANUSER) {
            if (userBean.getClerk() > 0 || userBean.getTest()) {
                marketId = userBean.getId();
                marketName=userBean.getRealName();
                toRecharge();
            } else {
                ToastUtil.showShort(MyApplication.getContextObject(), "非销售账号,请扫描销售账号");
            }
        }
    }

    private void showSimpleBottomSheetList() {
        new QMUIBottomSheet.BottomListSheetBuilder(getActivity())
                .addItem("充值500得550")
                .addItem("充值2000得2500")
                .addItem("充值6000得8000")
                .addItem("充值10000得15000")
                .setTitle("选择充值金额")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                rechargeMoney = 500;
                                tvRechargeMoney.setText("500元");
                                rechargeContent.setText("充值500得550");
                                break;
                            case 1:
                                rechargeMoney = 2000;
                                tvRechargeMoney.setText("2000元");
                                rechargeContent.setText("充值2000得2500");
                                break;
                            case 2:
                                rechargeMoney = 6000;
                                tvRechargeMoney.setText("6000元");
                                rechargeContent.setText("充值6000得8000");
                                break;
                            case 3:
                                rechargeMoney = 10000;
                                tvRechargeMoney.setText("10000元");
                                rechargeContent.setText("充值10000得15000");
                                break;
                            default:
                                break;
                        }


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
}
