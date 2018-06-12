package cn.kuwo.player.custom;

import android.annotation.SuppressLint;
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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.player.MyApplication;
import cn.kuwo.player.R;
import cn.kuwo.player.bean.FuncBean;
import cn.kuwo.player.util.ToastUtil;

@SuppressLint("ValidFragment")
public class ShowFuncFragment extends DialogFragment {
    private View view;
    private GridView gvFunc;
    private Button btnClose;
    private String[] funList = {"多桌结账", "挂账", "抹零", "打印预览订单", "整单打折"};
    private int mode;

    public ShowFuncFragment(int mode) {
        this.mode = mode;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        view = inflater.inflate(R.layout.fragment_show_func, container);
        findView();
        initData();
        setListener();
        return view;
    }

    private void setListener() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    private void initData() {
        FuncAdapter funcAdapter = new FuncAdapter();
        gvFunc.setAdapter(funcAdapter);
    }

    private void findView() {
        gvFunc = view.findViewById(R.id.gv_func);
        btnClose = view.findViewById(R.id.btn_close);
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
            getDialog().setCanceledOnTouchOutside(false);
        }
    }

    public class FuncAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return funList.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_func, parent, false);
                holder = new ViewHolder();
                holder.funcName = convertView.findViewById(R.id.func_name);
                holder.llRoot = convertView.findViewById(R.id.ll_root);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.funcName.setText(funList[position]);
            holder.llRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mode==1&&(position==0||position==1)){
                        if (position==0||position==1){
                            ToastUtil.showShort(MyApplication.getContextObject(),"尚未开放此功能");
                            return;
                        }
                    }
                    EventBus.getDefault().post(new FuncBean(position));
                    getDialog().dismiss();

                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView funcName;
            SquareLayout llRoot;
        }
    }

}
