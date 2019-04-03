package cn.kuwo.player.adapter;

import android.app.FragmentManager;
import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.TypeBean;
import cn.kuwo.player.custom.ShowComboMenuFragment;
import cn.kuwo.player.interfaces.MyItemClickListener;
import cn.kuwo.player.util.AppUtils;
import cn.kuwo.player.util.CalViewGroupUtil;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.RealmHelper;
import cn.kuwo.player.util.ToastUtil;


public class OffineAdapter extends RecyclerView.Adapter<OffineAdapter.MyViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<ProductBean> list;
    private RealmHelper mRealmHleper;
    private List<TypeBean> typeBeans;
    private MyItemClickListener mListener = null;
    private View view;
    private FragmentManager supportFragmentManager;

    public OffineAdapter(Context context, List<ProductBean> discountBeen, List<TypeBean> typeBeans, FragmentManager supportFragmentManager) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        mRealmHleper = new RealmHelper(context);
        this.list = discountBeen;
        this.typeBeans = typeBeans;
        this.supportFragmentManager = supportFragmentManager;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.adapter_offine, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        typeBeans = mRealmHleper.queryAllType();
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        List<ProductBean> productBeans = mRealmHleper.queryStoreCommodity(typeBeans.get(position).getNumber());
        holder.title.setText(typeBeans.get(position).getName() + "·" + productBeans.size());
        CommodityAdapter commodityAdapter = new CommodityAdapter(productBeans);
        holder.gvTable.setAdapter(commodityAdapter);
        CalViewGroupUtil.calGridViewWidthAndHeigh(2,holder.gvTable);
    }

    @Override
    public int getItemCount() {
        return typeBeans.size();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        GridView gvTable;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            gvTable = itemView.findViewById(R.id.gv_table);
        }
    }

    public class CommodityAdapter extends BaseAdapter {
        List<ProductBean> productBeans;

        public CommodityAdapter(List<ProductBean> productBeans) {
            this.productBeans = productBeans;
        }

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
        public View getView(int i, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(MyApplication.getContextObject()).inflate(R.layout.girdview_commodity, parent, false);
                holder = new ViewHolder();
                holder.name = view.findViewById(R.id.name);
                holder.number = view.findViewById(R.id.number);
                holder.weight =view.findViewById(R.id.weight);
                holder.price =  view.findViewById(R.id.price);
                holder.llCommodityItem =view.findViewById(R.id.ll_commodity_item);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            final ProductBean productBean = productBeans.get(i);
            holder.number.setText(productBean.getSerial());
            holder.name.setText(productBean.getName().toString());
            if (productBean.getSerial()!=null) {
                holder.weight.setText("菜品重量:" + productBean.getWeight() +  (productBean.getWeight()>20?"ml":"kg") + (productBean.getScale() > 0 ? "可抵扣牛肉重量:" + MyUtils.formatDouble(productBean.getScale() * productBean.getWeight()) + "kg" + "抵扣后需支付:" + productBean.getRemainMoney() + "元" : ""));

            }
            holder.price.setText("￥" + productBean.getPrice()+"\n"+"牛币价:"+productBean.getNb());
            holder.llCommodityItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });
            holder.llCommodityItem.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    return true;
                }
            });
            return view;
        }
    }

    private class ViewHolder {
        TextView number, name, weight, price;
        LinearLayout llCommodityItem;
    }
}

