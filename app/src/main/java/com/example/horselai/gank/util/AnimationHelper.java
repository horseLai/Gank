package com.example.horselai.gank.util;

import android.animation.ValueAnimator;
import android.view.View;

/**
 * Created by horseLai on 2017/7/18.
 * <p>
 * 包含常用动画操作
 */

public class AnimationHelper
{
    public static final int STATUS_HIDE = 0;
    public static final int STATUS_SHOW = 1;
    private int mCurrStatus = STATUS_SHOW;

    private View mView;

    float mStartY;

    public AnimationHelper(View view)
    {
        this.mView = view;
        mStartY = mView.getTop();
    }

    public static AnimationHelper get(View view)
    {
        return new AnimationHelper(view);
    }

    /**
     * Y方向上平滑移动
     *
     * @param valueFrom
     * @param valueTo
     */
    public void smoothMoveY(float valueFrom, float valueTo)
    {
        final ValueAnimator animator = ValueAnimator.ofFloat(valueFrom, valueTo);
        animator.setDuration(300L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                mView.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        animator.start();
    }

    public void showNavigationView()
    {
        smoothMoveY(mView.getTop(), mStartY);
        mCurrStatus = STATUS_SHOW;
    }

    public void hideNavigationView()
    {
        smoothMoveY(mView.getTop(), mStartY + mView.getHeight());
        mCurrStatus = STATUS_HIDE;
    }

    public void smoothMoveX(float valueFrom, float valueTo)
    {
        final ValueAnimator animator = ValueAnimator.ofFloat(valueFrom, valueTo);
        animator.setDuration(300L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                mView.setY((Float) valueAnimator.getAnimatedValue());
            }
        });
        animator.start();
    }

    public void setStartX(float x)
    {

    }

    public void setStartY(float y)
    {

    }

    public void setStatus(int status)
    {

    }

    public int getStatus()
    {
        return mCurrStatus;
    }
}
