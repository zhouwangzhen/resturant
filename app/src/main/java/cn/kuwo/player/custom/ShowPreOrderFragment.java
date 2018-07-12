package cn.kuwo.player.custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.api.TableApi;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.fragment.OrderFg;
import cn.kuwo.player.fragment.TableFg;
import cn.kuwo.player.print.Bill;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.ToastUtil;

@SuppressLint("ValidFragment")
public class ShowPreOrderFragment extends DialogFragment {
    private GridView gvTable;
    private Button btnSureOrder;
    private List<Object> preOrders;
    private View view;
    private ListAdapter listAdapter;
    private AVObject tableAVObject;
    QMUITipDialog tipDialog;

    @SuppressLint("ValidFragment")
    public ShowPreOrderFragment(AVObject tableAVObject, List<Object> preOrders) {
        Collections.sort(preOrders, new Comparator<Object>() {


            @Override
            public int compare(Object o1, Object o2) {
                HashMap<String, Object> format = ObjectUtil.format(o1);
                HashMap<String, Object> format1 = ObjectUtil.format(o2);
                return ObjectUtil.getInt(format, "cookSerial") - ObjectUtil.getInt(format1, "cookSerial");
            }
        });
        this.preOrders = preOrders;
        this.tableAVObject = tableAVObject;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.fragment_showlist, container);
        findView();
        initData();
        return view;
    }

    private void initData() {
        tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中")
                .create();
        listAdapter = new ListAdapter();
        gvTable.setAdapter(listAdapter);
    }

    private void findView() {
        gvTable = view.findViewById(R.id.gv_table);
        btnSureOrder = view.findViewById(R.id.btn_sure_order);
        setBackground();
        btnSureOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tableAVObject!=null) {
                    showDialog();
                    TableApi.addOrder(tableAVObject,preOrders).saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                hideDialog();
                                getDialog().dismiss();
                                Bill.printCateringFore(preOrders, tableAVObject, 0);
                                ToastUtil.showShort(MyApplication.getContextObject(), "下单成功");
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fragment_content, OrderFg.newInstance(tableAVObject.getObjectId(), true), "order").commit();
                                ProductUtil.saveOperateLog(0, preOrders, tableAVObject);
                            } else {
                                hideDialog();
                                ToastUtil.showShort(MyApplication.getContextObject(), e.getMessage());
                            }
                        }
                    });
                }
            }
        });

    }

    private void setBackground() {
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.dimAmount = 0.8f;
        getDialog().getWindow().setAttributes(lp);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public class ListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return preOrders.size();
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
                view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_show_list, parent, false);
                holder = new ViewHolder();
                holder.show_list_name = view.findViewById(R.id.show_list_name);
                holder.show_list_content = view.findViewById(R.id.show_list_content);
                holder.show_list_number = view.findViewById(R.id.show_list_number);
                holder.show_list_give = view.findViewById(R.id.show_list_give);
                holder.show_combo_content = view.findViewById(R.id.show_combo_content);
                holder.commodity_type = view.findViewById(R.id.commodity_type);
                holder.show_cookstyle = view.findViewById(R.id.show_cookstyle);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            HashMap<String, Object> format = ObjectUtil.format(preOrders.get(i));
            String nameContent="";
            nameContent+=MyUtils.getProductById(ObjectUtil.getString(format, "id")).getName();
            if (ObjectUtil.getString(format,"barcode").length()==18){
                nameContent+="("+(ProductUtil.calCommodityWeight(ObjectUtil.getString(format,"barcode"))>20?ProductUtil.calCommodityWeight(ObjectUtil.getString(format,"barcode"))+"ml":ProductUtil.calCommodityWeight(ObjectUtil.getString(format,"barcode"))+"kg")+")";
            }
            holder.show_list_name.setText(nameContent);
            if (!ObjectUtil.getString(format,"cookStyle").equals("")){
                holder.show_cookstyle.setText("做法:"+ObjectUtil.getString(format,"cookStyle"));
            }

            if (ObjectUtil.getString(format, "comment").length() > 0) {
                holder.show_list_content.setVisibility(View.VISIBLE);
                holder.show_list_content.setText("(备注:" + ObjectUtil.getString(format, "comment") + ")");
            }
            holder.show_list_number.setText(ObjectUtil.getDouble(format, "number") + "份");
            if (MyUtils.getProductById(ObjectUtil.getString(format, "id")).getGivecode().length() > 0) {
                ProductBean giveProductBean = MyUtils.getProductById(MyUtils.getProductById(ObjectUtil.getString(format, "id")).getGivecode());
                holder.show_list_give.setVisibility(View.VISIBLE);
                holder.show_list_give.setText("赠送菜品:" + giveProductBean.getName());
            }
            String contnet = "";
            if (format.containsKey("comboList") && ObjectUtil.getList(format, "comboList").size() > 0) {
                List<String> comboList = ObjectUtil.getList(format, "comboList");
                for (int j = 0; j < comboList.size(); j++) {
                    contnet += comboList.get(j) + "\n";
                }
                holder.show_combo_content.setText(contnet);
            }
            return view;
        }

        private class ViewHolder {
            TextView show_list_name;
            TextView show_list_content;
            TextView show_list_number;
            TextView show_list_give;
            TextView show_combo_content;
            TextView commodity_type;
            TextView show_cookstyle;
        }
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
}
