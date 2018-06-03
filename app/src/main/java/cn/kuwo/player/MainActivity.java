package cn.kuwo.player;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVLiveQuery;
import com.avos.avoscloud.AVLiveQueryEventHandler;
import com.avos.avoscloud.AVLiveQuerySubscribeCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogInCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.kuwo.player.activity.RetailActivity;
import cn.kuwo.player.base.BaseActivity;
import cn.kuwo.player.bean.NetBean;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.custom.CommomDialog;
import cn.kuwo.player.custom.ScanUserFragment;
import cn.kuwo.player.custom.ShowNoNetFragment;
import cn.kuwo.player.event.PrintEvent;
import cn.kuwo.player.event.SuccessEvent;
import cn.kuwo.player.fragment.CommodityFg;
import cn.kuwo.player.fragment.NetConnectFg;
import cn.kuwo.player.fragment.OrderFg;
import cn.kuwo.player.fragment.OrderListFg;
import cn.kuwo.player.fragment.SettingFg;
import cn.kuwo.player.fragment.SvipFg;
import cn.kuwo.player.fragment.TableFg;
import cn.kuwo.player.print.Bill;
import cn.kuwo.player.receiver.NetWorkStateReceiver;
import cn.kuwo.player.util.AppUtils;
import cn.kuwo.player.util.CameraProvider;
import cn.kuwo.player.util.DimenTool;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.RealmHelper;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.ToastUtil;
import io.realm.RealmList;

