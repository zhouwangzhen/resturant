package cn.kuwo.player.adapter;

import android.app.Application;
import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.kuwo.player.R;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.interfaces.MyItemClickListener;

public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.MyViewHolder> implements View.OnClickListener {
    private Context context;
    private List<ProductBean> commodityList;
    private List<String> codes;
    private List<Double> prices;
    private List<Double> weights;
    private LayoutInflater inflater;
    private MyItemClickListener mListener = null;


    public ScanAdapter(Context context, List<ProductBean> commodityList, List<String> codes, List<Double> prices, List<Double> weights) {
        this.context = context;
        this.commodityList = commodityList;
        this.codes = codes;
        this.prices = prices;
        this.weights = weights;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_meatinfo, parent, false);
        ScanAdapter.MyViewHolder holder = new ScanAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ProductBean productBean = commodityList.get(position);
        holder.meatName.setText(productBean.getName());
        double weight=weights.get(position);
        holder.meatPrice.setText(prices.get(position)+"");
        if (weight> 20) {
            holder.meatWeight.setText(weight + "ml");
        } else {
            holder.meatWeight.setText(weight + "kg");
        }
        holder.number.setText((position + 1) + "");
        holder.delete.setOnClickListener(this);
        holder.delete.setTag(position);
    }

    @Override
    public int getItemCount() {
        return codes.size();
    }

    @Override
    public void onClick(View v) {
        mListener.onItemClick(v, (Integer) v.getTag());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView meatName, meatWeight, meatPrice, number, meatPriceOrigin;
        Button delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            meatName = itemView.findViewById(R.id.meat_name);
            meatWeight = itemView.findViewById(R.id.meat_weight);
            meatPrice = itemView.findViewById(R.id.meat_price);
            number = itemView.findViewById(R.id.number);
            delete = itemView.findViewById(R.id.delete);
            meatPriceOrigin = itemView.findViewById(R.id.meat_price_origin);

        }
    }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mListener = listener;
    }
}
