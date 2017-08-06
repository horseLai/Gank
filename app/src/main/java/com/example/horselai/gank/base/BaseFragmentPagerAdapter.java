package com.example.horselai.gank.base;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by laixiaolong on 2016/11/12.
 * 适用于fragment 页数较少情况，fragment会被保存在内存中
 */

public class BaseFragmentPagerAdapter extends FragmentPagerAdapter
{
    private static final String TAG = "BaseFragmentPagerAdapte";
    private ArrayList<Fragment> fragments;
    private ArrayList<String> titles;

    public BaseFragmentPagerAdapter(FragmentManager fm)
    {
        super(fm);
        fragments = new ArrayList<>();
        titles = new ArrayList<>();
    }


    @Override public Fragment getItem(int position)
    {
        if (getCount() > 0) return fragments.get(position);
        return null;
    }

    public void addFragment(@NonNull Fragment fragment, @NonNull String title)
    {
        fragments.add(fragment);
        titles.add(title);
        notifyDataSetChanged();
    }


    @Override public int getCount()
    {
        return fragments.size();
    }

    @Override public CharSequence getPageTitle(int position)
    {
        if (getCount() > 0) return titles.get(position);
        return super.getPageTitle(position);
    }

    @Override public int getItemPosition(Object object)
    {
        return fragments.indexOf(object);
    }

    @Override public long getItemId(int position)
    {
        return super.getItemId(position);
    }

    public void clearAllFragment()
    {
        fragments.clear();
        titles.clear();
    }

}
