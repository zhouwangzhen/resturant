package cn.kuwo.player.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUIEmptyView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.adapter.OffineAdapter;
import cn.kuwo.player.api.CommodityApi;
import cn.kuwo.player.api.CommodityTypeApi;
import cn.kuwo.player.api.RuleApi;
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.RuleBean;
import cn.kuwo.player.bean.TypeBean;
import cn.kuwo.player.custom.ShowActivityFragment;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.RealmHelper;
import cn.kuwo.player.util.ToastUtil;
import io.realm.RealmList;

public class CommodityFg extends BaseFragment {
    private static String ARG_PARAM = "param_key";
    @BindView(R.id.rule_content)
    TextView ruleContent;
    Unbinder unbinder;
    @BindView(R.id.ll_current_activity)
    LinearLayout llCurrentActivity;
    Unbinder unbinder1;
    private Activity mActivity;
    private String mParam;
    @BindView(R.id.offinelist)
    RecyclerView offinelist;
    @BindView(R.id.btn_refrsh)
    Button btnRefrsh;
    @BindView(R.id.emptyView)
    QMUIEmptyView emptyView;
    private RealmHelper mRealmHleper;
    private OffineAdapter offineAdapter;

    public static CommodityFg newInstance() {
        return new CommodityFg();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fg_commodity;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mParam = getArguments().getString(ARG_PARAM);  //获取参数
    }

    @Override
    public void initData() {
        mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        List<RuleBean> ruleBeans = mRealmHleper.queryAllRule();
        if (ruleBeans.size() > 0) {
            RuleBean ruleBean = ruleBeans.get(0);
            String ruleInfo = "";
            if (ruleBean.getAllDiscount() != 1) {
                ruleInfo += "全场" + MyUtils.formatDouble(ruleBean.getAllDiscount() * 10) + "折优惠";
            }
            ruleContent.setText(ruleInfo);
        }
        final List<ProductBean> productBeans = mRealmHleper.queryAllProduct();
        final List<TypeBean> typeBeans = mRealmHleper.queryAllType();
        showDialog();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyApplication.getContextObject(), LinearLayout.VERTICAL, false);
        offinelist.setLayoutManager(linearLayoutManager);
        offineAdapter = new OffineAdapter(MyApplication.getContextObject(), productBeans, typeBeans, getActivity().getFragmentManager());
        offinelist.setAdapter(offineAdapter);
        hideDialog();

    }


    public void loadCommodity() {
        emptyView.show(true);
        CommodityApi.getOfflineCommodity().findInBackground(new FindCallback<AVObject>() {
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
                        productBean.setNb(avObject.getDouble("nb"));
                        productBean.setComboMenu(avObject.getString("comboMenu") == null ? "" : MyUtils.replaceBlank(avObject.getString("comboMenu").trim().replace(" ", "")));
                        RealmList<String> commentsList = new RealmList<>();
                        for (int k = 0; k < avObject.getList("comments").size(); k++) {
                            commentsList.add(avObject.getList("comments").get(k).toString());
                        }
                        productBean.setGiveRule(avObject.getInt("giveRule"));
                        productBean.setReviewCommodity(avObject.getString("reviewCommodity"));
                        productBean.setComments(commentsList);
                        productBean.setMerge(avObject.getBoolean("merge"));
                        mRealmHleper.addProduct(productBean);

                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyApplication.getContextObject(), LinearLayout.VERTICAL, false);
                    offinelist.setLayoutManager(linearLayoutManager);
                    offineAdapter = new OffineAdapter(MyApplication.getContextObject(), mRealmHleper.queryAllProduct(), mRealmHleper.queryAllType(), mActivity.getFragmentManager());
                    offinelist.setAdapter(offineAdapter);
                    emptyView.show(false);
                    hideDialog();
                } else {
                    hideDialog();
                }
            }
        });
        fetchRule();

    }

    private void fetchRule() {
        RuleApi.getRule().findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        Logger.d(list.get(0));
                        mRealmHleper = new RealmHelper(MyApplication.getContextObject());
                        mRealmHleper.deleteAll(RuleBean.class);
                        AVObject avObject = list.get(i);
                        RuleBean ruleBean = new RuleBean();
                        ruleBean.setNoMemberJoin(avObject.getBoolean("noMemberJoin"));
                        ruleBean.setDrinkJoin(avObject.getBoolean("drinkJoin"));
                        ruleBean.setOnlyMeatJoin(avObject.getBoolean("onlyMeatJoin"));
                        ruleBean.setMemberNoMoneyJoin(avObject.getBoolean("MemberNoMoneyJoin"));
                        ruleBean.setAllDiscount(avObject.getDouble("allDiscount"));
                        ruleBean.setDiscountContent(avObject.getString("discountContent"));
                        ruleBean.setNoMemberBOGO(avObject.getBoolean("NoMemberBOGO"));
                        ruleBean.setMemberDiscountJoin(avObject.getBoolean("MemberDiscountJoin"));
                        ruleBean.setWineJoin(avObject.getBoolean("wineJoin"));
                        ruleBean.setFoldOnFoldJoin(avObject.getBoolean("foldOnFoldJoin"));
                        RealmList<String> reduceList = new RealmList<>();
                        for (int k = 0; k < avObject.getList("fullReduce").size(); k++) {
                            reduceList.add(avObject.getList("fullReduce").get(k).toString());
                        }
                        ruleBean.setFullReduce(reduceList);

                        mRealmHleper.addRule(ruleBean);
                        for (String reduce : ruleBean.getFullReduce()) {
                            Logger.d(reduce);
                        }
                    }
                    List<RuleBean> ruleBeans = mRealmHleper.queryAllRule();
                    if (ruleBeans.size() > 0) {
                        RuleBean ruleBean = ruleBeans.get(0);
                        String ruleInfo = "";
                        if (ruleBean.getAllDiscount() != 1) {
                            ruleInfo += "全场" + MyUtils.formatDouble(ruleBean.getAllDiscount() * 10) + "折优惠";
                        }
                        ruleContent.setText(ruleInfo);
                    }

                }

            }
        });
    }

    public void loadType() {
        showDialog();
        emptyView.show(true);
        CommodityTypeApi.getCommodityType().findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
                    mRealmHleper.deleteAll(TypeBean.class);
                    for (int i = 0; i < list.size(); i++) {
                        AVObject avObject = list.get(i);
                        TypeBean typeBean = new TypeBean();
                        typeBean.setName(avObject.getString("name"));
                        typeBean.setNumber(avObject.getInt("number"));
                        typeBean.setStore(avObject.getInt("store"));
                        mRealmHleper.addType(typeBean);
                    }
                    loadCommodity();
                } else {
                    hideDialog();
                    emptyView.show(false);
                    ToastUtil.showLong(MyApplication.getContextObject(), "加载失败");
                }
            }
        });
    }

    public static CommodityFg newInstance(String str) {
        CommodityFg commodityFg = new CommodityFg();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM, str);
        commodityFg.setArguments(bundle);
        return commodityFg;
    }


    @OnClick({R.id.btn_refrsh, R.id.ll_current_activity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_refrsh:
                loadType();
                break;
            case R.id.ll_current_activity:
                ShowActivityFragment showActivityFragment = new ShowActivityFragment();
                showActivityFragment.show(getFragmentManager(), "showactivitylist");
                break;
        }
    }

}

