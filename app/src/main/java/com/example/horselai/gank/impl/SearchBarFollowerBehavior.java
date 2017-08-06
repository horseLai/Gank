package com.example.horselai.gank.impl;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by horseLai on 2017/7/20.
 */

public class SearchBarFollowerBehavior extends CoordinatorLayout.Behavior<View>
{

    public SearchBarFollowerBehavior()
    {
    }

    public SearchBarFollowerBehavior(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    @Override public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency)
    {
        return dependency instanceof CardView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency)
    {
        if (child instanceof ViewPager || child instanceof SwipeRefreshLayout) {
            child.setTop(dependency.getBottom() + 5);
        }

        return true;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes)
    {
        return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed)
    {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }
}
