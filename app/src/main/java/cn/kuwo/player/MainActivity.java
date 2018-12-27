package cn.kuwo.player;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVLiveQuery;
import com.avos.avoscloud.AVLiveQueryEventHandler;
import com.avos.avoscloud.AVLiveQuerySubscribeCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import cn.kuwo.player.event.RefundEvent;
import cn.kuwo.player.event.SuccessEvent;
import cn.kuwo.player.fragment.CommodityClassifyFg;
import cn.kuwo.player.fragment.CommodityFg;
import cn.kuwo.player.fragment.NbFg;
import cn.kuwo.player.fragment.NetConnectFg;
import cn.kuwo.player.fragment.OrderListFg;
import cn.kuwo.player.fragment.SettingFg;
import cn.kuwo.player.fragment.StoredFg;
import cn.kuwo.player.fragment.SvipFg;
import cn.kuwo.player.fragment.TableFg;
import cn.kuwo.player.fragment.activities.EventsActivity;
import cn.kuwo.player.fragment.credit.CreditActivity;
import cn.kuwo.player.print.Bill;
import cn.kuwo.player.receiver.NetWorkStateReceiver;
import cn.kuwo.player.receiver.USBBroadcastReceiver;
import cn.kuwo.player.util.AppUtils;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.CameraProvider;
import cn.kuwo.player.util.ErrorUtil;
import cn.kuwo.player.util.ImageUtil;
import cn.kuwo.player.util.LoginUtil;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.RealmHelper;
import cn.kuwo.player.util.RealmUtil;
import cn.kuwo.player.util.ScriptUtil;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.ToastUtil;
import cn.kuwo.player.util.UpgradeUtil;

