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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.bean.ProductBean;
import cn.kuwo.player.event.CouponEvent;
import cn.kuwo.player.fragment.OrderFg;
import cn.kuwo.player.print.Bill;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ToastUtil;

public class ShowCouponFragment extends DialogFragment {
    private GridView gvTable;
    private View view;
    private ListAdapter listAdapter;
    private List<AVObject> couponAVObject;
    private Double originTotalMoneny;
    private TextView title;
    private int type;
    QMUITipDialog tipDialog;
    public ShowCouponFragment(List<AVObject> orders, Double originTotalMoneny,int type) {
        Logger.d(orders.size());
        this.couponAVObject = orders;
        this.originTotalMoneny = originTotalMoneny;
        this.type=type;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.fragment_showcoupon, container);
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
        gvTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AVObject avObject = couponAVObject.get(position);
                double aDouble = avObject.getAVObject("type").getDouble("section");
                if (originTotalMoneny>=aDouble){
                    getDialog().dismiss();
                    Logger.d(avObject.getAVObject("type"));
                    EventBus.getDefault().post(new CouponEvent(type,avObject.getObjectId(),avObject.getDouble("gold"),avObject.getAVObject("type").getString("name")));
                }else{
                    ToastUtil.showShort(MyApplication.getContextObject(),"未达到指定金额");
                }
            }
        });
    }

    private void findView() {
        gvTable = view.findViewById(R.id.gv_table);
        title = view.findViewById(R.id.title);
        title.setText("我的线上优惠券列表"+originTotalMoneny+"元");
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
            return couponAVObject.size();
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
                view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_show_coupon, parent, false);
                holder = new ViewHolder();
                holder.show_list_name = (TextView) view.findViewById(R.id.show_list_name);
                holder.show_list_content = (TextView) view.findViewById(R.id.show_list_content);
                holder.show_list_number = (TextView) view.findViewById(R.id.show_list_number);
                holder.show_list_give = (TextView) view.findViewById(R.id.show_list_give);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            AVObject avObject = couponAVObject.get(i);
            holder.show_list_name.setText(avObject.getString("from"));
            holder.show_list_number.setText(avObject.getDouble("gold") + "");
            holder.show_list_content.setText("满"+avObject.getAVObject("type").getDouble("section")+"元可用");

            return view;
        }

        private class ViewHolder {
            TextView show_list_name,show_list_content,show_list_number,show_list_give;
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
