package cn.kuwo.player.fragment.inventory;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.kuwo.player.R;
import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by lovely on 2018/6/14
 */
public class InventoryActivity extends SupportActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        if (savedInstanceState == null) {
            loadRootFragment(R.id.fl_container, InventoryFuncFragment.newInstance());
        }
    }

}
