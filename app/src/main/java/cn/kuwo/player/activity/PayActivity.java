package cn.kuwo.player.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.base.BaseActivity;
import cn.kuwo.player.bean.UserBean;
import cn.kuwo.player.event.OrderDetail;
import cn.kuwo.player.event.PrintEvent;
import cn.kuwo.player.print.Bill;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.ToastUtil;

public class PayActivity extends BaseActivity {
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
    @BindView(R.id.orgin_price)
    TextView orginPrice;
    @BindView(R.id.svip_all_reduce)
    TextView svipAllReduce;
    @BindView(R.id.min_pay_money)
    TextView minPayMoney;
    @BindView(R.id.my_svip_reduce_weight)
    TextView mySvipReduceWeight;
    @BindView(R.id.my_svip_reduce_money)
    TextView mySvipReduceMoney;
    @BindView(R.id.tv_offline_content)
    TextView tvOfflineContent;
    @BindView(R.id.tv_offline_moeny)
    TextView tvOfflineMoeny;
    @BindView(R.id.tv_online_content)
    TextView tvOnlineContent;
    @BindView(R.id.tv_online_moeny)
    TextView tvOnlineMoeny;
    @BindView(R.id.store_reduce_money)
    TextView storeReduceMoney;
    @BindView(R.id.pay_content)
    TextView payContent;
    @BindView(R.id.total_money)
    TextView totalMoney;
    @BindView(R.id.cb_use_svip)
    CheckBox cbUseSvip;
    @BindView(R.id.gv_pay_style)
    GridView gvPayStyle;
    @BindView(R.id.btn_finish_pay)
    Button btnFinishPay;
    @BindView(R.id.full_reduce_money)
    TextView fullreduceMoney;
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
    @BindView(R.id.black_five_money)
    TextView blackFiveMoney;
    @BindView(R.id.ll_black_five)
    LinearLayout llBlackFive;
    @BindView(R.id.ll_full_reduce)
    LinearLayout llFullReduce;
    @BindView(R.id.ll_store_reduce)
    LinearLayout llStoreReduce;
    @BindView(R.id.store_reduce_rate)
    TextView storeReduceRate;
    private OrderDetail orderDetail;
    private List<Integer> paymentTypes = new ArrayList<>();
    private JSONObject jsonReduce;
    private int selectType = 0;
    private int escrow = -1;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private Context mContext;

    private String tableId = "";
    private String ensureContent = "";
    private String orderId = "";

