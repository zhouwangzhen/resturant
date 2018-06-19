package cn.kuwo.player.custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.api.InventoryApi;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.ToastUtil;

/**
 * Created by lovely on 2018/6/18
 */
@SuppressLint("ValidFragment")
public class ShowInventoryFragment extends DialogFragment {
    private HashMap<String, Object> totalCommoditys;
    private Map<String, Object> preInventorys;
    private Map<String, Object> behindInventorys;
    private ArrayList<String> ids = new ArrayList<>();
    private View view;
    private Button btnSureOrder;
    private TextView title;
    private GridView gvInventory;
    private ListAdapter listAdapter;
    private LinearLayout llDetail;
    private int type;
    QMUITipDialog tipDialog;

    @SuppressLint("ValidFragment")
    public ShowInventoryFragment(int type, HashMap<String, Object> totalCommoditys) {
        this.totalCommoditys = totalCommoditys;
        this.type = type;
        Iterator iter = totalCommoditys.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            ids.add(entry.getKey().toString());
        }

    }
    public ShowInventoryFragment(int type, Map<String, Object> preInventorys, Map<String, Object> behindInventorys) {
        this.type = type;
        this.preInventorys=preInventorys;
        this.behindInventorys=behindInventorys;
        Iterator iter = preInventorys.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            ids.add(entry.getKey().toString());
        }

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.fragment_show_inventory, container);
        findView();
        return view;
    }

    private void initData() {
        tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中")
                .create();
        listAdapter = new ListAdapter();
        gvInventory.setAdapter(listAdapter);
    }

    private void findView() {
        gvInventory = view.findViewById(R.id.gv_inventory);
        btnSureOrder = view.findViewById(R.id.btn_sure_order);
        title = view.findViewById(R.id.title);
        llDetail = view.findViewById(R.id.ll_detail);
        if (type==1){
            title.setText("晚间库存盘点");
        }else if(type==0){
            title.setText("早间库存盘点");
        }else{
            title.setText("每日库存消耗详情");
        }
        if (type!=2) {
            llDetail.setVisibility(View.GONE);
            gvInventory.setVisibility(View.VISIBLE);
            initData();
            btnSureOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                    if (ids.size() > 0) {
                        InventoryApi.addInventory(type, totalCommoditys).saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    hideDialog();
                                    if (getTargetFragment() != null) {
                                        Intent intent = new Intent();
                                        getTargetFragment().onActivityResult(1, 1, intent);
                                        if (type == 1) {
                                            ToastUtil.showLong(MyApplication.getContextObject(), "添加早间盘库记录成功");
                                        } else {
                                            ToastUtil.showLong(MyApplication.getContextObject(), "添加晚间盘库记录成功");
                                        }
                                        ToastUtil.showLong(MyApplication.getContextObject(), "添加入库记录成功");
                                        getDialog().dismiss();
                                    } else {
                                        getDialog().dismiss();
                                    }
                                } else {
                                    hideDialog();
                                    ToastUtil.showShort(MyApplication.getContextObject(), e.getMessage());
                                }
                            }
                        });

                    } else {
                        hideDialog();
                        ToastUtil.showShort(MyApplication.getContextObject(), "未选中商品");
                    }
                }
            });
        }else{
            llDetail.setVisibility(View.VISIBLE);
            gvInventory.setVisibility(View.GONE);
            HashMap<String, Double> finalOrders = ProductUtil.calOrderDifference(preInventorys, behindInventorys);
            setDynaimcView(finalOrders);

        }
    }

    private void setDynaimcView(HashMap<String, Double> finalOrders) {
        LayoutInflater inflater = LayoutInflater.from(MyApplication.getContextObject());
        View inflate = inflater.inflate(R.layout.view_inventory_difference, null);
        TextView itemName = (TextView) inflate.findViewById(R.id.item_name);
        TextView itemDetail = (TextView) inflate.findViewById(R.id.item_detail);
        TextView itemWeight = (TextView) inflate.findViewById(R.id.item_weight);
        for (Map.Entry<String, Double> entry : finalOrders.entrySet()){
            itemName.setText(entry.getKey());
            if (entry.getValue()>0){
                itemDetail.setText("新增");
            }else{
                itemDetail.setText("消耗");
            }
            itemWeight.setText(entry.getValue()+"");
        }

        llDetail.addView(inflate);
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

    public void showDialog() {
        tipDialog.show();
    }

    public void hideDialog() {
        if (tipDialog != null) {
            tipDialog.dismiss();
        }

    }

    public class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return ids.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_show_inventory, parent, false);
                holder = new ViewHolder();
                holder.meat_name = view.findViewById(R.id.meat_name);
                holder.meat_number = view.findViewById(R.id.meat_number);
                holder.meat_weight = view.findViewById(R.id.meat_weight);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            HashMap<String, Object> o = (HashMap<String, Object>) totalCommoditys.get(ids.get(i));
            holder.meat_name.setText(ObjectUtil.getString(o, "name"));
            holder.meat_number.setText(ObjectUtil.getInt(o, "number") + "");
            holder.meat_weight.setText(ObjectUtil.getDouble(o, "weight") + "kg");
            return view;
        }

        private class ViewHolder {
            TextView meat_name;
            TextView meat_number;
            TextView meat_weight;
        }
    }
}
