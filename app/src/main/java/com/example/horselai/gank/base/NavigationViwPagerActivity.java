package com.example.horselai.gank.base;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.View;

import com.example.horselai.gank.R;

/**
 * Created by horseLai on 2017/7/20.
 */

public abstract class NavigationViwPagerActivity extends AppbarSearchActivity
{
    private BottomNavigationView mNavigation;

    @Override protected boolean homeAsUpEnable()
    {
        return false;
    }

    @Override protected View.OnClickListener onToolbarClick()
    {
        return null;
    }

    @Override public int provideContentViewId()
    {
        return R.layout.activity_main;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setupAHNavigationView();
    }


    public BottomNavigationView getNavigationBar()
    {
        return mNavigation;
    }

    private void setupAHNavigationView()
    {
        mNavigation = (BottomNavigationView) findViewById(R.id.bottom_nav_tabs);
        mNavigation.setOnNavigationItemSelectedListener(createItemSelectedListener());

    }

    protected abstract BottomNavigationView.OnNavigationItemSelectedListener createItemSelectedListener();


}
