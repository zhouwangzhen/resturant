package cn.kuwo.player.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVLiveQuery;
import com.avos.avoscloud.AVLiveQueryEventHandler;
import com.avos.avoscloud.AVLiveQuerySubscribeCallback;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUIRadiusImageView;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
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
import cn.kuwo.player.adapter.PayKeyAdapter;
import cn.kuwo.player.adapter.ScanGoodAdapter;
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.UserBean;
import cn.kuwo.player.custom.CommodityTypeFragment;
import cn.kuwo.player.custom.RefundFragment;
import cn.kuwo.player.custom.ScanCommodityFragment;
import cn.kuwo.player.custom.ScanUserFragment;
import cn.kuwo.player.custom.ShowComboMenuFragment;
import cn.kuwo.player.custom.ShowPreOrderFragment;
import cn.kuwo.player.event.ClearEvent;
import cn.kuwo.player.event.ComboEvent;
import cn.kuwo.player.event.SuccessEvent;
import cn.kuwo.player.interfaces.MyItemClickListener;
import cn.kuwo.player.interfaces.MyItemLongClickListener;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.CameraProvider;
import cn.kuwo.player.util.DataUtil;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.SharedHelper;
import cn.kuwo.player.util.ToastUtil;

import static android.app.Activity.RESULT_OK;

