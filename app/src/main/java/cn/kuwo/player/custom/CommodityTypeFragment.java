package cn.kuwo.player.custom;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.TypeBean;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.RealmHelper;
import cn.kuwo.player.util.ToastUtil;

/**
 * Created by lovely on 2018/8/17
 */
public class CommodityTypeFragment extends DialogFragment {
    private View view;
    private QMUITipDialog tipDialog;
    private TabLayout tablayout;
    private GridView gvCommoidty;

    private List<TypeBean> typeBeans = new ArrayList<>();
    private List<ProductBean> productBeans=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.fragment_commodity_type, container);
        findView();
        initData();
        return view;
    }
    private void findView() {
        tablayout = view.findViewById(R.id.tablayout);
        gvCommoidty = view.findViewById(R.id.gv_commoidty);
        fetchCommodity();
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getCommodityList(typeBeans.get(tab.getPosition()).getNumber());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void fetchCommodity() {
        RealmHelper realmHelper = new RealmHelper(MyApplication.getContextObject());
        typeBeans.clear();
        typeBeans.addAll(realmHelper.queryAllType());
        if (typeBeans.size()>0){
            for (TypeBean typeBean : typeBeans) {
                tablayout.addTab(tablayout.newTab().setText(typeBean.getName()));
            }
        }
        getCommodityList(typeBeans.get(0).getNumber());
    }

    private void initData() {
        tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中")
                .create();
    }
    public void showDialog() {
        tipDialog.show();
    }

    public void hideDialog() {
        if (tipDialog != null) {
            tipDialog.dismiss();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.85), (int) (dm.heightPixels * 0.9));
            final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            getDialog().getWindow().setAttributes(layoutParams);
        }
    }
    private void getCommodityList(int position) {
        RealmHelper realmHelper = new RealmHelper(MyApplication.getContextObject());
        productBeans = realmHelper.queryCommodityByClassify(position);
        TableAdapter tableAdapter = new TableAdapter();
        gvCommoidty.setAdapter(tableAdapter);
    }
    public class TableAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return productBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (convertView==null){
                convertView=getLayoutInflater().inflate(R.layout.adapter_commodity_item,null);
                viewHolder=new ViewHolder();
                viewHolder.commodity_name=convertView.findViewById(R.id.commodity_name);
                viewHolder.commodity_price=convertView.findViewById(R.id.commodity_price);
                viewHolder.cv_table=convertView.findViewById(R.id.cv_table);
                convertView.setTag(viewHolder);
            }else{
                viewHolder= (ViewHolder) convertView.getTag();
            }
            final ProductBean productBean = productBeans.get(position);
            viewHolder.commodity_name.setText((productBean.getSerial()!=null?"【"+productBean.getSerial()+"】":"")+productBean.getName());
            viewHolder.commodity_price.setText(productBean.getPrice()+"元");
            viewHolder.cv_table.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<ProductBean> productBeans = ProductUtil.searchBySerial(productBean.getObjectId());
                    if (productBeans.size() > 0) {
                        ProductBean productBean = productBeans.get(0);
                        ShowComboMenuFragment showComboMenuFragment = new ShowComboMenuFragment(MyApplication.getContextObject(), productBean, false, productBean.getCode());
                        showComboMenuFragment.show(getActivity().getFragmentManager(), "showcomboMenu");
                    } else {
                        ToastUtil.showShort(MyApplication.getContextObject(), "没有查到此编号商品");
                    }
                }
            });
            return convertView;
        }

        private class ViewHolder{
            TextView commodity_name;
            TextView commodity_price;
            CardView cv_table;

        }
    }

}
