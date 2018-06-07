package cn.kuwo.player.fragment;

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
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
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
    @BindView(R.id.gv_table)
    GridView gvTable;
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
    Unbinder unbinder;
    @BindView(R.id.btn_hangup)
    TextView btnHangup;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    private boolean chooseBigTable = true;

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
        MainActivity activity = (MainActivity) getActivity();
        activity.fetchTable();
    }

    private void fetchHangUp() {
        HangUpApi.getHangUpOrders().countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e == null) {
                    if (i > 0) {
                        rlHangup.setVisibility(View.VISIBLE);
                        tvHangupNumber.setText(i + "");
                    } else {
                        rlHangup.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
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
                        gvTable.setAdapter(tableAdapter);
                        TextView remainTable = (TextView) getActivity().findViewById(R.id.remain_table);
                        remainTable.setText(ProductUtil.remainTable(list) + "");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        ToastUtil.showShort(MyApplication.getContextObject(), "网络连接错误");
                    }

                    hideDialog();
                } else {
                    hideDialog();
                    ToastUtil.showShort(MyApplication.getContextObject(), "网络连接错误");
                }
            }
        });
    }

    private void setListener() {
        radioBigTable.setChecked(true);
        rgChooseTableStyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
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
                view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_table, parent, false);
                holder = new ViewHolder();
                holder.tableNumber = (TextView) view.findViewById(R.id.table_number);
                holder.tableCommodity = (TextView) view.findViewById(R.id.table_commodity);
                holder.tablePrice = (TextView) view.findViewById(R.id.table_price);
                holder.tableSvipPrice = (TextView) view.findViewById(R.id.table_svip_price);
                holder.tablePeople = (TextView) view.findViewById(R.id.table_people);
                holder.tableDate = (TextView) view.findViewById(R.id.table_date);
                holder.tableTime = (TextView) view.findViewById(R.id.table_time);
                holder.card_hide = (CardView) view.findViewById(R.id.card_hide);
                holder.card_show = (CardView) view.findViewById(R.id.card_show);
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
                    holder.tableTime.setText(DateUtil.TimeInterval(avObject.getDate("startedAt")));
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

            holder.cv_table.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (avObject.getBoolean("spread")) {
                        holder.card_hide.setVisibility(View.VISIBLE);
                        holder.card_show.setVisibility(View.INVISIBLE);
                    } else {
                        holder.card_hide.setVisibility(View.INVISIBLE);
                        holder.card_show.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
            });
            holder.cv_table.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    OrderFg orderFg = OrderFg.newInstance(tables.get(i).getObjectId(), true);
                    ft.replace(R.id.fragment_content, orderFg, "order").commit();
                }
            });
            holder.card_show.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (avObject.getList("order").size() == 0 && avObject.getList("preOrder").size() == 0) {
                        final AVQuery<AVObject> table = new AVQuery<>("Table");
                        table.whereStartsWith("tableNumber", avObject.getString("tableNumber"));
                        table.whereEqualTo("spread", true);
                        table.whereEqualTo("active", 0);
                        table.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if (e == null && list.size() > 0) {

                                    for (int i = 0; i < list.size(); i++) {
                                        AVObject tableAVObject = list.get(i);
                                        tableAVObject.put("active", 1);
                                        tableAVObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                if (e == null) {
                                                    avObject.put("active", 0);
                                                    avObject.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(AVException e) {
                                                            if (e == null) {
                                                                ToastUtil.showShort(MyApplication.getContextObject(), "修改成功");
                                                                initData();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    } else {
                        new QMUIDialog.MessageDialogBuilder(getActivity())
                                .setTitle("温馨提示")
                                .setMessage("此桌有已经下单商品或预下单商品,不可变成小桌")
                                .addAction("确定", new QMUIDialogAction.ActionListener() {
                                    @Override
                                    public void onClick(QMUIDialog dialog, int index) {
                                        dialog.dismiss();
                                    }
                                })
                                .create(mCurrentDialogStyle).show();
                    }

                }
            });
            holder.card_hide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AVQuery<AVObject> tables = new AVQuery<>("Table");
                    tables.whereStartsWith("tableNumber", avObject.getString("tableNumber").substring(0, 1));
                    tables.findInBackground(new FindCallback<AVObject>() {
                        @Override
                        public void done(List<AVObject> list, AVException e) {
                            boolean ableMerge = true;
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getList("order").size() > 0 && list.get(i).getBoolean("spread")) {
                                    ableMerge = false;
                                    break;
                                }
                            }
                            if (!ableMerge) {
                                new QMUIDialog.MessageDialogBuilder(getActivity())
                                        .setTitle("温馨提示")
                                        .setMessage("此桌小桌中已经下单商品，不可隐藏")
                                        .addAction("确定", new QMUIDialogAction.ActionListener() {
                                            @Override
                                            public void onClick(QMUIDialog dialog, int index) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .create(mCurrentDialogStyle).show();
                            } else {
                                for (int i = 0; i < list.size(); i++) {
                                    AVObject avObject1 = list.get(i);
                                    if (list.get(i).getBoolean("spread")) {
                                        avObject1.put("active", 0);
                                        avObject1.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                initData();
                                            }
                                        });
                                    } else {
                                        avObject1.put("active", 1);
                                        avObject1.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(AVException e) {
                                                initData();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
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
            CardView card_hide;
            CardView card_show;
            CardView cv_table;
        }
    }
}
