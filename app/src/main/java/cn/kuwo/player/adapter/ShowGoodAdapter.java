package cn.kuwo.player.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.interfaces.MyItemClickListener;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;

public class ShowGoodAdapter extends RecyclerView.Adapter<ShowGoodAdapter.MyViewHolder>  {
    private Context mContext;
    private LayoutInflater inflater;
    private AVObject tableAVObject;
    private List<AVObject> orders;
    private List<Object> finalOrders;


    public   ShowGoodAdapter(Context context, AVObject tableAVObject, List<Object> finalOrders) {
        this.mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.tableAVObject = tableAVObject;
        this.finalOrders = finalOrders;
        orders = tableAVObject.getList("order");
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_show_goods, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        HashMap<String, Object> format = ObjectUtil.format(finalOrders.get(finalOrders.size() - position - 1));
        ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
        holder.tvName.setText(productBean.getName());
        Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_already);
        holder.tvPrice.setText("￥" + MyUtils.formatDouble(ObjectUtil.getDouble(format, "number") * productBean.getPrice()));
        Glide.with(MyApplication.getContextObject()).load(productBean.getUrl()).into(holder.imageAvatar);
        holder.imageState.setImageDrawable(drawable);
        holder.tvSerial.setText(productBean.getSerial());
        String weightContent = "菜品重量:" + ObjectUtil.getDouble(format, "weight")  + "kg";
        if (productBean.getScale() > 0) {
            weightContent += "超牛会员可抵扣" + MyUtils.formatDouble(ObjectUtil.getDouble(format, "weight")*productBean.getScale()) + "kg";
            if (productBean.getRemainMoney() > 0) {
                weightContent += ",额外需要" + MyUtils.formatDouble(productBean.getRemainMoney() * ObjectUtil.getDouble(format, "number"));
            }
        }
        holder.tvWeight.setText(weightContent);
        holder.tvNumber.setText("￥"+productBean.getPrice()+"*"+ObjectUtil.getDouble(format, "number")+"份");
    }

    @Override
    public int getItemCount() {
        return finalOrders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvSerial, tvWeight,tvNumber;
        LinearLayout llItem;
        ImageView imageAvatar, imageState;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            llItem = itemView.findViewById(R.id.ll_item);
            imageAvatar = itemView.findViewById(R.id.image_avatar);
            imageState = itemView.findViewById(R.id.image_state);
            tvSerial = itemView.findViewById(R.id.tv_serial);
            tvWeight = itemView.findViewById(R.id.tv_weight);
            tvWeight = itemView.findViewById(R.id.tv_weight);
            tvNumber = itemView.findViewById(R.id.tv_number);
        }
    }


}


