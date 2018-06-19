package cn.kuwo.player.fragment.inventory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.Gravity;
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
import com.bigkoo.pickerview.TimePickerView;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Unbinder;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.api.InventoryApi;
import cn.kuwo.player.custom.ShowInventoryFragment;
import cn.kuwo.player.util.DateUtil;
import cn.kuwo.player.util.ObjectUtil;
import cn.kuwo.player.util.ToastUtil;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by lovely on 2018/6/18
 */
public class InventoryRecordFragment extends SupportFragment {
    Date currentDate;
    TextView showDate;
    Button btnChangeDate;
    Button btnPrint;
    GridView gvOrder;
    Unbinder unbinder;
    QMUITipDialog tipDialog;
    private OrderAdapter orderAdapter;
    List<AVObject> orders = new ArrayList<>();

    public static InventoryRecordFragment newInstance() {
        return new InventoryRecordFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        findView(view);
        initData();
    }

    private void findView(View view) {
        showDate = view.findViewById(R.id.show_date);
        btnChangeDate = view.findViewById(R.id.btn_change_date);
        btnPrint = view.findViewById(R.id.btn_print);
        gvOrder = view.findViewById(R.id.gv_inventory_order);
        tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中")
                .create();
        btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatePickerView();
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> preInventorys = null;
                Map<String, Object> behindInventorys = null;
                for (int i = 0; i < orders.size(); i++) {
                    if (orders.get(i).getInt("type") == 0) {
                        preInventorys =orders.get(i).getMap("commodityDetail");
                        break;
                    }
                }
                for (int i = 0; i < orders.size(); i++) {
                    if (orders.get(i).getInt("type") == 1) {
                        behindInventorys =  orders.get(i).getMap("commodityDetail");
                        break;
                    }
                }
                ShowInventoryFragment showInventoryFragment = new ShowInventoryFragment(2, preInventorys, behindInventorys);
                showInventoryFragment.show(getFragmentManager(),"showInventory");
            }
        });
    }

    private void initData() {
        currentDate = DateUtil.getCurrentDate();
        currentDate.setHours(0);
        currentDate.setMinutes(0);
        currentDate.setSeconds(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        showDate.setText(sdf.format(currentDate) + "");
        try {
            getOrders(currentDate);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
    }

    private void getOrders(final Date date) throws ParseException {
        showDialog();
        InventoryApi.finalCurrentOrder(date).findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                hideDialog();
                if (e == null) {
                    orders = list;
                    findOrders();
                } else {
                    ToastUtil.showLong(MyApplication.getContextObject(), e.getMessage());
                }
            }
        });

    }

    private void findOrders() {
        orderAdapter = new OrderAdapter();
        gvOrder.setAdapter(orderAdapter);
    }

    private void setDatePickerView() {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2018, 5, 21);
        Calendar endDate = Calendar.getInstance();
        TimePickerView pvTime = new TimePickerView.Builder(getContext(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                currentDate = date;
                currentDate.setHours(0);
                currentDate.setMinutes(0);
                currentDate.setSeconds(0);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                showDate.setText(sdf.format(currentDate) + "");
                try {
                    getOrders(currentDate);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .gravity(Gravity.CENTER)
                .setTitleText("选择查询日期")
                .setDate(selectedDate)// 如果不设置的话，默认是系统时间*/
                .setRangDate(startDate, endDate)//起始终止年月日设定
                .build();
        pvTime.setDate(Calendar.getInstance());
        pvTime.show();
    }

    public int getLayoutId() {
        return R.layout.fg_inventory_order;
    }

    public void showDialog() {
        tipDialog.show();
    }

    public void hideDialog() {
        if (tipDialog != null) {
            tipDialog.dismiss();
        }

    }

    public class OrderAdapter extends BaseAdapter {
        private int selectIndex = -1;

        @Override
        public int getCount() {
            return orders.size();
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
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_inventory_order, parent, false);
                holder = new ViewHolder();
                holder.order_detail = convertView.findViewById(R.id.order_detail);
                holder.order_date = convertView.findViewById(R.id.order_date);
                holder.order_state = convertView.findViewById(R.id.order_state);
                holder.order_state_img = convertView.findViewById(R.id.order_state_img);
                holder.card_order = convertView.findViewById(R.id.card_order);
                holder.show_detail = convertView.findViewById(R.id.show_detail);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            AVObject avObject = orders.get(position);
            holder.order_date.setText("记录时间:" + DateUtil.formatDate(avObject.getDate("createdAt")));
            if (avObject.getInt("type") == 0) {
                holder.order_state.setText("早间入库记录");
            } else {
                holder.order_state.setText("晚间出库记录");
            }
            String detail = "";
            Map<String, Object> commodityDetail = avObject.getMap("commodityDetail");
            Map<String, String> map = new HashMap<String, String>();
            for (Map.Entry<String, Object> entry : commodityDetail.entrySet()) {
                try {
                    JSONObject value = new JSONObject(entry.getValue().toString());
                    detail += value.getString("name") + "----" + "*" + value.getInt("number") + "----" + value.getDouble("weight") + "kg";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            holder.order_detail.setText(detail);
            holder.show_detail.setVisibility(View.VISIBLE);
            return convertView;
        }
    }

    private class ViewHolder {
        TextView order_detail;
        TextView order_date;
        TextView order_state;
        TextView order_state_img;
        CardView card_order;
        LinearLayout show_detail;
    }
}
