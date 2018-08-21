package com.example.horselai.gank.base;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.impl.AnimationAdapter;
import com.example.horselai.gank.util.StatusBarCompat;

/**
 * Created by horseLai on 2017/7/20.
 */

public abstract class AppbarActivity extends BaseActivity
{

    private Toolbar mToolbar;
    private AppBarLayout mAppbar;
    private int mAppbarHeight;
    private int mTouchSlop;
    private boolean mIsShowing = true;
    private TextView mBarTitle;

    public Toolbar getToolbar()
    {
        return mToolbar;
    }

    public AppBarLayout getAppBarLayout()
    {
        return mAppbar;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(provideContentViewId());


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mAppbar = (AppBarLayout) findViewById(R.id.app_bar_layout);
        StatusBarCompat.compatToolbar(mToolbar, this);

        mToolbar.setOnClickListener(onToolbarClick());
        mToolbar.setTitleMarginStart(0);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        if (homeAsUpEnable()) {
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    finish();
                }
            });
        }

        mBarTitle = (TextView) findViewById(R.id.toolbar_bar_title);

        if (Build.VERSION.SDK_INT >= 21) {
            mToolbar.setElevation(10f);
        }

        mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();

    }

    public TextView getBarTitleView()
    {
        return mBarTitle;
    }

    protected abstract boolean homeAsUpEnable();

    protected abstract View.OnClickListener onToolbarClick();


    /**
     * <p>
     * 所提供的layoutId 必须包含<code>R.id.toolbar</code>
     * 和<code>R.id.app_bar_layout</code>，分别代表Toolbar
     * 和AppBarLayout
     * </p>
     *
     * @return
     */
    public abstract int provideContentViewId();


    /**
     * 主要用于列表滑动时，根据滑动距离及方向来自动隐藏和显示Appbar
     *
     * @param dy
     */
    public void autoHideAndShow(int dy)
    {
        if (Math.abs(dy) < mTouchSlop) return;

        if (dy < 0 && mIsShowing) {
            hideAppbar();
        } else if (dy > 0 && !mIsShowing) {
            showAppbar();
        }
        mIsShowing = !mIsShowing;
    }


    public void hideAppbar()
    {
        mToolbar.clearAnimation();
        mToolbar.animate().translationY(-mAppbarHeight).alpha(0f).setDuration(300L).setListener(new AnimationAdapter()
        {
            @Override public void onAnimationEnd(Animator animation)
            {
                mToolbar.setVisibility(View.INVISIBLE);
                mAppbar.setBackgroundColor(Color.TRANSPARENT);
            }

        }).start();
    }


    public void showAppbar()
    {
        mToolbar.clearAnimation();
        mToolbar.animate().translationY(0).alpha(1f).setDuration(300L).setListener(new AnimationAdapter()
        {
            @Override public void onAnimationStart(Animator animation)
            {
                mToolbar.setVisibility(View.VISIBLE);
            }

        }).start();
    }


    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        mAppbarHeight = mAppbar.getHeight();
    }


}
