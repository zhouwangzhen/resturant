package cn.kuwo.player.custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cn.kuwo.player.R;
import cn.kuwo.player.bean.RateBean;

public class ShowWholeSaleFragment extends DialogFragment {
    private View view;
    private NumTipSeekBar rateSeekbar;
    private TextView rateNumber;
    private Button btnEnsure;
    private int rate;

    @SuppressLint("ValidFragment")
    public ShowWholeSaleFragment(int rate){
        this.rate=rate;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view=inflater.inflate(R.layout.fragment_whole_sale,container);
        findView();
        return view;
    }

    private void findView() {
        rateSeekbar=view.findViewById(R.id.rate_seekbar);
        rateNumber=view.findViewById(R.id.rate_number);
        btnEnsure=view.findViewById(R.id.btn_ensure);
        rateSeekbar.setOnProgressChangeListener(new NumTipSeekBar.OnProgressChangeListener() {
            @Override
            public void onChange(int selectProgress) {
                rateNumber.setText("整单"+selectProgress+"%折");
            }
        });
        rateSeekbar.setSelectProgress(rate);
        btnEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new RateBean(rateSeekbar.getSelectProgress()));
                getDialog().dismiss();
            }
        });
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
