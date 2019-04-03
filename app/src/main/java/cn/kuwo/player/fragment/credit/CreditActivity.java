package cn.kuwo.player.fragment.credit;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.kuwo.player.R;
import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by lovely on 2018/8/21
 */
public class CreditActivity extends SupportActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);
        loadRootFragment(R.id.fl_credit_container, CreditMenuFragment.newInstance());
    }
}
