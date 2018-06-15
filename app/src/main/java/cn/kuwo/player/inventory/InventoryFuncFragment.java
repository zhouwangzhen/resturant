package cn.kuwo.player.inventory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.SupportActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import cn.kuwo.player.R;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * Created by lovely on 2018/6/14
 */
public class InventoryFuncFragment extends SupportFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        view.findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPreFragment()==null){
                    getActivity().finish();
                }else{
                    pop();
                }
            }
        });
        view.findViewById(R.id.func_morining).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(MorningFragment.newInstance());
            }
        });

    }

    public int getLayoutId() {
        return R.layout.fg_inventory_func;
    }

    public static InventoryFuncFragment newInstance() {
        return new InventoryFuncFragment();
    }
}
