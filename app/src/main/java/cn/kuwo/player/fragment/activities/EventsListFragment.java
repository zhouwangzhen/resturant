package cn.kuwo.player.fragment.activities;

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
import cn.kuwo.player.util.LoadingUtil;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by lovely on 2018/7/23
 */
public class EventsListFragment extends SupportFragment {
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.func_sign_log)
    TextView funcSignLog;
    @BindView(R.id.func_card_convert)
    TextView funcCardConvert;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public static EventsListFragment newInstance() {
        EventsListFragment eventsListFragment = new EventsListFragment();
        return eventsListFragment;
    }

    public int getLayoutId() {
        return R.layout.fg_event_list;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.tv_back, R.id.func_sign_log,R.id.func_card_convert})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                getActivity().finish();
                break;
            case R.id.func_sign_log:
                start(SignLogFragment.newInstance());
                break;
            case R.id.func_card_convert:
                start(CardConvertFragment.newInstance());
                break;
        }
    }
}