public class MainActivity extends BaseActivity {
    @BindView(R.id.menu_stored)
    TextView menuStored;
    @BindView(R.id.ll_table)
    LinearLayout llTable;
    @BindView(R.id.menu_nb)
    TextView menuNb;
    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R.id.menu_update_info)
    TextView menuUpdateInfo;
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
    @BindView(R.id.menu_order)
    TextView menuOrder;
    @BindView(R.id.remain_table)
    TextView remainTable;
    @BindView(R.id.gv_table)
    GridView gvTable;
    FragmentTransaction ft;
    @BindView(R.id.waiter_avatar)
    QMUIRadiusImageView waiterAvatar;
    @BindView(R.id.waiter_name)
    TextView waiterName;
    @BindView(R.id.menu_activity)
    TextView menuActivity;
    @BindView(R.id.menu_credit)
    TextView menuCredit;
    private AVQuery<AVObject> table;
    NetWorkStateReceiver netWorkStateReceiver;
    ShowNoNetFragment showNoNetFragment = null;
    private Context mContext;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }


    @Override
    public void initData() {
        mContext = this;
        test();
        LoginUtil.checkSystemLogin();
        checkCashierLogin();
        checkLocalStorageCommodity();
        setListener();
        showDialog();
        AVQuery<AVObject> query = new AVQuery<>("OffineControl");
        query.whereEqualTo("store", CONST.STORECODE);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    final AVObject avObject = list.get(0);
                    hideDialog();
                    if (MyUtils.getVersionCode(MyApplication.getContextObject()) < avObject.getInt("version") && avObject.getAVFile("upgrade") != null) {
                        UpgradeUtil.checkInfo(mContext);
                    }
                } else {
                    hideDialog();
                }
            }
        });
    }

    /**
     * 系统账号登录
     */
    private void checkCashierLogin() {
        if (SharedHelper.readBoolean("cashierLogin")) {
            waiterName.setText("收银人员:" + SharedHelper.read("cashierName"));
        } else {
            SharedHelper.saveBoolean("cashierLogin", false);
            new ScanUserFragment(0).show(getSupportFragmentManager(), "scanuser");
        }
    }

    private void checkLocalStorageCommodity() {
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
                Intent intent = new Intent(MainActivity.this, RetailActivity.class);
                startActivityForResult(intent, 2);
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

    @OnClick({R.id.ll_table, R.id.menu_commodity, R.id.menu_print, R.id.menu_update, R.id.menu_order, R.id.menu_stored, R.id.menu_activity, R.id.menu_nb, R.id.menu_update_info, R.id.menu_credit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_table:
                if (!AppUtils.isFastDoubleClick()) {
                    switchFragment("table");
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
            case R.id.menu_order:
                switchFragment("order");
                break;
            case R.id.menu_stored:
                switchFragment("stored");
                break;
            case R.id.menu_activity:
                Intent intent = new Intent(MainActivity.this, EventsActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_nb:
                switchFragment("nb");
                break;
            case R.id.menu_update_info:
                UpgradeUtil.checkInfo(this);
                break;
            case R.id.menu_credit:
                startActivity(new Intent(MainActivity.this, CreditActivity.class));
                break;
        }
    }


    /**
     * fragment切换
     */
    public void switchFragment(String tag) {
        resetState();
        ft = getSupportFragmentManager().beginTransaction();
        switch (tag) {
            case "commodity":
                setSelectState(menuCommodity, R.drawable.icon_menu);
                CommodityClassifyFg commodityClassifyFg = new CommodityClassifyFg();
                ft.replace(R.id.fragment_content, commodityClassifyFg, "commodity").commit();
                break;
            case "table":
                setSelectState(menuTable, R.drawable.icon_table);
                TableFg tableFg = TableFg.newInstance("");
                ft.replace(R.id.fragment_content, tableFg, "table").commit();
                remainTable.setTextColor(getResources().getColor(R.color.white));
                break;
            case "netconnect":
                setSelectState(menuPrint, R.drawable.icon_print);
                NetConnectFg netConnectFg = NetConnectFg.newInstance("");
                ft.replace(R.id.fragment_content, netConnectFg, "netconnect").commit();
                break;
            case "order":
                setSelectState(menuOrder, R.drawable.icon_order);
                OrderListFg orderListFg = OrderListFg.newInstance("");
                ft.replace(R.id.fragment_content, orderListFg, "orderlist").commit();
                break;
            case "setting":
                setSelectState(menuUpdate, R.drawable.icon_update);
                SettingFg settingFg = SettingFg.newInstance("");
                ft.replace(R.id.fragment_content, settingFg, "setting").commit();
                break;
            case "stored":
                setSelectState(menuStored, R.drawable.icon_recharge_nor);
                StoredFg storedFg = StoredFg.newInstance("");
                ft.replace(R.id.fragment_content, storedFg, "stored").commit();
                break;
            case "nb":
                setSelectState(menuNb, R.drawable.icon_recharge_nor);
                NbFg nbFg = NbFg.newInstance("");
                ft.replace(R.id.fragment_content, nbFg, "nb").commit();
                break;
            case "credit":
                setSelectState(menuCredit, R.drawable.icon_inventory);
                break;
        }

    }

    /**
     * 重置状态
     */
    private void resetState() {
        menuCommodity.setTextColor(getResources().getColor(R.color.purple));
        menuTable.setTextColor(getResources().getColor(R.color.purple));
        menuPrint.setTextColor(getResources().getColor(R.color.purple));
        menuUpdate.setTextColor(getResources().getColor(R.color.purple));
        remainTable.setTextColor(getResources().getColor(R.color.purple));
        menuOrder.setTextColor(getResources().getColor(R.color.purple));
        menuStored.setTextColor(getResources().getColor(R.color.purple));
        menuNb.setTextColor(getResources().getColor(R.color.purple));
    }

    private void setSelectState(TextView view, int resourceId) {
        view.setTextColor(getResources().getColor(R.color.white));
    }

    public void fetchTable() {
        table = new AVQuery<>("Table");
        table.orderByAscending("tableNumber");
        table.whereEqualTo("active", 1);
        table.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e != null) {
                    ErrorUtil.NETERROR();
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
        checkUsbDevice();
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
            AVObject avObject = new AVObject("PrintLog");
            avObject.put("order", event.getOrders());
            avObject.put("tableInfo", event.getTableAVObject().get("tableNumber").toString());
            avObject.put("errorCode", event.getCode());
            avObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                }
            });
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
    public void onMessageEvent(final RefundEvent event) {
        if (event.getCode() <= -1) {
            new CommomDialog(this, R.style.dialog, "退菜小票机打印失败", new CommomDialog.OnCloseListener() {
                @Override
                public void onClick(Dialog dialog, boolean confirm) {
                    if (confirm) {
                        dialog.dismiss();
                    }
                }
            })
                    .setTitle("提示").setNegativeButton("人工通知").setPositiveButton("人工通知").show();
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

    private void checkUsbDevice() {
        try {
            if (CameraProvider.hasCamera()) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
                filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
                registerReceiver(new USBBroadcastReceiver(), filter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2) {
            resetState();
            setSelectState(menuNb, R.drawable.icon_recharge_nor);
            NbFg nbFg = NbFg.newInstance(data.getStringExtra("userId"));
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_content, nbFg, "nb").commit();
        }
    }


    private void test() {



    }

}
