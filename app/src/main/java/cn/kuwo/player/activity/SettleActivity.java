package cn.kuwo.player.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.adapter.ShowRetailAdapter;
import cn.kuwo.player.api.CouponApi;
import cn.kuwo.player.api.HangUpApi;
import cn.kuwo.player.base.BaseActivity;
import cn.kuwo.player.bean.FuncBean;
import cn.kuwo.player.bean.RateBean;
import cn.kuwo.player.bean.RetailBean;
import cn.kuwo.player.bean.UserBean;
import cn.kuwo.player.custom.CommomDialog;
import cn.kuwo.player.custom.ScanUserFragment;
import cn.kuwo.player.custom.ShowCouponFragment;
import cn.kuwo.player.custom.ShowFuncFragment;
import cn.kuwo.player.custom.ShowReduceListFragment;
import cn.kuwo.player.custom.ShowWholeSaleFragment;
import cn.kuwo.player.event.CouponEvent;
import cn.kuwo.player.event.OrderDetail;
import cn.kuwo.player.print.Bill;
import cn.kuwo.player.util.ApiManager;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.CameraProvider;
import cn.kuwo.player.util.DataUtil;
import cn.kuwo.player.util.DateUtil;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.T;
import cn.kuwo.player.util.ToastUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettleActivity extends BaseActivity {
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
    @BindView(R.id.orgin_price)
    TextView orginPrice;
    @BindView(R.id.svip_all_reduce)
    TextView svipAllReduce;
    @BindView(R.id.ll_max_reduce)
    LinearLayout llMaxReduce;
    @BindView(R.id.min_pay_money)
    TextView minPayMoney;
    @BindView(R.id.my_svip_reduce_weight)
    TextView mySvipReduceWeight;
    @BindView(R.id.ll_my_reduce)
    LinearLayout llMyReduce;
    @BindView(R.id.my_svip_reduce_money)
    TextView mySvipReduceMoney;
    @BindView(R.id.tv_offline_content)
    TextView tvOfflineContent;
    @BindView(R.id.tv_offline_moeny)
    TextView tvOfflineMoeny;
    @BindView(R.id.ll_offline_moeny)
    LinearLayout llOfflineMoeny;
    @BindView(R.id.tv_online_content)
    TextView tvOnlineContent;
    @BindView(R.id.tv_online_moeny)
    TextView tvOnlineMoeny;
    @BindView(R.id.ll_online_moeny)
    LinearLayout llOnlineMoeny;
    @BindView(R.id.store_reduce_money)
    TextView storeReduceMoney;
    @BindView(R.id.pay_content)
    TextView payContent;
    @BindView(R.id.total_money)
    TextView totalMoney;
    @BindView(R.id.cb_use_svip)
    CheckBox cbUseSvip;
    @BindView(R.id.btn_pay)
    Button btnPay;
    @BindView(R.id.full_reduce_money)
    TextView fullreduceMoney;
    @BindView(R.id.more_fuc)
    Button moreFuc;
    @BindView(R.id.delete_odd_money)
    TextView deleteOddMoney;
    @BindView(R.id.ll_delete_odd)
    LinearLayout llDeleteOdd;
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
    @BindView(R.id.black_five_money)
    TextView blackFiveMoney;
    @BindView(R.id.ll_black_five)
    LinearLayout llBlackFive;
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
    private Context context;
    private RetailBean retailBean;
    private LinearLayoutManager linearLayoutManager;
    private ShowRetailAdapter showRetailAdapter;
    private AVObject userAVObject = null;
    private String userId = "";
    private String useMeatId = "";
    private String rateReduceRemark = "";
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
    private Double blackfiveMoney = 0.0;
    private Double chargeReduceMoney = 0.0;
    private int orderRate = 100;
    private Double ratereduceMoney = 0.0;
    private CouponEvent onlineCouponEvent = null;
    private CouponEvent offlineCouponEvent = null;
    private Boolean isSvip = false;
    private List<Object> orders = new ArrayList<>();
    private List<Object> useExchangeList = new ArrayList<>();
    private AVObject avUser;
    private UserBean userBean;
    private int REQUEST_CODE_SCAN = 111;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private static boolean isHangUp = false;
    private static boolean isNbPay = false;
    private Double nb = 0.0;
    private Double nbTotalMoney = 0.0;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_settle;
    }

    /**
     * 设置商品展示布局
     */
    @Override
    public void initData() {
        isNbPay = false;
        context = this;
        isHangUp = getIntent().getBooleanExtra("isHangUp", false);
        retailBean = (RetailBean) getIntent().getSerializableExtra("retailBean");
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycleScanGood.setLayoutManager(linearLayoutManager);
        showRetailAdapter = new ShowRetailAdapter(MyApplication.getContextObject(), retailBean);
        recycleScanGood.setAdapter(showRetailAdapter);
        orders = ObjectUtil.toObject(retailBean.getIds(), retailBean.getPrices(), retailBean.getWeight());
        cbUseSvip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                refreshList();
            }
        });
        if (isHangUp) {
            if (getIntent().getStringExtra("userId") != null) {
                userId = getIntent().getStringExtra("userId");
                getUserInfo(getIntent().getStringExtra("userId"));
            }
        }
        setData();
        setListener();
    }

    private void getUserInfo(final String userId) {
        showDialog();
        AVQuery<AVObject> query = new AVQuery<>("_User");
        query.getInBackground(userId, new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().QueryofflineRecharge(userId);
                    responseBodyCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            hideDialog();
                            if (response.code() == 200) {

                                try {
                                    String responseText = DataUtil.JSONTokener(response.body().string());
                                    JSONObject jsonObject = null;
                                    jsonObject = new JSONObject(responseText);
                                    nb = jsonObject.getDouble("amount");


                                    userBean = new UserBean(CONST.UserCode.SCANCUSTOMER,
                                            avObject.getObjectId(),
                                            avObject.getString("username"),
                                            avObject.getString("realName") == null ? avObject.getString("nickName") : avObject.getString("realName"),
                                            avObject.getInt("vip"),
                                            avObject.getDouble("credits"),
                                            avObject.getDouble("stored"),
                                            MyUtils.formatDouble(avObject.getDouble("gold") - avObject.getDouble("arrears")),
                                            avObject.getBoolean("test"),
                                            avObject.getInt("clerk"),
                                            0.0,
                                            null,
                                            false,
                                            avObject.getString("avatar"),
                                            false,
                                            nb);
                                    userNb.setText("牛币:" + nb);
                                    llShowMember.setVisibility(View.VISIBLE);
                                    if (userBean.getAvatar() != null && !userBean.getAvatar().equals("")) {
                                        Glide.with(MyApplication.getContextObject()).load(userBean.getAvatar()).into(userAvatar);
                                    }
                                    userTel.setText("用户手机号:" + userBean.getUsername());
                                    userStored.setText("消费金:" + userBean.getStored());
                                    userWhitebar.setText("白条:" + userBean.getBalance());
                                    userMeatweight.setText("牛肉额度:" + userBean.getMeatWeight() + "kg");
                                    userMeatweight.setText("牛肉额度:" + userBean.getMeatWeight() + "kg");
                                    hasMeatWeight = userBean.getMeatWeight();
                                    useMeatId = userBean.getMeatId();
                                    if (userBean.getSvip()) {
                                        svipAvatar.setVisibility(View.VISIBLE);
                                        userType.setText("超牛会员");
                                        isSvip = true;
                                    } else {
                                        svipAvatar.setVisibility(View.GONE);
                                        userType.setText("普通会员");
                                        isSvip = false;
                                    }
                                    signUser.setText("退出登录");
                                    setData();
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            hideDialog();
                        }
                    });

                } else {
                    hideDialog();
                    Logger.d(e.getMessage());
                }
            }
        });
    }

    /**
     * 获取基本数据信息
     */
    public void setData() {
        if (userId.length() == 0) {
            llShowMember.setVisibility(View.INVISIBLE);
            signUser.setText("用户登录");
        }
        originTotalMoneny = MyUtils.totalPrice(retailBean.getPrices());
        orginPrice.setText(originTotalMoneny + "元");
        totalMoney.setText(originTotalMoneny + "元");
        totalNumber.setText(retailBean.getCodes().size() + "");
        refreshList();
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
                    ShowCouponFragment showCouponFragment = new ShowCouponFragment(list, actualTotalMoneny, 2);
                    showCouponFragment.show(getSupportFragmentManager(), "showpcoupon");
                } else {
                    ToastUtil.showShort(MyApplication.getContextObject(), "网络错误");

                }
            }
        });
    }

    private void getUserCoupon() {
        if (userBean != null) {
            showDialog();
            CouponApi.getCouponOnline(userBean.getUsername()).findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    hideDialog();
                    if (e == null) {
                        ShowCouponFragment showCouponFragment = new ShowCouponFragment(list, actualTotalMoneny, 1);
                        showCouponFragment.show(getSupportFragmentManager(), "showpcoupon");
                    } else {
                        ToastUtil.showShort(MyApplication.getContextObject(), "网络错误");
                    }
                }
            });
        } else {
            ToastUtil.showShort(MyApplication.getContextObject(), "请用户先登录后查看");
        }
    }

    private void refreshList() {
        originTotalMoneny = 0.0;
        meatReduceMoney = 0.0;
        offlineCouponMoney = 0.0;
        onlineCouponMoney = 0.0;
        activityReduceMoney = 0.0;
        myMeatReduceMoney = 0.0;
        myMeatReduceWeight = 0.0;
        fullReduceMoney = 0.0;
        totalNumber.setText(orders.size() + "");
        originTotalMoneny = MyUtils.totalPrice(retailBean.getPrices());
        Double noDiscountMoney = ProductUtil.calNoDiscountMoney(orders);
        orginPrice.setText(originTotalMoneny + "");
        if (userId.length() > 0 && !isNbPay) {
            cbUseSvip.setVisibility(View.VISIBLE);
        } else {
            cbUseSvip.setVisibility(View.GONE);
        }
        nbTotalMoney = ProductUtil.calNbTotalMoney(orders);
        nbPrice.setText(nbTotalMoney + "");
        if (nb >= nbTotalMoney) {
            chooseNb.setVisibility(View.VISIBLE);
        }
        meatReduceMoney = ProductUtil.calMeatduceMoney(orders, retailBean.getPrices());
        meatReduceWeight = ProductUtil.calMeatduceWeight(orders, retailBean.getWeight());
        if (chargeReduceMoney > 0) {
            if (originTotalMoneny >= 200) {
                chargeReduceMoney = 200.0;
            } else {
                chargeReduceMoney = originTotalMoneny;
            }
        }
        svipAllReduce.setText(meatReduceWeight + "kg");
        if (!isNbPay) {
            if (userId.length() > 0) {
                if (hasMeatWeight >= meatReduceWeight) {//用户牛肉大于等于可兑换的牛肉重量
                    mySvipReduceWeight.setText(meatReduceWeight + "kg");
                    mySvipReduceMoney.setText("-" + meatReduceMoney);
                    myMeatReduceWeight = meatReduceWeight;
                    myMeatReduceMoney = meatReduceMoney;
                    List<Object> centerOrder;
                    try {
                        centerOrder = ObjectUtil.deepCopy(orders);
                    } catch (Exception e) {
                        centerOrder = new ArrayList<>();
                        e.printStackTrace();
                    }
                    useExchangeList = ProductUtil.canExchangeMeatList(centerOrder, hasMeatWeight, retailBean.getWeight());
                } else {
                    List<Object> centerOrder;
                    try {
                        centerOrder = ObjectUtil.deepCopy(orders);
                    } catch (Exception e) {
                        centerOrder = new ArrayList<>();
                        e.printStackTrace();
                    }
                    useExchangeList = ProductUtil.canExchangeMeatList(centerOrder, hasMeatWeight, retailBean.getWeight());
                    myMeatReduceWeight = ProductUtil.calculateTotalWeight(useExchangeList);
                    myMeatReduceMoney = ProductUtil.calculateTotalMoney(useExchangeList);
                    mySvipReduceWeight.setText(myMeatReduceWeight + "kg");
                    mySvipReduceMoney.setText("-" + myMeatReduceMoney);
                }

            } else {
                mySvipReduceWeight.setText("0.0kg");
                mySvipReduceMoney.setText("-0.0");
            }
            blackfiveMoney = ProductUtil.calBlackFiveReduce(cbUseSvip.isChecked(), useExchangeList, ProductUtil.calExchangeMeatList(orders), orders);
            if (blackfiveMoney > 0 && userBean != null) {
                llBlackFive.setVisibility(View.VISIBLE);
                blackFiveMoney.setText("-" + blackfiveMoney);
            } else {
                blackfiveMoney = 0.0;
                blackFiveMoney.setText("0.0");
                llBlackFive.setVisibility(View.GONE);
            }

            if (onlineCouponEvent != null) {
                onlineCouponMoney = onlineCouponEvent.getMoney();
                tvOnlineMoeny.setText("-" + onlineCouponMoney);
                tvOnlineContent.setText(onlineCouponEvent.getContent());
            }
            if (offlineCouponEvent != null) {
                offlineCouponMoney = offlineCouponEvent.getMoney();
                tvOfflineMoeny.setText("-" + offlineCouponMoney);
                tvOfflineContent.setText(offlineCouponEvent.getContent());
            }
            chargeDeduce.setText("-" + chargeReduceMoney);
            if (cbUseSvip.isChecked()) {
                if (userBean != null) {
                    activityReduceMoney = MyUtils.formatDouble((originTotalMoneny - chargeReduceMoney - myMeatReduceMoney - blackfiveMoney - offlineCouponMoney - onlineCouponMoney - noDiscountMoney) * (1 - MyUtils.getDayRate()));
                }
                if (activityReduceMoney < 0) activityReduceMoney = 0.0;
                actualTotalMoneny = originTotalMoneny - chargeReduceMoney - offlineCouponMoney - blackfiveMoney - onlineCouponMoney - activityReduceMoney - myMeatReduceMoney;
                if (actualTotalMoneny < 0) actualTotalMoneny = 0.0;
                totalMoney.setText("￥" + actualTotalMoneny + "元");
                storeReduceMoney.setText("-" + activityReduceMoney);
            } else {
                if (userBean != null) {
                    activityReduceMoney = MyUtils.formatDouble((originTotalMoneny - chargeReduceMoney - offlineCouponMoney - blackfiveMoney - onlineCouponMoney - noDiscountMoney) * (1 - MyUtils.getDayRate()));
                }
                if (activityReduceMoney < 0) activityReduceMoney = 0.0;
                actualTotalMoneny = originTotalMoneny - chargeReduceMoney - offlineCouponMoney - blackfiveMoney - onlineCouponMoney - activityReduceMoney;
                if (actualTotalMoneny < 0) actualTotalMoneny = 0.0;
                totalMoney.setText("￥" + actualTotalMoneny + "元");
                storeReduceMoney.setText("-" + activityReduceMoney);
            }
            if (deleteoddMoney > 0) {
                llDeleteOdd.setVisibility(View.VISIBLE);
                deleteOddMoney.setText("-" + deleteoddMoney);
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
            if (activityReduceMoney > 0) {
                llStoreReduce.setVisibility(View.VISIBLE);
            } else {
                llStoreReduce.setVisibility(View.GONE);
            }
            if (fullReduceMoney > 0) {
                llFullReduce.setVisibility(View.VISIBLE);
            } else {
                llFullReduce.setVisibility(View.GONE);
            }
            actualTotalMoneny -= deleteoddMoney;
            actualTotalMoneny = MyUtils.formatDouble(actualTotalMoneny) >= 0 ? MyUtils.formatDouble(actualTotalMoneny) : 0.0;
            totalMoney.setText("￥" + actualTotalMoneny + "元");
            minPayMoney.setText("-" + meatReduceMoney);
        } else {
            actualTotalMoneny = nbTotalMoney;
            totalMoney.setText("￥" + actualTotalMoneny + "牛币");
        }
    }

    @OnClick({R.id.sign_user, R.id.btn_pay, R.id.ll_max_reduce, R.id.ll_my_reduce, R.id.more_fuc, R.id.choose_nb, R.id.user_charge_nb})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_user:
                if (userId.length() > 0) {
                    new CommomDialog(this, R.style.dialog, "确定要取消此订单的用户信息？", new CommomDialog.OnCloseListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                userBean = null;
                                dialog.dismiss();
                                userId = "";
                                avUser = null;
                                chooseNb.setText("当前付款状态:正常支付(点击切换成牛币支付)");
                                llNoNb.setVisibility(View.VISIBLE);
                                chooseNb.setVisibility(View.GONE);
                                nb = 0.0;
                                isNbPay = false;
                                ToastUtil.showShort(MyApplication.getContextObject(), "清空用户数据成功");
                                setData();
                            }

                        }
                    }).setTitle("提示").setNegativeButton("取消").setPositiveButton("退出").show();
                } else {
                    chooseScanType();
                }
                break;
            case R.id.btn_pay:
                if (isNbPay) {
                    useNbPay();
                } else {
                    if (userBean != null) {
                        if (userBean != null && nb >= nbTotalMoney && !isNbPay) {
                            new CommomDialog(context, R.style.dialog, "用户牛币充足,用牛币更优惠,确认不帮他使用么", new CommomDialog.OnCloseListener() {
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

                }
                break;
            case R.id.ll_max_reduce:
                ShowReduceListFragment showReduceListFragment = new ShowReduceListFragment(ProductUtil.calExchangeMeatList(orders), 0);
                showReduceListFragment.show(getSupportFragmentManager(), "showreducelist");
                break;
            case R.id.ll_my_reduce:
                showReduceListFragment = new ShowReduceListFragment(useExchangeList, 1);
                showReduceListFragment.show(getSupportFragmentManager(), "showreducelist");
                break;
            case R.id.more_fuc:
                ShowFuncFragment showFuncFragment = new ShowFuncFragment(1);
                showFuncFragment.show(getSupportFragmentManager(), "showfunc");
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
                Intent intent = getIntent();
                intent.putExtra("userId", userBean.getId());
                setResult(2, intent);
                finish();
                break;
        }
    }


    private void skipPayPage() {
        Intent intent = new Intent(SettleActivity.this, PayActivity.class);
        Bundle bundle = new Bundle();
        OrderDetail orderDetail = new OrderDetail(null, hasMeatWeight, originTotalMoneny,
                actualTotalMoneny, meatReduceWeight, meatReduceMoney, myMeatReduceWeight, myMeatReduceMoney, cbUseSvip.isChecked(),
                onlineCouponEvent, offlineCouponEvent, activityReduceMoney, isSvip, useExchangeList, useMeatId, ProductUtil.calExchangeMeatList(orders), userBean, orders, fullReduceMoney,
                deleteoddMoney, orderRate, rateReduceRemark, ratereduceMoney, blackfiveMoney, chargeReduceMoney);
        bundle.putSerializable("table", (Serializable) orderDetail);
        if (isHangUp) {
            bundle.putString("remark", getIntent().getStringExtra("remark"));
            bundle.putString("hangUpId", getIntent().getStringExtra("hangUpId"));
            bundle.putBoolean("isHangUp", getIntent().getBooleanExtra("isHangUp", false));
        }
        intent.putExtras(bundle);
        startActivityForResult(intent, 1);
    }

    private void chooseScanType() {
        if (CameraProvider.hasCamera()) {
            if (SharedHelper.readBoolean("useGun")) {
                ScanUserFragment scanUserFragment = new ScanUserFragment(1);
                scanUserFragment.show(getSupportFragmentManager(), "scanuser");
            } else {
                if (MyUtils.getCameraPermission(getApplicationContext())) {
                    Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
                    intent.putExtra(Constant.INTENT_ZXING_CONFIG, MyUtils.caremaSetting());
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }
            }

        } else {
            ScanUserFragment scanUserFragment = new ScanUserFragment(1);
            scanUserFragment.show(getSupportFragmentManager(), "scanuser");
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
    public void UserMessgae(UserBean bean) {
        if (bean.getCallbackCode() == CONST.UserCode.SCANCUSTOMER) {
            userBean = bean;
            llShowMember.setVisibility(View.VISIBLE);
            if (userBean.getAvatar() != null && !userBean.getAvatar().equals("")) {
                Glide.with(MyApplication.getContextObject()).load(userBean.getAvatar()).into(userAvatar);
            }
            userTel.setText("用户手机号:" + userBean.getUsername());
            userStored.setText("消费金:" + userBean.getStored());
            userWhitebar.setText("白条:" + userBean.getBalance());
            userMeatweight.setText("牛肉额度:" + userBean.getMeatWeight() + "kg");
            userMeatweight.setText("牛肉额度:" + userBean.getMeatWeight() + "kg");
            hasMeatWeight = userBean.getMeatWeight();
            userId = userBean.getId();
            useMeatId = userBean.getMeatId();
            nb = userBean.getNb();
            userNb.setText("牛币:" + nb);
            if (userBean.getSvip()) {
                svipAvatar.setVisibility(View.VISIBLE);
                userType.setText("超牛会员");
                isSvip = true;
            } else {
                svipAvatar.setVisibility(View.GONE);
                userType.setText("普通会员");
                isSvip = false;
            }
            signUser.setText("退出登录");
            setData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FuncBean event) {
        setFunc(event.getCode());

    }

    private void setFunc(int resultCode) {
        switch (resultCode) {
            case 1://挂单
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
                showWholeSaleFragment.show(getSupportFragmentManager(), "showwholesale");
                break;
            case 5:
                orderRate = CONST.NEARBYSTAFFRATE;
                rateReduceRemark = "周边员工";
                deleteoddMoney = 0.0;
                refreshList();
                break;
            case 6:
                if (userBean != null) {
                    llChargeDeduce.setVisibility(View.VISIBLE);
                    if (originTotalMoneny >= 200) {
                        chargeReduceMoney = 200.0;
                    } else {
                        chargeReduceMoney = originTotalMoneny;
                    }
                    chargeDeduce.setText("-" + chargeReduceMoney);
                    cbUseSvip.setChecked(false);
                    refreshList();
                } else {
                    T.L("请先扫描用户二维码");
                }
                break;
        }

    }

    private void hangUp() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this);
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
                            Toast.makeText(MyApplication.getContextObject(), "请输入挂单原因", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create(mCurrentDialogStyle).show();


    }

    private void printPreOrder() {
        LinkedHashMap<String, Double> reduceMap = new LinkedHashMap<>();
        if (chargeReduceMoney > 0) {
            reduceMap.put("充值牛币反馈200优惠", chargeReduceMoney);
        }
        if (meatReduceMoney > 0 && cbUseSvip.isChecked()) {
            reduceMap.put("牛肉抵扣金额", meatReduceMoney);
        }
        if (blackfiveMoney > 0) {
            reduceMap.put("周五冻肉半价优惠", blackfiveMoney);
        }
        if (offlineCouponMoney > 0) {
            reduceMap.put(offlineCouponEvent.getContent(), offlineCouponMoney);
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
            String content = "整单" + orderRate + "折优惠";
            reduceMap.put(content, ratereduceMoney);
        }
        if (deleteoddMoney > 0) {
            reduceMap.put("抹零", deleteoddMoney);
        }
        Bill.printPreOrderRest(MyApplication.getContextObject(), originTotalMoneny, actualTotalMoneny, reduceMap, useExchangeList, ProductUtil.calExchangeMeatList(orders), orders, getIntent().getStringExtra("remark"), isNbPay, nbTotalMoney);
    }

    private void deleteOdd() {
        double v = (actualTotalMoneny / 10 - new Double(actualTotalMoneny).intValue() / 10) * 10;
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(context);
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
                                if (Double.parseDouble(text) > 10) {
                                    if (!CameraProvider.hasCamera()) {
                                        deleteoddMoney = Double.parseDouble(text);
                                        dialog.dismiss();
                                        refreshList();
                                    } else {
                                        Toast.makeText(context, "超过可抹零的权限", Toast.LENGTH_SHORT).show();

                                    }
                                } else {
                                    deleteoddMoney = Double.parseDouble(text);
                                    dialog.dismiss();
                                    refreshList();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "请输入正确金额", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(context, "请输入正确金额", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create(mCurrentDialogStyle).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            Intent intent = getIntent();
            setResult(1, intent);
            finish();
        } else if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                showDialog();
                String code = data.getStringExtra(Constant.CODED_CONTENT);
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("payCode", code.trim());
                AVCloud.callFunctionInBackground("payCodeGetUser", parameters, new FunctionCallback<Map<String, Object>>() {
                    @Override
                    public void done(final Map<String, Object> object, AVException e) {
                        if (e == null) {
                            showDialog();
                            Map<String, Object> parameter = new HashMap<String, Object>();
                            parameter.put("userID", object.get("objectId").toString());
                            AVCloud.callFunctionInBackground("svip", parameter, new FunctionCallback<Map<String, Object>>() {
                                @Override
                                public void done(final Map<String, Object> objectMap, AVException e) {
                                    if (e == null) {
                                        Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().QueryofflineRecharge(object.get("objectId").toString());
                                        responseBodyCall.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                if (response.code() == 200) {
                                                    hideDialog();
                                                    try {
                                                        String responseText = DataUtil.JSONTokener(response.body().string());
                                                        JSONObject jsonObject = new JSONObject(responseText);
                                                        nb = jsonObject.getDouble("amount");
                                                    } catch (Exception e1) {
                                                        e1.printStackTrace();
                                                    }
                                                    userNb.setText("牛币:" + nb);
                                                    userBean = new UserBean(CONST.UserCode.SCANCUSTOMER,
                                                            object.get("objectId").toString(),
                                                            object.get("username").toString(),
                                                            object.get("realName") == null ? object.get("nickName").toString() : object.get("realName").toString(),
                                                            Integer.parseInt(object.get("vip").toString()),
                                                            MyUtils.formatDouble(Double.parseDouble(object.get("credits").toString())),
                                                            MyUtils.formatDouble(Double.parseDouble(object.get("stored").toString())),
                                                            MyUtils.formatDouble(Double.parseDouble(object.get("gold").toString()) - Double.parseDouble(object.get("arrears").toString())),
                                                            (Boolean) object.get("Test"),
                                                            Integer.parseInt(object.get("clerk").toString()),
                                                            MyUtils.formatDouble(Double.parseDouble(objectMap.get("meatWeight").toString())),
                                                            objectMap.get("meatId").toString().length() > 0 ? objectMap.get("meatId").toString() : "",
                                                            (Boolean) objectMap.get("svip"),
                                                            object.get("avatarurl").toString(),
                                                            (Boolean) objectMap.get("alreadySVIP"),
                                                            nb);
                                                    llShowMember.setVisibility(View.VISIBLE);
                                                    if (userBean.getAvatar() != null && !userBean.getAvatar().equals("")) {
                                                        Glide.with(MyApplication.getContextObject()).load(userBean.getAvatar()).into(userAvatar);
                                                    }
                                                    userTel.setText("用户手机号:" + userBean.getUsername());
                                                    userStored.setText("消费金:" + userBean.getStored());
                                                    userWhitebar.setText("白条:" + userBean.getBalance());
                                                    userMeatweight.setText("牛肉额度:" + userBean.getMeatWeight() + "kg");
                                                    userMeatweight.setText("牛肉额度:" + userBean.getMeatWeight() + "kg");
                                                    hasMeatWeight = userBean.getMeatWeight();
                                                    userId = userBean.getId();
                                                    useMeatId = userBean.getMeatId();
                                                    if (userBean.getSvip()) {
                                                        svipAvatar.setVisibility(View.VISIBLE);
                                                        userType.setText("超牛会员");
                                                        isSvip = true;
                                                    } else {
                                                        svipAvatar.setVisibility(View.GONE);
                                                        userType.setText("普通会员");
                                                        isSvip = false;
                                                    }
                                                    signUser.setText("退出登录");
                                                    setData();
                                                } else {
                                                    hideDialog();
                                                    T.show(response);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                hideDialog();
                                                ToastUtil.showShort(MyApplication.getContextObject(), t.getMessage());
                                            }
                                        });


                                    } else {
                                        hideDialog();
                                        ToastUtil.showShort(MyApplication.getContextObject(), e.getMessage());
                                    }
                                }
                            });

                        } else {
                            hideDialog();
                            ToastUtil.showShort(MyApplication.getContextObject(), e.getMessage());
                        }
                    }
                });
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CouponEvent event) {
        Logger.d(event);
        if (event.getType() == 1) {
            onlineCouponEvent = event;
        } else if (event.getType() == 2) {
            offlineCouponEvent = event;
        }
        deleteoddMoney = 0.0;
        setData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RateBean event) {
        rateReduceRemark = event.getContent();
        orderRate = event.getRate();
        deleteoddMoney = 0.0;
        refreshList();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    private void HangUpOrder(String content) {
        showDialog();
        final AVObject hangUpOrder = HangUpApi.saveHangUpOrderByRest(retailBean, content);
        hangUpOrder.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    ToastUtil.showLong(context, "挂单成功");
                    setResult(1, getIntent());
                    finish();
                } else {
                    hideDialog();
                    ToastUtil.showShort(MyApplication.getContextObject(), e.getMessage() + "订单信息错误");
                }
            }
        });
    }

    private void useNbPay() {
        new QMUIDialog.MessageDialogBuilder(this)
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
        if (userBean != null) {
            parameters.put("paymentType", CONST.MALLPAYMENTTYPE.NB_PAY);
            parameters.put("customerId", userBean.getId());
            final List order = orders;
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
                        mallOrder.put("cashier", AVObject.createWithoutData("_User", new SharedHelper(getApplicationContext()).read("cashierId")));
                        mallOrder.put("market", AVObject.createWithoutData("_User", new SharedHelper(getApplicationContext()).read("cashierId")));
                        mallOrder.put("orderStatus", AVObject.createWithoutData("MallOrderStatus", CONST.OrderState.ORDER_STATUS_FINSIH));
                        mallOrder.put("escrow", 25);
                        mallOrder.put("offline", true);
                        mallOrder.put("store", 1);
                        mallOrder.put("commodityDetail", orders);
                        if (isHangUp) {
                            mallOrder.put("hangUp", true);
                            mallOrder.put("message", getIntent().getStringExtra("remark"));
                            mallOrder.put("type", 3);
                        } else {
                            mallOrder.put("type", 1);
                        }
                        Map<String, Double> escrowDetail = new HashMap<>();
                        escrowDetail.put("牛币", nbTotalMoney);
                        mallOrder.put("escrowDetail", escrowDetail);
                        mallOrder.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    Bill.printRetailBillByNb(MyApplication.getContextObject(), order, 25, userBean, nbTotalMoney, nb, originTotalMoneny);
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
                        ToastUtil.showShort(MyApplication.getContextObject(), "网络繁忙请重试" + e.getMessage());
                    }
                }
            });
        }
    }

    private void resetTable() {
        if (getIntent().getBooleanExtra("isHangUp", false)) {
            AVQuery<AVObject> query = new AVQuery<>("HangUpOrder");
            query.getInBackground(getIntent().getStringExtra("hangUpId"), new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    hideDialog();
                    if (e == null) {
                        avObject.put("active", 0);
                        avObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                Intent intent = getIntent();
                                setResult(1, intent);
                                finish();
                            }
                        });
                    } else {
                        Intent intent = getIntent();
                        setResult(1, intent);
                        finish();
                    }
                }
            });
        } else {
            hideDialog();
            Intent intent = getIntent();
            setResult(1, intent);
            finish();
        }
    }

}
