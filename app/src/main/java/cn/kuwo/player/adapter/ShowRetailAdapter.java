package cn.kuwo.player.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.RetailBean;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ProductUtil;

public class ShowRetailAdapter extends RecyclerView.Adapter<ShowRetailAdapter.MyViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private RetailBean retailBean;

    public ShowRetailAdapter(Context context, RetailBean retailBean) {
        this.mContext = context;
        this.retailBean=retailBean;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_scan_commodity, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ProductBean productBean =MyUtils.getProductById(retailBean.getIds().get(position));
        Double price = retailBean.getPrices().get(position);
        Double weight = retailBean.getWeight().get(position);
        if (productBean.getUrl()!=null&&!productBean.getUrl().equals("")){
            Glide.with(MyApplication.getContextObject()).load(productBean.getUrl()).into(holder.imageAvatar);
        }
        holder.tvPrice.setText("￥"+price+"元");
        holder.tvCommodityName.setText(productBean.getName());
        if (weight> 20) {
            holder.tvWeight.setText(weight + "ml");
        } else {
            holder.tvWeight.setText(weight + "kg");
        }
    }

    @Override
    public int getItemCount() {
        return retailBean.getCodes().size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommodityName, tvPrice, tvWeight;
        ImageView imageAvatar;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvCommodityName = itemView.findViewById(R.id.tv_commodity_name);
            imageAvatar = itemView.findViewById(R.id.image_avatar);
            tvWeight = itemView.findViewById(R.id.tv_weight);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
