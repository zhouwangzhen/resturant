package cn.kuwo.player.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.logging.Logger;

import cn.kuwo.player.R;
import cn.kuwo.player.bean.TypeBean;

/**
 * Created by lovely on 2018/9/22
 */
public class LeftMenuAdapter extends BaseAdapter {

    private Context context;
    private int selectItem=0;
    private List<TypeBean> list;

    public LeftMenuAdapter(Context context, List<TypeBean> typeBeans) {
        this.context = context;
        this.list = typeBeans;
    }

    public int getSelectItem() {
        return selectItem;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView=View.inflate(context, R.layout.item_left_menu,null);
            viewHolder.tv_name=convertView.findViewById(R.id.item_name);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        if (position == selectItem) {
            viewHolder.tv_name.setBackgroundColor(Color.WHITE);
            viewHolder.tv_name.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            viewHolder.tv_name.setBackgroundColor(context.getResources().getColor(R.color.background));
            viewHolder.tv_name.setTextColor(context.getResources().getColor(R.color.black));
        }
        viewHolder.tv_name.setText(list.get(position).getName());
        return convertView;
    }
    static class ViewHolder{
        private TextView tv_name;
    }
}
