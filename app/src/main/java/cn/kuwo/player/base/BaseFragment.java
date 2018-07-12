package cn.kuwo.player.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

import butterknife.ButterKnife;
import cn.kuwo.player.MyApplication;
import cn.kuwo.player.util.AppUtils;

public abstract class BaseFragment extends Fragment {
    protected BaseActivity mActivity;
    QMUITipDialog tipDialog;

    protected abstract int getLayoutId();

    protected BaseActivity getHoldingActivity() {
        return mActivity;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (BaseActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        ButterKnife.bind(getActivity());
        tipDialog = new QMUITipDialog.Builder(getContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord("加载中")
                .create(false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public abstract void initData();

    public void showDialog() {
        tipDialog.show();
    }

    public void hideDialog() {
        if (tipDialog != null) {
            tipDialog.dismiss();
        }
    }
    public Float getDensity() {
        try {
            return AppUtils.getScreenDensity(getContext());
        }catch (Exception e){
            return 2f;
        }

    }
}
