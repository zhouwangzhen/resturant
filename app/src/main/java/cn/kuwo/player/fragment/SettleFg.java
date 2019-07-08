package cn.kuwo.player.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.adapter.ShowGoodAdapter;
import cn.kuwo.player.api.CouponApi;
import cn.kuwo.player.api.HangUpApi;
import cn.kuwo.player.api.TableApi;
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.bean.FuncBean;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.RateBean;
import cn.kuwo.player.bean.UserBean;
import cn.kuwo.player.custom.CommomDialog;
import cn.kuwo.player.custom.PasswordDialog;
import cn.kuwo.player.custom.ScanUserFragment;
import cn.kuwo.player.custom.ShowCouponFragment;
import cn.kuwo.player.custom.ShowFuncFragment;
import cn.kuwo.player.custom.ShowWholeSaleFragment;
import cn.kuwo.player.event.CouponEvent;
import cn.kuwo.player.event.OrderDetail;
import cn.kuwo.player.interfaces.MyItemClickListener;
import cn.kuwo.player.print.Bill;
import cn.kuwo.player.util.ApiManager;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.CameraProvider;
import cn.kuwo.player.util.DataUtil;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.SpUtils;
import cn.kuwo.player.util.T;
import cn.kuwo.player.util.ToastUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class SettleFg extends BaseFragment {
    private static String tableId = "param_key";
    private static boolean isHangUp = false;
    private static String remark = "";
    @BindView(R.id.user_avatar)
    QMUIRadiusImageView userAvatar;
    @BindView(R.id.svip_avatar)
    ImageView svipAvatar;
    @BindView(R.id.user_type)
    TextView userType;
    @BindView(R.id.user_tel)
    TextView userTel;
    @BindView(R.id.user_whitebar)
    TextView userWhitebar;
    @BindView(R.id.user_stored)
    TextView userStored;
    @BindView(R.id.user_meatweight)
    TextView userMeatweight;
    @BindView(R.id.ll_show_member)
    LinearLayout llShowMember;
    @BindView(R.id.sign_user)
    Button signUser;
    @BindView(R.id.total_number)
    TextView totalNumber;
    @BindView(R.id.recycle_scan_good)
    RecyclerView recycleScanGood;
    @BindView(R.id.total_money)
    TextView totalMoney;
    @BindView(R.id.btn_pay)
    Button btnPay;
    Unbinder unbinder;
    @BindView(R.id.orgin_price)
    TextView orginPrice;
    @BindView(R.id.store_reduce_money)
    TextView storeReduceMoney;
    @BindView(R.id.tv_offline_moeny)
    TextView tvOfflineMoeny;
    @BindView(R.id.ll_offline_moeny)
    LinearLayout llOfflineMoeny;
    @BindView(R.id.tv_online_moeny)
    TextView tvOnlineMoeny;
    @BindView(R.id.ll_online_moeny)
    LinearLayout llOnlineMoeny;
    @BindView(R.id.tv_online_content)
    TextView tvOnlineContent;
    @BindView(R.id.tv_offline_content)
    TextView tvOfflineContent;
    @BindView(R.id.pay_content)
    TextView payContent;
    @BindView(R.id.table_number)
    TextView tableNumber;
    @BindView(R.id.full_reduce_money)
    TextView fullreduceMoney;
    @BindView(R.id.delete_odd_money)
    TextView deleteOddMoney;
    @BindView(R.id.ll_delete_odd)
    LinearLayout llDeleteOdd;
    @BindView(R.id.more_fuc)
    Button moreFuc;
    @BindView(R.id.rate_reduce_content)
    TextView rateReduceContent;
    @BindView(R.id.rate_reduce_money)
    TextView rateReduceMoney;
    @BindView(R.id.ll_rate_reduce)
    LinearLayout llRateReduce;
    @BindView(R.id.ll_store_reduce)
    LinearLayout llStoreReduce;
    @BindView(R.id.ll_full_reduce)
    LinearLayout llFullReduce;
    @BindView(R.id.store_reduce_rate)
    TextView storeReduceRate;
    @BindView(R.id.user_nb)
    TextView userNb;
    @BindView(R.id.choose_nb)
    TextView chooseNb;
    @BindView(R.id.nb_price)
    TextView nbPrice;
    @BindView(R.id.ll_no_nb)
    LinearLayout llNoNb;
    @BindView(R.id.user_charge_nb)
    TextView userChargeNb;
    @BindView(R.id.charge_deduce)
    TextView chargeDeduce;
    @BindView(R.id.ll_charge_deduce)
    LinearLayout llChargeDeduce;
    @BindView(R.id.cb_use_svip)
    CheckBox cbUseSvip;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private int REQUEST_CODE_SCAN = 111;
    private int REQUEST_FUNC = 100;
    private int REQUEST_RATE = 102;
    private int offlineCouponNumber = 0;
    private Activity mActivity;
    private AVObject tableAVObject;
    private String userId = "";
    private String useMeatId = "";
    private Double originTotalMoneny = 0.0;
    private Double actualTotalMoneny = 0.0;
    private Double offlineCouponMoney = 0.0;
    private Double onlineCouponMoney = 0.0;
    private Double meatReduceMoney = 0.0;
    private Double meatReduceWeight = 0.0;
    private Double myMeatReduceMoney = 0.0;
    private Double myMeatReduceWeight = 0.0;
    private Double activityReduceMoney = 0.0;
    private Double fullReduceMoney = 0.0;
    private Double hasMeatWeight = 0.0;
    private Double deleteoddMoney = 0.0;
    private int orderRate = 100;
    private String rateReduceRemark = "";
    private Double ratereduceMoney = 0.0;
    private CouponEvent onlineCouponEvent = null;
    private CouponEvent offlineCouponEvent = null;
    private Boolean isSvip = false;
    private Double nb = 0.0;
    private Double nbTotalMoney = 0.0;
    private List<Object> orders = new ArrayList<>();
    private List<Object> useExchangeList = new ArrayList<>();
    private List<Double> weights = new ArrayList<>();
    private List<Double> prices = new ArrayList<>();
    private HashMap<String, Object> otherTableOrders = new HashMap<>();
    private ArrayList<String> selectTableNumber = new ArrayList<>();
    private ArrayList<String> selectTableIds = new ArrayList<>();
    private boolean isNbPay = false;
    LinearLayoutManager linearLayoutManager;
    ShowGoodAdapter showGoodAdapter;
    private PasswordDialog mDialog;
    private String mPassword;

    @Override
    protected int getLayoutId() {
        return R.layout.fg_settle;
    }

    @Override
    public void initData() {
        showDialog();
        final AVQuery<AVObject> table;
        if (isHangUp) {
            table = new AVQuery<>("HangUpOrder");
        } else {
            table = new AVQuery<>("Table");
        }

        table.include("user");
        table.getInBackground(tableId, new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    tableAVObject = avObject;
                    tableNumber.setText("当前桌号:" + avObject.getString("tableNumber"));
                    if (tableAVObject.getAVObject("user") != null) {
                        Map<String, Object> parameter = new HashMap<String, Object>();
                        parameter.put("userID", avObject.getAVObject("user").getObjectId());
                        userId = avObject.getAVObject("user").getObjectId();
                        Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().QueryofflineRecharge(avObject.getAVObject("user").getObjectId());
                        responseBodyCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.code() == 200 || response.code() == 200) {
                                    try {
                                        String responseText = DataUtil.JSONTokener(response.body().string());
                                        JSONObject jsonObject = new JSONObject(responseText);
                                        nb = jsonObject.getDouble("amount");
                                        userNb.setText("牛币:" + nb);
                                        AVObject userObject = tableAVObject.getAVObject("user");
                                        llShowMember.setVisibility(View.VISIBLE);
                                        Double whiteBarBalance = MyUtils.formatDouble(MyUtils.formatDouble(userObject.getDouble("gold")) - MyUtils.formatDouble(userObject.getDouble("arrears")));
                                        Double storedBalance = MyUtils.formatDouble(userObject.getDouble("stored"));
                                        AVFile avatar = (AVFile) userObject.get("avatar");
                                        if (avatar != null) {
                                            Glide.with(MyApplication.getContextObject()).load(avatar.getUrl()).into(userAvatar);
                                        }
                                        userTel.setText("用户手机号:" + userObject.getString("username"));
                                        userStored.setText("消费金:" + storedBalance);
                                        userWhitebar.setText("白条:" + whiteBarBalance);
                                        signUser.setText("退出登录");
                                        fetchCommodity(tableAVObject);
                                        hideDialog();
                                    } catch (Exception e1) {
                                        hideDialog();
                                        Logger.d(e1.getMessage());
                                        ToastUtil.showShort(getContext(), e1.getMessage());
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
                    } else {
                        hideDialog();
                        fetchCommodity(tableAVObject);
                        llShowMember.setVisibility(View.INVISIBLE);
                        signUser.setText("用户登录");
                    }

                } else {
                    hideDialog();
                }

            }
        });
        setListener();
        mPassword = SpUtils.getString("password", "", SpUtils.KEY_ACCOUNT);
        if (TextUtils.isEmpty(mPassword)) {
            ToastUtil.showLong(mActivity, "请先设置密码");
        }
    }

    private void showPasswordDialog() {
        if (mDialog == null) {
            mDialog = new PasswordDialog(mActivity, R.style.dialog);
            mDialog.setTitle("请输入密码")
                    .setPasswordHint("请输入6位数字密码")
                    .setSingle(true)
                    .show();
            mDialog.setListener((password, oldPassword) -> {
                        if (TextUtils.isEmpty(mPassword)) {
                            ToastUtil.showLong(mActivity, "请先设置密码");
                        } else {
                            if (mPassword.equals(password)) {
                                mDialog.dismiss();
                            } else {
                                ToastUtil.showShort(mActivity, "密码错误，请重新输入");
                            }
                        }
                    });
        } else {
            mDialog.show();
        }

    }

    private void setListener() {
        llOfflineMoeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStoreCoupon();
            }
        });
        llOnlineMoeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserCoupon();

            }
        });
    }

    private void getStoreCoupon() {
        CouponApi.getCouponOffline().findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    ShowCouponFragment showCouponFragment = new ShowCouponFragment(list, actualTotalMoneny + offlineCouponMoney, 2);
                    showCouponFragment.show(getActivity().getSupportFragmentManager(), "showpcoupon");
                } else {
                    ToastUtil.showShort(MyApplication.getContextObject(), "网络错误");

                }
            }
        });
    }

    private void getUserCoupon() {
        if (tableAVObject.getAVObject("user") != null) {
            showDialog();
            CouponApi.getCouponOnline(tableAVObject.getAVObject("user").getString("username")).findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    hideDialog();
                    if (e == null) {
                        ShowCouponFragment showCouponFragment = new ShowCouponFragment(list, actualTotalMoneny, 1);
                        showCouponFragment.show(getActivity().getSupportFragmentManager(), "showpcoupon");
                    } else {
                        ToastUtil.showShort(MyApplication.getContextObject(), "网络错误");
                    }
                }
            });
        } else {
            ToastUtil.showShort(MyApplication.getContextObject(), "请用户先登录后查看");
        }
    }

    public static SettleFg newInstance(String str, Boolean isHangUp) {
        SettleFg settleFg = new SettleFg();
        Bundle bundle = new Bundle();
        bundle.putString(tableId, str);
        bundle.putBoolean("isHangUp", isHangUp);
        settleFg.setArguments(bundle);
        return settleFg;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        tableId = getArguments().getString(tableId);  //获取参数
        isHangUp = getArguments().getBoolean("isHangUp");
    }

    private void fetchCommodity(AVObject tableAVObject) {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recycleScanGood.setLayoutManager(linearLayoutManager);
        try {
            orders = ObjectUtil.deepCopy(tableAVObject.getList("order"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        refreshList();

    }

    /**
     * 刷新数据
     */
    private void refreshList() {
        originTotalMoneny = 0.0;
        meatReduceMoney = 0.0;
        offlineCouponMoney = 0.0;
        onlineCouponMoney = 0.0;
        activityReduceMoney = 0.0;
        myMeatReduceMoney = 0.0;
        myMeatReduceWeight = 0.0;
        fullReduceMoney = 0.0;
        try {
            orders = ObjectUtil.deepCopy(tableAVObject.getList("order"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Double noDiscountMoney = ProductUtil.calNoDiscountMoney(tableAVObject.getList("order"));
        orders = ProductUtil.calOtherOder(orders, otherTableOrders);
        if (orders.size() != tableAVObject.getList("order").size()) {
            String tableContent = "当前桌号:" + tableAVObject.getString("tableNumber");
            for (int i = 0; i < selectTableNumber.size(); i++) {
                tableContent += "+" + selectTableNumber.get(i);
            }
            tableNumber.setText(tableContent);
        } else {
            tableNumber.setText("当前桌号:" + tableAVObject.getString("tableNumber"));
        }
        showGoodAdapter = new ShowGoodAdapter(getContext(), tableAVObject, orders);
        recycleScanGood.setAdapter(showGoodAdapter);
        showGoodAdapter.setOnItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                changeCommodity(postion);
            }
        });
        totalNumber.setText(orders.size() + "");
        originTotalMoneny = ProductUtil.calculateTotalMoney(orders, new ArrayList<Object>());
        orginPrice.setText(originTotalMoneny + "");
        nbTotalMoney = ProductUtil.calNbTotalMoney(orders);
        nbPrice.setText(nbTotalMoney + "");
        if (nb >= nbTotalMoney && tableAVObject.get("user") != null) {
            chooseNb.setVisibility(View.VISIBLE);
        }
        if (!isNbPay) {
            meatReduceMoney = ProductUtil.calMeatduceMoney(orders, prices);
            meatReduceWeight = ProductUtil.calMeatduceWeight(orders, weights);
            if (onlineCouponEvent != null) {
                onlineCouponMoney = onlineCouponEvent.getMoney();
                tvOnlineMoeny.setText("-" + onlineCouponMoney);
                tvOnlineContent.setText(onlineCouponEvent.getContent());
            }
            if (offlineCouponEvent != null) {
                offlineCouponMoney = offlineCouponEvent.getMoney() * offlineCouponNumber;
                tvOfflineMoeny.setText("-" + offlineCouponMoney);
                tvOfflineContent.setText(offlineCouponEvent.getContent() + "*" + offlineCouponNumber + "张");
            }

            if (tableAVObject.getAVObject("user") != null) {
                activityReduceMoney = MyUtils.formatDouble((originTotalMoneny - offlineCouponMoney - onlineCouponMoney - noDiscountMoney) * (1 - MyUtils.getDayRate()));
                if (activityReduceMoney < 0) activityReduceMoney = 0.0;
            }
            actualTotalMoneny = originTotalMoneny - offlineCouponMoney - onlineCouponMoney - activityReduceMoney;
            if (actualTotalMoneny < 0) actualTotalMoneny = 0.0;
            totalMoney.setText("￥" + actualTotalMoneny + "元");
            storeReduceMoney.setText("-" + activityReduceMoney);

            fullReduceMoney = MyUtils.formatDouble(ProductUtil.calFullReduceMoney(actualTotalMoneny) > actualTotalMoneny ? actualTotalMoneny : ProductUtil.calFullReduceMoney(actualTotalMoneny));
            fullreduceMoney.setText("-" + fullReduceMoney);
            actualTotalMoneny -= fullReduceMoney;
            if (orderRate != 100) {
                llRateReduce.setVisibility(View.VISIBLE);
                ratereduceMoney = MyUtils.formatDouble(((double) actualTotalMoneny) * (100 - orderRate) / 100);
                actualTotalMoneny -= ratereduceMoney;
                rateReduceContent.setText("整单" + MyUtils.formatDouble((double) orderRate / 10) + "折优惠" + "(" + rateReduceRemark + ")");
                rateReduceMoney.setText("-" + ratereduceMoney);
            } else {
                ratereduceMoney = 0.0;
                llRateReduce.setVisibility(View.GONE);
            }
            if (deleteoddMoney > 0) {
                llDeleteOdd.setVisibility(View.VISIBLE);
                deleteOddMoney.setText("-" + deleteoddMoney);
                actualTotalMoneny -= deleteoddMoney;

            } else {
                llDeleteOdd.setVisibility(View.GONE);
                deleteOddMoney.setText("0");
            }
            if (activityReduceMoney > 0) {
                storeReduceRate.setText("开业" + MyUtils.getDayRate() + "折优惠");
                llStoreReduce.setVisibility(View.VISIBLE);
            } else {
                llStoreReduce.setVisibility(View.GONE);
            }
            if (fullReduceMoney > 0) {
                llFullReduce.setVisibility(View.VISIBLE);
            } else {
                llFullReduce.setVisibility(View.GONE);
            }
            actualTotalMoneny = MyUtils.formatDouble(actualTotalMoneny) >= 0 ? MyUtils.formatDouble(actualTotalMoneny) : 0.0;
            totalMoney.setText("￥" + actualTotalMoneny + "元");
        } else {
            actualTotalMoneny = nbTotalMoney;
            totalMoney.setText("￥" + actualTotalMoneny + "牛币");
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

    @OnClick({R.id.sign_user, R.id.btn_pay, R.id.more_fuc, R.id.choose_nb, R.id.user_charge_nb})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_user:
                if (tableAVObject != null) {
                    if (tableAVObject.getAVObject("user") != null) {
                        new QMUIDialog.MessageDialogBuilder(getActivity())
                                .setTitle("温馨提示")
                                .setMessage("确定要取消此订单的用户信息？")
                                .addAction("取消", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                    }
                                })
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                        showDialog();
                                        tableAVObject.put("user", null);
                                        tableAVObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                if (e == null) {
                                                    hideDialog();
                                                    chooseNb.setText("当前付款状态:正常支付(点击切换成牛币支付)");
                                                    llNoNb.setVisibility(View.VISIBLE);
                                                    isNbPay = false;
                                                    nb = 0.0;
                                                    chooseNb.setVisibility(View.GONE);
                                                    ToastUtil.showShort(MyApplication.getContextObject(), "清空用户数据成功");
                                                    initData();
                                                } else {
                                                    hideDialog();
                                                    ToastUtil.showShort(MyApplication.getContextObject(), "网络错误" + e.getMessage());
                                                }
                                            }
                                        });
                                    }
                                })
                                .create(mCurrentDialogStyle).show();

                    } else {
                        chooseScanType();
                    }

                }
                break;
            case R.id.btn_pay:
                showPasswordDialog();
                if (isNbPay) {
                    useNbPay();
                } else {
                    if (tableAVObject.getAVObject("user") != null && nb >= nbTotalMoney && !isNbPay) {
                        new CommomDialog(getContext(), R.style.dialog, "用户牛币充足,用牛币更优惠,确认不帮他使用么", new CommomDialog.OnCloseListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    skipPayPage();
                                    dialog.dismiss();
                                }

                            }
                        })
                                .setTitle("提示").setNegativeButton("去重新选择").setPositiveButton("有钱任性,就这么结账").show();
                    } else {
                        skipPayPage();
                    }

                }
                break;
            case R.id.more_fuc:
                ShowFuncFragment showFuncFragment = new ShowFuncFragment(0);
                showFuncFragment.setTargetFragment(this, REQUEST_FUNC);
                showFuncFragment.show(getFragmentManager(), "showfunc");
                break;
            case R.id.choose_nb:
                if (!isNbPay) {
                    chooseNb.setText("当前付款状态:使用牛币支付(点击切换成正常支付)");
                    llNoNb.setVisibility(View.GONE);
                } else {
                    chooseNb.setText("当前付款状态:正常支付(点击切换成牛币支付)");
                    llNoNb.setVisibility(View.VISIBLE);
                }
                isNbPay = !isNbPay;
                refreshList();
                break;
            case R.id.user_charge_nb:
                if (tableAVObject.getAVObject("user") != null) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    NbFg nbFg = NbFg.newInstance(tableAVObject.getAVObject("user").getObjectId());
                    ft.replace(R.id.fragment_content, nbFg, "nb").commit();
                }
                break;
        }
    }

    private void skipPayPage() {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        PayFg payFg = PayFg.newInstance("");
        Bundle bundle = new Bundle();
        OrderDetail orderDetail = new OrderDetail(tableAVObject, hasMeatWeight, originTotalMoneny,
                actualTotalMoneny, meatReduceWeight, meatReduceMoney, myMeatReduceWeight, myMeatReduceMoney, false,
                onlineCouponEvent, offlineCouponEvent, offlineCouponNumber, activityReduceMoney, isSvip, useExchangeList, useMeatId, ProductUtil.calExchangeMeatList(orders), orders, selectTableIds, selectTableNumber, fullReduceMoney, isHangUp, deleteoddMoney,
                orderRate, rateReduceRemark, ratereduceMoney);
        bundle.putSerializable("table", (Serializable) orderDetail);
        payFg.setArguments(bundle);
        ft.replace(R.id.fragment_content, payFg, "pay").commit();
    }


    /**
     * 判断是否有摄像头
     */
    private void chooseScanType() {
        if (CameraProvider.hasCamera()) {
            if (SharedHelper.readBoolean("useGun")) {
                ScanUserFragment scanUserFragment = new ScanUserFragment(1);
                scanUserFragment.show(getFragmentManager(), "scanuser");
            } else {
                if (MyUtils.getCameraPermission(getContext())) {
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    intent.putExtra(Constant.INTENT_ZXING_CONFIG, MyUtils.caremaSetting());
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }
            }
        } else {
            ScanUserFragment scanUserFragment = new ScanUserFragment(1);
            scanUserFragment.show(getFragmentManager(), "scanuser");
        }
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
                            hideDialog();
                            Logger.d("用户登录");
                            llShowMember.setVisibility(View.VISIBLE);
                            Double whiteBarBalance = MyUtils.formatDouble(Double.parseDouble(object.get("gold").toString()) - Double.parseDouble(object.get("arrears").toString()));
                            Double storedBalance = MyUtils.formatDouble(Double.parseDouble(object.get("stored").toString()));
                            if (object.get("avatarurl") != null && !object.get("avatarurl").equals("")) {
                                Glide.with(MyApplication.getContextObject()).load(object.get("avatarurl").toString()).into(userAvatar);
                            }
                            userTel.setText("用户手机号:" + object.get("username").toString());
                            userStored.setText("消费金:" + storedBalance);
                            userWhitebar.setText("白条:" + whiteBarBalance);
                            userId = object.get("objectId").toString();
                            if (tableAVObject != null) {
                                tableAVObject.put("user", AVObject.createWithoutData("_User", userId));
                                tableAVObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e != null) {
                                            ToastUtil.showShort(getContext(), e.getMessage());
                                        } else {
                                            initData();
                                        }
                                    }
                                });
                            }
                            initData();
                            signUser.setText("退出登录");

                        } else {
                            hideDialog();
                            ToastUtil.showShort(getContext(), e.getMessage());
                        }
                    }
                });

            }
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CouponEvent event) {

        if (event.getType() == 1) {
            onlineCouponEvent = event;
            refreshList();
        } else if (event.getType() == 2) {
            offlineCouponEvent = event;
            offlineCouponNumber = 1;
            ChooseOfflineCouponNumber();
            refreshList();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FuncBean event) {
        setFunc(event.getCode());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RateBean event) {
        orderRate = event.getRate();
        rateReduceRemark = event.getContent();
        deleteoddMoney = 0.0;
        refreshList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UserMessgae(UserBean userBean) {
        if (userBean.getCallbackCode() == CONST.UserCode.SCANCUSTOMER) {
            llShowMember.setVisibility(View.VISIBLE);
            if (userBean.getAvatar() != null && !userBean.getAvatar().equals("")) {
                Glide.with(MyApplication.getContextObject()).load(userBean.getAvatar()).into(userAvatar);
            }
            userTel.setText("用户手机号:" + userBean.getUsername());
            userStored.setText("消费金:" + userBean.getStored());
            userWhitebar.setText("白条:" + userBean.getBalance());
            userMeatweight.setText("牛肉额度:" + userBean.getMeatWeight() + "kg");
            userId = userBean.getId();
            if (tableAVObject != null) {
                tableAVObject.put("user", AVObject.createWithoutData("_User", userId));
                tableAVObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e != null) {
                            ToastUtil.showShort(getContext(), e.getMessage());
                        } else {
                            initData();
                        }
                    }
                });
            }
            signUser.setText("退出登录");

        }
    }


    private void setFunc(int resultCode) {
        switch (resultCode) {
            case 0:
                chooseMerge();//合并订单结账
                break;
            case 1://挂账
                if (isHangUp) {
                    ToastUtil.showShort(MyApplication.getContextObject(), "挂账订单不可继续挂账");
                } else {
                    hangUp();//挂账
                }
                break;
            case 2://抹零
                deleteOdd();
                break;
            case 3://生成预订单
                printPreOrder();
                break;
            case 4://整单打折
                ShowWholeSaleFragment showWholeSaleFragment = new ShowWholeSaleFragment(orderRate, rateReduceRemark);
                showWholeSaleFragment.setTargetFragment(this, REQUEST_RATE);
                showWholeSaleFragment.show(getFragmentManager(), "showwholesale");
                break;
            case 5://附近员工打折
                orderRate = CONST.NEARBYSTAFFRATE;
                rateReduceRemark = "周边员工";
                deleteoddMoney = 0.0;
                refreshList();
                break;
            case 6://充值1000抹零200
                T.L("餐饮订单不可以使用");
                break;
        }

    }

    private void printPreOrder() {
        LinkedHashMap<String, Double> reduceMap = new LinkedHashMap<>();
        if (offlineCouponMoney > 0) {
            reduceMap.put(offlineCouponEvent.getContent() + "*" + offlineCouponNumber + "张", MyUtils.formatDouble(offlineCouponMoney));
        }
        if (onlineCouponMoney > 0) {
            reduceMap.put(onlineCouponEvent.getContent(), onlineCouponMoney);
        }
        if (activityReduceMoney > 0) {
            reduceMap.put("开业" + MyUtils.getDayRate() + "折优惠", activityReduceMoney);
        }
        if (fullReduceMoney > 0) {
            reduceMap.put("满减优惠", fullReduceMoney);
        }
        if (orderRate != 100) {
            String content = "整单" + orderRate + "折优惠" + "(" + rateReduceRemark + ")";
            reduceMap.put(content, ratereduceMoney);
        }
        if (deleteoddMoney > 0) {
            reduceMap.put("抹零", deleteoddMoney);
        }
        Bill.printPreOrder(MyApplication.getContextObject(), tableAVObject, originTotalMoneny, actualTotalMoneny, reduceMap, useExchangeList, ProductUtil.calExchangeMeatList(orders), isNbPay, nbTotalMoney);
    }


    private void chooseMerge() {
        showDialog();
        AVQuery<AVObject> tables = new AVQuery<>("Table");
        tables.whereNotEqualTo("objectId", tableAVObject.getObjectId());
        tables.whereGreaterThan("customer", 0);
        tables.whereExists("order");
        tables.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(final List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        hideDialog();
                        ToastUtil.showShort(MyApplication.getContextObject(), "暂无可合并结账的订单");
                    } else {
                        hideDialog();
                        final String[] items = new String[list.size()];
                        final String[] tableIds = new String[list.size()];
                        for (int i = 0; i < list.size(); i++) {
                            items[i] = list.get(i).getString("tableNumber");
                            tableIds[i] = list.get(i).getObjectId();
                        }
                        final QMUIDialog.MultiCheckableDialogBuilder builder = new QMUIDialog.MultiCheckableDialogBuilder(getActivity())
                                .setTitle("选择合并结账的桌号")
                                .addItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        builder.addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        });
                        builder.addAction("确定合并", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                final String[] tableSelectIds = new String[builder.getCheckedItemIndexes().length];
                                for (int i = 0; i < builder.getCheckedItemIndexes().length; i++) {
                                    tableSelectIds[i] = tableIds[builder.getCheckedItemIndexes()[i]];
                                }
                                AVQuery<AVObject> table = new AVQuery<>("Table");
                                table.whereContainedIn("objectId", Arrays.asList(tableSelectIds));
                                table.findInBackground(new FindCallback<AVObject>() {
                                    @Override
                                    public void done(List<AVObject> list, AVException e) {
                                        if (e == null) {
                                            otherTableOrders = new HashMap<>();
                                            selectTableNumber = new ArrayList<>();
                                            selectTableIds = new ArrayList<>();
                                            for (int i = 0; i < list.size(); i++) {
                                                selectTableNumber.add(list.get(i).getString("tableNumber"));
                                                selectTableIds.add(list.get(i).getObjectId());
                                                otherTableOrders.put(tableSelectIds[i], list.get(i).getList("order"));
                                            }
                                            refreshList();
                                        }
                                    }
                                });
                                dialog.dismiss();
                            }
                        });
                        builder.create(mCurrentDialogStyle).show();
                    }
                } else {
                    hideDialog();
                    ToastUtil.showShort(MyApplication.getContextObject(), "网络异常");
                }
            }
        });

    }

    private void deleteOdd() {
        double v = (actualTotalMoneny / 10 - new Double(actualTotalMoneny).intValue() / 10) * 10;
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("请输入抹零金额(当前实付款" + actualTotalMoneny + ")")
                .setPlaceholder("在此输入抹零金额")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .setCanceledOnTouchOutside(false)
                .setDefaultText("" + MyUtils.formatDouble(v))
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        String text = builder.getEditText().getText().toString();
                        if (text != null && text.length() > 0) {
                            try {
                                deleteoddMoney = Double.parseDouble(text);
                                dialog.dismiss();
                                refreshList();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "请输入正确金额", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getActivity(), "请输入正确金额", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create(mCurrentDialogStyle).show();
    }

    private void hangUp() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("提示")
                .setPlaceholder("在此输入挂单原因")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .setCanceledOnTouchOutside(false)
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        String text = builder.getEditText().getText().toString();
                        if (text != null && text.length() > 0) {
                            HangUpOrder(text);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "请输入挂单原因", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create(mCurrentDialogStyle).show();


    }

    private void HangUpOrder(String content) {
        showDialog();
        if (tableAVObject != null) {
            final AVObject hangUpOrder = HangUpApi.saveHangUpOrder(tableAVObject, content);
            hangUpOrder.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        TableApi.clearTable(tableAVObject).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                hideDialog();
                                if (e == null) {
                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                    TableFg tableFg = TableFg.newInstance("");
                                    ft.replace(R.id.fragment_content, tableFg, "table").commit();
                                    new QMUITipDialog.Builder(getContext())
                                            .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                            .setTipWord("挂单成功")
                                            .create();
                                } else {
                                    ToastUtil.showShort(MyApplication.getContextObject(), e.getMessage() + "订单信息错误");
                                    hangUpOrder.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(AVException e) {
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        hideDialog();
                        ToastUtil.showShort(MyApplication.getContextObject(), e.getMessage() + "订单信息错误");
                    }
                }
            });

        } else {
            hideDialog();
            ToastUtil.showShort(MyApplication.getContextObject(), "订单信息错误");
        }
    }

    private void ChooseOfflineCouponNumber() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
        builder.setTitle("请输入可选择优惠券的数量(订单总金额" + (actualTotalMoneny + offlineCouponMoney) + ")")
                .setPlaceholder("输入数量")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .setCanceledOnTouchOutside(false)
                .setDefaultText("1")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        String text = builder.getEditText().getText().toString();
                        if (text != null && text.length() > 0) {
                            try {
                                if (MyUtils.isNumber(text) && Integer.parseInt(text) > 0) {
                                    if (actualTotalMoneny + offlineCouponMoney >= MyUtils.formatDouble(Integer.parseInt(text) * offlineCouponEvent.getMoney())) {
                                        offlineCouponNumber = Integer.parseInt(text);
                                        dialog.dismiss();
                                        refreshList();
                                    } else {
                                        ToastUtil.showShort(MyApplication.getContextObject(), "优惠金额超出订单金额");
                                    }
                                } else {
                                    ToastUtil.showShort(MyApplication.getContextObject(), "输入数量有误");
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "请输入正确金额", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getActivity(), "请输入正确金额", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create(mCurrentDialogStyle).show();
    }

    private void useNbPay() {
        new QMUIDialog.MessageDialogBuilder(getActivity())
                .setTitle("付款确认")
                .setMessage("确认使用" + nbTotalMoney + "个牛币支付此订单？")
                .addAction("取消", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确定", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        showDialog();
                        Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().offlineConsume(userId, SharedHelper.read("cashierId"), SharedHelper.read("cashierId"), nbTotalMoney, 2);
                        responseBodyCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                hideDialog();
                                if (response.code() == 200 || response.code() == 201) {
                                    Logger.d("消费成功");
                                    finishOrder();
                                } else {
                                    Logger.d(response.code());
                                    T.show(response);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                hideDialog();
                                T.L("网络错误,请重试");
                            }
                        });
                    }
                })
                .create(mCurrentDialogStyle).show();

    }

    private void finishOrder() {
        showDialog();
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (tableAVObject.getAVObject("user") != null) {
            parameters.put("paymentType", CONST.MALLPAYMENTTYPE.NB_PAY);
            parameters.put("customerId", tableAVObject.getAVObject("user").getObjectId());
            final List order = tableAVObject.getList("order");
            final List<String> ids = ProductUtil.calTotalIds(order);
            parameters.put("commodityids", ids);
            parameters.put("paysum", nbTotalMoney);
            parameters.put("sum", nbTotalMoney);
            AVCloud.callFunctionInBackground("offlineMallOrder", parameters, new FunctionCallback<Map<String, Map<String, Object>>>() {

                @Override
                public void done(Map<String, Map<String, Object>> map, AVException e) {
                    if (e == null) {
                        String orderId = map.get("order").get("objectId").toString();
                        AVObject mallOrder = AVObject.createWithoutData("MallOrder", orderId);
                        mallOrder.put("cashier", AVObject.createWithoutData("_User", new SharedHelper(getContext()).read("cashierId")));
                        mallOrder.put("market", AVObject.createWithoutData("_User", new SharedHelper(getContext()).read("cashierId")));
                        mallOrder.put("orderStatus", AVObject.createWithoutData("MallOrderStatus", CONST.OrderState.ORDER_STATUS_FINSIH));
                        mallOrder.put("escrow", 25);
                        mallOrder.put("startedAt", tableAVObject.getDate("startedAt"));
                        mallOrder.put("customer", tableAVObject.getInt("customer"));
                        mallOrder.put("endAt", new Date());
                        mallOrder.put("offline", true);
                        mallOrder.put("store", 1);
                        mallOrder.put("refundDetail", tableAVObject.getList("refundOrder"));
                        final String finalTableNumber = tableAVObject.getString("tableNumber") + ProductUtil.calOtherTable(selectTableNumber);
                        mallOrder.put("tableNumber", finalTableNumber);
                        mallOrder.put("commodityDetail", orders);
                        mallOrder.put("maxMeatDeduct", new List[0]);
                        mallOrder.put("realMeatDeduct", new List[0]);
                        Map<String, Double> escrowDetail = new HashMap<>();
                        escrowDetail.put("牛币", nbTotalMoney);
                        mallOrder.put("escrowDetail", escrowDetail);
                        if (isHangUp) {
                            mallOrder.put("hangUp", true);
                            mallOrder.put("message", tableAVObject.getString("remark"));
                            mallOrder.put("type", 3);
                        } else {
                            mallOrder.put("type", 0);
                        }
                        mallOrder.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    Bill.printSettleBillByNb(MyApplication.getContextObject(), tableAVObject, 25, finalTableNumber, orders, nb);
                                    resetTable();
                                } else {
                                    finishOrder();
                                    ToastUtil.showShort(MyApplication.getContextObject(), "网络繁忙请重试" + e.getMessage());
                                }
                            }
                        });
                    } else {
                        hideDialog();
                        finishOrder();
                        Logger.d(e.getMessage());
                        ToastUtil.showShort(MyApplication.getContextObject(), "网络繁忙请重试" + e.getMessage());
                    }
                }
            });
        }else{

        }
    }

    private void resetTable() {
        showDialog();
        if (isHangUp) {
            tableAVObject.put("active", 0);
            tableAVObject.put("settledAt", new Date());
            tableAVObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        hideDialog();
                        for (int i = 0; i < selectTableNumber.size(); i++) {
                            AVObject table = AVObject.createWithoutData("Table", selectTableNumber.get(i));
                            table.put("order", new List[0]);
                            table.put("preOrder", new List[0]);
                            table.put("refundOrder", new List[0]);
                            table.put("customer", 0);
                            table.put("startedAt", null);
                            table.put("user", null);
                            table.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        hideDialog();
                                    } else {
                                        hideDialog();
                                        resetTable();
                                    }
                                }
                            });
                        }
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_content, TableFg.newInstance(""), "table").commit();
                    } else {
                        hideDialog();
                        ToastUtil.showShort(MyApplication.getContextObject(), "网络错误" + e.getMessage());
                        resetTable();
                    }
                }
            });
        } else {
            AVObject avObject = tableAVObject;
            avObject.put("order", new List[0]);
            avObject.put("preOrder", new List[0]);
            avObject.put("refundOrder", new List[0]);
            avObject.put("customer", 0);
            avObject.put("startedAt", null);
            avObject.put("user", null);
            avObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        hideDialog();
                        for (int i = 0; i < selectTableNumber.size(); i++) {
                            AVObject table = AVObject.createWithoutData("Table", selectTableNumber.get(i));
                            table.put("order", new List[0]);
                            table.put("preOrder", new List[0]);
                            table.put("refundOrder", new List[0]);
                            table.put("customer", 0);
                            table.put("startedAt", null);
                            table.put("user", null);
                            table.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null) {
                                        hideDialog();
                                        Logger.d("清浊");

                                    } else {
                                        hideDialog();
                                        resetTable();
                                    }

                                }
                            });
                        }
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_content, TableFg.newInstance(""), "table").commit();
                    } else {
                        ToastUtil.showShort(MyApplication.getContextObject(), "网络错误" + e.getMessage());
                        resetTable();
                    }
                }
            });
        }
    }


    /**
     * 判断是否是大众点评商品
     */
    private void changeCommodity(final int postion) {
        final HashMap<String, Object> hashMap = (HashMap<String, Object>) orders.get(orders.size() - postion - 1);
        ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(hashMap, "id"));
        if (productBean.getReviewCommodity() != null) {
            final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
            builder.setTitle("当前商品" + productBean.getName() + "*" + ObjectUtil.getDouble(hashMap, "number") + "份" + "==>可大众点评购券抵扣(" + MyUtils.getProductById(productBean.getReviewCommodity()).getName() + ")\n请选择抵扣数量")
                    .setPlaceholder("在此输入抵扣")
                    .setInputType(InputType.TYPE_CLASS_TEXT)
                    .setCanceledOnTouchOutside(false)
                    .setDefaultText("" + ObjectUtil.getDouble(hashMap, "number").intValue())
                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction("确定", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            String text = builder.getEditText().getText().toString();
                            if (text != null && text.length() > 0 && MyUtils.isNumber(text) && ObjectUtil.getDouble(hashMap, "number") >= Double.parseDouble(text)) {
                                try {
                                    DataUtil.changeDZDPCommodity(tableAVObject, orders, postion, Double.parseDouble(text));
                                    dialog.dismiss();
                                    refreshList();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), "请输入正确金额", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getActivity(), "请输入正确金额", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .create(mCurrentDialogStyle).show();
        } else if (productBean.getSerial().equals("110")) {
            final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
            builder.setTitle("修改你组团我买单的人数")
                    .setPlaceholder("在此输入人数")
                    .setInputType(InputType.TYPE_CLASS_TEXT)
                    .setCanceledOnTouchOutside(false)
                    .setDefaultText("" + ObjectUtil.getDouble(hashMap, "number").intValue())
                    .addAction("取消", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                        }
                    })
                    .addAction("确定", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            String text = builder.getEditText().getText().toString();
                            if (text != null && text.length() > 0 && MyUtils.isNumber(text) && ObjectUtil.getDouble(hashMap, "number") >= Double.parseDouble(text) && Double.parseDouble(text) > 0) {
                                try {
                                    DataUtil.changeGroupNumber(tableAVObject, orders, postion, Double.parseDouble(text));
                                    dialog.dismiss();
                                    refreshList();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), "请输入正确金额", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getActivity(), "请输入正确金额", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .create(mCurrentDialogStyle).show();
        } else{
            if (ObjectUtil.getDouble(hashMap,"price")!=0) {
                final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getActivity());
                builder.setTitle("将此菜转为赠送菜品,输入赠送数量")
                        .setInputType(InputType.TYPE_CLASS_TEXT)
                        .setCanceledOnTouchOutside(false)
                        .setDefaultText("" + ObjectUtil.getDouble(hashMap, "number").intValue())
                        .addAction("取消", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                dialog.dismiss();
                            }
                        })
                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                            @Override
                            public void onClick(QMUIDialog dialog, int index) {
                                String text = builder.getEditText().getText().toString();
                                if (text != null && text.length() > 0 && MyUtils.isNumber(text) && ObjectUtil.getDouble(hashMap, "number") >= Double.parseDouble(text) && Double.parseDouble(text) > 0) {
                                    try {
                                        DataUtil.addHangUpOrder(tableAVObject, orders, postion, Double.parseDouble(text));
                                        dialog.dismiss();
                                        refreshList();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), "输入有误", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(getActivity(), "请输入正确数量", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .create(mCurrentDialogStyle).show();
            }else{
                T.L("金额为0元不可操作");
            }
        }
    }
}
