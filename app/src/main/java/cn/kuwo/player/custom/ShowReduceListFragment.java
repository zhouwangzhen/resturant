package cn.kuwo.player.custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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

import com.orhanobut.logger.Logger;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import cn.kuwo.player.R;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;

public class ShowReduceListFragment extends DialogFragment {
    private View view;
    private GridView gvTable;
    private TextView title;
    private List<Object> list;
    private int type;
    private ListAdapter listAdapter;
    @SuppressLint("ValidFragment")
    public ShowReduceListFragment(List<Object> list, int type) {
        this.list = list;
        this.type = type;
        Logger.d(list);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.show_reduce_list, container);
        findView();
        initData();
        return view;
    }

    private void initData() {
    }

    private void findView() {
        gvTable = view.findViewById(R.id.gv_table);
        title = view.findViewById(R.id.title);
        if (type == 1) {
            title.setText("我的牛肉额度可兑换的牛肉详情");
        }
        listAdapter = new ListAdapter();
        gvTable.setAdapter(listAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialog.getWindow().setLayout((int) (dm.widthPixels * 0.75), (int) (dm.widthPixels * 0.5));
            final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            getDialog().getWindow().setAttributes(layoutParams);
        }
    }


    public class ListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
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
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_show_meat_list, parent, false);
                holder = new ViewHolder();
                holder.meat_name=view.findViewById(R.id.meat_name);
                holder.meat_number=view.findViewById(R.id.meat_number);
                holder.meat_reduce_money=view.findViewById(R.id.meat_reduce_money);
                holder.meat_reduce_weight=view.findViewById(R.id.meat_reduce_weight);
                holder.meat_extra_money=view.findViewById(R.id.meat_extra_money);
                holder.meat_weight=view.findViewById(R.id.meat_weight);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            HashMap<String, Object> hashMap = (HashMap<String, Object>) list.get(position);
            holder.meat_name.setText(ObjectUtil.getString(hashMap,"name"));
            holder.meat_number.setText(ObjectUtil.getDouble(hashMap,"number")+"份");
            if(MyUtils.getProductById(ObjectUtil.getString(hashMap,"id")).getCode().length()==5){
                holder.meat_reduce_money.setText(MyUtils.formatDouble(ObjectUtil.getDouble(hashMap,"price"))+"元");
            }else {
                holder.meat_reduce_money.setText(MyUtils.formatDouble((MyUtils.getProductById(ObjectUtil.getString(hashMap, "id")).getPrice() - MyUtils.getProductById(ObjectUtil.getString(hashMap, "id")).getRemainMoney()) * ObjectUtil.getDouble(hashMap, "number")) + "元");
            }
            try {
                holder.meat_weight.setText(ObjectUtil.getString(hashMap, "weight")+"kg");
            }catch (Exception e){
                e.printStackTrace();
            }
            holder.meat_reduce_weight.setText(ObjectUtil.getDouble(hashMap,"meatWeight")+"kg");
            holder.meat_extra_money.setText(MyUtils.formatDouble(MyUtils.getProductById(ObjectUtil.getString(hashMap,"id")).getRemainMoney()*ObjectUtil.getDouble(hashMap,"number"))+"元");
            return view;
        }

        private class ViewHolder {
            TextView meat_name;
            TextView meat_number;
            TextView meat_reduce_money;
            TextView meat_reduce_weight;
            TextView meat_extra_money;
            TextView meat_weight;

        }
    }
}
