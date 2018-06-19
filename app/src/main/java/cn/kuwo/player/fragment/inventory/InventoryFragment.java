package cn.kuwo.player.fragment.inventory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.adapter.InventoryAdapter;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.custom.ShowInventoryFragment;
import cn.kuwo.player.interfaces.MyItemClickListener;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.ToastUtil;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by lovely on 2018/6/14
 */
public class InventoryFragment extends SupportFragment {
    EditText scanMeatcode;
    RecyclerView recycleScan;
    TextView totalMoney;
    TextView submitOrder;
    FrameLayout flTotal;
    TextView noInfo;
    TextView title;
    private static String ARG_PARAM = "param_type";
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private InventoryAdapter inventoryAdapter;
    private ArrayList<ProductBean> commodityList = new ArrayList<>();
    private ArrayList<Double> prices = new ArrayList<>();
    private ArrayList<Double> weights = new ArrayList<>();
    private ArrayList<String> codes = new ArrayList<>();
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<Integer> numbers = new ArrayList<>();
    private String barcode = "";
    private Double money = 0.0;
    private int type = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {
            scanMeatcode.setText("");
            if (MyUtils.getProductBean(barcode).size() > 0) {
                addProduct(barcode);
            } else {
                ToastUtil.showShort(MyApplication.getContextObject(), "扫码商品有误");
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPreFragment() == null) {
                    getActivity().finish();
                } else {
                    pop();
                }
            }
        });
        recycleScan = view.findViewById(R.id.recycle_scan);
        totalMoney = view.findViewById(R.id.total_money);
        submitOrder = view.findViewById(R.id.submit_order);
        flTotal = view.findViewById(R.id.fl_total);
        noInfo = view.findViewById(R.id.noInfo);
        title = view.findViewById(R.id.title);
        scanMeatcode = view.findViewById(R.id.scan_meatcode);
        if (type==1){
            title.setText("晚间库存盘点");
        }else{
            title.setText("早间库存盘点");
        }
        linearLayoutManager = new LinearLayoutManager(MyApplication.getContextObject());
        recycleScan.setLayoutManager(linearLayoutManager);
        inventoryAdapter = new InventoryAdapter(MyApplication.getContextObject(), commodityList, codes, prices, weights, numbers);
        recycleScan.setAdapter(inventoryAdapter);
        inventoryAdapter.setOnItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                deleteData(postion);
            }
        });
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanMeatcode.setText("214000188800080006");
            }
        });
        setListener();

    }

    private void setListener() {
        scanMeatcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    if (delayRun != null) {
                        mHandler.removeCallbacks(delayRun);
                    }
                    barcode = s.toString().trim();
                    mHandler.postDelayed(delayRun, 300);
                }
            }
        });
        submitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> totalCommoditys = ProductUtil.calMergeCommodity(ids, numbers, weights);
                ShowInventoryFragment showInventoryFragment = new ShowInventoryFragment(type,totalCommoditys);
                showInventoryFragment.setTargetFragment(InventoryFragment.this, 1);
                showInventoryFragment.show(getFragmentManager(), "showinventory");
            }
        });

    }

    private void addProduct(String barcode) {
        ProductBean productBean = MyUtils.getProductBean(barcode).get(0);
        commodityList.add(productBean);
        codes.add(barcode);
        numbers.add(1);
        weights.add(ProductUtil.calCommodityWeight(barcode));
        prices.add(ProductUtil.calCommodityMoney(barcode));
        ids.add(ProductUtil.calCommodityId(barcode));
        inventoryAdapter.notifyDataSetChanged();
        money = MyUtils.totalPrice(prices);
        totalMoney.setText("共" + codes.size() + "件" + "     总计:" + money + "元");
        if (barcode.length() > 0) {
            noInfo.setVisibility(View.GONE);
            flTotal.setVisibility(View.VISIBLE);
        }
    }

    private void deleteData(int postion) {
        codes.remove(postion);
        prices.remove(postion);
        commodityList.remove(postion);
        weights.remove(postion);
        ids.remove(postion);
        numbers.remove(postion);
        inventoryAdapter.notifyDataSetChanged();
        money = MyUtils.totalPrice(prices);
        totalMoney.setText("共" + codes.size() + "件" + "     总计:" + money + "元");
        if (codes.size() > 0) {
            noInfo.setVisibility(View.GONE);
            flTotal.setVisibility(View.VISIBLE);
        } else {
            noInfo.setVisibility(View.VISIBLE);
            flTotal.setVisibility(View.GONE);
        }
    }

    public int getLayoutId() {
        return R.layout.fg_inventory_morning;
    }

    public static InventoryFragment newInstance(int type) {
        InventoryFragment inventoryFragment = new InventoryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PARAM, type);
        inventoryFragment.setArguments(bundle);
        return inventoryFragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            pop();
        }
    }

    @Override
    protected void onNewBundle(Bundle args) {
        super.onNewBundle(args);
        type=args.getInt("type",0);
        ToastUtil.showLong(MyApplication.getContextObject(),type+"");
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        type=getArguments().getInt(ARG_PARAM);
        Logger.d(type);
    }
}
