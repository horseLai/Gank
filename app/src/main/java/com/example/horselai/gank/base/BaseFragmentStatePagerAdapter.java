package com.example.horselai.gank.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by laixiaolong on 2016/11/3.
 * 适用于fragment 页数较多情况， 当页面不可见时，页面被销毁，只保存状态
 */

public class BaseFragmentStatePagerAdapter extends FragmentStatePagerAdapter
{
    protected ArrayList<Fragment> mFragments;
    protected ArrayList<String> mTitles;

    public BaseFragmentStatePagerAdapter(FragmentManager fm)
    {
        super(fm);
        mFragments = new ArrayList<>();
        mTitles = new ArrayList<>();
    }

    /**
     * @param fragment
     * @param title
     */
    public void addFragment(Fragment fragment, String title)
    {
        mFragments.add(fragment);
        mTitles.add(title);
    }

    @Override public Fragment getItem(int position)
    {
        return mFragments.get(position);
    }

    @Override public int getCount()
    {
        return mFragments.size();
    }

    @Override public CharSequence getPageTitle(int position)
    {
        return mTitles.get(position);
    }


    public void clearAllFragment()
    {
        mFragments.clear();
        mTitles.clear();
    }
}
