package cn.edu.sjtu.travelguide.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mDataList;

    public BaseFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    public BaseFragmentPagerAdapter(FragmentManager fm, List<Fragment> dataList) {
        super(fm);
        mDataList = dataList;
    }

    @Override
    public Fragment getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

}
