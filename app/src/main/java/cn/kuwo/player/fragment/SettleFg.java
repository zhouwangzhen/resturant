package cn.kuwo.player.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import cn.kuwo.player.adapter.ShowGoodAdapter;
import cn.kuwo.player.api.CouponApi;
import cn.kuwo.player.api.HangUpApi;
import cn.kuwo.player.api.TableApi;
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.bean.UserBean;
import cn.kuwo.player.custom.ScanUserFragment;
import cn.kuwo.player.custom.ShowCouponFragment;
import cn.kuwo.player.custom.ShowFuncFragment;
import cn.kuwo.player.custom.ShowReduceListFragment;
import cn.kuwo.player.event.CouponEvent;
import cn.kuwo.player.event.OrderDetail;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.CameraProvider;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.ToastUtil;

import static android.app.Activity.RESULT_OK;

public class SettleFg extends BaseFragment {
    private static String tableId = "param_key";
    private static boolean isHangUp = false;
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
    @BindView(R.id.cb_use_svip)
    CheckBox cbUseSvip;
    @BindView(R.id.svip_all_reduce)
    TextView svipAllReduce;
    @BindView(R.id.min_pay_money)
    TextView minPayMoney;
    @BindView(R.id.store_reduce_money)
    TextView storeReduceMoney;
    @BindView(R.id.my_svip_reduce_money)
    TextView mySvipReduceMoney;
    @BindView(R.id.my_svip_reduce_weight)
    TextView mySvipReduceWeight;
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
    @BindView(R.id.ll_max_reduce)
    LinearLayout llMaxReduce;
    @BindView(R.id.ll_my_reduce)
    LinearLayout llMyReduce;
    @BindView(R.id.pay_content)
    TextView payContent;
    @BindView(R.id.table_number)
    TextView tableNumber;
    @BindView(R.id.full_reduce_money)
    TextView fullreduceMoney;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private int REQUEST_CODE_SCAN = 111;
    private int REQUEST_FUNC = 100;
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
    private CouponEvent onlineCouponEvent = null;
    private CouponEvent offlineCouponEvent = null;
    private Boolean isSvip = false;
    private List<Object> orders = new ArrayList<>();
    private List<Object> useExchangeList = new ArrayList<>();
    private List<Double> weights = new ArrayList<>();
    private List<Double> prices = new ArrayList<>();
    private HashMap<String, Object> otherTableOrders = new HashMap<>();
    private ArrayList<String> selectTableNumber = new ArrayList<>();
    private ArrayList<String> selectTableIds = new ArrayList<>();

