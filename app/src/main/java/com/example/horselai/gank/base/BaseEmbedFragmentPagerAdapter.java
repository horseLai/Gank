package com.example.horselai.gank.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by laixiaolong on 2017/3/30.
 * <p>
 * 用于解决 Fragment中内嵌ViewPager，而ViewPager中又有Fragment时ViewPager中界面不更新的情形
 * <br/>
 * 注意：这个效率不高，所以尽量使用在数量少的ViewPager中
 */

public abstract class BaseEmbedFragmentPagerAdapter extends PagerAdapter
{
    private static final String TAG = "ReadingFragmentPagerAda";
    private FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction;
    private Fragment mCurrentPrimaryItem;

    public BaseEmbedFragmentPagerAdapter(FragmentManager fm)
    {
        this.mFragmentManager = fm;
    }

    @Override public abstract int getCount();

    @Override public boolean isViewFromObject(View view, Object object)
    {
        return ((Fragment) object).getView() == view;
    }

    @Override public abstract int getItemPosition(Object object);


    @Override public void setPrimaryItem(ViewGroup container, int position, Object object)
    {
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }


    @Override public Object instantiateItem(ViewGroup container, int position)
    {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        final Fragment fragment = getItem(position);
        mCurTransaction.add(container.getId(), fragment);

        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }


    public abstract Fragment getItem(int position);


    @Override public void destroyItem(ViewGroup container, int position, Object object)
    {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.detach((Fragment) object);
    }

    @Override public void finishUpdate(ViewGroup container)
    {
        if (mCurTransaction != null) {
            mCurTransaction.commitNowAllowingStateLoss();
            mCurTransaction = null;
        }
    }

    @Override public abstract CharSequence getPageTitle(int position);


    @Override public float getPageWidth(int position)
    {
        return super.getPageWidth(position);
    }
}
