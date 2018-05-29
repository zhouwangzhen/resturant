package cn.kuwo.player.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.custom.FlowRadioGroup;
import cn.kuwo.player.print.Bill;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.CameraProvider;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.ToastUtil;

import static android.app.Activity.RESULT_OK;

public class SvipFg extends BaseFragment {
    private static String ARG_PARAM = "param_key";
    @BindView(R.id.tv_final_money)
    TextView tvFinalMoney;
    Unbinder unbinder;
    @BindView(R.id.btn_refrsh)
    Button btnRefrsh;
    @BindView(R.id.recharge_content)
    LinearLayout rechargeContent;
    private int REQUEST_CODE_SCAN = 111;
    private int REQUEST_CODE_SCAN_USER = 112;
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
    @BindView(R.id.vipdate_1)
    RadioButton vipdate1;
    @BindView(R.id.vipdate_12)
    RadioButton vipdate12;
    @BindView(R.id.rg_vipstyle)
    FlowRadioGroup rgVipstyle;
    @BindView(R.id.pay_balance)
    RadioButton payBalance;
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
    @BindView(R.id.reset_data)
    TextView resetData;
    private Activity mActivity;
    private String mParam;
    private String userId = "";
    private String commodityId = CONST.SVIPSTYLE.DATE_12_MONTH;
    private Double commodityMoney = 6000.0;
    private String commodityContent="超牛会员1年";
    private int escrow = 3;
    private Double whiteBarBalance = 0.0;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fg_svip;
    }

    @Override
    public void initData() {
        rgPaystyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.pay_balance:
                        escrow = 11;
                        break;
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
        rgVipstyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.vipdate_1:
                        commodityId = CONST.SVIPSTYLE.DATE_11_MONTH;
                        commodityMoney = 600.0;
                        if (commodityMoney <= whiteBarBalance) {
                            payBalance.setVisibility(View.VISIBLE);
                        } else {
                            payBalance.setVisibility(View.GONE);
                        }
                        commodityContent="超牛会员1个月体验";
                        tvFinalMoney.setText(commodityMoney + "元");
                        break;
                    case R.id.vipdate_12:
                        commodityId = CONST.SVIPSTYLE.DATE_12_MONTH;
                        commodityMoney = 5000.0;
                        if (commodityMoney <= whiteBarBalance) {
                            payBalance.setVisibility(View.VISIBLE);
                        } else {
                            payBalance.setVisibility(View.GONE);
                        }
                        commodityContent="超牛会员1年";
                        tvFinalMoney.setText(commodityMoney + "元");
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

    public static SvipFg newInstance(String str) {
        SvipFg svipFg = new SvipFg();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM, str);
        svipFg.setArguments(bundle);
        return svipFg;
    }

    @OnClick({R.id.btn_scan_user, R.id.btn_recharge, R.id.reset_data, R.id.btn_refrsh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scan_user:
                if (CameraProvider.hasCamera()) {
                    if (MyUtils.getCameraPermission(MyApplication.getContextObject())) {
                        Intent intent = new Intent(getActivity(), CaptureActivity.class);
                        intent.putExtra(Constant.INTENT_ZXING_CONFIG, MyUtils.caremaSetting());
                        startActivityForResult(intent, REQUEST_CODE_SCAN);
                    }
                }
                break;
            case R.id.btn_recharge:
                payAndPrint();
                break;
            case R.id.reset_data:
                reset();
                llNoUser.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_refrsh:
                getUserInfo();
                break;
        }
    }

    /**
     * 刷新用户信息
     */
    private void getUserInfo() {
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
                                whiteBarBalance = MyUtils.formatDouble(avObject.getDouble("gold") - avObject.getDouble("arrears"));
                                Double storedBalance = MyUtils.formatDouble(avObject.getDouble("stored"));
                                tvStored.setText("消费金:" + storedBalance);
                                tvBalance.setText("白条:" + whiteBarBalance);
                                tvMeatweight.setText("牛肉额度:" + objectMap.get("meatWeight").toString() + "kg");
                                if ((Boolean) objectMap.get("alreadySVIP")) {
                                    tvIsBuy.setText("是否购买过超牛会员:已购买过");
                                    vipdate1.setVisibility(View.GONE);
                                } else {
                                    tvIsBuy.setText("是否购买过超牛会员:未购买过");
                                    vipdate1.setVisibility(View.VISIBLE);
                                }
                                if ((Boolean) objectMap.get("svip")) {
                                    tvIsSvip.setText("会员类型:超牛会员");
                                } else {
                                    tvIsSvip.setText("会员类型:普通会员");
                                }
                                if (commodityMoney<=whiteBarBalance){
                                    payBalance.setVisibility(View.VISIBLE);
                                }else{
                                    payBalance.setVisibility(View.GONE);
                                }
                                vipdate12.setChecked(true);
                                rechargeContent.setVisibility(View.VISIBLE);
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

    /**
     * 重置信息
     */
    private void reset() {
        escrow = 3;
        whiteBarBalance = 0.0;
        userId = "";
        commodityId = CONST.SVIPSTYLE.DATE_12_MONTH;
        commodityMoney = 6000.0;
        rechargeContent.setVisibility(View.GONE);
    }

    /**
     * 充值订单
     */
    private void PayAndBind() {
        showDialog();
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (escrow == 11) {
            parameters.put("paymentType", "577b364a79bc440032772ba5");
        } else {
            parameters.put("paymentType", "59794daf128fe10056f43170");
        }
        String[] commodity = new String[1];
        commodity[0] = commodityId;
        parameters.put("commodityids", commodity);
        parameters.put("paysum", commodityMoney);
        parameters.put("customerId", userId);
        parameters.put("sum", commodityMoney);
        AVCloud.callFunctionInBackground("offlineMallOrder", parameters, new FunctionCallback<Map<String, Map<String, Object>>>() {
            @Override
            public void done(Map<String, Map<String, Object>> map, AVException e) {
                if (e == null) {
                    final String orderId = map.get("order").get("objectId").toString();
                    AVObject mallOrder = AVObject.createWithoutData("MallOrder", orderId);
                    mallOrder.put("cashier", AVObject.createWithoutData("_User", new SharedHelper(getContext()).read("cashierId")));
                    mallOrder.put("market", AVObject.createWithoutData("_User", new SharedHelper(getContext()).read("cashierId")));
                    mallOrder.put("orderStatus", AVObject.createWithoutData("MallOrderStatus", CONST.OrderState.ORDER_STATUS_FINSIH));
                    mallOrder.put("escrow", escrow);
                    mallOrder.put("store", CONST.STORECODE);
                    mallOrder.put("reduce", 0);
                    mallOrder.put("offline", true);
                    mallOrder.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                bindPower(orderId);
                            } else {
                                hideDialog();
                                ToastUtil.showShort(MyApplication.getContextObject(), e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 绑定超牛会员
     */
    private void bindPower(String orderId) {
        AVObject power = new AVObject("Power");
        Calendar c;
        if (commodityId == CONST.SVIPSTYLE.DATE_1_MONTH) {
            power.put("multiple", 1);
            power.put("gold", 600);
            power.put("meatSum", 5);
            power.put("meatNum", 5);
            c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.MONTH, 1);
            power.put("endDate", c.getTime());
            power.put("type", AVObject.createWithoutData("PowerType", CONST.POWERTYLE.EXPERIENCE));
        } else if (commodityId == CONST.SVIPSTYLE.DATE_12_MONTH) {
            power.put("multiple", 1);
            power.put("gold", 5000);
            power.put("meatSum", 60);
            power.put("meatNum", 60);
            c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.MONTH, 12);
            power.put("endDate", c.getTime());
            power.put("type", AVObject.createWithoutData("PowerType", CONST.POWERTYLE.MEMBER));
        }
        power.put("active", 1);
        power.put("user", AVObject.createWithoutData("_User", userId));
        power.put("order", AVObject.createWithoutData("MallOrder", orderId));
        power.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    hideDialog();
                    btnRefrsh.setVisibility(View.VISIBLE);
                    ToastUtil.showShort(MyApplication.getContextObject(), "充值绑定成功");
                    Bill.printSvipBill(commodityContent,commodityMoney,0.0,commodityMoney,escrow);
                    reset();
                } else {
                    hideDialog();
                    ToastUtil.showShort(MyApplication.getContextObject(), e.getMessage());
                }
            }
        });
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
                                    Logger.d(objectMap);
                                    hideDialog();
                                    llNoUser.setVisibility(View.GONE);
                                    tvTel.setText("用户手机号:" + object.get("username").toString());
                                    whiteBarBalance = MyUtils.formatDouble(Double.parseDouble(object.get("gold").toString()) - Double.parseDouble(object.get("arrears").toString()));
                                    Double storedBalance = MyUtils.formatDouble(Double.parseDouble(object.get("stored").toString()));
                                    tvStored.setText("消费金:" + storedBalance);
                                    tvBalance.setText("白条:" + whiteBarBalance);
                                    tvMeatweight.setText("牛肉额度:" + objectMap.get("meatWeight").toString() + "kg");
                                    userId = object.get("objectId").toString();
                                    if ((Boolean) objectMap.get("alreadySVIP")) {
                                        tvIsBuy.setText("是否购买过超牛会员:已购买过");
                                        vipdate1.setVisibility(View.GONE);
                                    } else {
                                        tvIsBuy.setText("是否购买过超牛会员:未购买过");
                                        vipdate1.setVisibility(View.VISIBLE);
                                    }
                                    if ((Boolean) objectMap.get("svip")) {
                                        tvIsSvip.setText("会员类型:超牛会员");
                                    } else {
                                        tvIsSvip.setText("会员类型:普通会员");
                                    }
                                    Logger.d(commodityMoney);
                                    Logger.d(whiteBarBalance);
                                    if (commodityMoney<=whiteBarBalance){
                                        payBalance.setVisibility(View.VISIBLE);
                                    }else{
                                        payBalance.setVisibility(View.GONE);
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
                            if (Integer.parseInt(objectMap.get("clerk").toString()) > 6 || (Boolean) objectMap.get("test")) {
                                PayAndBind();
                            } else {
                                ToastUtil.showShort(MyApplication.getContextObject(), "非销售账号,请扫描销售账号");
                            }
                        }

                    }
                });
            }
        }
    }

    /**
     * 充值信息确认
     */
    private void payAndPrint() {
        if (escrow == 3 || escrow == 4 || escrow == 5 || escrow == 6) {
            new QMUIDialog.MessageDialogBuilder(getActivity())
                    .setTitle("支付信息确认")
                    .setMessage("确定收款" + commodityMoney + "元成功？")
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
                            Intent intent = new Intent(getActivity(), CaptureActivity.class);
                            intent.putExtra(Constant.INTENT_ZXING_CONFIG, MyUtils.caremaSetting());
                            startActivityForResult(intent, REQUEST_CODE_SCAN_USER);
                        }
                    })
                    .create(mCurrentDialogStyle).show();
        } else {
            new QMUIDialog.MessageDialogBuilder(getActivity())
                    .setTitle("支付信息确认")
                    .setMessage("确定使用白条" + commodityMoney + "元？")
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
                            Intent intent = new Intent(getActivity(), CaptureActivity.class);
                            intent.putExtra(Constant.INTENT_ZXING_CONFIG, MyUtils.caremaSetting());
                            startActivityForResult(intent, REQUEST_CODE_SCAN_USER);
                        }
                    })
                    .create(mCurrentDialogStyle).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
