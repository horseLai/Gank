package com.example.horselai.gank.impl;

import android.content.Context;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.horselai.gank.util.AnimationHelper;

/**
 * Created by horseLai on 2017/8/3.
 */

public class NavigationAutoHideBehavior extends CoordinatorLayout.Behavior<BottomNavigationView>
{

    private int mTouchSlop;
    private AnimationHelper mAnimationHelper;
    private static final String TAG = "NavigationAutoHideBehav";

    public NavigationAutoHideBehavior()
    {
        super();
    }

    public NavigationAutoHideBehavior(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }


    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, BottomNavigationView child, View directTargetChild, View target, int nestedScrollAxes)
    {
        if (mAnimationHelper == null) mAnimationHelper = new AnimationHelper(child);
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }


    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, BottomNavigationView child, View target, int dx, int dy, int[] consumed)
    {
        if (Math.abs(dy) <= mTouchSlop) return;


        Log.i(TAG, "onNestedPreScroll: dy >>: " + dy);
        final int status = mAnimationHelper.getStatus();
        if (dy < 0 && status == AnimationHelper.STATUS_HIDE) {

            mAnimationHelper.showNavigationView();
        } else if (dy > 0 && status == AnimationHelper.STATUS_SHOW) { //hideNavigationView
            mAnimationHelper.hideNavigationView();
        }

    }
}
