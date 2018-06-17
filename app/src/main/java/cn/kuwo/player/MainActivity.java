package cn.kuwo.player;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.avos.avoscloud.SaveCallback;
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
import cn.kuwo.player.api.CommodityApi;
import cn.kuwo.player.api.RuleApi;
import cn.kuwo.player.base.BaseActivity;
import cn.kuwo.player.bean.NetBean;
import cn.kuwo.player.custom.CommomDialog;
import cn.kuwo.player.custom.ScanUserFragment;
import cn.kuwo.player.custom.ShowNoNetFragment;
import cn.kuwo.player.event.ClearEvent;
import cn.kuwo.player.event.PrintEvent;
import cn.kuwo.player.event.SuccessEvent;
import cn.kuwo.player.fragment.CommodityFg;
import cn.kuwo.player.fragment.NetConnectFg;
import cn.kuwo.player.fragment.OrderFg;
import cn.kuwo.player.fragment.OrderListFg;
import cn.kuwo.player.fragment.SettingFg;
import cn.kuwo.player.fragment.StoredFg;
import cn.kuwo.player.fragment.SvipFg;
import cn.kuwo.player.fragment.TableFg;
import cn.kuwo.player.inventory.InventoryActivity;
import cn.kuwo.player.print.Bill;
import cn.kuwo.player.receiver.NetWorkStateReceiver;
import cn.kuwo.player.util.AppUtils;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.RealmHelper;
import cn.kuwo.player.util.RealmUtil;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.ToastUtil;

public class MainActivity extends BaseActivity {
    @BindView(R.id.menu_stored)
    TextView menuStored;
    @BindView(R.id.menu_inventory)
    TextView menuInventory;
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
        checkLocalStorageSame();
        setListener();
        test();
    }

    private void test() {
//        AVQuery<AVObject> query = new AVQuery<>("OfflineCommodity");
//        query.whereEqualTo("type",2);
//        query.whereEqualTo("store",0);
//        query.whereStartsWith("name","澳");
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                for (int i=0;i<list.size();i++){
//                    AVObject avObject = list.get(i);
//                    avObject.put("combo",0);
//                    avObject.put("active",0);
//                    avObject.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(AVException e) {
//
//                        }
//                    });
//                }
//            }
//        });
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
            waiterName.setText("收银人员:"+sharedHelper.read("cashierName"));
        } else {
            sharedHelper.saveBoolean("cashierLogin", false);
            ScanUserFragment scanUserFragment = new ScanUserFragment(0);
            scanUserFragment.show(getSupportFragmentManager(), "scanuser");
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
                            initializeFragment();
                        }
                    } else {
                        fetchTable();
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
                SharedHelper sharedHelper = new SharedHelper(MyApplication.getContextObject());
                sharedHelper.saveBoolean("cashierLogin", false);
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

    @OnClick({R.id.ll_table, R.id.menu_commodity, R.id.menu_print, R.id.menu_update, R.id.menu_svip, R.id.menu_order, R.id.menu_stored,R.id.menu_inventory})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_table:
                if (!AppUtils.isFastDoubleClick()) {
                    switchFragment("table");
//                    fetchTable();
                }
                break;
            case R.id.menu_commodity:
                if (!AppUtils.isFastDoubleClick()) {
                    switchFragment("commodity");
                }
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
            case R.id.menu_stored:
                switchFragment("stored");
                break;
            case R.id.menu_inventory:
                startActivity(new Intent(MainActivity.this, InventoryActivity.class));
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
            setSelectState(menuSvip, R.drawable.icon_svip_sel);
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
        } else if (tag == "stored") {
            setSelectState(menuStored, R.drawable.icon_recharge_nor);
            StoredFg storedFg = StoredFg.newInstance("");
            ft.replace(R.id.fragment_content, storedFg, "stored").commit();
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
        left = getResources().getDrawable(R.drawable.icon_svip_nor);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        menuSvip.setCompoundDrawables(left, null, null, null);
        left = getResources().getDrawable(R.drawable.icon_order_nor);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        menuOrder.setCompoundDrawables(left, null, null, null);
        left = getResources().getDrawable(R.drawable.icon_recharge);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        menuStored.setCompoundDrawables(left, null, null, null);
        menuCommodity.setTextColor(getResources().getColor(R.color.purple));
        menuTable.setTextColor(getResources().getColor(R.color.purple));
        menuPrint.setTextColor(getResources().getColor(R.color.purple));
        menuUpdate.setTextColor(getResources().getColor(R.color.purple));
        remainTable.setTextColor(getResources().getColor(R.color.purple));
        menuSvip.setTextColor(getResources().getColor(R.color.purple));
        menuOrder.setTextColor(getResources().getColor(R.color.purple));
        menuStored.setTextColor(getResources().getColor(R.color.purple));
    }

    private void setSelectState(TextView view, int resourceId) {
        Drawable left;
        left = getResources().getDrawable(resourceId);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        view.setCompoundDrawables(left, null, null, null);
        view.setTextColor(getResources().getColor(R.color.white));
    }

    public void fetchTable() {
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
                                if (!AppUtils.isFastDoubleClick()) {
                                    ft = getSupportFragmentManager().beginTransaction();
                                    OrderFg orderFg = OrderFg.newInstance(tables.get(position).getObjectId(), true);
                                    ft.replace(R.id.fragment_content, orderFg, "order").commit();
                                }
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
        subscribeQuery();

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
            TextView tableNumber, tableCommodity, tablePrice, tableSvipPrice, tablePeople;
            RelativeLayout rlTableDetail;
            LinearLayout llTable;
        }
    }


    public void loadCommodity() {
        fetchRule();
        CommodityApi.getOfflineCommodity().findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(final List<AVObject> list, AVException e) {
                if (e == null) {
                    RealmUtil.setProductBeanRealm(list);
                    fetchTable();
                    initializeFragment();
                }
            }
        });
    }

    private void fetchRule() {
        RuleApi.getRule().findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    RealmUtil.setRuleBeanRealm(list);

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
                        Bill.printSettleBill(MyApplication.getContextObject(), event.getOrderDetail(), event.getJsonObject(), event.getEscrow(), event.getTableNumber());
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ClearEvent clearEvent) {
        if (clearEvent.getCode() == 0) {
            showDialog();
        } else {
            hideDialog();
        }
    }
}
