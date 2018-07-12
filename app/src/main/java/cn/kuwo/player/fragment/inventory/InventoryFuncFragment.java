package cn.kuwo.player.fragment.inventory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
                InventoryFragment inventoryFragment = InventoryFragment.newInstance(0);
                start(inventoryFragment);
            }
        });
        view.findViewById(R.id.func_night).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InventoryFragment inventoryFragment = InventoryFragment.newInstance(1);
                start(inventoryFragment);
            }
        });
        view.findViewById(R.id.func_total).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start(InventoryRecordFragment.newInstance());
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
