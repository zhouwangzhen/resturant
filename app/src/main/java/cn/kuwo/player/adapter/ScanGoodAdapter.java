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
import android.widget.Toast;

import com.avos.avoscloud.AVObject;
import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.interfaces.MyItemClickListener;
import cn.kuwo.player.interfaces.MyItemLongClickListener;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ProductUtil;

public class ScanGoodAdapter extends RecyclerView.Adapter<ScanGoodAdapter.MyViewHolder> implements View.OnClickListener,View.OnLongClickListener {
    private Context mContext;
    private LayoutInflater inflater;
    private MyItemClickListener mListener = null;
    private AVObject tableAVObject;
    private List<Object> orders;
    private List<Object> preOrders;
    private List<Object> refundOrders;
    private MyItemLongClickListener myItemLongClickListener = null;

    public ScanGoodAdapter(Context context, AVObject tableAVObject) {
        this.mContext = context;
        inflater = LayoutInflater.from(MyApplication.getContextObject());
        this.tableAVObject = tableAVObject;
        orders = tableAVObject.getList("order");
        preOrders = tableAVObject.getList("preOrder");
        refundOrders = tableAVObject.getList("refundOrder");
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_scan_goods, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (position < preOrders.size()) {//添加新的
            HashMap<String, Object> format = ObjectUtil.format(preOrders.get(preOrders.size() - position - 1));
            ProductBean preProductBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            String contnet = preProductBean.getName();
            if (!ObjectUtil.getString(format,"cookStyle").equals("")){
                contnet+="(做法:"+ObjectUtil.getString(format,"cookStyle")+")";
            }
            if (format.containsKey("comboList") && ObjectUtil.getList(format, "comboList").size() > 0) {
                List<String> comboList = ObjectUtil.getList(format, "comboList");
                for (int j = 0; j < comboList.size(); j++) {
                    if (j == 0) {
                        contnet += " (";
                    }
                    contnet += comboList.get(j);
                    if (j == comboList.size() - 1) {
                        contnet += ")";
                    } else {
                        contnet += ",";
                    }
                }

            }
            holder.tvName.setText(contnet);
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_dot);
            holder.tvNumber.setText("x" + ObjectUtil.getDouble(format, "number") + "份");
            holder.tvPrice.setText("￥" + ObjectUtil.getDouble(format, "price"));
            if (preProductBean.getUrl()!=null&&!preProductBean.getUrl().equals("")){
                Glide.with(MyApplication.getContextObject()).load(preProductBean.getUrl()).into(holder.imageAvatar);
            }
            holder.imageState.setImageDrawable(drawable);
            holder.tvSerial.setText(preProductBean.getSerial());
            String weightContent = "菜品重量:" + ObjectUtil.getDouble(format, "weight")  + (ObjectUtil.getDouble(format, "weight")>20?"ml":"kg");
            if (preProductBean.getScale() > 0) {
                weightContent += "超牛会员可抵扣" + MyUtils.formatDouble(ObjectUtil.getDouble(format, "weight")*preProductBean.getScale()) + "kg";
                if (preProductBean.getRemainMoney() > 0) {
                    weightContent += ",额外需要" + MyUtils.formatDouble(preProductBean.getRemainMoney() * ObjectUtil.getDouble(format, "number"));
                }
            }
            holder.tvWeight.setText(weightContent);
            holder.tvComment.setText(ObjectUtil.getString(format, "comment").length() > 0 ? "备注:" + ObjectUtil.getString(format, "comment") : "备注:无");
            if (ObjectUtil.getString(format, "presenter").length() > 0) {
                holder.tvGive.setVisibility(View.VISIBLE);
                try {
                    holder.tvGive.setText("赠送:" + MyUtils.getProductById(ObjectUtil.getString(format, "presenter")).getName());
                } catch (Exception e) {
                }

            } else {
                holder.tvGive.setVisibility(View.GONE);
            }
        } else if (position < preOrders.size() + orders.size()) {//已经下单的
            HashMap<String, Object> format = ObjectUtil.format(orders.get(orders.size() - (position - preOrders.size()) - 1));
            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            String contnet = productBean.getName();
            if (format.containsKey("comboList") && ObjectUtil.getList(format, "comboList").size() > 0) {
                List<String> comboList = ObjectUtil.getList(format, "comboList");
                for (int j = 0; j < comboList.size(); j++) {
                    if (j == 0) {
                        contnet += " (";
                    }
                    contnet += comboList.get(j);
                    if (j == comboList.size() - 1) {
                        contnet += ")";
                    } else {
                        contnet += ",";
                    }
                }

            }
            holder.tvName.setText(contnet);
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_already);
            holder.tvNumber.setText("x" + ObjectUtil.getDouble(format, "number") + "份");
            holder.tvPrice.setText("￥" + ObjectUtil.getDouble(format, "price"));
            if (productBean.getUrl()!=null&&!productBean.getUrl().equals("")){
                Glide.with(MyApplication.getContextObject()).load(productBean.getUrl()).into(holder.imageAvatar);
            }
            holder.imageState.setImageDrawable(drawable);
            holder.tvSerial.setText(productBean.getSerial());
            String weightContent = "菜品重量:" + ObjectUtil.getDouble(format, "weight")  +(ObjectUtil.getDouble(format, "weight")>20?"ml":"kg");
            if (productBean.getScale() > 0) {
                weightContent += "超牛会员可抵扣" + MyUtils.formatDouble(ObjectUtil.getDouble(format, "weight")*productBean.getScale()) + "kg";
                if (productBean.getRemainMoney() > 0) {
                    weightContent += ",额外需要" + MyUtils.formatDouble(productBean.getRemainMoney() * ObjectUtil.getDouble(format, "number"));
                }
            }
            holder.tvWeight.setText(weightContent);
            if (ObjectUtil.getString(format, "presenter").length() > 0) {
                holder.tvGive.setVisibility(View.VISIBLE);
                try {
                    holder.tvGive.setText("赠送:" + MyUtils.getProductById(ObjectUtil.getString(format, "presenter")).getName());
                } catch (Exception e) {
                }
            } else {
                holder.tvGive.setVisibility(View.GONE);
            }
            holder.tvComment.setText(ObjectUtil.getString(format, "comment").length() > 0 ? "备注:" + ObjectUtil.getString(format, "comment") : "备注:无");
        } else {
            HashMap<String, Object> format = ObjectUtil.format(refundOrders.get(refundOrders.size() - (position - preOrders.size() - orders.size()) - 1));
            ProductBean productBean = MyUtils.getProductById(ObjectUtil.getString(format, "id"));
            holder.tvName.setText(productBean.getName());
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_delete);
            holder.tvNumber.setText("x" + ObjectUtil.getDouble(format, "number"));
            holder.tvPrice.setText("￥" + ObjectUtil.getDouble(format, "price"));
            if (productBean.getUrl()!=null&&!productBean.getUrl().equals("")){
                Glide.with(MyApplication.getContextObject()).load(productBean.getUrl()).into(holder.imageAvatar);
            }
            holder.imageState.setImageDrawable(drawable);
            holder.tvSerial.setText(productBean.getSerial());
            String weightContent = "菜品重量:" + ObjectUtil.getDouble(format, "weight")  + (ObjectUtil.getDouble(format, "weight")>20?"ml":"kg");
            if (productBean.getScale() > 0) {
                weightContent += "超牛会员可抵扣" + MyUtils.formatDouble(ObjectUtil.getDouble(format, "weight")*productBean.getScale()) + "kg";
                if (productBean.getRemainMoney() > 0) {
                    weightContent += ",额外需要" + MyUtils.formatDouble(productBean.getRemainMoney() * ObjectUtil.getDouble(format, "number"));
                }
            }
            holder.tvWeight.setText(weightContent);
            if (ObjectUtil.getString(format, "presenter").length() > 0) {
                holder.tvGive.setVisibility(View.VISIBLE);
                try {
                    holder.tvGive.setText("赠送:" + MyUtils.getProductById(MyUtils.getProductById(ObjectUtil.getString(format, "presenter")).getName()).getName());
                } catch (Exception e) {
                }
            } else {
                holder.tvGive.setVisibility(View.GONE);
            }
            holder.tvComment.setText(ObjectUtil.getString(format, "comment").length() > 0 ? "备注:" + ObjectUtil.getString(format, "comment") : "备注:无");
        }
        holder.tvComment.setOnClickListener(this);
        holder.llItem.setOnClickListener(this);
        holder.llItem.setOnLongClickListener(this);
        holder.tvComment.setTag(position);
        holder.llItem.setTag(position);
    }

    @Override
    public int getItemCount() {
        return orders.size() + preOrders.size() + refundOrders.size();
    }

    @Override
    public void onClick(View v) {
        mListener.onItemClick(v, (Integer) v.getTag());
    }

    @Override
    public boolean onLongClick(View v) {
        myItemLongClickListener.onItemClick(v, (Integer) v.getTag());
        return false;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvNumber, tvPrice, tvSerial, tvWeight, tvGive, tvComment;
        LinearLayout llItem, llChangeNumber;
        ImageView imageAvatar, imageState;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvNumber = itemView.findViewById(R.id.tv_number);
            tvPrice = itemView.findViewById(R.id.tv_price);
            llItem = itemView.findViewById(R.id.ll_item);
            llChangeNumber = itemView.findViewById(R.id.ll_change_number);
            imageAvatar = itemView.findViewById(R.id.image_avatar);
            imageState = itemView.findViewById(R.id.image_state);
            tvSerial = itemView.findViewById(R.id.tv_serial);
            tvWeight = itemView.findViewById(R.id.tv_weight);
            tvWeight = itemView.findViewById(R.id.tv_weight);
            tvGive = itemView.findViewById(R.id.tv_give);
            tvComment = itemView.findViewById(R.id.tv_comment);
        }
    }

    public void setOnItemClickListener(MyItemClickListener listener) {
        this.mListener = listener;
    }
    public void setOnItemLongClickListene(MyItemLongClickListener myListener) {
        this.myItemLongClickListener = myListener;
    }

}


