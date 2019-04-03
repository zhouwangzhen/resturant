package cn.kuwo.player.fragment.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.logging.Logger;

import cn.kuwo.player.R;
import cn.kuwo.player.util.T;
import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by lovely on 2018/7/23
 */
public class EventsActivity extends SupportActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        loadRootFragment(R.id.fl_event_container, EventsListFragment.newInstance());
    }
}