    LinearLayoutManager linearLayoutManager;
    ShowGoodAdapter showGoodAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fg_settle;
    }

    @Override
    public void initData() {
        showDialog();
        final AVQuery<AVObject> table;
        if (isHangUp){
             table = new AVQuery<>("HangUpOrder");
        }else{
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
                        AVCloud.callFunctionInBackground("svip", parameter, new FunctionCallback<Map<String, Object>>() {
                            @Override
                            public void done(Map<String, Object> objectMap, AVException e) {
                                if (e == null) {
                                    hideDialog();
                                    AVObject userObject = tableAVObject.getAVObject("user");
                                    llShowMember.setVisibility(View.VISIBLE);
                                    Double whiteBarBalance = MyUtils.formatDouble(MyUtils.formatDouble(userObject.getDouble("gold")) - MyUtils.formatDouble(userObject.getDouble("arrears")));
                                    Double storedBalance = MyUtils.formatDouble(userObject.getDouble("stored"));
                                    AVFile avatar = (AVFile) userObject.get("avatar");
                                    Glide.with(MyApplication.getContextObject()).load(avatar.getUrl()).into(userAvatar);
                                    userTel.setText("用户手机号:" + userObject.getString("username"));
                                    userStored.setText("消费金:" + storedBalance);
                                    userWhitebar.setText("白条:" + whiteBarBalance);
                                    userMeatweight.setText("牛肉额度:" + objectMap.get("meatWeight").toString() + "kg");
                                    hasMeatWeight = Double.parseDouble(objectMap.get("meatWeight").toString());
                                    useMeatId = objectMap.get("meatId").toString().length() > 0 ? objectMap.get("meatId").toString() : "";
                                    if ((Boolean) objectMap.get("svip")) {
                                        svipAvatar.setVisibility(View.VISIBLE);
                                        userType.setText("超牛会员");
                                        isSvip = true;
                                    } else {
                                        svipAvatar.setVisibility(View.GONE);
                                        userType.setText("普通会员");
                                        isSvip = false;
                                    }
                                    signUser.setText("退出登录");
                                    fetchCommodity(tableAVObject);
                                } else {
                                    hideDialog();
                                    fetchCommodity(tableAVObject);
                                    ToastUtil.showShort(getContext(), e.getMessage());
                                }
                            }
                        });
                    } else {
                        hideDialog();
                        fetchCommodity(tableAVObject);
                        llShowMember.setVisibility(View.INVISIBLE);
                        signUser.setText("用户登录");
                    }

                }else{
                    ToastUtil.showShort(getContext(), "获取订单信息错误"+e.getMessage());
                }

            }
        });
        cbUseSvip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                refreshList();
            }
        });
        setListener();

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

    public static SettleFg newInstance(String str,Boolean isHangUp) {
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
        isHangUp=getArguments().getBoolean("isHangUp");
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
        showGoodAdapter = new ShowGoodAdapter(getContext(), tableAVObject, orders);
        recycleScanGood.setAdapter(showGoodAdapter);
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
        orders = ProductUtil.calOtherOder(orders, otherTableOrders);
        Logger.d("final订单:" + orders.size());
        Logger.d("origin订单:" + tableAVObject.getList("order").size());
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
        totalNumber.setText(orders.size() + "");
        originTotalMoneny = ProductUtil.calculateTotalMoney(orders, new ArrayList<Object>());
        orginPrice.setText(originTotalMoneny + "");
        if (tableAVObject.getAVObject("user") != null) {
            cbUseSvip.setVisibility(View.VISIBLE);
        } else {
            cbUseSvip.setVisibility(View.GONE);
        }
        meatReduceMoney = ProductUtil.calMeatduceMoney(orders, prices);
        meatReduceWeight = ProductUtil.calMeatduceWeight(orders, weights);
        svipAllReduce.setText(meatReduceWeight + "kg");
        if (tableAVObject.getAVObject("user") != null) {
            if (hasMeatWeight >= meatReduceWeight) {//用户牛肉大于等于可兑换的牛肉重量
                mySvipReduceWeight.setText(meatReduceWeight + "kg");
                mySvipReduceMoney.setText("-" + meatReduceMoney);
                myMeatReduceWeight = meatReduceWeight;
                myMeatReduceMoney = meatReduceMoney;
                List<Object> centerOrder = new ArrayList<>();
                try {
                    centerOrder = ObjectUtil.deepCopy(orders);
                } catch (Exception e) {
                    centerOrder = new ArrayList<>();
                    e.printStackTrace();
                }
                useExchangeList = ProductUtil.canExchangeMeatList(centerOrder, hasMeatWeight, weights);
            } else {
                List<Object> centerOrder = new ArrayList<>();
                try {
                    centerOrder = ObjectUtil.deepCopy(orders);
                } catch (Exception e) {
                    centerOrder = new ArrayList<>();
                    e.printStackTrace();
                }
                useExchangeList = ProductUtil.canExchangeMeatList(centerOrder, hasMeatWeight, weights);
                Logger.d(useExchangeList);
                myMeatReduceWeight = ProductUtil.calculateTotalWeight(useExchangeList);
                myMeatReduceMoney = ProductUtil.calculateTotalMoney(useExchangeList);
                mySvipReduceWeight.setText(myMeatReduceWeight + "kg");
                mySvipReduceMoney.setText("-" + myMeatReduceMoney);
            }

        } else {
            mySvipReduceWeight.setText("0.0kg");
            mySvipReduceMoney.setText("-0.0");
        }
        if (onlineCouponEvent != null) {
            onlineCouponMoney = onlineCouponEvent.getMoney();
            tvOnlineMoeny.setText("-" + onlineCouponMoney);
            tvOnlineContent.setText(onlineCouponEvent.getContent());
        }
        if (offlineCouponEvent != null) {
            offlineCouponMoney = offlineCouponEvent.getMoney();
            Logger.d(offlineCouponMoney);
            tvOfflineMoeny.setText("-" + offlineCouponMoney);
            tvOfflineContent.setText(offlineCouponEvent.getContent());
        }
        if (cbUseSvip.isChecked()) {
            activityReduceMoney = MyUtils.formatDouble((originTotalMoneny - myMeatReduceMoney - offlineCouponMoney - onlineCouponMoney) * (1 - MyUtils.getDayRate()));
            if (activityReduceMoney < 0) activityReduceMoney = 0.0;
            actualTotalMoneny = originTotalMoneny - offlineCouponMoney - onlineCouponMoney - activityReduceMoney - myMeatReduceMoney;
            if (actualTotalMoneny < 0) actualTotalMoneny = 0.0;
            totalMoney.setText("￥" + actualTotalMoneny + "元");
            storeReduceMoney.setText("-" + activityReduceMoney);
        } else {
            activityReduceMoney = MyUtils.formatDouble((originTotalMoneny - offlineCouponMoney - onlineCouponMoney) * (1 - MyUtils.getDayRate()));
            if (activityReduceMoney < 0) activityReduceMoney = 0.0;
            actualTotalMoneny = originTotalMoneny - offlineCouponMoney - onlineCouponMoney - activityReduceMoney;
            if (actualTotalMoneny < 0) actualTotalMoneny = 0.0;
            totalMoney.setText("￥" + actualTotalMoneny + "元");
            storeReduceMoney.setText("-" + activityReduceMoney);
        }

        fullReduceMoney = MyUtils.formatDouble(ProductUtil.calFullReduceMoney(actualTotalMoneny) > actualTotalMoneny ? actualTotalMoneny : ProductUtil.calFullReduceMoney(actualTotalMoneny));
        fullreduceMoney.setText("-" + fullReduceMoney);
        actualTotalMoneny -= fullReduceMoney;
        actualTotalMoneny = MyUtils.formatDouble(actualTotalMoneny);
        totalMoney.setText("￥" + actualTotalMoneny + "元");
        minPayMoney.setText("-" + meatReduceMoney);
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

    @OnClick({R.id.sign_user, R.id.btn_pay, R.id.ll_max_reduce, R.id.ll_my_reduce, R.id.more_fuc})
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
                                        tableAVObject.put("user", null);
                                        tableAVObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                if (e == null) {
                                                    ToastUtil.showShort(MyApplication.getContextObject(), "清空用户数据成功");
                                                    initData();
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
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                PayFg payFg = PayFg.newInstance("");
                Bundle bundle = new Bundle();
                OrderDetail orderDetail = new OrderDetail(tableAVObject, hasMeatWeight, originTotalMoneny,
                        actualTotalMoneny, meatReduceWeight, meatReduceMoney, myMeatReduceWeight, myMeatReduceMoney, cbUseSvip.isChecked(),
                        onlineCouponEvent, offlineCouponEvent, activityReduceMoney, isSvip, useExchangeList, useMeatId, ProductUtil.calExchangeMeatList(orders), orders, selectTableIds, selectTableNumber, fullReduceMoney,isHangUp);
                bundle.putSerializable("table", (Serializable) orderDetail);
                payFg.setArguments(bundle);
                ft.replace(R.id.fragment_content, payFg, "pay").commit();
                break;
            case R.id.ll_max_reduce:
                ShowReduceListFragment showReduceListFragment = new ShowReduceListFragment(ProductUtil.calExchangeMeatList(orders), 0);
                showReduceListFragment.show(getFragmentManager(), "showreducelist");
                break;
            case R.id.ll_my_reduce:
                showReduceListFragment = new ShowReduceListFragment(useExchangeList, 1);
                showReduceListFragment.show(getFragmentManager(), "showreducelist");
                break;
            case R.id.more_fuc:
                ShowFuncFragment showFuncFragment = new ShowFuncFragment();
                showFuncFragment.setTargetFragment(this, REQUEST_FUNC);
                showFuncFragment.show(getFragmentManager(), "showfunc");
                break;
        }
    }


    /**
     * 判断是否有摄像头
     */
    private void chooseScanType() {
        if (CameraProvider.hasCamera()) {
            if (MyUtils.getCameraPermission(getContext())) {
                Intent intent = new Intent(getActivity(), CaptureActivity.class);
                intent.putExtra(Constant.INTENT_ZXING_CONFIG, MyUtils.caremaSetting());
                startActivityForResult(intent, REQUEST_CODE_SCAN);
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
                            showDialog();
                            Map<String, Object> parameter = new HashMap<String, Object>();
                            parameter.put("userID", object.get("objectId").toString());
                            AVCloud.callFunctionInBackground("svip", parameter, new FunctionCallback<Map<String, Object>>() {
                                @Override
                                public void done(Map<String, Object> objectMap, AVException e) {
                                    if (e == null) {
                                        hideDialog();
                                        Logger.d("用户登录");
                                        llShowMember.setVisibility(View.VISIBLE);
                                        Double whiteBarBalance = MyUtils.formatDouble(Double.parseDouble(object.get("gold").toString()) - Double.parseDouble(object.get("arrears").toString()));
                                        Double storedBalance = MyUtils.formatDouble(Double.parseDouble(object.get("stored").toString()));
                                        Glide.with(MyApplication.getContextObject()).load(object.get("avatarurl").toString()).into(userAvatar);
                                        userTel.setText("用户手机号:" + object.get("username").toString());
                                        userStored.setText("消费金:" + storedBalance);
                                        userWhitebar.setText("白条:" + whiteBarBalance);
                                        userMeatweight.setText("牛肉额度:" + objectMap.get("meatWeight").toString() + "kg");
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
                                        if ((Boolean) objectMap.get("svip")) {
                                            svipAvatar.setVisibility(View.VISIBLE);
                                            userType.setText("超牛会员");
                                        } else {
                                            svipAvatar.setVisibility(View.GONE);
                                            userType.setText("普通会员");
                                        }
                                        initData();
                                        signUser.setText("退出登录");
                                    } else {
                                        hideDialog();
                                        ToastUtil.showShort(getContext(), e.getMessage());
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
        } else if (requestCode == REQUEST_FUNC) {
            setFunc(resultCode);
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
            Logger.d(event);
            onlineCouponEvent = event;
            refreshList();
        } else if (event.getType() == 2) {
            offlineCouponEvent = event;
            refreshList();
            Logger.d(offlineCouponEvent);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UserMessgae(UserBean userBean) {
        if (userBean.getCallbackCode() == CONST.UserCode.SCANCUSTOMER) {
            llShowMember.setVisibility(View.VISIBLE);
            Glide.with(MyApplication.getContextObject()).load(userBean.getAvatar()).into(userAvatar);
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
            if (userBean.getSvip()) {
                svipAvatar.setVisibility(View.VISIBLE);
                userType.setText("超牛会员");
            } else {
                svipAvatar.setVisibility(View.GONE);
                userType.setText("普通会员");
            }
            signUser.setText("退出登录");
            Logger.d(userBean);
        }
    }


    private void setFunc(int resultCode) {
        switch (resultCode) {
            case 0:
                chooseMerge();//合并订单结账
                break;
            case 1:
                if (isHangUp){
                    ToastUtil.showShort(MyApplication.getContextObject(),"挂账订单不可继续挂账");
                }else{
                    hangUp();//挂账
                }

                break;
        }

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
                        String  text = builder.getEditText().getText().toString();
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
    private void HangUpOrder(String content){
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
}
