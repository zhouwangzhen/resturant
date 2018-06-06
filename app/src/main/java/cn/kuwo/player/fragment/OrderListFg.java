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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.bigkoo.pickerview.TimePickerView;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import org.json.JSONException;
import org.json.JSONObject;

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
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.custom.ShowStatisticsDialog;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.DateUtil;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.ToastUtil;

public class OrderListFg extends BaseFragment {
    private static String ARG_PARAM = "param_key";
    @BindView(R.id.btn_change_date)
    Button btnChangeDate;
    Unbinder unbinder;
    @BindView(R.id.show_date)
    TextView showDate;
    @BindView(R.id.btn_print)
    Button btnPrint;
    private Activity mActivity;
    private String mParam;
    @BindView(R.id.gv_table)
    GridView gvTable;
    HashMap<String, Object> ordersDetail;
    OrderListAdapter orderListAdapter;
    List<AVObject> orders = new ArrayList<>();
    List<AVObject> testUsers = new ArrayList<>();
    Date currentDate;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fg_orderlist;
    }

    @Override
    public void initData() {
        getTestUser();
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
                currentDate.setHours(0);
                currentDate.setMinutes(0);
                currentDate.setSeconds(0);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                showDate.setText(sdf.format(currentDate) + "");
                try {
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
     * 获取测试用户信息
     */
    private void getTestUser() {
        showDialog();
        AVQuery<AVObject> user = new AVQuery<>("_User");
        user.whereEqualTo("test", true);
        user.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    testUsers = list;
                     currentDate = DateUtil.getCurrentDate();
                    currentDate.setHours(0);
                    currentDate.setMinutes(0);
                    currentDate.setSeconds(0);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    showDate.setText(sdf.format(currentDate) + "");
                    try {
                        getOrders(currentDate);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    hideDialog();
                    ToastUtil.showLong(MyApplication.getContextObject(), "网络错误");
                }
            }
        });
    }

    /**
     * 获取订单信息
     */
    private void getOrders(Date date) throws ParseException {
        showDialog();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Long time = date.getTime();
        Long nextTime = time + 24 * 60 * 60 * 1000;
        String d = sdf.format(nextTime);
        Date nextDate = sdf.parse(d);
        AVObject mallOrderStatusFinsh = AVObject.createWithoutData("MallOrderStatus", CONST.OrderState.ORDER_STATUS_FINSIH);
        AVObject mallOrderStatusRefund = AVObject.createWithoutData("MallOrderStatus", CONST.OrderState.ORDER_STATUS_CANCEL);
        List<AVObject> orderSratus = new ArrayList<AVObject>();
        orderSratus.add(mallOrderStatusFinsh);
        orderSratus.add(mallOrderStatusRefund);
        AVQuery<AVObject> mallOrder = new AVQuery<>("MallOrder");
        mallOrder.whereEqualTo("store", CONST.STORECODE);
        mallOrder.whereEqualTo("offline", true);
        mallOrder.whereEqualTo("active", 1);
//        mallOrder.whereNotContainedIn("user", testUsers);
        mallOrder.include("user");
        mallOrder.include("orderStatus");
        mallOrder.include("paymentType");
        mallOrder.include("cashier");
        mallOrder.include("market");
        mallOrder.include("useSystemCoupon.type");
        mallOrder.include("useUserCoupon.type");
        mallOrder.orderByDescending("createdAt");
        mallOrder.addDescendingOrder("endAt");
        mallOrder.whereGreaterThan("createdAt", date);
        mallOrder.whereLessThan("createdAt", nextDate);
        mallOrder.whereContainedIn("orderStatus", orderSratus);
        mallOrder.limit(1000);
        mallOrder.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                hideDialog();
                if (e == null) {
                    orders = list;
                    orderListAdapter = new OrderListAdapter();
                    gvTable.setAdapter(orderListAdapter);
                    statisticsData();
                } else {
                    ToastUtil.showShort(MyApplication.getContextObject(), "网络连接错误");
                }
            }
        });
    }

    private void statisticsData() {
         ordersDetail = ProductUtil.statisticsTotalOrder(orders);
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


    @OnClick({R.id.btn_change_date, R.id.btn_print})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_change_date:
                setDatePickerView();
                break;
            case R.id.btn_print:
                ShowStatisticsDialog showStatisticsDialog = new ShowStatisticsDialog(ordersDetail,currentDate);
                showStatisticsDialog.show(getActivity().getFragmentManager(),"showstatistic");
                break;
        }
    }


    public class OrderListAdapter extends BaseAdapter {
        private int selectIndex = -1;

        @Override
        public int getCount() {
            return orders.size();
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
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            AVObject avObject = orders.get(position);
            if (avObject.getDate("startedAt") != null) {
                holder.order_date.setText("用餐时间:" + DateUtil.formatDate(avObject.getDate("startedAt")) + "~" + DateUtil.formatDate(avObject.getDate("endAt")));
                holder.order_table_number.setText("桌号:" + avObject.getString("tableNumber"));
                holder.order_paysum.setText("用餐人数:" + avObject.getInt("customer"));
            }else{
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
            if (avObject.getBoolean("outside")){
                holder.order_state_img.setBackgroundResource(R.drawable.order_retail);
            }else{
                holder.order_state_img.setBackgroundResource(R.drawable.order_res);
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
            settleContent += "牛肉抵扣重量:" + meatweigth + "kg\n";
            settleContent += "实付金额:" + MyUtils.formatDouble(avObject.getDouble("paysum") - avObject.getDouble("reduce")) + "\n";
            holder.order_settle.setText(settleContent);
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
            holder.card_order.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new QMUIDialog.MessageDialogBuilder(getActivity())
                            .setTitle("温馨提示")
                            .setMessage("是否确定整单退款?")
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
                                }
                            })
                            .setCanceledOnTouchOutside(false)
                            .create(mCurrentDialogStyle).show();
                    return false;
                }
            });
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
            LinearLayout show_detail;
            CardView card_order;
        }
    }

}