public class MainActivity extends BaseActivity {
    private int REQUEST_CODE_SCAN = 111;
    @BindView(R.id.menu_retail)
    TextView menuRetail;
    @BindView(R.id.menu_table)
    TextView menuTable;
    @BindView(R.id.menu_commodity)
    TextView menuCommodity;
    @BindView(R.id.menu_print)
    TextView menuPrint;
    @BindView(R.id.menu_update)
    TextView menuUpdate;
    @BindView(R.id.menu_svip)
    TextView menuSvip;
    @BindView(R.id.menu_order)
    TextView menuOrder;
    @BindView(R.id.remain_table)
    TextView remainTable;
    @BindView(R.id.gv_table)
    GridView gvTable;
    List<AVObject> tables = new ArrayList<>();
    FragmentTransaction ft;
    @BindView(R.id.waiter_avatar)
    QMUIRadiusImageView waiterAvatar;
    @BindView(R.id.waiter_name)
    TextView waiterName;
    private AVQuery<AVObject> table;
    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;
    NetWorkStateReceiver netWorkStateReceiver;
    ShowNoNetFragment showNoNetFragment = null;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }


    @Override
    public void initData() {
        LoginSystemUser();
        checkIsCashierDesk();
        checkLocalStorageSame();
        setListener();

    }

    /**
     * 系统账号登录
     */
    private void LoginSystemUser() {
        if (AVUser.getCurrentUser() == null) {
            AVUser.logInInBackground("13888888888", "123456789", new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e != null) {
                        showShortToast(e.getMessage());
                    }
                }
            });
        }
        SharedHelper sharedHelper = new SharedHelper(MyApplication.getContextObject());
        if (sharedHelper.readBoolean("cashierLogin")) {
            waiterName.setText(sharedHelper.read("cashierName"));
        } else {
            ScanUserFragment scanUserFragment = new ScanUserFragment(0);
            scanUserFragment.show(getSupportFragmentManager(), "scanuser");
        }
    }

    private void checkIsCashierDesk() {
        if (CameraProvider.hasCamera()) {
            menuRetail.setVisibility(View.GONE);
        } else {
            menuRetail.setVisibility(View.VISIBLE);
        }
    }

    private void checkLocalStorageSame() {
        final RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        if (mRealmHleper.queryAllProduct().size() == 0) {
            loadCommodity();
        } else {
            AVQuery<AVObject> offlineCommodity = new AVQuery<>("OfflineCommodity");
            offlineCommodity.whereEqualTo("active", 1);
            offlineCommodity.whereEqualTo("store", 1);
            offlineCommodity.countInBackground(new CountCallback() {
                @Override
                public void done(int i, AVException e) {
                    if (e == null) {
                        if (i != mRealmHleper.queryAllProduct().size()) {
                            loadCommodity();
                        } else {
                            fetchTable();
                            subscribeQuery();
                            initializeFragment();
                        }
                    } else {
                        fetchTable();
                        subscribeQuery();
                        initializeFragment();
                    }
                }
            });

        }
    }



    /**
     * 设置监听
     */
    private void setListener() {
        waiterName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ScanUserFragment scanUserFragment = new ScanUserFragment(0);
                scanUserFragment.show(getSupportFragmentManager(), "scanuser");
                return false;
            }
        });
        menuRetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RetailActivity.class));
            }
        });
    }

    /**
     * 初始化fragment
     */
    private void initializeFragment() {
        ft = getSupportFragmentManager().beginTransaction();
        TableFg tableFg = TableFg.newInstance("");
        ft.replace(R.id.fragment_content, tableFg, "table").commitAllowingStateLoss();
    }

    @OnClick({R.id.ll_table, R.id.menu_commodity, R.id.menu_print, R.id.menu_update, R.id.menu_svip, R.id.menu_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_table:
                switchFragment("table");
                fetchTable();
                break;
            case R.id.menu_commodity:
                switchFragment("commodity");
                break;
            case R.id.menu_print:
                switchFragment("netconnect");
                break;
            case R.id.menu_update:
                switchFragment("setting");
                break;
            case R.id.menu_svip:
                switchFragment("svip");
                break;
            case R.id.menu_order:
                switchFragment("order");
                break;
        }
    }

    /**
     * fragment切换
     */
    public void switchFragment(String tag) {
        resetState();
        ft = getSupportFragmentManager().beginTransaction();
        if (tag == "commodity") {
            setSelectState(menuCommodity, R.drawable.icon_menu);
            CommodityFg commodityFg = CommodityFg.newInstance("");
            ft.replace(R.id.fragment_content, commodityFg, "commodity").commit();
        } else if (tag == "table") {
            setSelectState(menuTable, R.drawable.icon_table);
            TableFg tableFg = TableFg.newInstance("");
            ft.replace(R.id.fragment_content, tableFg, "table").commit();
            remainTable.setTextColor(getResources().getColor(R.color.white));
        } else if (tag == "netconnect") {
            setSelectState(menuPrint, R.drawable.icon_print);
            NetConnectFg netConnectFg = NetConnectFg.newInstance("");
            ft.replace(R.id.fragment_content, netConnectFg, "netconnect").commit();
        } else if (tag == "svip") {
            setSelectState(menuSvip, R.drawable.icon_recharge_nor);
            SvipFg svipFg = SvipFg.newInstance("");
            ft.replace(R.id.fragment_content, svipFg, "svip").commit();
        } else if (tag == "order") {
            setSelectState(menuOrder, R.drawable.icon_order);
            OrderListFg orderListFg = OrderListFg.newInstance("");
            ft.replace(R.id.fragment_content, orderListFg, "orderlist").commit();
        } else if (tag == "setting") {
            setSelectState(menuUpdate, R.drawable.icon_update);
            SettingFg settingFg = SettingFg.newInstance("");
            ft.replace(R.id.fragment_content, settingFg, "setting").commit();
        }

    }

    /**
     * 重置状态
     */
    private void resetState() {
        Drawable left;
        left = getResources().getDrawable(R.drawable.icon_menu_nor);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        menuCommodity.setCompoundDrawables(left, null, null, null);
        left = getResources().getDrawable(R.drawable.icon_table_nor);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        menuTable.setCompoundDrawables(left, null, null, null);
        left = getResources().getDrawable(R.drawable.icon_print_nor);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        menuPrint.setCompoundDrawables(left, null, null, null);
        left = getResources().getDrawable(R.drawable.icon_update_nor);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        menuUpdate.setCompoundDrawables(left, null, null, null);
        left = getResources().getDrawable(R.drawable.icon_recharge);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        menuSvip.setCompoundDrawables(left, null, null, null);
        left = getResources().getDrawable(R.drawable.icon_order_nor);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        menuOrder.setCompoundDrawables(left, null, null, null);
        menuCommodity.setTextColor(getResources().getColor(R.color.purple));
        menuTable.setTextColor(getResources().getColor(R.color.purple));
        menuPrint.setTextColor(getResources().getColor(R.color.purple));
        menuUpdate.setTextColor(getResources().getColor(R.color.purple));
        remainTable.setTextColor(getResources().getColor(R.color.purple));
        menuSvip.setTextColor(getResources().getColor(R.color.purple));
        menuOrder.setTextColor(getResources().getColor(R.color.purple));
    }

    private void setSelectState(TextView view, int resourceId) {
        Drawable left;
        left = getResources().getDrawable(resourceId);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        view.setCompoundDrawables(left, null, null, null);
        view.setTextColor(getResources().getColor(R.color.white));
    }

    private void fetchTable() {
        table = new AVQuery<>("Table");
        table.orderByAscending("tableNumber");
        table.whereEqualTo("active", 1);
        table.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    try {
                        tables = list;
                        TableAdapter tableAdapter = new TableAdapter();
                        gvTable.setAdapter(tableAdapter);
                        gvTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ft = getSupportFragmentManager().beginTransaction();
                                OrderFg orderFg = OrderFg.newInstance(tables.get(position).getObjectId(), true);
                                ft.replace(R.id.fragment_content, orderFg, "order").commit();
                            }
                        });
                    } catch (Exception e1) {
                        ToastUtil.showShort(MyApplication.getContextObject(), "网络连接错误");
                    }

                } else {
                    ToastUtil.showShort(MyApplication.getContextObject(), "网络连接错误");
                }
            }
        });

    }

    /**
     * 绑定订阅事件
     */
    private void subscribeQuery() {
        if (table != null) {
            AVLiveQuery avLiveQuery = AVLiveQuery.initWithQuery(table);
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
                    if (e != null) {
                        ToastUtil.showShort(MyApplication.getContextObject(), "订阅失败");
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
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
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(MyApplication.getContextObject()).inflate(R.layout.adapter_table_left, parent, false);
                holder = new ViewHolder();
                holder.tableNumber = view.findViewById(R.id.table_number);
                holder.tableCommodity = view.findViewById(R.id.table_commodity);
                holder.tablePrice = view.findViewById(R.id.table_price);
                holder.tableSvipPrice = view.findViewById(R.id.table_svip_price);
                holder.tablePeople = view.findViewById(R.id.table_people);
                holder.rlTableDetail = view.findViewById(R.id.rl_table_detail);
                holder.llTable = view.findViewById(R.id.ll_table);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            AVObject avObject = tables.get(i);
            holder.tableNumber.setText(avObject.getString("tableNumber") + "号桌");
            if (avObject.getInt("customer") != 0) {

                holder.tablePrice.setText("￥" + ProductUtil.calculateTotalMoney(avObject));
                holder.tableSvipPrice.setText("超牛价钱￥" + ProductUtil.calculateMinMoney(avObject));
                holder.tableCommodity.setText(avObject.getList("order").size() + avObject.getList("preOrder").size() + "道菜品");
                holder.tablePeople.setText(avObject.getInt("customer") + "人");
                holder.rlTableDetail.setVisibility(View.VISIBLE);
                holder.tableSvipPrice.setVisibility(View.VISIBLE);
            } else {
                holder.tablePeople.setText("空闲");
                holder.rlTableDetail.setVisibility(View.GONE);
                holder.tableSvipPrice.setVisibility(View.GONE);
            }
            return view;
        }

        private class ViewHolder {
            TextView tableNumber;
            TextView tableCommodity;
            TextView tablePrice;
            TextView tableSvipPrice;
            TextView tablePeople;
            RelativeLayout rlTableDetail;
            LinearLayout llTable;
        }
    }


    public void loadCommodity() {
        final RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        final AVQuery<AVObject> offlineCommodity = new AVQuery<>("OfflineCommodity");
        offlineCommodity.addAscendingOrder("type");
        offlineCommodity.whereEqualTo("store", 1);
        offlineCommodity.addAscendingOrder("serial");
        offlineCommodity.limit(500);
        offlineCommodity.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(final List<AVObject> list, AVException e) {
                if (e == null) {
                    mRealmHleper.deleteAll(ProductBean.class);
                    for (int i = 0; i < list.size(); i++) {
                        AVObject avObject = list.get(i);
                        ProductBean productBean = new ProductBean();
                        productBean.setName(avObject.get("name").toString());
                        productBean.setCode(avObject.get("code").toString());
                        productBean.setObjectId(avObject.getAVObject("commodity").getObjectId());
                        productBean.setPrice(avObject.getDouble("price"));
                        productBean.setWeight(avObject.getDouble("weight"));
                        productBean.setType(avObject.getInt("type"));
                        productBean.setSale(avObject.getInt("sale"));
                        productBean.setCombo(avObject.getInt("combo"));
                        productBean.setRate(avObject.getDouble("rate"));
                        productBean.setPerformance(avObject.getInt("performance"));
                        productBean.setGivecode(TextUtils.isEmpty(avObject.getString("givecode")) ? "" : avObject.getString("givecode"));
                        productBean.setStore(avObject.getInt("store"));
                        productBean.setMeatWeight(avObject.getDouble("meatWeight"));
                        productBean.setSerial(avObject.getString("serial"));
                        productBean.setUrl(avObject.getAVFile("avatar").getUrl());
                        productBean.setScale(avObject.getDouble("scale"));
                        productBean.setRemainMoney(avObject.getDouble("remainMoney"));
                        productBean.setActive(avObject.getInt("active"));
                        productBean.setComboMenu(avObject.getString("comboMenu") == null ? "" : MyUtils.replaceBlank(avObject.getString("comboMenu").trim().replace(" ", "")));
                        RealmList<String> commentsList = new RealmList<>();
                        for (int k = 0; k < avObject.getList("comments").size(); k++) {
                            commentsList.add(avObject.getList("comments").get(k).toString());
                        }
                        productBean.setGiveRule(avObject.getInt("giveRule"));
                        productBean.setComments(commentsList);
                        mRealmHleper.addProduct(productBean);
                    }
                    fetchTable();
                    initializeFragment();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(netWorkStateReceiver);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final SuccessEvent event) {
        if (event.getCode() <= -1) {
            new CommomDialog(this, R.style.dialog, event.getMessage(), new CommomDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        Bill.printCateringFore(event.getOrders(), event.getTableAVObject(), event.getCode());
                        dialog.dismiss();
                    }

                }
            })
                    .setTitle("提示").setNegativeButton("人工通知小票打印失败").setPositiveButton("重新尝试打印").show();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final PrintEvent event) {
        if (event.getCode() <= -1) {
            new CommomDialog(this, R.style.dialog, event.getMessage(), new CommomDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        Bill.printSettleBill(MyApplication.getContextObject(), event.getOrderDetail(), event.getJsonObject(), event.getEscrow());
                        dialog.dismiss();
                    }

                }
            })
                    .setTitle("提示").setNegativeButton("放弃打印小票").setPositiveButton("重新尝试打印").show();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NetBean netBean) {
        if (netBean.getCode() == -1) {
            showNoNetFragment = new ShowNoNetFragment();
            showNoNetFragment.show(getSupportFragmentManager(), "shownoNet");
        } else if (netBean.getCode() == 0) {
            if (showNoNetFragment != null) {
                showNoNetFragment.getDialog().dismiss();
            }
        }
    }
}