    private Double whiteBarBalance;
    private Double storedBalance;
    private Boolean paySuccess = false;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_pay;
    }

    @Override
    public void initData() {
        orderDetail = (OrderDetail) getIntent().getSerializableExtra("table");
        mContext = this;
        setData();
        setPayment();
    }

    private void setPayment() {
        final PaymentAdapter paymentAdapter = new PaymentAdapter();
        gvPayStyle.setAdapter(paymentAdapter);
        gvPayStyle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectType = position;
                ensureContent = ProductUtil.setPaymentContent(paymentTypes.get(position), orderDetail.getActualMoney(), storedBalance, whiteBarBalance);
                paymentAdapter.notifyDataSetChanged();
            }
        });
        btnFinishPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QMUIDialog.MessageDialogBuilder(mContext)
                        .setTitle("收款提示")
                        .setMessage(ensureContent)
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
                                toFinishOrder();
                            }
                        })
                        .create(mCurrentDialogStyle).show();
            }
        });
    }

    private void toFinishOrder() {
        showDialog();
        escrow = paymentTypes.get(selectType);
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (orderDetail.getUserBean() != null) {
            if (escrow == 1 || escrow == 11 || escrow == 12) {//纯消费金和白条支付
                parameters.put("paymentType", "577b364a79bc440032772ba5");
            } else if (escrow == 3 || escrow == 4 || escrow == 5 || escrow == 6) {//纯线下第三方支付
                parameters.put("paymentType", "59794daf128fe10056f43170");
            } else {//混合支付
                parameters.put("paymentType", "59794db8ac502e0069a377d0");
            }
            parameters.put("customerId", orderDetail.getUserBean().getId());
        } else {
            parameters.put("paymentType", "59794daf128fe10056f43170");
            parameters.put("customerId", CONST.ACCOUNT.SYSTEMACCOUNT);
        }
        final List order = orderDetail.getOrders();
        List<String> ids = ProductUtil.calTotalIds(order);
        parameters.put("commodityids", ids);
        parameters.put("paysum", orderDetail.getActualMoney());
        parameters.put("sum", orderDetail.getTotalMoney());
        Logger.d(parameters);
        AVCloud.callFunctionInBackground("offlineMallOrder", parameters, new FunctionCallback<Map<String, Map<String, Object>>>() {
            @Override
            public void done(Map<String, Map<String, Object>> map, AVException e) {
                if (e == null) {
                    orderId = map.get("order").get("objectId").toString();
                    AVObject mallOrder = AVObject.createWithoutData("MallOrder", orderId);
                    mallOrder.put("cashier", AVObject.createWithoutData("_User", new SharedHelper(MyApplication.getContextObject()).read("cashierId")));
                    mallOrder.put("orderStatus", AVObject.createWithoutData("MallOrderStatus", CONST.OrderState.ORDER_STATUS_FINSIH));
                    mallOrder.put("escrow", escrow);
                    mallOrder.put("offline", true);
                    mallOrder.put("store", 1);
                    mallOrder.put("outside", true);
                    mallOrder.put("commodityDetail", orderDetail.getOrders());
                    if (orderDetail.getChooseReduce() && orderDetail.getUserBean() != null && orderDetail.getMyReduceMoney() > 0) {
                        mallOrder.put("meatWeights", ProductUtil.listToList(orderDetail.getUseExchangeList()));
                        mallOrder.put("meatDetail", ProductUtil.listToObject(orderDetail.getUseExchangeList()));
                        mallOrder.put("useMeat", AVObject.createWithoutData("Meat", orderDetail.getUseMeatId()));

                    }
                    if (getIntent().getBooleanExtra("isHangUp", false)) {
                        mallOrder.put("hangUp", true);
                        mallOrder.put("message", getIntent().getStringExtra("remark"));
                        mallOrder.put("type", 4);

                    }
                    Map<String, Double> escrowDetail = ProductUtil.managerEscrow(orderDetail.getActualMoney(), escrow, orderDetail.getUserBean());
                    mallOrder.put("escrowDetail", escrowDetail);
                    mallOrder.put("type", 1);
                    jsonReduce = new JSONObject();
                    try {
                        if (orderDetail.getChooseReduce() && orderDetail.getUserBean() != null && orderDetail.getMyReduceMoney() > 0) {
                            jsonReduce.put("牛肉抵扣金额", orderDetail.getMyReduceMoney());
                        }
                        if (orderDetail.getBlackfiveMoney() > 0) {
                            jsonReduce.put("周五冻肉半价优惠", orderDetail.getBlackfiveMoney());
                        }
                        if (orderDetail.getOnlineCouponEvent() != null) {
                            jsonReduce.put(orderDetail.getOnlineCouponEvent().getContent(), orderDetail.getOnlineCouponEvent().getMoney());
                            mallOrder.put("useUserCoupon", AVObject.createWithoutData("Coupon", orderDetail.getOnlineCouponEvent().getId()));
                        }
                        if (orderDetail.getOfflineCouponEvent() != null) {
                            jsonReduce.put(orderDetail.getOfflineCouponEvent().getContent(), orderDetail.getOfflineCouponEvent().getMoney());
                            mallOrder.put("useSystemCoupon", AVObject.createWithoutData("Coupon", orderDetail.getOfflineCouponEvent().getId()));
                        }

                        if (orderDetail.getActivityMoney() > 0) {
                            jsonReduce.put("开业" + MyUtils.getDayRate() + "折优惠", orderDetail.getActivityMoney());
                        }
                        if (orderDetail.getFullReduceMoney() > 0) {
                            jsonReduce.put("满减优惠", orderDetail.getFullReduceMoney());
                        }
                        if (orderDetail.getRate() != 100) {
                            String content = orderDetail.getRateContent() + orderDetail.getRate() + "折优惠";
                            jsonReduce.put(content, orderDetail.getRateReduceMoney());
                        }
                        if (orderDetail.getDeleteoddMoney() > 0) {
                            jsonReduce.put("抹零", orderDetail.getDeleteoddMoney());
                        }
                        mallOrder.put("reduceDetail", jsonReduce);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    checkExplode();
                    mallOrder.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            hideDialog();
                            if (e == null) {
                                showDialog();
                                paySuccess = true;
                                Bill.printRetailBill(MyApplication.getContextObject(), orderDetail, jsonReduce, escrow, orderDetail.getUserBean());
                                ToastUtil.showShort(MyApplication.getContextObject(), "订单结算完成");

                            } else {
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

    private void checkExplode() {
        if (!SharedHelper.readBoolean("test")) {
            final int explodeNumber = ProductUtil.calExplodeNumbers(orderDetail.getOrders());
            if (ProductUtil.calExplodeNumbers(orderDetail.getOrders()) > 0) {
                final AVQuery<AVObject> query = new AVQuery<>("OffineControl");
                query.whereEqualTo("store", 1);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
                            AVObject avObject = list.get(0);
                            avObject.put("number", avObject.getInt("number") + explodeNumber);
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e != null) {
                                        checkExplode();
                                    }
                                }
                            });
                        }
                    }
                });
            }
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

    private void setData() {
        signUser.setVisibility(View.GONE);
        orginPrice.setText("￥" + orderDetail.getTotalMoney() + "");
        svipAllReduce.setText("" + orderDetail.getMaxReduceWeight() + "kg");
        minPayMoney.setText("" + orderDetail.getMaxReduceMoney());
        mySvipReduceWeight.setText(orderDetail.getMyReduceWeight() + "kg");
        mySvipReduceMoney.setText(orderDetail.getMyReduceMoney() + "");
        storeReduceMoney.setText("-" + orderDetail.getActivityMoney());
        totalMoney.setText("￥" + orderDetail.getActualMoney());
        fullreduceMoney.setText("-" + orderDetail.getFullReduceMoney());
        if (orderDetail.getFullReduceMoney() > 0) {
            llFullReduce.setVisibility(View.VISIBLE);
        } else {
            llFullReduce.setVisibility(View.GONE);
        }
        if (orderDetail.getActivityMoney() > 0) {
            llStoreReduce.setVisibility(View.VISIBLE);
            storeReduceRate.setText("开业" + MyUtils.getDayRate() + "折优惠");
        } else {
            llStoreReduce.setVisibility(View.GONE);
        }
        if (orderDetail.getOfflineCouponEvent() != null) {
            tvOfflineContent.setText(orderDetail.getOfflineCouponEvent().getContent());
            tvOfflineMoeny.setText("-" + orderDetail.getOfflineCouponEvent().getMoney());
        }
        if (orderDetail.getOnlineCouponEvent() != null) {
            tvOnlineContent.setText(orderDetail.getOnlineCouponEvent().getContent());
            tvOnlineMoeny.setText("-" + orderDetail.getOnlineCouponEvent().getMoney());
        }
        if (orderDetail.getDeleteoddMoney() > 0) {
            llDeleteOdd.setVisibility(View.VISIBLE);
            deleteOddMoney.setText("-" + orderDetail.getDeleteoddMoney());
        } else {
            llDeleteOdd.setVisibility(View.GONE);
        }
        if (orderDetail.getBlackfiveMoney() > 0) {
            llBlackFive.setVisibility(View.VISIBLE);
            blackFiveMoney.setText("-" + orderDetail.getBlackfiveMoney());
        } else {
            llBlackFive.setVisibility(View.GONE);
        }
        if (orderDetail.getRate() != 100) {
            llRateReduce.setVisibility(View.VISIBLE);
            rateReduceContent.setText(orderDetail.getRateContent() + MyUtils.formatDouble((double) orderDetail.getRate() / 10) + "折优惠");
            rateReduceMoney.setText("-" + orderDetail.getRateReduceMoney());

        } else {
            llRateReduce.setVisibility(View.GONE);
        }
        if (orderDetail.getUserBean() != null) {
            setUserInfo(orderDetail.getUserBean());
        } else {
            paymentTypes.add(3);
            paymentTypes.add(4);
            paymentTypes.add(5);
            paymentTypes.add(6);
            paymentTypes.add(21);
            paymentTypes.add(22);
        }
        ensureContent = ProductUtil.setPaymentContent(paymentTypes.get(0), orderDetail.getActualMoney(), storedBalance, whiteBarBalance);

    }

    private void setUserInfo(UserBean user) {
        llShowMember.setVisibility(View.VISIBLE);
        userTel.setText("用户手机号:" + user.getUsername());
        whiteBarBalance = user.getBalance();
        storedBalance = user.getStored();
        userStored.setText("消费金:" + storedBalance);
        userWhitebar.setText("白条:" + whiteBarBalance);
        userMeatweight.setText("牛肉额度:" + orderDetail.getMyMeatWeight() + "kg");
        if (orderDetail.getSvip()) {
            svipAvatar.setVisibility(View.VISIBLE);
            userType.setText("超牛会员");
        } else {
            svipAvatar.setVisibility(View.GONE);
            userType.setText("普通会员");
        }
        addPayment(orderDetail.getActualMoney(), whiteBarBalance, storedBalance);
    }

    private void addPayment(Double actualMoney, Double whiteBarBalance, Double storedBalance) {
        if (whiteBarBalance < 0.0) whiteBarBalance = 0.0;
        if (storedBalance < 0) storedBalance = 0.0;

        if (storedBalance >= actualMoney) {
            paymentTypes.add(1);
        } else if (storedBalance == 0 && whiteBarBalance >= actualMoney) {
            paymentTypes.add(11);
        } else if (storedBalance > 0 && whiteBarBalance + storedBalance >= actualMoney) {
            paymentTypes.add(12);
        } else if (storedBalance > 0 && whiteBarBalance == 0) {
            paymentTypes.add(7);
            paymentTypes.add(8);
            paymentTypes.add(9);
            paymentTypes.add(10);
        } else if (whiteBarBalance > 0 && storedBalance == 0) {
            paymentTypes.add(13);
            paymentTypes.add(14);
            paymentTypes.add(15);
            paymentTypes.add(16);
        } else if (whiteBarBalance > 0 && storedBalance > 0 && whiteBarBalance + storedBalance < actualMoney) {
            paymentTypes.add(17);
            paymentTypes.add(18);
            paymentTypes.add(19);
            paymentTypes.add(20);
        }
        paymentTypes.add(3);
        paymentTypes.add(4);
        paymentTypes.add(5);
        paymentTypes.add(6);
        paymentTypes.add(21);
        paymentTypes.add(22);
        ensureContent = ProductUtil.setPaymentContent(paymentTypes.get(0), orderDetail.getActualMoney(), storedBalance, whiteBarBalance);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    public class PaymentAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return paymentTypes.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(MyApplication.getContextObject()).inflate(R.layout.adapter_payment, parent, false);
                holder = new ViewHolder();
                holder.pay_money = (TextView) view.findViewById(R.id.pay_money);
                holder.cb_pay_name = (TextView) view.findViewById(R.id.cb_pay_name);
                holder.image_avatar = (TextView) view.findViewById(R.id.image_avatar);

                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            ProductUtil.setPaymentDetail(orderDetail, paymentTypes.get(i), storedBalance, whiteBarBalance, holder.cb_pay_name, holder.pay_money);
            if (i == selectType) {
                holder.image_avatar.setBackground(getResources().getDrawable(R.drawable.icon_select));
            } else {
                holder.image_avatar.setBackground(getResources().getDrawable(R.drawable.icon_no_select));

            }

            return view;
        }

        private class ViewHolder {
            TextView pay_money;
            TextView cb_pay_name;
            TextView image_avatar;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PrintEvent event) {
        if (event.getCode() == 0) {
            resetTable();
        } else if (event.getCode() == 1) {
            new QMUIDialog.MessageDialogBuilder(mContext)
                    .setTitle("温馨提示")
                    .setMessage(event.getMessage())
                    .addAction("稍后再说", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            resetTable();
                        }
                    })
                    .addAction("重试", new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            Bill.printRetailBill(MyApplication.getContextObject(), orderDetail, jsonReduce, escrow, orderDetail.getUserBean());
                        }
                    })
                    .setCanceledOnTouchOutside(false)
                    .create(mCurrentDialogStyle).show();
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

    @Override
    public void onBackPressed() {
        if (paySuccess) {

        } else {
            super.onBackPressed();
        }

    }
}
