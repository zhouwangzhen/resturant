package cn.kuwo.player.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.adapter.LeftMenuAdapter;
import cn.kuwo.player.adapter.RightMenuAdapter;
import cn.kuwo.player.api.CommodityApi;
import cn.kuwo.player.api.CommodityTypeApi;
import cn.kuwo.player.api.RuleApi;
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.RuleBean;
import cn.kuwo.player.bean.TypeBean;
import cn.kuwo.player.bean.entity.SideDishEntity;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.RealmHelper;
import cn.kuwo.player.util.ToastUtil;
import io.realm.RealmList;

/**
 * Created by lovely on 2018/9/22
 */
public class CommodityClassifyFg extends BaseFragment {
    @BindView(R.id.lv_menu)
    ListView lvMenu;
    @BindView(R.id.lv_classify)
    ListView lvClassify;
    @BindView(R.id.tv_commodity_classify_tile)
    TextView tvCommodityClassifyTile;
    Unbinder unbinder;
    @BindView(R.id.btn_refrsh)
    Button btnRefrsh;
    private RealmHelper mRealmHleper;

    private LeftMenuAdapter leftMenuAdapter;
    private RightMenuAdapter rightMenuAdapter;

    private List<TypeBean> typeBeans = new ArrayList<>();
    private List<ProductBean> productBeans = new ArrayList<>();

    private int selectIndex = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fg_commodity_classify;
    }

    @Override
    public void initData() {
        setData();
        btnRefrsh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadType();
            }
        });
    }

    private void setData() {
        Fresco.initialize(getContext());
        leftMenuAdapter = new LeftMenuAdapter(getContext(), typeBeans);
        lvMenu.setAdapter(leftMenuAdapter);
        rightMenuAdapter = new RightMenuAdapter(getContext(), productBeans, typeBeans);
        lvClassify.setAdapter(rightMenuAdapter);
        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                leftMenuAdapter.setSelectItem(position);
                leftMenuAdapter.notifyDataSetInvalidated();
                tvCommodityClassifyTile.setText(typeBeans.get(position).getName());
                lvClassify.setSelection(position);
            }
        });
        lvClassify.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int scrollState;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.scrollState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    return;
                }
                int current = firstVisibleItem;
                if (selectIndex != current && current >= 0) {
                    selectIndex = current;
                    tvCommodityClassifyTile.setText(typeBeans.get(selectIndex).getName());
                    leftMenuAdapter.setSelectItem(selectIndex);
                    leftMenuAdapter.notifyDataSetInvalidated();
                }
            }
        });
        fetchCommodity();
    }


    private void fetchCommodity() {
        mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        typeBeans.clear();
        productBeans.clear();
        typeBeans.addAll(mRealmHleper.queryAllType());
        productBeans.addAll(mRealmHleper.queryAllProduct());
        if (typeBeans.size()>0){
            tvCommodityClassifyTile.setText(typeBeans.get(0).getName());
            leftMenuAdapter.notifyDataSetChanged();
            rightMenuAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void loadType() {
        showDialog();
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
                    ToastUtil.showLong(MyApplication.getContextObject(), "加载失败");
                }
            }
        });
    }

    public void loadCommodity() {
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
                        productBean.setClassify(avObject.getInt("classify"));
                        RealmList<String> commentsList = new RealmList<>();
                        for (int k = 0; k < avObject.getList("comments").size(); k++) {
                            commentsList.add(avObject.getList("comments").get(k).toString());
                        }
                        productBean.setGiveRule(avObject.getInt("giveRule"));
                        productBean.setReviewCommodity(avObject.getString("reviewCommodity"));
                        productBean.setComments(commentsList);
                        productBean.setMerge(avObject.getBoolean("merge"));
                        productBean.setNbDiscountType(avObject.getInt("nb_discount_type"));
                        productBean.setNbDiscountRate(avObject.getDouble("nb_discount_rate"));
                        productBean.setNbDiscountPrice(avObject.getDouble("nb_discount_price"));
                        productBean.setSpecial(avObject.getString("special"));
                        if (avObject.getList("sideDish") != null && avObject.getList("sideDish").size() > 0) {
                            List<HashMap<String,Object>> sideDish = (List<HashMap<String,Object>>) avObject.getList("sideDish");
                            RealmList<SideDishEntity> objects = new RealmList<>();
                            for (int j = 0; j < sideDish.size(); j++) {
                                SideDishEntity sideDishEntity = new SideDishEntity();
                                sideDishEntity.setName(sideDish.get(j).get("name").toString());
                                sideDishEntity.setPrice(Double.parseDouble(sideDish.get(j).get("price").toString()));
                                objects.add(sideDishEntity);
                            }
                            productBean.setSidedish(objects);
                        } else {
                            productBean.setSidedish(new RealmList<SideDishEntity>());
                        }
                        mRealmHleper.addProduct(productBean);

                    }
                    hideDialog();
                    setData();
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
                    }

                }

            }
        });
    }
}
