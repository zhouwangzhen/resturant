package cn.kuwo.player.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import cn.kuwo.player.R;

/**
 * Created by lovely on 2018/6/19
 */
public class ShowMeatFragment extends DialogFragment {
    private Context context;
    private String code;
    private View view;
    public ShowMeatFragment(Context context,String code){
        this.context=context;
        this.code=code;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.fragment_show_commodity, container);
        return view;
    }

}
