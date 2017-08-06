package com.example.horselai.gank.app;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by laixiaolong on 2017/2/8.
 * <p>
 * fragment回退栈，使用时记得把activity中的onBackPressed()中的super.onBackPressed()注释掉
 */

public class FragmentBackStack
{

    private static final String TAG = "FragmentBackStack >>> ";
    private final FragmentManager mFragmentManager;

    public FragmentBackStack(AppCompatActivity activity)
    {
        mFragmentManager = activity.getSupportFragmentManager();
    }

    public static FragmentBackStack newInstance(AppCompatActivity activity)
    {
        return new FragmentBackStack(activity);
    }

    /**
     * 设置显示fragment；
     * <p>
     * <br/>注意：调用该方法会根据isBasic传参数决定是否将当前fragment添加到回退栈 <br/>
     *
     * @param containerId 用来承载fragment的容器id
     * @param fragment    需要显示的fragment
     * @param isBasic     true: 如果当前fragment是基础fragment，一般为第一个设置的fragment
     */
    public void setupFragment(@IdRes int containerId, Fragment fragment, boolean isBasic)
    {
        final String tag = fragment.getClass().getSimpleName().toLowerCase();
        final FragmentTransaction transaction = mFragmentManager.beginTransaction().replace(containerId, fragment, tag).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE).setAllowOptimization(true);
        if (!isBasic) transaction.addToBackStack(tag);
        transaction.commit();
    }


    /**
     * 设置显示fragment；
     * <br/>注意：该fragment会添加到回退栈<br/>
     *
     * @param containerId 用来承载fragment的容器id
     * @param fragment    需要显示的fragment
     */
    public void setupNormalFragment(@IdRes int containerId, Fragment fragment)
    {
        setupFragment(containerId, fragment, false);
    }

    /**
     * 设置基础fragment，一般为第一个设置的fragment
     *
     * @param containerId
     * @param basicFragment
     */
    public void setupBasicFragment(@IdRes int containerId, Fragment basicFragment)
    {
        setupFragment(containerId, basicFragment, true);
    }


    /**
     * 回退fragment栈
     *
     * @return true，如果栈中还有fragment， false如果栈空了
     */
    public boolean popFragmentStack()
    {
        if (mFragmentManager.popBackStackImmediate()) {
            Log.i(TAG, "popFragmentStack: BackStackEntryCount >> " + mFragmentManager.getBackStackEntryCount());
            return true;
        }
        return false;
    }

}
