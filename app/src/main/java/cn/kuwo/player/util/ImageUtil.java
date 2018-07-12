package cn.kuwo.player.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import cn.kuwo.player.R;

/**
 * Created by lovely on 2018/6/23
 */
public class ImageUtil {
    public static void setDrawableLeft(Context context, int drawableId, TextView tv) {
        Drawable left;
        left = context.getResources().getDrawable(drawableId);
        left.setBounds(0, 0, left.getMinimumWidth(), left.getMinimumHeight());
        tv.setCompoundDrawables(left, null, null, null);
    }
}
