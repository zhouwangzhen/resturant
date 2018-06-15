package cn.kuwo.player.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.FindCallback;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.kuwo.player.R;
import cn.kuwo.player.activity.RetailActivity;
import cn.kuwo.player.activity.SettleActivity;
import cn.kuwo.player.api.HangUpApi;
import cn.kuwo.player.base.BaseFragment;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.bean.RetailBean;
import cn.kuwo.player.util.DataUtil;
import cn.kuwo.player.util.DateUtil;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;

public class HangUpFragment extends BaseFragment {
    private static String ARG_PARAM = "param_key";
    @BindView(R.id.gv_hangup)
    GridView gvHangup;
    private Activity mActivity;
    private String mParam;
    private List<AVObject> hangUpList;
    private HangAdapter hangAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fg_hangup_list;
    }

    @Override
    public void initData() {
        showDialog();
        HangUpApi.getHangUpOrders().findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    hideDialog();
                    hangUpList = list;
                    hangAdapter = new HangAdapter();
                    gvHangup.setAdapter(hangAdapter);
                }else{
                    hideDialog();
                }
            }
        });

    }

    public static HangUpFragment newInstance(String str) {
        HangUpFragment hangUpFragment = new HangUpFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM, str);
        hangUpFragment.setArguments(bundle);
        return hangUpFragment;
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mParam = getArguments().getString(ARG_PARAM);  //获取参数
    }


    public class HangAdapter extends BaseAdapter {
        private int selectIndex = -1;

        @Override
        public int getCount() {
            return hangUpList.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_hangup, parent, false);
                holder = new ViewHolder();
                holder.order_date = convertView.findViewById(R.id.order_date);
                holder.order_state = convertView.findViewById(R.id.order_state);
                holder.order_state_img = convertView.findViewById(R.id.order_state_img);
                holder.order_detail = convertView.findViewById(R.id.order_detail);
                holder.show_detail = convertView.findViewById(R.id.show_detail);
                holder.card_order = convertView.findViewById(R.id.card_order);
                holder.btn_to_settle = convertView.findViewById(R.id.btn_to_settle);
                holder.order_remark = convertView.findViewById(R.id.order_remark);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final AVObject avObject = hangUpList.get(position);
            holder.order_date.setText("订单时间:" + DateUtil.formatLongDate(avObject.getDate("createdAt")));
            holder.order_remark.setText("备注信息:" + avObject.getString("remark"));
            holder.order_state.setText("订单状态:挂单中");
            String commodityList = "菜品详情\n";
            if (avObject.getInt("type")==0){
                for (int i = 0; i < avObject.getList("order").size(); i++) {
                    HashMap<String, Object> order = ObjectUtil.format(avObject.getList("order").get(i));
                    commodityList += order.get("name").toString() + "*" + order.get("number").toString() + "份\n";
                }
            }else if(avObject.getInt("type")==1){

                for (int i = 0; i < avObject.getList("order").size(); i++) {
                    HashMap<String, Object> order = ObjectUtil.format(avObject.getList("order").get(i));
                    commodityList += order.get("name").toString() + "*" + order.get("weight").toString() + (Double.parseDouble(order.get("weight").toString())>20?"ml":"kg")+"份\n";
                }
            }

            holder.order_detail.setText(commodityList);
            holder.card_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectIndex = position;
                    if (holder.show_detail.getVisibility() == View.VISIBLE) {
                        holder.show_detail.setVisibility(View.GONE);
                    } else {
                        holder.show_detail.setVisibility(View.VISIBLE);
                    }
                    hangAdapter.notifyDataSetChanged();
                }
            });
            if (position != selectIndex) {
                holder.show_detail.setVisibility(View.GONE);
            }
            holder.btn_to_settle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (avObject.getInt("type")==0){
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.fragment_content, SettleFg.newInstance(avObject.getObjectId(),true)).commit();
                    }else{
                        Intent intent = new Intent(getActivity(), SettleActivity.class);
                        RetailBean retailBean = DataUtil.buildRetailBean((List<Object>)avObject.get("order"));
                        intent.putExtra("retailBean", retailBean);
                        intent.putExtra("isHangUp", true);
                        intent.putExtra("remark",avObject.getString("remark"));
                        intent.putExtra("hangUpId",avObject.getObjectId());
                        startActivityForResult(intent,1);
                    }

                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView order_date;
            TextView order_state;
            TextView order_detail;
            TextView order_remark;
            TextView order_state_img;
            LinearLayout show_detail;
            CardView card_order;
            Button btn_to_settle;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            initData();
        }
    }
}
