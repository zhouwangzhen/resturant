package cn.kuwo.player.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.player.R;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.TypeBean;
import cn.kuwo.player.custom.GridViewForScrollView;

/**
 * Created by lovely on 2018/9/22
 */
public class RightMenuAdapter extends BaseAdapter {
    private Context context;
    private List<ProductBean> commodityBeans;
    private List<TypeBean> typeBeans;



    public RightMenuAdapter(Context context, List<ProductBean> productBeans, List<TypeBean> typeBeans) {
        this.context=context;
        this.commodityBeans=productBeans;
        this.typeBeans=typeBeans;
    }

    @Override
    public int getCount() {
        if (typeBeans!=null){
            return typeBeans.size();
        }else {
            return 10;
        }
    }

    @Override
    public Object getItem(int position) {
        return commodityBeans.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_right_commodity, null);
            viewHolder = new ViewHolder();
            viewHolder.gridView = convertView.findViewById(R.id.gridView);
            viewHolder.blank = convertView.findViewById(R.id.blank);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        List<ProductBean> classifyBeans = new ArrayList<>();
        for (int i=0;i<commodityBeans.size();i++){
            if (commodityBeans.get(i).getType()==typeBeans.get(position).getNumber()){
                classifyBeans.add(commodityBeans.get(i));
            }
        }
        RigthCommodityItemAdapter rigthCommodityItemAdapter = new RigthCommodityItemAdapter(context, classifyBeans);
        viewHolder.gridView.setAdapter(rigthCommodityItemAdapter);
        viewHolder.blank.setText(typeBeans.get(position).getName());
        return convertView;
    }
    static class ViewHolder{
        private GridViewForScrollView gridView;
        private TextView blank;
    }
}
