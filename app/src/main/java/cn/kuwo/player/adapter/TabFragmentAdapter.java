package cn.kuwo.player.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import cn.kuwo.player.custom.TabFragment;

/**
 * Created by lovely on 2018/8/17
 */
public class TabFragmentAdapter extends FragmentStatePagerAdapter {
    List<String> titileList;
    List<TabFragment> fmList;
    public TabFragmentAdapter(FragmentManager fm, List<TabFragment> fmList, List<String> titileList) {
        super(fm);
        this.fmList=fmList;
        this.titileList=titileList;
    }

    @Override
    public Fragment getItem(int position) {
        return fmList.get(position);
    }

    @Override
    public int getCount() {
        if(fmList==null){
            return 0;
        }else{
            return fmList.size();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titileList.get(position);
    }
}
