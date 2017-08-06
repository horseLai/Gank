package com.example.horselai.gank.impl;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by laixiaolong on 2017/4/24.
 */

public class MyViewPager extends ViewPager
{
    private boolean mCanShift;

    public MyViewPager(Context context)
    {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        return mCanShift && super.onInterceptTouchEvent(ev);
    }

    @Override public boolean onTouchEvent(MotionEvent ev)
    {
        return super.onTouchEvent(ev) && mCanShift;
    }

    public void setPageShiftEnable(boolean enable)
    {
        this.mCanShift = enable;
    }
}
