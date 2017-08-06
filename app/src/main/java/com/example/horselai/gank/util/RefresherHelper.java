package com.example.horselai.gank.util;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.example.horselai.gank.R;


/**
 * Created by laixiaolong on 2017/4/22.
 * <p>
 * 集成了SwipeRefreshLayout的常用方法， 减少重复代码
 */
public class RefresherHelper
{
    private SwipeRefreshLayout mRefreshLayout;

    /**
     * @param parent   父容器
     * @param resId    SwipeRefreshLayout 的id
     * @param listener
     */
    public RefresherHelper(View parent, @IdRes int resId, @NonNull SwipeRefreshLayout.OnRefreshListener listener)
    {
        mRefreshLayout = (SwipeRefreshLayout) parent.findViewById(resId);
        mRefreshLayout.setOnRefreshListener(listener);
        mRefreshLayout.setColorSchemeResources(R.color.lightBrown, R.color.hotPink, R.color.lightBlue);
    }

    public void startRefreshing()
    {
        mRefreshLayout.setRefreshing(true);
    }


    public void stopRefreshing()
    {
        mRefreshLayout.setRefreshing(false);
    }

    public boolean isRefreshing()
    {
        return mRefreshLayout.isRefreshing();
    }

    /**
     * 当前是否处于圆圈显示状态
     *
     * @return
     */
    public boolean isRefresherShow()
    {
        return mRefreshLayout.isShown() || mRefreshLayout.isLaidOut() || mRefreshLayout.isActivated();
    }


    public SwipeRefreshLayout getRefresher()
    {
        return mRefreshLayout;
    }

}
