package cn.kuwo.player;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVLiveQuery;
import com.avos.avoscloud.AVLiveQueryEventHandler;
import com.avos.avoscloud.AVLiveQuerySubscribeCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import cn.kuwo.player.event.RefundEvent;
import cn.kuwo.player.event.SuccessEvent;
import cn.kuwo.player.fragment.CommodityFg;
import cn.kuwo.player.fragment.NbFg;
import cn.kuwo.player.fragment.NetConnectFg;
import cn.kuwo.player.fragment.OrderListFg;
import cn.kuwo.player.fragment.SettingFg;
import cn.kuwo.player.fragment.StoredFg;
import cn.kuwo.player.fragment.SvipFg;
import cn.kuwo.player.fragment.TableFg;
import cn.kuwo.player.fragment.inventory.InventoryActivity;
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
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.ToastUtil;

public class MainActivity extends BaseActivity {
    @BindView(R.id.menu_stored)
    TextView menuStored;
    @BindView(R.id.menu_inventory)
    TextView menuInventory;
    @BindView(R.id.ll_table)
    LinearLayout llTable;
    @BindView(R.id.menu_nb)
    TextView menuNb;
    @BindView(R.id.fragment_content)
    FrameLayout fragmentContent;
    @BindView(R.id.menu_update_info)
    TextView menuUpdateInfo;
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
    NetWorkStateReceiver netWorkStateReceiver;
    ShowNoNetFragment showNoNetFragment = null;
    ProgressDialog mProgressDialog;
    private Context mContext;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }


    @Override
    public void initData() {
        mContext=this;
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
                    if ( MyUtils.getVersionCode(MyApplication.getContextObject()) <avObject.getInt("version") && avObject.getAVFile("upgrade") != null) {
                        String upgradeUrl = avObject.getAVFile("upgrade").getUrl();
                        ShowDialog(upgradeUrl);
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

    @OnClick({R.id.ll_table, R.id.menu_commodity, R.id.menu_print, R.id.menu_update, R.id.menu_svip, R.id.menu_order, R.id.menu_stored, R.id.menu_inventory, R.id.menu_nb, R.id.menu_update_info})
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
            case R.id.menu_nb:
                switchFragment("nb");
                break;
            case R.id.menu_update_info:
                checkInfo();
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
        } else if (tag == "nb") {
            setSelectState(menuNb, R.drawable.icon_recharge_nor);
            NbFg nbFg = NbFg.newInstance("");
            ft.replace(R.id.fragment_content, nbFg, "nb").commit();
        }

    }

    /**
     * 重置状态
     */
    private void resetState() {
        ImageUtil.setDrawableLeft(this, R.drawable.icon_menu_nor, menuCommodity);
        ImageUtil.setDrawableLeft(this, R.drawable.icon_table_nor, menuTable);
        ImageUtil.setDrawableLeft(this, R.drawable.icon_print_nor, menuPrint);
        ImageUtil.setDrawableLeft(this, R.drawable.icon_update_nor, menuUpdate);
        ImageUtil.setDrawableLeft(this, R.drawable.icon_svip_nor, menuSvip);
        ImageUtil.setDrawableLeft(this, R.drawable.icon_order_nor, menuOrder);
        ImageUtil.setDrawableLeft(this, R.drawable.icon_recharge, menuStored);
        ImageUtil.setDrawableLeft(this, R.drawable.icon_recharge, menuNb);
        menuCommodity.setTextColor(getResources().getColor(R.color.purple));
        menuTable.setTextColor(getResources().getColor(R.color.purple));
        menuPrint.setTextColor(getResources().getColor(R.color.purple));
        menuUpdate.setTextColor(getResources().getColor(R.color.purple));
        remainTable.setTextColor(getResources().getColor(R.color.purple));
        menuSvip.setTextColor(getResources().getColor(R.color.purple));
        menuOrder.setTextColor(getResources().getColor(R.color.purple));
        menuStored.setTextColor(getResources().getColor(R.color.purple));
        menuNb.setTextColor(getResources().getColor(R.color.purple));
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
        if (CameraProvider.hasCamera()) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            registerReceiver(new USBBroadcastReceiver(), filter);
        }


    }

    private void test() {
    }

    private void checkInfo() {
        showDialog();
        AVQuery<AVObject> query = new AVQuery<>("OffineControl");
        query.whereEqualTo("store", CONST.STORECODE);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    final AVObject avObject = list.get(0);
                    final SharedHelper sharedHelper = new SharedHelper(mContext);
                    if (!avObject.getString("updateDate").equals(sharedHelper.read("updateDate"))) {
                        CommodityApi.getOfflineCommodity().findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(final List<AVObject> list, AVException e) {
                                if (e == null) {
                                    RealmUtil.setProductBeanRealm(list);
                                    sharedHelper.save("updateDate", avObject.getString("updateDate"));
                                    checkVersion(avObject);
                                }else{
                                    hideDialog();
                                }
                            }
                        });
                    }else{
                        checkVersion(avObject);
                    }
                } else {
                    hideDialog();
                }
            }
        });
    }

    private void checkVersion(AVObject avObject) {
        hideDialog();
        if ( MyUtils.getVersionCode(MyApplication.getContextObject()) <avObject.getInt("version") && avObject.getAVFile("upgrade") != null) {
            String upgradeUrl = avObject.getAVFile("upgrade").getUrl();
            ShowDialog(upgradeUrl);
        } else {
            ToastUtil.showShort(MyApplication.getContextObject(), "已经是最新最新数据");
        }
    }
    private void ShowDialog(final String upgradeUrl) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage("您的版本过低，请去更新最新版本，如不更新将无法继续使用")
                .setPositiveButton("更新",
                        new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestWritePermission(upgradeUrl);
                                } else {
                                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                        startDownloadApk(upgradeUrl);
                                    } else {
                                        Toast.makeText(MyApplication.getContextObject(), "请确认外部存储可用", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        })
                .setNegativeButton(android.R.string.no,
                        new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                .setCancelable(false)
                .create()
                .show();
    }
    private void startDownloadApk(final String url) {
        mProgressDialog = new ProgressDialog(this, android.R.style.Theme_Material_Light_Dialog);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle("正在下载中");
        mProgressDialog.setMax(100);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    downLoadFile(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 下载文件
     *
     * @return
     * @throws IOException
     */
    private File downLoadFile(String upgradeUrl) throws IOException {
        URL url = new URL(upgradeUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        InputStream is = conn.getInputStream();
        final File file = new File(Environment.getExternalStorageDirectory(), "app.apk");
        FileOutputStream fos = new FileOutputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buffer = new byte[1024];
        int len;
        int current = 0;
        int total = conn.getContentLength();
        float percent;
        while ((len = bis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
            current += len;
            //获取当前下载量
            percent = (float) current / (float) total;
            mProgressDialog.setProgress((int) (percent * 100));
        }
        fos.close();
        bis.close();
        is.close();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
                installApk(file);
            }
        });
        return file;
    }

    /**
     * `
     * 安装apk
     *
     * @param file
     */
    private void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        Uri contentUri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            contentUri = Uri.fromFile(file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");

        }

        startActivity(intent);
    }

    private void requestWritePermission(String upgradeUrl) {
        int permissionCheck = ContextCompat.checkSelfPermission(MyApplication.getContextObject(), "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                startDownloadApk(upgradeUrl);
            } else {
                Toast.makeText(MyApplication.getContextObject(), "请确认外部存储可用", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MyApplication.getContextObject(), "请设置读写存储权限", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions((Activity) this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 100);
        }
    }
}
