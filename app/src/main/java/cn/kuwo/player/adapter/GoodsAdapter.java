package cn.kuwo.player.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.kuwo.player.R;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.interfaces.MyItemClickListener;

public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.MyViewHolder>implements View.OnClickListener  {
    private Context context;
    private LayoutInflater inflater;
    private List<ProductBean> list;
    private MyItemClickListener mListener = null;

    public GoodsAdapter(Context context, List<ProductBean> productBeen) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.list = productBeen;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_goods, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ProductBean productBean = list.get(position);
        holder.name.setText(productBean.getName());
        holder.name.setTag(position);
        holder.name.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        mListener.onItemClick(v, (int) v.getTag());
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
        }
    }
    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mListener = listener;
    }
}

