package cn.kuwo.player.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.bigkoo.pickerview.TimePickerView;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.api.MallGoldLogApi;
import cn.kuwo.player.api.MallOrderApi;
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.custom.ShowStatisticsDialog;
import cn.kuwo.player.print.Bill;
import cn.kuwo.player.util.ApiManager;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.DateUtil;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.RechargeUtil;
import cn.kuwo.player.util.StatisticsUtil;
import cn.kuwo.player.util.T;
import cn.kuwo.player.util.ToastUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListFg extends BaseFragment {
    private static String ARG_PARAM = "param_key";
    @BindView(R.id.btn_change_date)
    Button btnChangeDate;
    Unbinder unbinder;
    @BindView(R.id.show_date)
    TextView showDate;
    @BindView(R.id.btn_print)
    Button btnPrint;
    @BindView(R.id.state_res)
    TextView stateRes;
    @BindView(R.id.state_retail)
    TextView stateRetail;
    @BindView(R.id.state_recharge)
    TextView stateRecharge;
    @BindView(R.id.state_hangup)
    TextView stateHangup;
    Unbinder unbinder1;
    @BindView(R.id.ll_state_res)
    LinearLayout llStateRes;
    @BindView(R.id.ll_state_retail)
    LinearLayout llStateRetail;
    @BindView(R.id.ll_state_recharge)
    LinearLayout llStateRecharge;
    @BindView(R.id.ll_state_hangup)
    LinearLayout llStateHangup;
    private Activity mActivity;
    private String mParam;
    private JSONArray offline_operations=new JSONArray();
    @BindView(R.id.gv_table)
    GridView gvTable;
    HashMap<String, Object> ordersDetail;
    OrderListAdapter orderListAdapter;
    List<AVObject> orders = new ArrayList<>();
    List<AVObject> findOrders = new ArrayList<>();
    List<AVObject> testUsers = new ArrayList<>();
    List<AVObject> rechargeOrders = new ArrayList<>();
    Date currentDate;
    private int orderType = -1;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fg_orderlist;
    }

    @Override
    public void initData() {
        currentDate = DateUtil.getCurrentDate();
//        currentDate.setHours(0);
//        currentDate.setMinutes(0);
//        currentDate.setSeconds(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        showDate.setText(sdf.format(currentDate) + "");
        try {

            getOrders(currentDate);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
    }

    private void setDatePickerView() {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2018, 5, 21);
        Calendar endDate = Calendar.getInstance();
        TimePickerView pvTime = new TimePickerView.Builder(getContext(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                currentDate = date;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                showDate.setText(sdf.format(currentDate) + "");
                try {
                    Logger.d(DateUtil.getLasterTimeStamp(currentDate));
                    getOrders(currentDate);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .gravity(Gravity.CENTER)
                .setTitleText("选择查询日期")
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .build();
        pvTime.setDate(Calendar.getInstance());
        pvTime.show();
    }

    /**
     * 获取订单信息
     */
    private void getOrders(final Date date) throws ParseException {
        showDialog();
        Call<ResponseBody> responseBodyCall = ApiManager.getInstance().getRetrofitService().rechargeQuery(DateUtil.getZeroTimeStampBySecond(date),
             DateUtil.getLasterTimeStampBySecond(date),
                "recharge",
                2,
                true
        );
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code()==200||response.code()==201){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        offline_operations = jsonObject.getJSONArray("niu_token_offline_operations");
                        Logger.d(offline_operations);
                        findMallGoldOrder(date);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    T.show(response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });


    }

    private void findMallGoldOrder(final Date date) {
        MallGoldLogApi.finalAllMallGold(date).findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    rechargeOrders = list;
                    MallOrderApi.findMallOrder(date).findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            hideDialog();
                            if (e == null) {
                                orders = list;
                                findOrders();
                                statisticsData();
                                setStateNumber();
                            } else {
                                ToastUtil.showShort(MyApplication.getContextObject(), "网络连接错误");
                            }
                        }
                    });
                }
            }
        });
    }

    private void setStateNumber() {
        HashMap<Integer, Integer> orderTypes = (HashMap<Integer, Integer>) ordersDetail.get("orderTypes");
        if (orderTypes.containsKey(0)) {
            stateRes.setText(orderTypes.get(0) + "笔");
        }
        if (orderTypes.containsKey(1)) {
            stateRetail.setText(orderTypes.get(1) + "笔");
        }
        if (orderTypes.containsKey(2) || rechargeOrders.size() > 0) {
            stateRecharge.setText((orderTypes.containsKey(2) ? orderTypes.get(2) : 0) + rechargeOrders.size() + "笔");
        }
        if (orderTypes.containsKey(3)) {
            stateHangup.setText(orderTypes.get(3) + "笔");
        }

    }

    private void statisticsData() {
        ordersDetail = StatisticsUtil.TotalOrder(orders, rechargeOrders);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mParam = getArguments().getString(ARG_PARAM);  //获取参数
    }

    public static OrderListFg newInstance(String str) {
        OrderListFg orderListFg = new OrderListFg();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM, str);
        orderListFg.setArguments(bundle);
        return orderListFg;
    }


    @OnClick({R.id.btn_change_date, R.id.btn_print, R.id.ll_state_res, R.id.ll_state_retail, R.id.ll_state_recharge, R.id.ll_state_hangup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_change_date:
                setDatePickerView();
                break;
            case R.id.btn_print:
                ShowStatisticsDialog showStatisticsDialog = new ShowStatisticsDialog(ordersDetail, currentDate);
                showStatisticsDialog.show(getActivity().getFragmentManager(), "showstatistic");
                break;
            case R.id.ll_state_res:
                orderType = 0;
                findOrders();
                break;
            case R.id.ll_state_retail:
                orderType = 1;
                findOrders();
                break;
            case R.id.ll_state_recharge:
                orderType = 2;
                findOrders();
                break;
            case R.id.ll_state_hangup:
                orderType = 3;
                findOrders();
                break;
        }
    }

    private void findOrders() {
        findOrders.removeAll(findOrders);
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getInt("type") == orderType || orderType == -1) {
                findOrders.add(orders.get(i));
            }
        }
        if (orderType == 2 || orderType == -1) {
            findOrders.addAll(rechargeOrders);

        }
        orderListAdapter = new OrderListAdapter();
        gvTable.setAdapter(orderListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }


    public class OrderListAdapter extends BaseAdapter {
        private int selectIndex = -1;

        @Override
        public int getCount() {
            return findOrders.size()+offline_operations.length();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_order_list, parent, false);
                holder = new ViewHolder();
                holder.order_date = view.findViewById(R.id.order_date);
                holder.order_state = view.findViewById(R.id.order_state);
                holder.order_state_img = view.findViewById(R.id.order_state_img);
                holder.order_table_number = view.findViewById(R.id.order_table_number);
                holder.order_paysum = view.findViewById(R.id.order_paysum);
                holder.order_detail = view.findViewById(R.id.order_detail);
                holder.order_settle = view.findViewById(R.id.order_settle);
                holder.show_detail = view.findViewById(R.id.show_detail);
                holder.order_memberstyle = view.findViewById(R.id.order_memberstyle);
                holder.card_order = view.findViewById(R.id.card_order);
                holder.btn_reprint = view.findViewById(R.id.btn_reprint);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            if (position<findOrders.size()) {
                final AVObject avObject = findOrders.get(position);
                if (avObject.getClassName().equals("MallOrder")) {
                    if (avObject.getDate("startedAt") != null) {
                        holder.order_date.setText("用餐时间:" + DateUtil.formatDate(avObject.getDate("startedAt")) + "~" + DateUtil.formatDate(avObject.getDate("endAt")));
                        holder.order_table_number.setText("桌号:" + avObject.getString("tableNumber"));
                        holder.order_paysum.setText("用餐人数:" + avObject.getInt("customer"));
                    } else {
                        holder.order_date.setText("点单时间:" + DateUtil.formatDate(avObject.getDate("createdAt")));
                        holder.order_paysum.setText("");
                        holder.order_table_number.setText("");
                    }
                    if (avObject.getAVObject("orderStatus").getObjectId().equals(CONST.OrderState.ORDER_STATUS_FINSIH)) {
                        holder.order_state.setText("订单状态:已完成");
                    } else {
                        holder.order_state.setText("订单状态:已退款");
                    }
                    if (!avObject.getAVUser("user").getObjectId().equals(CONST.ACCOUNT.SYSTEMACCOUNT)) {
                        holder.order_memberstyle.setText("会员用户:" + avObject.getAVUser("user").getString("username"));
                    } else {
                        holder.order_memberstyle.setText("非会员账号");
                    }
                    String commodityList = "菜品详情\n";
                    for (int i = 0; i < avObject.getList("commodityDetail").size(); i++) {
                        HashMap<String, Object> commodityDetail = ObjectUtil.format(avObject.getList("commodityDetail").get(i));
                        commodityList += commodityDetail.get("name").toString() + "*" + commodityDetail.get("number").toString() + "份\n";
                    }
                    if (avObject.getInt("type") == 0) {
                        holder.order_state_img.setBackgroundResource(R.drawable.order_res);
                    } else if (avObject.getInt("type") == 1) {
                        holder.order_state_img.setBackgroundResource(R.drawable.order_retail);
                    } else if (avObject.getInt("type") == 2) {
                        holder.order_state_img.setBackgroundResource(R.drawable.order_recharge);
                    } else if (avObject.getInt("type") == 3) {
                        holder.order_state_img.setBackgroundResource(R.drawable.icon_hangup_state);
                    }
                    holder.order_detail.setText(commodityList);
                    String settleContent = "                                     结账详情\n";
                    settleContent += "订单原价:" + avObject.getDouble("paysum") + "\n";
                    JSONObject reduceDetail = new JSONObject((Map) avObject.get("reduceDetail"));
                    Iterator iterator = reduceDetail.keys();
                    while (iterator.hasNext()) {
                        try {
                            String key = (String) iterator.next();
                            Double value = reduceDetail.getDouble(key);
                            settleContent += key + ":" + "-" + value + "\n";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    settleContent += "优惠总价:-" + avObject.getDouble("reduce") + "\n";
                    Double meatweigth = 0.0;
                    List meatWeights = avObject.getList("meatWeights");
                    for (int i = 0; i < meatWeights.size(); i++) {
                        meatweigth += Double.parseDouble(meatWeights.get(i).toString());
                    }
                    settleContent += "牛肉抵扣重量:" + MyUtils.formatDouble(meatweigth) + "kg\n";
                    settleContent += "实付金额:" + MyUtils.formatDouble(avObject.getDouble("paysum") - avObject.getDouble("reduce")) + "\n";
                    JSONObject escrowDetail = new JSONObject((Map) avObject.get("escrowDetail"));
                    Iterator iterator1 = escrowDetail.keys();
                    while (iterator1.hasNext()) {
                        try {
                            String key = (String) iterator1.next();
                            Double value = escrowDetail.getDouble(key);
                            settleContent += key + ":" + "-" + value + "\n";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    holder.order_settle.setText(settleContent);

                    holder.btn_reprint.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (avObject.getInt("type") == 2) {
                                Bill.reprintSvipBill(avObject);
                            } else {
                                Bill.reprintBill(MyApplication.getContextObject(), avObject);
                            }

                        }
                    });
                } else if (avObject.getClassName().equals("MallGoldLog")) {
                    holder.order_state_img.setBackgroundResource(R.drawable.order_recharge);
                    holder.order_table_number.setText("");
                    holder.order_paysum.setText("");
                    holder.order_date.setText("充值时间:" + DateUtil.formatDate(avObject.getDate("createdAt")));
                    holder.order_memberstyle.setText("会员用户:" + avObject.getAVObject("user").getString("username"));
                    holder.order_detail.setText("消费金充值" + RechargeUtil.findRealMoney(avObject.getDouble("change")) + "元");
                    String payContent = "";
                    switch (avObject.getInt("escrow")) {
                        case 3:
                            payContent = "支付宝支付";
                            break;
                        case 4:
                            payContent = "微信支付";
                            break;
                        case 5:
                            payContent = "银行卡支付";
                            break;
                        case 6:
                            payContent = "现金支付";
                            break;
                    }
                    holder.order_settle.setText("支付方式:" + payContent);
                    holder.btn_reprint.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AVQuery<AVObject> query = new AVQuery<>("_User");
                            query.getInBackground(avObject.getString("market"), new GetCallback<AVObject>() {
                                @Override
                                public void done(AVObject userObject, AVException e) {
                                    if (e == null) {
                                        Bill.reprintRechargeBill(avObject, userObject.getString("realName") != null && !userObject.getString("realName").equals("") ? userObject.getString("realName") : userObject.getString("nickName"));
                                    } else {
                                        ToastUtil.showShort(MyApplication.getContextObject(), e.getMessage());
                                    }
                                }
                            });


                        }
                    });
                }
                holder.card_order.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectIndex = position;
                        if (holder.show_detail.getVisibility() == View.VISIBLE) {
                            holder.show_detail.setVisibility(View.GONE);
                        } else {
                            holder.show_detail.setVisibility(View.VISIBLE);
                        }
                        orderListAdapter.notifyDataSetChanged();
                    }
                });
                if (position != selectIndex) {
                    holder.show_detail.setVisibility(View.GONE);
                }
            }else{

            }
            return view;
        }

        private class ViewHolder {
            TextView order_date;
            TextView order_state;
            TextView order_table_number;
            TextView order_paysum;
            TextView order_detail;
            TextView order_settle;
            TextView order_state_img;
            TextView order_memberstyle;
            Button btn_reprint;
            LinearLayout show_detail;
            CardView card_order;
        }
    }

}
