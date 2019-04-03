package cn.kuwo.player.fragment.credit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.bigkoo.pickerview.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.kuwo.player.R;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.DateUtil;
import cn.kuwo.player.util.LoadingUtil;
import cn.kuwo.player.util.T;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by lovely on 2018/8/21
 */
public class ExchangeReocrdFragment extends SupportFragment {

    Date currentDate;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.show_date)
    TextView showDate;
    @BindView(R.id.btn_change_date)
    Button btnChangeDate;
    @BindView(R.id.gv_sign)
    GridView gvSign;
    private List<AVObject> nbRechargeLogs = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    private void initView(View view) {
        title.setText("积分扣除详情");
        currentDate = DateUtil.getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        showDate.setText(sdf.format(currentDate) + "");
        getOrders();
    }

    private void getOrders() {
        LoadingUtil.show(getContext(), "加载中");
        AVQuery<AVObject> query = new AVQuery<>("CreditsLog");
        query.whereEqualTo("active", 1);
        query.whereLessThan("change", 0);
        query.whereEqualTo("store", 2);
        query.include("type");
        query.include("user");
        query.addDescendingOrder("createdAt");
        query.whereNotEqualTo("user",AVObject.createWithoutData("_User", "5655460f00b0bf379ee81d75"));
        query.whereGreaterThan("createdAt", new Date(DateUtil.getZeroTimeStamp(currentDate)));
        query.whereLessThan("createdAt", new Date(DateUtil.getLasterTimeStamp(currentDate)));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                LoadingUtil.hide();
                if (e == null) {
                    nbRechargeLogs = list;
                    ExchangeAdapter exchangeAdapter = new ExchangeAdapter();
                    gvSign.setAdapter(exchangeAdapter);
                } else {
                    T.L(e.getMessage());
                }
            }
        });
    }

    private void setDatePickerView() {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(2018, 4, 21);
        Calendar endDate = Calendar.getInstance();
        TimePickerView pvTime = new TimePickerView.Builder(getContext(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                currentDate = date;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                showDate.setText(sdf.format(currentDate) + "");
                getOrders();

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
        return R.layout.fg_sign_log;
    }

    public static ExchangeReocrdFragment newInstance() {
        return new ExchangeReocrdFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.tv_back, R.id.btn_change_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                pop();
                break;
            case R.id.btn_change_date:
                setDatePickerView();
                break;
        }
    }


    public class ExchangeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return nbRechargeLogs.size();
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
                view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_exchange_list, parent, false);
                holder = new ViewHolder();
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.order_date = view.findViewById(R.id.order_date);
            holder.order_number = view.findViewById(R.id.order_number);
            holder.order_user = view.findViewById(R.id.order_user);
            holder.order_type = view.findViewById(R.id.order_type);
            AVObject avObject = nbRechargeLogs.get(i);
            holder.order_date.setText("兑换时间:" + DateUtil.formatLongDate(avObject.getCreatedAt()));
            holder.order_number.setText("兑换数量:" + avObject.getDouble("change") + "积分");
            holder.order_user.setText("用户:" + avObject.getAVObject("user").getString("username"));
            holder.order_type.setText("兑换类型:" + avObject.getAVObject("type").getString("name"));
            return view;
        }

        private class ViewHolder {
            private TextView order_date;
            private TextView order_number;
            private TextView order_user;
            private TextView order_type;
        }
    }
}

