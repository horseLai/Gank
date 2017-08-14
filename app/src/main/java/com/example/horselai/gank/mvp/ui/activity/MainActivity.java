package com.example.horselai.gank.mvp.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.BaseFragmentPagerAdapter;
import com.example.horselai.gank.base.FragmentCallback;
import com.example.horselai.gank.base.NavigationViwPagerActivity;
import com.example.horselai.gank.http.loader.BitmapManager;
import com.example.horselai.gank.http.loader.ImageLoader;
import com.example.horselai.gank.http.service.AsyncService;
import com.example.horselai.gank.mvp.ui.fragment.HomeFragment;
import com.example.horselai.gank.mvp.ui.fragment.MeFragment;
import com.example.horselai.gank.mvp.ui.fragment.SaveAndHistoryFragment;
import com.example.horselai.gank.util.Utils;

import java.io.IOException;

public class MainActivity extends NavigationViwPagerActivity
{

    private static final String TAG = "MainActivity";
    private ViewPager mViewPager;
    private ImageView mImageBg;
    private ArrayMap<Integer, Integer> mItemPos;


    @Override protected boolean homeAsUpEnable()
    {
        return false;
    }

    @Override protected View.OnClickListener onToolbarClick()
    {
        return null;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getToolbar().setLogo(R.drawable.logo_work);
        getToolbar().setLongClickable(true);
        getToolbar().setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override public boolean onLongClick(View v)
            {
                Utils.alertDialog(MainActivity.this, getResources().getString(R.string.app_name), getResources().getString(R.string.logo_desc), R.drawable.logo_work);
                return true;
            }
        });

        setupViewPager();
        initOthers();
    }

    @Override
    protected BottomNavigationView.OnNavigationItemSelectedListener createItemSelectedListener()
    {
        mItemPos = new ArrayMap<>();
        mItemPos.put(R.id.navigation_home, 0);
        mItemPos.put(R.id.navigation_history, 1);
        mItemPos.put(R.id.navigation_me, 2);
        return new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                mViewPager.setCurrentItem(mItemPos.get(item.getItemId()));
                return true;
            }
        };
    }

    private void checkItem(MenuItem item)
    {
        final BottomNavigationView navigationBar = getNavigationBar();
        final Menu menu = navigationBar.getMenu();
        final int size = menu.size();
        for (int i = 0; i < size; i++) {
            menu.getItem(i).setChecked(false);
        }
        item.setChecked(true);
    }


    private void initOthers()
    {
        final TextView barTitle = getBarTitleView();
        barTitle.setText("  干货集中营");
        barTitle.setTextColor(getResources().getColor(R.color.lightBlack));

        mImageBg = (ImageView) findViewById(R.id.iv_bg);

    }

    void setupViewPager()
    {
        mViewPager = (ViewPager) findViewById(R.id.home_page_container);
        mViewPager.setOffscreenPageLimit(3); //保持3页缓存，即切换时不会重新创建
        mViewPager.setPersistentDrawingCache(ViewGroup.PERSISTENT_ALL_CACHES);

        final BaseFragmentPagerAdapter pagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new HomeFragment().setCallback(mFragmentCallback), "home");
        pagerAdapter.addFragment(new SaveAndHistoryFragment().setCallback(mFragmentCallback), "ic_history_white");
        pagerAdapter.addFragment(new MeFragment().setCallback(mFragmentCallback), "me");
        mViewPager.setAdapter(pagerAdapter);

    }


    @Override protected void onDestroy()
    {
        super.onDestroy();

        BitmapManager.getInstance().releaseImage(mImageBg);
        gc();

        try {
            ImageLoader.getImageLoader().close();
            AsyncService.getService().close();
            Process.killProcess(Process.myPid());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void onBackPressed()
    {
        super.onBackPressed();
    }


    private final FragmentCallback mFragmentCallback = new FragmentCallback()
    {
        @Override public void onHandleCommand(int comm, Object obj)
        {
            //do something currently
        }
    };


    @Override public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged: ");

    }
}
