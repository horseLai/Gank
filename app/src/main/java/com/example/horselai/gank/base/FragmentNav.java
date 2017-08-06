package com.example.horselai.gank.base;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.HashMap;

/**
 * Created by laixiaolong on 2017/4/23.
 */

public class FragmentNav
{
    private FragmentTransaction mTransaction;
    private FragmentManager mFragmentManager;
    private HashMap<String, Fragment> mFragmentMap;


    public FragmentNav(FragmentManager manager)
    {
        mFragmentManager = manager;
        mFragmentMap = new HashMap<>();
    }


    public <T extends BaseFragment> void show(@IdRes int containerViewId, Class<? extends BaseFragment> toShow, FragmentCallback callback)
    {
        if (mTransaction == null) {
            mTransaction = mFragmentManager.beginTransaction();
        }
        //表示已经添加过了，并且已经处理完成 (选一而排外)
        /*if (!mFragmentMap.isEmpty() && showTarget(toShow)) {
            mTransaction.commit();
            mTransaction = null;
            return;
        }*/

        //未添加，在这里添加
        T fragment = null;
        String tag = "";
        try {
            fragment = (T) toShow.newInstance();
            if (callback != null) fragment.setCallback(callback);
            tag = fragment.getClass().getSimpleName();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (fragment == null) return;

        mTransaction.add(containerViewId, fragment, tag);
        mFragmentMap.put(tag, fragment);
        mTransaction.commit();
        mTransaction = null;

    }


    private boolean showTarget(Class<? extends BaseFragment> toShow)
    {
        boolean isShown = false;
        for (Fragment f : mFragmentMap.values()) {
            if (toShow.equals(f.getClass())) {
                mTransaction.show(f);  //显示目标
                isShown = true;
            }
            mTransaction.hide(f); //隐藏其他
        }
        return isShown;
    }


}
