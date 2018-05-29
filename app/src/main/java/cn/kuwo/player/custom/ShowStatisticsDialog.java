package cn.kuwo.player.custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.print.Bill;

@SuppressLint("ValidFragment")
public class ShowStatisticsDialog extends DialogFragment {
    private HashMap<String, Object> ordersDetail;
    private View view;
    private LinearLayout llDetail,llCommodity;
    private Button btnSureOrder;
    private Date orderDate;

    public ShowStatisticsDialog(HashMap<String, Object> ordersDetail, Date orderDate) {
        this.ordersDetail = ordersDetail;
        this.orderDate=orderDate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.fragment_show_statistic, container);
        findView();
        initData();
        setListener();
        return view;
    }

    private void setListener() {
        btnSureOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bill.printTotalBill(MyApplication.getContextObject(),ordersDetail,orderDate);
            }
        });
    }

    @SuppressLint("InflateParams")
    private void initData() {
        setItem("线上收款金额",ordersDetail.get("onlineMoney").toString());
        setItem("线下收款金额",ordersDetail.get("offlineMoney").toString());
        setItem("会员数",ordersDetail.get("member").toString());
        setItem("非会员数",ordersDetail.get("noMember").toString());
        setItem("餐饮单数",ordersDetail.get("retailNumber").toString());
        setItem("零售单数",ordersDetail.get("restaurarntNumber").toString());
        setItem("抵扣牛肉重量",ordersDetail.get("reduceWeight").toString());
        List<Map.Entry<String,Double>>  numbersList = (  List<Map.Entry<String,Double>> ) ordersDetail.get("numbers");
        for(Map.Entry<String,Double> mapping:numbersList){
            setCommodityItem(mapping.getKey(),mapping.getValue()+"份");
        }

    }
    private void setItem(String content, String detail) {
        LayoutInflater inflater = LayoutInflater.from(MyApplication.getContextObject());
        View inflate = inflater.inflate(R.layout.view_statistics_item, null);
        TextView itemName = (TextView) inflate.findViewById(R.id.item_name);
        TextView itemDetail = (TextView) inflate.findViewById(R.id.item_detial);
        itemName.setText(content);
        itemDetail.setText(detail);
        llDetail.addView(inflate);

    }

    private void setCommodityItem(String content, String detail) {
        LayoutInflater inflater = LayoutInflater.from(MyApplication.getContextObject());
        View inflate = inflater.inflate(R.layout.view_statistics_item, null);
        TextView itemName = (TextView) inflate.findViewById(R.id.item_name);
        TextView itemDetail = (TextView) inflate.findViewById(R.id.item_detial);
        itemName.setText(content);
        itemDetail.setText(detail);
        llCommodity.addView(inflate);

    }
    private void findView() {
        llDetail = view.findViewById(R.id.ll_detail);
        llCommodity = view.findViewById(R.id.ll_commodity);
        btnSureOrder = view.findViewById(R.id.btn_sure_order);
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.dimAmount = 0.8f;
        getDialog().getWindow().setAttributes(lp);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.75), ViewGroup.LayoutParams.WRAP_CONTENT);
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
}
