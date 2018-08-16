package cn.kuwo.player.fragment.activities;

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
import android.widget.TextView;

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
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.custom.ShowConsumpteFrgament;
import cn.kuwo.player.service.entity.ConsumpteLog;
import cn.kuwo.player.service.entity.SignLog;
import cn.kuwo.player.service.presenter.SignLogPresenter;
import cn.kuwo.player.service.presenter.StoreConsumptePresenter;
import cn.kuwo.player.service.view.ConsumpteLogView;
import cn.kuwo.player.service.view.SignLogView;
import cn.kuwo.player.util.CONST;
import cn.kuwo.player.util.DateUtil;
import cn.kuwo.player.util.LoadingUtil;
import cn.kuwo.player.util.T;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by lovely on 2018/7/23
 */
public class SignLogFragment extends SupportFragment {
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.show_date)
    TextView showDate;
    @BindView(R.id.btn_change_date)
    Button btnChangeDate;
    @BindView(R.id.gv_sign)
    GridView gvSign;
    Date currentDate;
    private List<SignLog> signLogs=new ArrayList<>();
    private ConsumpteLog mConsumpteLog;
    private StoreConsumptePresenter storeConsumptePresenter=new StoreConsumptePresenter();
    private SignLogPresenter signLogPresenter=new SignLogPresenter(getContext());
    private SignLogAdapter signLogAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    private void initView(View view) {
        title.setText("用户每日签到消费详情");
        signLogPresenter.onCreate();
        signLogPresenter.attachView(signLog);
        storeConsumptePresenter.onCreate();
        storeConsumptePresenter.attachView(consumpteLogView);
        currentDate = DateUtil.getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        showDate.setText(sdf.format(currentDate) + "");
        getOrders();
    }

    private void getOrders() {
        LoadingUtil.show(getContext(),"获取签到列表");
        signLogPresenter.getSignLog(DateUtil.getZeroTimeStampBySecond(currentDate),
                DateUtil.getLasterTimeStampBySecond(currentDate),
                2,
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

    public static SignLogFragment newInstance() {
        return new SignLogFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.tv_back,R.id.btn_change_date})
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
    private SignLogView signLog=new SignLogView() {
        @Override
        public void onSuccess(List<SignLog> nbRechargeLogs) {
            LoadingUtil.hide();
            signLogs=nbRechargeLogs;
            signLogAdapter = new SignLogAdapter();
            gvSign.setAdapter(signLogAdapter);
        }

        @Override
        public void onError(String result) {
            LoadingUtil.hide();
        }
    };
    private ConsumpteLogView consumpteLogView=new ConsumpteLogView() {
        @Override
        public void onSuccess(ConsumpteLog consumpteLogList) {
            LoadingUtil.hide();
            if(consumpteLogList==null){
                T.L("没有消费信息");
            }else{
                mConsumpteLog=consumpteLogList;
                ShowConsumpteFrgament showConsumpteFrgament = new ShowConsumpteFrgament(consumpteLogList);
                showConsumpteFrgament.show(getActivity().getFragmentManager(),"showCousumpte");
            }

        }

        @Override
        public void onError(String result) {
            LoadingUtil.hide();
        }
    };

    public class SignLogAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return signLogs.size();
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
            if (view==null){
                view=LayoutInflater.from(getContext()).inflate(R.layout.adapter_sign_list,parent,false);
                holder=new ViewHolder();
                holder.order_date=view.findViewById(R.id.order_date);
                holder.card_order=view.findViewById(R.id.card_order);
                holder.order_number=view.findViewById(R.id.order_number);
                view.setTag(holder);
            }else{
                holder= (ViewHolder) view.getTag();
            }
            final SignLog signLog = signLogs.get(position);
            holder.order_number.setText(signLog.getAmount()+"个币");
            holder.order_date.setText("签到时间:" + DateUtil.formatDate(new Date(signLog.getUpdated_at() * 1000))+"\n手机号:"+signLog.getTarget_user().getUsername()+
            (signLog.getTarget_user().getReal_name()!=null?"("+signLog.getTarget_user().getReal_name()+")":""));
            holder.card_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadingUtil.show(getContext(),"获取消费信息");
                    storeConsumptePresenter.getConsumeLog(1,signLog.getTarget_user_id());
                }
            });
            return view;
        }
        private class ViewHolder{
            private TextView order_date;
            private TextView order_number;
            private CardView card_order;

        }
    }
}
