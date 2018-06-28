package cn.kuwo.player.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVLiveQuery;
import com.avos.avoscloud.AVLiveQueryEventHandler;
import com.avos.avoscloud.AVLiveQuerySubscribeCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.kuwo.player.MainActivity;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.api.HangUpApi;
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.util.DateUtil;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.ToastUtil;

public class TableFg extends BaseFragment {
    private static String ARG_PARAM = "param_key";
    @BindView(R.id.gv_tables)
    GridView gvTables;
    TableAdapter tableAdapter;
    List<AVObject> tables = new ArrayList<>();
    @BindView(R.id.radio_big_table)
    RadioButton radioBigTable;
    @BindView(R.id.radio_small_table)
    RadioButton radioSmallTable;
    @BindView(R.id.rg_choose_table_style)
    RadioGroup rgChooseTableStyle;
    @BindView(R.id.tv_hangup_number)
    TextView tvHangupNumber;
    @BindView(R.id.rl_hangup)
    RelativeLayout rlHangup;
    @BindView(R.id.btn_hangup)
    TextView btnHangup;
    private Activity mActivity;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private boolean chooseBigTable = true;
    private AVLiveQuery avLiveQuery;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_table;
    }

    @Override
    public void initData() {
        showDialog();
        setListener();
        fetchTable();
        fetchHangUp();
    }

    private void fetchHangUp() {
        HangUpApi.getHangUpOrders().countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null) {
                    try {

                        if (i > 0) {
                            rlHangup.setVisibility(View.VISIBLE);
                            tvHangupNumber.setText(i + "");
                        } else {
                            rlHangup.setVisibility(View.INVISIBLE);
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }
    private void fetchTable() {
        final AVQuery<AVObject> table = new AVQuery<>("Table");
        table.orderByAscending("tableNumber");
        table.whereEqualTo("spread", !chooseBigTable);
        table.whereEqualTo("active", 1);
        table.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    try {
                        tables = list;
                        tableAdapter = new TableAdapter();
                        gvTables.setAdapter(tableAdapter);
                        TextView remainTable = (TextView) mActivity.findViewById(R.id.remain_table);
                        remainTable.setText(ProductUtil.remainTable(list) + "");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        Logger.d(e1.getMessage());
                    }
                    hideDialog();
                } else {
                    hideDialog();
                    ToastUtil.showShort(MyApplication.getContextObject(), "网络连接错误");
                }
            }
        });
        subscribeQuery(table);
    }
    private void subscribeQuery(AVQuery<AVObject> query) {
         avLiveQuery = AVLiveQuery.initWithQuery(query);
        avLiveQuery.setEventHandler(new AVLiveQueryEventHandler() {
            @Override
            public void onObjectUpdated(AVObject avObject, List<String> updateKeyList) {
                super.onObjectUpdated(avObject, updateKeyList);
                fetchTable();
            }
        });
        avLiveQuery.subscribeInBackground(new AVLiveQuerySubscribeCallback() {
            @Override
            public void done(AVException e) {
            }
        });
    }
    private void setListener() {
        radioBigTable.setChecked(true);
        rgChooseTableStyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                showDialog();
                switch (checkedId) {
                    case R.id.radio_big_table:
                        chooseBigTable = true;
                        fetchTable();
                        break;
                    case R.id.radio_small_table:
                        chooseBigTable = false;
                        fetchTable();
                        break;
                }
            }
        });
        btnHangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                HangUpFragment hangUpFragment = HangUpFragment.newInstance("");
                ft.replace(R.id.fragment_content, hangUpFragment, "hangup").commit();
            }
        });
    }


    public static TableFg newInstance(String str) {
        TableFg tableFg = new TableFg();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM, str);
        tableFg.setArguments(bundle);
        return tableFg;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        avLiveQuery.unsubscribeInBackground(new AVLiveQuerySubscribeCallback() {
            @Override
            public void done(AVException e) {

            }
        });
    }


    public class TableAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return tables.size();
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
        public View getView(final int i, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                view=getLayoutInflater().inflate(R.layout.adapter_table,null);
                holder = new ViewHolder();
                holder.tableNumber = (TextView) view.findViewById(R.id.table_number);
                holder.tableCommodity = (TextView) view.findViewById(R.id.table_commodity);
                holder.tablePrice = (TextView) view.findViewById(R.id.table_price);
                holder.tableSvipPrice = (TextView) view.findViewById(R.id.table_svip_price);
                holder.tablePeople = (TextView) view.findViewById(R.id.table_people);
                holder.tableDate = (TextView) view.findViewById(R.id.table_date);
                holder.tableTime = (TextView) view.findViewById(R.id.table_time);
                holder.cv_table = (CardView) view.findViewById(R.id.cv_table);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final AVObject avObject = tables.get(i);
            holder.tableNumber.setText(avObject.getString("tableNumber"));
            if (avObject.getInt("customer") != 0) {
                holder.tableNumber.setBackgroundResource(R.drawable.shape_red_circle);
                holder.tablePrice.setText("￥" + ProductUtil.calculateTotalMoney(avObject));
                holder.tableSvipPrice.setText("超牛价钱￥" + ProductUtil.calculateMinMoney(avObject));
                holder.tableCommodity.setText(avObject.getList("order").size() + avObject.getList("preOrder").size() + "道菜品");
                holder.tablePeople.setText(avObject.getInt("customer") + "人");
                try {
                    holder.tableDate.setText(DateUtil.formatDate(avObject.getDate("startedAt")));
                    holder.tableTime.setText("牛币价格￥"+ProductUtil.calNbTotalMoney(avObject.getList("order")));
                } catch (Exception e) {
                    ToastUtil.showShort(MyApplication.getContextObject(), "获取时间错误");
                }

            } else {
                holder.tableNumber.setBackgroundResource(R.drawable.shape_green_circle);
                holder.tableCommodity.setText("空闲");
                holder.tablePeople.setText(avObject.getInt("accommodate") + "人桌");
                holder.tableDate.setText("");
                holder.tableTime.setText("");
            }
            holder.cv_table.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    OrderFg orderFg = OrderFg.newInstance(tables.get(i).getObjectId(), true);
                    ft.replace(R.id.fragment_content, orderFg, "order").commit();
                }
            });
            return view;
        }

        private class ViewHolder {
            TextView tableNumber;
            TextView tableCommodity;
            TextView tablePrice;
            TextView tableSvipPrice;
            TextView tablePeople;
            TextView tableDate;
            TextView tableTime;
            CardView cv_table;
        }
    }
}
