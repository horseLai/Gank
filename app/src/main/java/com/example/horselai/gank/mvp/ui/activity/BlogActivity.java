package com.example.horselai.gank.mvp.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.AppbarActivity;
import com.example.horselai.gank.base.BaseFragmentStatePagerAdapter;
import com.example.horselai.gank.mvp.ui.fragment.BlogGlowingFragment;
import com.example.horselai.gank.mvp.ui.fragment.BlogLuanXiangFragment;
import com.example.horselai.gank.mvp.ui.fragment.BlogMeituanFragment;
import com.example.horselai.gank.mvp.ui.fragment.BlogMoguFragment;
import com.example.horselai.gank.mvp.ui.fragment.BlogO2IoFragment;
import com.example.horselai.gank.mvp.ui.fragment.BlogProductFragment;

public class BlogActivity extends AppbarActivity
{

    @Override protected boolean homeAsUpEnable()
    {
        return true;
    }

    @Override protected View.OnClickListener onToolbarClick()
    {
        return null;
    }

    @Override public int provideContentViewId()
    {
        return R.layout.activity_all_categories;
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getToolbar().setLogo(R.drawable.logo_work);
        getBarTitleView().setText("  博客");

        setupViewPager();
    }

    private void setupViewPager()
    {

        final TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        final ViewPager vpContainer = (ViewPager) findViewById(R.id.vp_container);
        tabs.setupWithViewPager(vpContainer);

        final BaseFragmentStatePagerAdapter adapter = new BaseFragmentStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BlogProductFragment(), "产品");
        adapter.addFragment(new BlogMeituanFragment(), "美团");
        adapter.addFragment(new BlogO2IoFragment(), "Auto.Io");
        adapter.addFragment(new BlogGlowingFragment(), "Glow");
        adapter.addFragment(new BlogMoguFragment(), "蘑菇街");
        adapter.addFragment(new BlogLuanXiangFragment(), "乱象");
        vpContainer.setAdapter(adapter);

    }


}

