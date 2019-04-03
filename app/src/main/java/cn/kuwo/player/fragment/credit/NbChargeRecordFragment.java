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
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.kuwo.player.R;
import cn.kuwo.player.service.entity.NbRechargeLog;
import cn.kuwo.player.service.presenter.NbRechargeLogPresenter;
import cn.kuwo.player.service.view.NbRechargeLogView;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.DateUtil;
import cn.kuwo.player.util.LoadingUtil;
import cn.kuwo.player.util.T;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by lovely on 2018/8/22
 */
public class NbChargeRecordFragment extends SupportFragment {
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
    private List<NbRechargeLog> nbRechargeLogs = new ArrayList<>();
    private NbRechargeLogPresenter nbRechargeLogPresenter = new NbRechargeLogPresenter(getContext());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    private void initView(View view) {
        title.setText("牛币补偿详情");
        currentDate = DateUtil.getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        showDate.setText(sdf.format(currentDate) + "");
        nbRechargeLogPresenter.onCreate();
        nbRechargeLogPresenter.attachView(mNbRechargeLog);
        getOrders();
    }

    private void getOrders() {
       LoadingUtil.show(getContext(),"加载中");
        nbRechargeLogPresenter.getNbRechagreLog(DateUtil.getZeroTimeStampBySecond(currentDate),
                DateUtil.getLasterTimeStampBySecond(currentDate),
                2,
                1,
                CONST.isShowTEST);

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

    public static NbChargeRecordFragment newInstance() {
        return new NbChargeRecordFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        nbRechargeLogPresenter.onStop();
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
           ExchangeAdapter.ViewHolder holder;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_exchange_list, parent, false);
                holder = new ExchangeAdapter.ViewHolder();
                view.setTag(holder);
            } else {
                holder = (ExchangeAdapter.ViewHolder) view.getTag();
            }
            holder.order_date = view.findViewById(R.id.order_date);
            holder.order_number = view.findViewById(R.id.order_number);
            holder.order_user = view.findViewById(R.id.order_user);
            holder.order_type = view.findViewById(R.id.order_type);
            NbRechargeLog nbRechargeLog = nbRechargeLogs.get(i);
            holder.order_date.setText("充值时间:" + DateUtil.formatDate(new Date(nbRechargeLog.getCreated_at() * 1000)));
            holder.order_number.setText("充值牛币金额:" + nbRechargeLog.getAmount());
            holder.order_user.setText("用户:" + nbRechargeLog.getTarget_user().getUsername());
            holder.order_type.setText("补偿原因:" + nbRechargeLog.getMessage().getGift_reason());
            return view;
        }

        private class ViewHolder {
            private TextView order_date;
            private TextView order_number;
            private TextView order_user;
            private TextView order_type;
        }
    }
    private NbRechargeLogView mNbRechargeLog = new NbRechargeLogView() {
        @Override
        public void onSuccess(List<NbRechargeLog> nbRechargeLog) {
            LoadingUtil.hide();
            Logger.d(nbRechargeLog);
            nbRechargeLogs=nbRechargeLog;
            ExchangeAdapter exchangeAdapter = new ExchangeAdapter();
            gvSign.setAdapter(exchangeAdapter);

        }

        @Override
        public void onError(String result) {
            LoadingUtil.hide();
            T.L(result);
        }
    };
}
