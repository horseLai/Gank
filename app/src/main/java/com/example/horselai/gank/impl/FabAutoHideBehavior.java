package com.example.horselai.gank.impl;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.horselai.gank.util.AnimationHelper;

/**
 * Created by horseLai on 2017/8/8.
 */

public class FabAutoHideBehavior extends CoordinatorLayout.Behavior<FloatingActionButton>
{
    private int mTouchSlop;
    AnimationHelper mAnimationHelper;

    public FabAutoHideBehavior(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes)
    {
        if (mAnimationHelper == null) mAnimationHelper = new AnimationHelper(target);
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dx, int dy, int[] consumed)
    {
        if (Math.abs(dy) <= mTouchSlop) return;

        final int status = mAnimationHelper.getStatus();
        if (dy < 0 && status == AnimationHelper.STATUS_HIDE) {

            mAnimationHelper.showNavigationView();
        } else if (dy > 0 && status == AnimationHelper.STATUS_SHOW) { //hideNavigationView
            mAnimationHelper.hideNavigationView();
        }

    }
}
