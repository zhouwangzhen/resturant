package cn.kuwo.player.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by lovely on 2018/6/20
 */
public class CalViewGroupUtil {


    /**
     * 计算GridView宽高
     * @param gridView
     */
    public static void calGridViewWidthAndHeigh(int numColumns ,GridView gridView) {

        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);

            if ((i+1)%numColumns == 0) {
                totalHeight += listItem.getMeasuredHeight();
            }

            if ((i+1) == len && (i+1)%numColumns != 0) {
                totalHeight += listItem.getMeasuredHeight();
            }
        }

        totalHeight += 40;

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        gridView.setLayoutParams(params);
    }
}
