package cn.kuwo.player.fragment.credit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.kuwo.player.R;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by lovely on 2018/8/21
 */
public class CreditMenuFragment extends SupportFragment {

    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.func_exchange_log)
    TextView funcExchangeLog;
    @BindView(R.id.func_exchange_gift)
    TextView funcExchangeGift;
    @BindView(R.id.func_exchange_lottery)
    TextView funcExchangeLottery;
    @BindView(R.id.func_decuct_log)
    TextView funcDecuctLog;
    @BindView(R.id.func_nb_compense)
    TextView funcNbCompense;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    private void initView(View view) {
    }

    public static CreditMenuFragment newInstance() {
        CreditMenuFragment creditMenuFragment = new CreditMenuFragment();
        return creditMenuFragment;
    }

    public int getLayoutId() {
        return R.layout.fg_credit_menu;
    }


    @OnClick({R.id.tv_back, R.id.func_exchange_log, R.id.func_decuct_log, R.id.func_exchange_gift, R.id.func_exchange_lottery, R.id.func_nb_compense,R.id.func_nb_log})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                getActivity().finish();
                break;
            case R.id.func_exchange_log:
                start(ExchangeNbFragment.newInstance());
                break;
            case R.id.func_decuct_log:
                start(ExchangeReocrdFragment.newInstance());
                break;
            case R.id.func_exchange_gift:
                start(ExchangeGiftFragment.newInstance());
                break;
            case R.id.func_exchange_lottery:
                start(ExchangeLotteryFragment.newInstance());
                break;
            case R.id.func_nb_compense:
                start(CompenseFragment.newInstance());
                break;
            case R.id.func_nb_log:
                start(NbChargeRecordFragment.newInstance());
                break;
        }
    }
}

