package com.example.horselai.gank.mvp.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.horselai.gank.R;
import com.example.horselai.gank.app.App;
import com.example.horselai.gank.base.BaseFragment;
import com.example.horselai.gank.base.BaseFragmentPagerAdapter;
import com.example.horselai.gank.impl.TabSelectedListener;
import com.example.horselai.gank.service.ScanHistoryService;

/**
 * Created by horseLai on 2017/7/29.
 */

public class SaveAndHistoryFragment extends BaseFragment
{

    private LocalBroadcastManager mBroadcastManager;

    @Override protected int provideLayoutId()
    {
        return R.layout.fragment_history;
    }

    @Override protected void initView(View rootView, Bundle savedInstanceState)
    {
        final TabLayout tabs = (TabLayout) rootView.findViewById(R.id.tabs);
        tabs.setSmoothScrollingEnabled(true);
        final ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.vp_container);

        TabLayout.Tab tab = tabs.newTab();
        tab.setIcon(R.drawable.ic_history_white);
        tab.setText("历史");
        tabs.addTab(tab, 0);

        tab = tabs.newTab();
        tab.setIcon(R.drawable.ic_star_white);
        tab.setText("收藏");
        tabs.addTab(tab, 1);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override public void onPageSelected(int position)
            {
                tabs.getTabAt(position).select();
            }
        });
        tabs.addOnTabSelectedListener(new TabSelectedListener()
        {
            @Override public void onTabSelected(TabLayout.Tab tab)
            {
                viewPager.setCurrentItem(tabs.getSelectedTabPosition());
            }
        });


        final BaseFragmentPagerAdapter pagerAdapter = new BaseFragmentPagerAdapter(getFragmentManager());
        pagerAdapter.addFragment(new HistoryFragment(), "浏览记录");
        pagerAdapter.addFragment(new SaveFragment(), "收藏记录");
        viewPager.setAdapter(pagerAdapter);


    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mBroadcastManager = LocalBroadcastManager.getInstance(App.context());
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ScanHistoryService.ACTION_SAVE_DONE);
        filter.addAction(ScanHistoryService.ACTION_SAVED_ALREADY);
        mBroadcastManager.registerReceiver(mReceiver, filter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override public void onReceive(Context context, Intent intent)
        {
            if (ScanHistoryService.ACTION_SAVE_DONE.equals(intent.getAction())) {
                App.toastShort("收藏成功！");
            } else if (ScanHistoryService.ACTION_SAVED_ALREADY.equals(intent.getAction())) {
                App.toastShort("已存在收藏列表！");
            }
        }
    };


    @Override public void onDestroy()
    {
        super.onDestroy();
        mBroadcastManager.unregisterReceiver(mReceiver);
    }
}
