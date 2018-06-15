package cn.kuwo.player.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.adapter.GoodsAdapter;
import cn.kuwo.player.adapter.ScanAdapter;
import cn.kuwo.player.base.BaseActivity;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.RetailBean;
import cn.kuwo.player.custom.FlowRadioGroup;
import cn.kuwo.player.interfaces.MyItemClickListener;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.ToastUtil;

public class RetailActivity extends BaseActivity {
    @BindView(R.id.scan_meatcode)
    EditText scanMeatcode;
    @BindView(R.id.recycle_scan)
    RecyclerView recycleScan;
    @BindView(R.id.total_money)
    TextView totalMoney;
    @BindView(R.id.submit_order)
    TextView submitOrder;
    @BindView(R.id.fl_total)
    FrameLayout flTotal;
    @BindView(R.id.noInfo)
    TextView noInfo;
    @BindView(R.id.rg_category)
    FlowRadioGroup rgCategory;
    @BindView(R.id.recycle_other)
    RecyclerView recycleOther;
    @BindView(R.id.list_other)
    LinearLayout listOther;
    @BindView(R.id.other_goods)
    TextView otherGoods;
    @BindView(R.id.fun_list)
    LinearLayout funList;
    @BindView(R.id.tv_back)
    LinearLayout tvBack;
    @BindView(R.id.goods_explode)
    RadioButton goodsExplode;
    private GridLayoutManager gridLayoutManager;
    private LinearLayoutManager linearLayoutManager;
    private ScanAdapter scanAdapter;
    private GoodsAdapter goodsAdapter;
    private ArrayList<ProductBean> commodityList = new ArrayList<>();
    private ArrayList<Double> prices = new ArrayList<>();
    private ArrayList<Double> weights = new ArrayList<>();
    private ArrayList<String> codes = new ArrayList<>();
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();

    private String barcode = "";
    private Double money = 0.0;

    @SuppressLint("HandlerLeak")
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

    @Override
    protected int getContentViewId() {
        return R.layout.activity_retail;
    }

    @Override
    public void initData() {
        linearLayoutManager = new LinearLayoutManager(this);
        recycleScan.setLayoutManager(linearLayoutManager);
        scanAdapter = new ScanAdapter(getApplication(), commodityList, codes, prices, weights);
        recycleScan.setAdapter(scanAdapter);
        scanAdapter.setOnItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                deleteData(postion);
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
                Intent intent = new Intent(RetailActivity.this, SettleActivity.class);
                RetailBean retailBean = new RetailBean(ids, codes, prices, weights,names);
                intent.putExtra("retailBean", retailBean);
                startActivityForResult(intent, 1);
            }
        });
//        scanMeatcode.setText("08066145");
//        scanMeatcode.setText("214000188800080006");
//        scanMeatcode.setText("219999911500002306");
        otherGoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listOther.getVisibility() == View.VISIBLE) {
                    listOther.setVisibility(View.GONE);
                } else {
                    listOther.setVisibility(View.VISIBLE);
                    LoadingGoods(1);
                }
            }
        });
        rgCategory.check(R.id.goods_explode);
        rgCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.goods_explode:
                        LoadingGoods(1);
                        break;
                    case R.id.goods_wine:
                        LoadingGoods(3);
                }
            }
        });
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void LoadingGoods(int index) {
        final List<ProductBean> otherGoods = ProductUtil.getOtherGoods(index);
        gridLayoutManager = new GridLayoutManager(this, 2);
        recycleOther.setLayoutManager(gridLayoutManager);
        goodsAdapter = new GoodsAdapter(getApplicationContext(), otherGoods);
        recycleOther.setAdapter(goodsAdapter);
        goodsAdapter.setOnItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                String code = otherGoods.get(postion).getCode();
                scanMeatcode.setText(code);
            }
        });
    }

    private void addProduct(String barcode) {
        ProductBean productBean = MyUtils.getProductBean(barcode).get(0);
        commodityList.add(productBean);
        codes.add(barcode);
        names.add(productBean.getName());
        weights.add(ProductUtil.calCommodityWeight(barcode));
        prices.add(ProductUtil.calCommodityMoney(barcode));
        ids.add(ProductUtil.calCommodityId(barcode));
        scanAdapter.notifyDataSetChanged();
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
        names.remove(postion);
        scanAdapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            codes = new ArrayList<>();
            prices = new ArrayList<>();
            codes = new ArrayList<>();
            weights = new ArrayList<>();
            ids = new ArrayList<>();
            names = new ArrayList<>();
            money = 0.0;
            commodityList = new ArrayList<>();
            noInfo.setVisibility(View.VISIBLE);
            flTotal.setVisibility(View.GONE);
            listOther.setVisibility(View.GONE);
            scanAdapter = new ScanAdapter(getApplication(), commodityList, codes, prices, weights);
            recycleScan.setAdapter(scanAdapter);
            scanAdapter.setOnItemClickListener(new MyItemClickListener() {
                @Override
                public void onItemClick(View view, int postion) {
                    deleteData(postion);
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
}
