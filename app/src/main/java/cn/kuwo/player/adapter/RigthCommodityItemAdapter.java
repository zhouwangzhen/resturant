package cn.kuwo.player.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import cn.kuwo.player.R;
import cn.kuwo.player.bean.CommodityBean;
import cn.kuwo.player.bean.ProductBean;

/**
 * Created by lovely on 2018/9/22
 */
public class RigthCommodityItemAdapter extends BaseAdapter {
    private Context context;
    private List<ProductBean> beans;

    public RigthCommodityItemAdapter(Context context, List<ProductBean> beans) {
        this.context = context;
        this.beans = beans;
    }

    @Override
    public int getCount() {
        if (beans != null) {
            return beans.size();
        } else {
            return 10;
        }
    }

    @Override
    public Object getItem(int position) {
        return beans.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductBean productBean = beans.get(position);
        ViewHold viewHold = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_right_item, null);
            viewHold = new ViewHold();
            viewHold.tv_name = (TextView) convertView.findViewById(R.id.item_home_name);
            viewHold.item_price = (TextView) convertView.findViewById(R.id.item_price);
            viewHold.item_nb_price = (TextView) convertView.findViewById(R.id.item_nb_price);
            viewHold.iv_icon = (SimpleDraweeView) convertView.findViewById(R.id.item_album);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        viewHold.tv_name.setText((productBean.getSerial()==null?productBean.getSerial():"")+" "+productBean.getName());
        viewHold.item_price.setText("价格:"+productBean.getPrice());
        viewHold.item_nb_price.setText("牛币价"+productBean.getNb());
        Uri uri = Uri.parse("https://qfile.aobeef.cn/361876d35ee46349c23f.png");
        viewHold.iv_icon.setImageURI(uri);
        return convertView;


    }

    private static class ViewHold {
        private TextView tv_name;
        private TextView item_price;
        private TextView item_nb_price;
        private SimpleDraweeView iv_icon;
    }

}
