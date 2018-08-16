package cn.kuwo.player.custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import cn.kuwo.player.R;
import cn.kuwo.player.service.entity.ConsumpteLog;
import cn.kuwo.player.util.ObjectUtil;

/**
 * Created by lovely on 2018/7/26
 */
public class ShowConsumpteFrgament extends DialogFragment {
    private ConsumpteLog mConsumpteLog;
    private View view;
    private TextView totalCount,topThreeCommodity,lastOrder;
    @SuppressLint("ValidFragment")
    public ShowConsumpteFrgament(ConsumpteLog consumpteLogList) {
        this.mConsumpteLog=consumpteLogList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view=inflater.inflate(R.layout.fragment_show_consumpte,container);
        findView();
        initData();
        return view;
    }

    private void findView() {
        totalCount=view.findViewById(R.id.total_count);
        topThreeCommodity=view.findViewById(R.id.top_three_commodites);
        lastOrder=view.findViewById(R.id.last_order);
    }

    private void initData() {
        setBackground();
        totalCount.setText("在此店的总消费次数:"+"\n"+mConsumpteLog.getConsumption_count());
        String topthreeContent="";
        if (mConsumpteLog.getTop_three_store_commodities()!=null) {
            for (String content : mConsumpteLog.getTop_three_store_commodities()) {
                topthreeContent += content + "\n";
            }
            topThreeCommodity.setText("在此店的消费前三的商品:"+"\n"+topthreeContent);
        }else{
            topThreeCommodity.setText("在此店的消费前三的商品:"+"\n"+"无");
        }

        String commodityContent="";
        if (mConsumpteLog.getLast_store_order()!=null&&mConsumpteLog.getLast_store_order().getCommodityDetail()!=null){
            for (int i = 0; i < mConsumpteLog.getLast_store_order().getCommodityDetail().size(); i++) {
                ConsumpteLog.LastStoreOrderBean.CommodityDetailBean commodityDetailBean = mConsumpteLog.getLast_store_order().getCommodityDetail().get(i);
                commodityContent+=commodityDetailBean.getName()+"*"+commodityDetailBean.getNumber()+"\n";
            }
            lastOrder.setText("上次消费详情\n"+commodityContent);
        }else{
            lastOrder.setText("上次消费详情\n"+"无");
        }
    }

    private void setBackground() {
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
