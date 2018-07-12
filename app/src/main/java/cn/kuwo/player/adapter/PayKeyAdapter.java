package cn.kuwo.player.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.logging.Logger;

import cn.kuwo.player.R;

public class PayKeyAdapter extends BaseAdapter {

    private Context context;
    private int height;

    public PayKeyAdapter(Context context,int height) {
        this.context=context;
        this.height=height;
    }

    private String[] texts = {
            //九宫格图片下方文字的设置
            "1",
            "2",
            "3",
            //  "清空",
            "4",
            "5",
            "6",
            //  "←",
            "7",
            "8",
            "9",
            //  "=",
            "扫描",
            "0",
            "✖"
            // "+",
            //  "收款",
    };

    private String[] types = {
            //存放在text的tag中
            "num",
            "num",
            "num",
            //   "cancel",
            "num",
            "num",
            "num",
            //   "back",
            "num",
            "num",
            "num",
            //  "equal",
            "point",
            "num",
            "back",
            //  "plus",
            //  "pay",
    };

    @Override
    public int getCount() {
        return texts.length;
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
    public View getView(int position, View view, ViewGroup viewgroup) {
        ImgTextWrapper wrapper;
        if(view==null) {
            wrapper = new ImgTextWrapper();
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.grid_menu_item_pay, null);
            view.setTag(wrapper);
            view.setPadding(5, 5, 5, 5);  //每格的间距
        } else {
            wrapper = (ImgTextWrapper)view.getTag();
        }


        wrapper.textView = (TextView)view.findViewById(R.id.GridTextView);
        wrapper.iv_back= (ImageView) view.findViewById(R.id.iv_back);
        wrapper.GridItemLayout= (FrameLayout) view.findViewById(R.id.GridItemLayout);
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) wrapper.GridItemLayout.getLayoutParams();
//        layoutParams.height=(height-40)/4;
//            view.setLayoutParams(layoutParams);

        if (position<11){
            wrapper.textView.setVisibility(View.VISIBLE);
            wrapper.iv_back.setVisibility(View.GONE);
            wrapper.textView.setText(texts[position]);
        }else {
            wrapper.textView.setVisibility(View.GONE);
            wrapper.iv_back.setVisibility(View.VISIBLE);
        }

        wrapper.textView.setTag(types[position]);
        if(types[position].equals("back")
                || types[position].equals("cancel")
                || types[position].equals("equal")){
            // wrapper.textView.setBackgroundResource(R.drawable.btn_cell_delete);
        }else if(types[position].equals("pay")){
            //  wrapper.textView.setBackgroundResource(R.drawable.btn_cell_pay);
            //  wrapper.textView.setTextColor(Color.WHITE);
        }

//	  wrapper.textView = (TextView)view.findViewById(R.id.GridItemText);
//	  wrapper.textView.setText(texts[position]);
//	  wrapper.textView.setTag(payTypes[position]);



        return view;
    }

    class ImgTextWrapper {
        ImageView iv_back;
        TextView textView;
        FrameLayout GridItemLayout;
    }
}