public class OrderFg extends BaseFragment {
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
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.ll_show_member)
    LinearLayout llShowMember;
    @BindView(R.id.btn_to_pay)
    Button btnToPay;
    Unbinder unbinder3;
    @BindView(R.id.btn_place_order)
    Button btnPlaceOrder;
    @BindView(R.id.btn_choose_type)
    Button btnChooseType;
    @BindView(R.id.btn_to_clear)
    Button btnToClear;
    Unbinder unbinder1;
    @BindView(R.id.user_nb)
    TextView userNb;
    @BindView(R.id.nb_money)
    TextView nbMoney;
    @BindView(R.id.user_charge_nb)
    TextView userChargeNb;
    @BindView(R.id.btn_choose_commodity)
    Button btnChooseCommodity;
    private int REQUEST_CODE_SCAN = 111;
    private static String ARG_PARAM = "param_table_id";
    private static String ARG_PARAM1 = "param_save_data";
    @BindView(R.id.order_table)
    TextView orderTable;
    @BindView(R.id.order_people)
    TextView orderPeople;
    @BindView(R.id.gridview_commodity)
    GridView gridview;
    @BindView(R.id.editMemberAmount)
    EditText editMemberAmount;
    @BindView(R.id.recycle_scan_good)
    RecyclerView recycleScanGood;
    @BindView(R.id.total_money)
    TextView totalMoney;
    @BindView(R.id.total_number)
    TextView totalNumber;
    @BindView(R.id.order_change_table)
    Button orderChangeTable;
    @BindView(R.id.spinner_people)
    Spinner spinnerPeople;
    Unbinder unbinder;
    @BindView(R.id.sign_user)
    Button signUser;
    private int mode = 0;//0:正常点单,1:赠菜模式
    private boolean isSvip = false;
    private Activity mActivity;
    private String tableId = "";
    private int initializeNumner = 0;
    private String userId = "";
    private Boolean saveData = true;
    private AVQuery<AVObject> table;
    private AVObject tableAVObject;
    private List<Object> orders = new ArrayList<>();
    private List<Object> preOrders = new ArrayList<>();
    private List<Object> initializePreOrders = new ArrayList<>();
    private AVLiveQuery avLiveQuery;

    LinearLayoutManager linearLayoutManager;
    ScanGoodAdapter scanGoodAdapter;

    public static OrderFg newInstance(String str, Boolean saveState) {
        OrderFg orderFg = new OrderFg();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM, str);
        bundle.putBoolean(ARG_PARAM1, saveState);
        orderFg.setArguments(bundle);
        return orderFg;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fg_order;
    }

    private int mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog;

    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        tableId = getArguments().getString(ARG_PARAM);  //获取参数
        try {
            saveData = getArguments().getBoolean(ARG_PARAM1);
        } catch (Exception e) {
            e.printStackTrace();
            saveData = true;
        }

    }

    @Override
    public void initData() {
        setUpPayKeyAdapter();
        setParams();
        setChangeTypeListener();
    }

    private void setView() {
        if (tableAVObject != null && tableAVObject.getDate("startedAt") != null && tableAVObject.getList("order").size() == 0 && tableAVObject.getList("preOrder").size() == 0) {
            btnToClear.setVisibility(View.VISIBLE);
        } else {
            btnToClear.setVisibility(View.GONE);
        }
    }

    private void setChangeTypeListener() {
        btnChooseType.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String content = "";
                if (mode == 1) {
                    content = "是否切换到点菜模式?";
                } else {
                    content = "是否切换到赠菜模式?";
                }
                new QMUIDialog.MessageDialogBuilder(getActivity())
                        .setTitle("温馨提示")
                        .setMessage(content)
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
                                if (mode == 1) {
                                    mode = 0;
                                    btnChooseType.setText("当前模式:选菜模式");
                                } else {
                                    mode = 1;
                                    btnChooseType.setText("当前模式:赠菜模式");
                                }
                            }
                        })
                        .create(mCurrentDialogStyle).show();
                return false;
            }

        });
    }

    private void setParams() {
        editMemberAmount.setFocusableInTouchMode(false);
        QueryTable();
        subscribeQuery();
    }

    private void setSpinnerPeople() {
        spinnerPeople.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (tableAVObject != null) {
                    if (tableAVObject.getInt("customer") != Integer.parseInt(spinnerPeople.getSelectedItem().toString())) {
                        tableAVObject.put("customer", Integer.parseInt(spinnerPeople.getSelectedItem().toString()));
                        if (tableAVObject.getDate("startedAt") == null) {
                            tableAVObject.put("startedAt", new Date());
                        }
                        tableAVObject.put("preOrder", preOrders);
                        tableAVObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e != null) {
                                    ToastUtil.showShort(MyApplication.getContextObject(), e.getMessage());
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void QueryTable() {
        showDialog();
        table = new AVQuery<>("Table");
        table.include("user");
        table.getInBackground(tableId, new GetCallback<AVObject>() {
            @Override
            public void done(final AVObject avObject, AVException e) {
                if (e == null) {
                    try {
                        tableAVObject = avObject;
                        orderTable.setText("当前桌号:" + tableAVObject.getString("tableNumber") + "号桌");
                        orderPeople.setText(tableAVObject.getInt("accommodate") + "人座");
                        fetchCommodity(tableAVObject);
                        if (tableAVObject.getInt("customer") > 0) {
                            spinnerPeople.setSelection(tableAVObject.getInt("customer") - 1);
                        }
                        setView();
                        setSpinnerPeople();
                        refreshList();
                        if (tableAVObject.getAVObject("user") != null) {
                            AVObject userObject = avObject.getAVObject("user");
                            llShowMember.setVisibility(View.VISIBLE);
                            Double whiteBarBalance = MyUtils.formatDouble(MyUtils.formatDouble(userObject.getDouble("gold")) - MyUtils.formatDouble(userObject.getDouble("arrears")));
                            Double storedBalance = MyUtils.formatDouble(userObject.getDouble("stored"));
                            AVFile avatar = (AVFile) userObject.get("avatar");
                            if (avatar != null) {
                                Glide.with(MyApplication.getContextObject()).load(avatar.getUrl()).into(userAvatar);
                            }
                            userTel.setText("用户手机号:" + userObject.getString("username"));
                            userStored.setText("消费金:" + storedBalance);
                            userWhitebar.setText("白条:" + whiteBarBalance);
                            try {
                                if (e == null) {
                                    hideDialog();
                                    userId = avObject.getObjectId();
                                    signUser.setText("退出登录");
                                } else {
                                    userMeatweight.setText("牛肉额度:" + "0.0" + "kg");
                                    hideDialog();
                                    ToastUtil.showShort(getContext(), "获取超牛信息错误");
                                }
                            } catch (Exception e1) {
                                hideDialog();
                                e1.printStackTrace();
                            }
                        } else {
                            hideDialog();
                            llShowMember.setVisibility(View.INVISIBLE);
                            signUser.setText("用户登录");

                        }

                    } catch (Exception e2) {
                        hideDialog();
                        e2.printStackTrace();
                    }
                } else {
                    hideDialog();
                    ToastUtil.showShort(getContext(), "获取订单信息错误");
                }
            }
        });

    }

    /**
     * 绑定订阅事件
     */
    private void subscribeQuery() {
    }

    private void fetchCommodity(AVObject tableAVObject) {
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recycleScanGood.setLayoutManager(linearLayoutManager);
        orders = tableAVObject.getList("order");
        preOrders = tableAVObject.getList("preOrder");
        initializePreOrders = tableAVObject.getList("preOrder");
        if (orders.size() > 0 || preOrders.size() > 0 || tableAVObject.getList("refundOrder").size() > 0) {
            initializeNumner = preOrders.size();//标记是否有添加新产品
            refreshList();
        }
        setListener();
    }

    private void setListener() {
        editMemberAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String serial = editMemberAmount.getText().toString().trim();
                if (serial.length() == 3) {
                    List<ProductBean> productBeans = ProductUtil.searchBySerial(serial);
                    if (productBeans.size() > 0) {
                        editMemberAmount.setText("");
                        ProductBean productBean = productBeans.get(0);
                        if (!ProductUtil.checkIsGive(productBean.getType()) || (ProductUtil.checkIsGive(productBean.getType()) && tableAVObject.getAVObject("user") != null)) {
                            ShowComboMenuFragment showComboMenuFragment = new ShowComboMenuFragment(MyApplication.getContextObject(), productBean, false, productBean.getCode());
                            showComboMenuFragment.show(getActivity().getFragmentManager(), "showcomboMenu");
                        } else {
                            ToastUtil.showShort(MyApplication.getContextObject(), "登录会员后选取");
                        }
                    } else {
                        ToastUtil.showShort(MyApplication.getContextObject(), "没有查到此编号商品");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void refreshList() {
        scanGoodAdapter = new ScanGoodAdapter(getContext(), tableAVObject);
        recycleScanGood.setAdapter(scanGoodAdapter);
        scanGoodAdapter.setOnItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, final int postion) {
                if (postion < preOrders.size()) {
                    int index = preOrders.size() - postion - 1;
                    HashMap<String, Object> format = ObjectUtil.format(preOrders.get(index));
                    ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
                    ShowComboMenuFragment showComboMenuFragment = new ShowComboMenuFragment(MyApplication.getContextObject(), productBean, true, preOrders.get(index), index);
                    showComboMenuFragment.show(getActivity().getFragmentManager(), "showcomboMenu");
                }


            }
        });
        scanGoodAdapter.setOnItemLongClickListene(new MyItemLongClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position + 1 > preOrders.size() && position < preOrders.size() + orders.size()) {
                    if (view.getId() == R.id.ll_item) {
                        RefundFragment refundFragment = new RefundFragment(tableAVObject, orders, orders.size() + preOrders.size() - position - 1);
                        refundFragment.show(getActivity().getSupportFragmentManager(), "refund");
                    }
                }
            }
        });
        totalMoney.setText("￥" + ProductUtil.calculateTotalMoney(orders, preOrders) + "元");
        nbMoney.setText(ProductUtil.calNbTotalMoney(orders) + "牛币");
        totalNumber.setText(preOrders.size() + orders.size() + "");
    }

    private void setUpPayKeyAdapter() {
        try {
            gridview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    gridview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    int height = gridview.getMeasuredHeight();
                    final PayKeyAdapter adapter = new PayKeyAdapter(MyApplication.getContextObject(), height);
                    gridview.setAdapter(adapter);
                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String keyType = view.findViewById(R.id.GridTextView).getTag().toString();
                            String textValue = ((TextView) view.findViewById(R.id.GridTextView)).getText().toString();
                            String editText = editMemberAmount.getText().toString();
                            if (keyType.equals("num")) {
                                //数字键
                                try {
                                    if (editText.length() == 4) {
                                        Toast.makeText(MyApplication.getContextObject(), "编号不能超过3位数", Toast.LENGTH_SHORT).show();
                                    } else {
                                        editMemberAmount.setText(editText + textValue);
                                    }
                                } catch (Exception e) {
                                }
                            }
                            if (keyType.equals("back")) {

                                try {
                                    editMemberAmount.setText("");
                                } catch (Exception e) {
                                    editMemberAmount.setText("");
                                }
                            }
                            if (keyType.equals("point")) {
                                ScanCommodityFragment scanCommodityFragment = new ScanCommodityFragment();
                                scanCommodityFragment.show(getFragmentManager(), "scancommodity");
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存修改状态
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (avLiveQuery != null) {
            avLiveQuery.unsubscribeInBackground(new AVLiveQuerySubscribeCallback() {
                @Override
                public void done(AVException e) {
                }
            });
        }
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().post(new ClearEvent(0));
        if (tableAVObject != null && preOrders.size() == 0 && orders.size() == 0) {
            if (tableAVObject != null) {
                AVQuery<AVObject> query = new AVQuery<>("Table");
                query.getInBackground(tableId, new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject table, AVException e) {
                        if (e == null) {
                            if (table.getInt("customer") > 0) {
                                table.put("preOrder", preOrders);
                                table.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        EventBus.getDefault().post(new ClearEvent(1));
                                    }
                                });
                            } else {
                                EventBus.getDefault().post(new ClearEvent(1));
                            }
                        } else {
                            ToastUtil.showShort(MyApplication.getContextObject(), "网络繁忙");
                            EventBus.getDefault().post(new ClearEvent(1));
                        }
                    }
                });
            } else {
                hideDialog();
            }
        } else {
            if (tableAVObject != null && preOrders.size() >= 0 && saveData) {
                if (tableAVObject.getInt("customer") == 0) {
                    tableAVObject.put("customer", 1);
                }
                tableAVObject.put("preOrder", preOrders);
                if (tableAVObject.getDate("startedAt") == null) {
                    tableAVObject.put("startedAt", new Date());
                }
                tableAVObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        EventBus.getDefault().post(new ClearEvent(1));
                    }
                });
            } else {
                EventBus.getDefault().post(new ClearEvent(1));
            }
        }
        unbinder1.unbind();
    }


    @OnClick({R.id.order_change_table, R.id.sign_user, R.id.btn_to_pay, R.id.btn_place_order, R.id.btn_to_clear, R.id.user_charge_nb, R.id.btn_choose_commodity})
    public void onViewClicked(View view) {
        try {
            switch (view.getId()) {
                case R.id.order_change_table:
                    if (tableAVObject != null) {
                        final AVQuery<AVObject> table = new AVQuery<>("Table");
                        table.whereEqualTo("active", 1);
                        table.whereEqualTo("customer", 0);
                        table.addAscendingOrder("tableNumber");
                        table.findInBackground(new FindCallback<AVObject>() {
                            @Override
                            public void done(List<AVObject> list, AVException e) {
                                if (e == null) {
                                    if (list.size() > 0) {
                                        final String[] tables = new String[list.size()];
                                        for (int i = 0; i < list.size(); i++) {
                                            tables[i] = list.get(i).getString("tableNumber");
                                        }
                                        try {

                                            new QMUIDialog.CheckableDialogBuilder(getActivity())
                                                    .setCheckedIndex(-1)
                                                    .addItems(tables, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                            final AVQuery<AVObject> tableNew = new AVQuery<>("Table");
                                                            tableNew.whereEqualTo("tableNumber", tables[which]);
                                                            tableNew.findInBackground(new FindCallback<AVObject>() {
                                                                @Override
                                                                public void done(List<AVObject> list, AVException e) {
                                                                    if (e == null) {
                                                                        AVObject avObject = list.get(0);
                                                                        avObject.put("order", orders);
                                                                        avObject.put("preOrder", preOrders);
                                                                        avObject.put("refundOrder", tableAVObject.getList("refundOrder"));
                                                                        avObject.put("customer", Integer.parseInt(spinnerPeople.getSelectedItem().toString()));
                                                                        avObject.put("startedAt", tableAVObject.getDate("startedAt"));
                                                                        if (tableAVObject.getAVObject("user") != null) {
                                                                            avObject.put("user", AVObject.createWithoutData("_User", tableAVObject.getAVObject("user").getObjectId()));
                                                                        }
                                                                        avObject.saveInBackground(new SaveCallback() {
                                                                            @Override
                                                                            public void done(AVException e) {
                                                                                if (e == null) {
                                                                                    ArrayList<Object> objects = new ArrayList<>();
                                                                                    tableAVObject.put("order", new List[0]);
                                                                                    tableAVObject.put("preOrder", new List[0]);
                                                                                    tableAVObject.put("refundOrder", new List[0]);
                                                                                    tableAVObject.put("customer", 0);
                                                                                    tableAVObject.put("startedAt", null);
                                                                                    tableAVObject.put("user", null);
                                                                                    tableAVObject.saveInBackground(new SaveCallback() {
                                                                                        @Override
                                                                                        public void done(AVException e) {
                                                                                            if (e == null) {
                                                                                                ToastUtil.showShort(MyApplication.getContextObject(), "换桌成功");
                                                                                                preOrders.removeAll(preOrders);
                                                                                                orders.removeAll(orders);
                                                                                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                                                                ft.replace(R.id.fragment_content, TableFg.newInstance(""), "table").commit();

                                                                                            } else {
                                                                                                ToastUtil.showShort(MyApplication.getContextObject(), e.getMessage());
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                } else {
                                                                                    ToastUtil.showShort(MyApplication.getContextObject(), e.getMessage());
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    })
                                                    .create(mCurrentDialogStyle).show();
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                    } else {
                                        ToastUtil.showShort(MyApplication.getContextObject(), "暂无可换桌号");
                                    }
                                }
                            }
                        });
                    }
                    break;
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
                                            showDialog();
                                            tableAVObject.put("user", null);
                                            tableAVObject.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(AVException e) {
                                                    if (e == null) {
                                                        hideDialog();
                                                        ToastUtil.showShort(MyApplication.getContextObject(), "清空用户数据成功");
                                                        initData();
                                                    } else {
                                                        hideDialog();
                                                        Logger.d(e.getMessage());
                                                        ToastUtil.showShort(MyApplication.getContextObject(), "网络错误" + e.getMessage());
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
                case R.id.btn_to_pay:
                    if (tableAVObject != null) {
                        if (orders.size() > 0) {
                            if (preOrders.size() > 0) {
                                new QMUIDialog.MessageDialogBuilder(getActivity())
                                        .setTitle("温馨提示")
                                        .setMessage("还有未下单商品,是否继续下单？")
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
                                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                ft.replace(R.id.fragment_content, SettleFg.newInstance(tableAVObject.getObjectId(), false)).commit();
                                            }
                                        })
                                        .create(mCurrentDialogStyle).show();
                            } else {
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fragment_content, SettleFg.newInstance(tableAVObject.getObjectId(), false)).commit();
                            }
                        } else {
                            ToastUtil.showShort(MyApplication.getContextObject(), "至少下单一个商品");
                        }

                    } else {
                        ToastUtil.showShort(MyApplication.getContextObject(), "网络错误,回退重新点击重试");
                    }
                    break;
                case R.id.btn_place_order:
                    if (tableAVObject != null) {
                        if (preOrders.size() > 0) {
                            saveData = false;
                            ShowPreOrderFragment showPreOrderFragment = new ShowPreOrderFragment(tableAVObject, preOrders);
                            showPreOrderFragment.show(getActivity().getSupportFragmentManager(), "showpreorder");
                        } else {
                            ToastUtil.showShort(MyApplication.getContextObject(), "请先选择商品");
                        }
                    }
                    break;
                case R.id.btn_to_clear:
                    if (tableAVObject != null && orders.size() == 0 && preOrders.size() == 0) {
                        new QMUIDialog.MessageDialogBuilder(getActivity())
                                .setTitle("温馨提示")
                                .setMessage("是否要清空此桌？")
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
                                        if (tableAVObject != null) {
                                            showDialog();
                                            AVQuery<AVObject> query = new AVQuery<>("Table");
                                            query.getInBackground(tableId, new GetCallback<AVObject>() {
                                                @Override
                                                public void done(AVObject table, AVException e) {
                                                    if (e == null) {
                                                        hideDialog();
                                                        table.put("customer", 0);
                                                        table.put("order", new List[0]);
                                                        table.put("preOrder", new List[0]);
                                                        table.put("refundOrder", new List[0]);
                                                        table.put("startedAt", null);
                                                        table.put("user", null);
                                                        table.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(AVException e) {
                                                                if (e == null) {
                                                                    EventBus.getDefault().post(new ClearEvent(1));
                                                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                                    ft.replace(R.id.fragment_content, TableFg.newInstance("")).commit();
                                                                } else {
                                                                    Logger.d(e.getMessage());
                                                                }

                                                            }
                                                        });
                                                    } else {
                                                        hideDialog();
                                                        ToastUtil.showShort(MyApplication.getContextObject(), "网络繁忙");
                                                        EventBus.getDefault().post(new ClearEvent(1));
                                                    }
                                                }
                                            });
                                        } else {
                                            hideDialog();
                                        }
                                    }
                                })
                                .create(mCurrentDialogStyle).show();
                    }
                    break;
                case R.id.user_charge_nb:
                    if (tableAVObject.getAVObject("user") != null) {
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        NbFg nbFg = NbFg.newInstance(tableAVObject.getAVObject("user").getObjectId());
                        ft.replace(R.id.fragment_content, nbFg, "nb").commit();
                    }
                    break;
                case R.id.btn_choose_commodity:
                    CommodityTypeFragment commodityTypeFragment = new CommodityTypeFragment();
                    commodityTypeFragment.show(getActivity().getSupportFragmentManager(),"orderbyname");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chooseScanType() {
        if (CameraProvider.hasCamera()) {
            if (SharedHelper.readBoolean("useGun")) {
                ScanUserFragment scanUserFragment = new ScanUserFragment(1);
                scanUserFragment.show(getFragmentManager(), "scanuser");
            } else {
                if (MyUtils.getCameraPermission(getContext())) {
                    Intent intent = new Intent(getActivity(), CaptureActivity.class);
                    intent.putExtra(Constant.INTENT_ZXING_CONFIG, MyUtils.caremaSetting());
                    startActivityForResult(intent, REQUEST_CODE_SCAN);
                }
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
                            hideDialog();
                            llShowMember.setVisibility(View.VISIBLE);
                            Double whiteBarBalance = MyUtils.formatDouble(Double.parseDouble(object.get("gold").toString()) - Double.parseDouble(object.get("arrears").toString()));
                            Double storedBalance = MyUtils.formatDouble(Double.parseDouble(object.get("stored").toString()));
                            if (object.get("avatarurl") != null && !object.get("avatarurl").equals("")) {
                                Glide.with(MyApplication.getContextObject()).load(object.get("avatarurl").toString()).into(userAvatar);
                            }
                            userTel.setText("用户手机号:" + object.get("username").toString());
                            userStored.setText("消费金:" + storedBalance);
                            userWhitebar.setText("白条:" + whiteBarBalance);
                            userId = object.get("objectId").toString();
                            if (tableAVObject != null) {
                                tableAVObject.put("user", AVObject.createWithoutData("_User", userId));
                                if (tableAVObject.get("startedAt") == null) {
                                    tableAVObject.put("startedAt", new Date());
                                }
                                tableAVObject.put("preOrder", preOrders);
                                tableAVObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e != null) {
                                            ToastUtil.showShort(getContext(), e.getMessage());
                                        }
                                    }
                                });
                            }
                            signUser.setText("退出登录");


                        } else {
                            hideDialog();
                            ToastUtil.showShort(getContext(), e.getMessage());
                        }
                    }
                });

            }
        }
    }


    /**
     * 添加商品
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ComboEvent event) {
        if (tableAVObject != null) {
            if (!event.getEdit()) {
                preOrders.add(DataUtil.addHashMap(
                        event,
                        tableAVObject,
                        isSvip,
                        userId,
                        mode));
                setView();
                refreshList();
            } else {
                DataUtil.updateIndexOder(
                        event,
                        preOrders,
                        tableAVObject,
                        isSvip,
                        userId,
                        mode
                );
//                DataUtil.additionalCharge(preOrders, event, mode, true,event.getOriginNumber());
                if (tableAVObject != null && tableAVObject.getDate("startedAt") == null) {
                    tableAVObject.put("startedAt", new Date());
                    if (tableAVObject.getInt("customer") == 0) {
                        tableAVObject.put("customer", 1);
                    }
                    tableAVObject.put("preOrder", preOrders);
                    tableAVObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {

                        }
                    });
                }
                setView();
                refreshList();
            }

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
    public void onMessageEvent(final SuccessEvent event) {
        if (event.getCode() == 0) {
            refreshList();
            setView();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void UserMessgae(UserBean userBean) {
        if (userBean.getCallbackCode() == CONST.UserCode.SCANCUSTOMER) {
            llShowMember.setVisibility(View.VISIBLE);
            if (userBean.getAvatar() != null && !userBean.getAvatar().equals("")) {
                Glide.with(MyApplication.getContextObject()).load(userBean.getAvatar()).into(userAvatar);
            }
            userTel.setText("用户手机号:" + userBean.getUsername());
            userStored.setText("消费金:" + userBean.getStored());
            userWhitebar.setText("白条:" + userBean.getBalance());
            userMeatweight.setText("牛肉额度:" + userBean.getMeatWeight() + "kg");
            userId = userBean.getId();
            if (tableAVObject != null) {
                tableAVObject.put("user", AVObject.createWithoutData("_User", userId));
                tableAVObject.put("preOrder", preOrders);
                tableAVObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e != null) {
                            ToastUtil.showShort(getContext(), e.getMessage());
                        }
                    }
                });
            }
            if (userBean.getSvip()) {
                svipAvatar.setVisibility(View.VISIBLE);
                userType.setText("超牛会员");
                isSvip = true;
            } else {
                svipAvatar.setVisibility(View.GONE);
                userType.setText("普通会员");
            }
            signUser.setText("退出登录");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder1 = ButterKnife.bind(this, rootView);
        return rootView;
    }

}
