package cn.kuwo.player.custom;

import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.bean.RuleBean;
import cn.kuwo.player.util.MyUtils;
import cn.kuwo.player.util.ProductUtil;
import cn.kuwo.player.util.RealmHelper;
import io.realm.RealmList;

public class ShowActivityFragment extends DialogFragment {
    private LinearLayout llActivityList;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.fragment_show_acticity_list, container);
        findView();
        initView();
        return view;
    }

    private void findView() {
        llActivityList = view.findViewById(R.id.ll_acticity_list);
    }

    private void initView() {
        RealmHelper mRealmHleper = new RealmHelper(MyApplication.getContextObject());
        List<RuleBean> ruleBeans = mRealmHleper.queryAllRule();
        if (ruleBeans.size() > 0) {
            RuleBean ruleBean = ruleBeans.get(0);
            String ruleInfo = "";
            if (ruleBean.getAllDiscount() != 1) {
                TextView tv1 = new TextView(MyApplication.getContextObject());
                tv1.setGravity(Gravity.CENTER);
                tv1.setTextSize(18f);
                tv1.setTextColor(getResources().getColor(R.color.black));
                tv1.setText("全场折扣" + MyUtils.formatDouble(ruleBean.getAllDiscount() * 10) + "折优惠");
                llActivityList.addView(tv1);
            }
            if (ruleBean.getFullReduce() != null) {
                RealmList<String> fullReduce = ruleBean.getFullReduce();
                TextView tv1 = new TextView(MyApplication.getContextObject());
                tv1.setGravity(Gravity.CENTER);
                tv1.setTextSize(18f);
                tv1.setTextColor(getResources().getColor(R.color.black));
                String fullReduceContent = "满减活动 ";
                for (int i = 0; i < fullReduce.size(); i++) {
                    fullReduceContent += "满"+fullReduce.get(i).replace("-","减") + ",";
                }
                tv1.setText(fullReduceContent);
                llActivityList.addView(tv1);
            }
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
}
