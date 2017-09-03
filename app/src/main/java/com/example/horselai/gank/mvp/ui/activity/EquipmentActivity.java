package com.example.horselai.gank.mvp.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.AppbarActivity;
import com.example.horselai.gank.base.BaseFragmentStatePagerAdapter;
import com.example.horselai.gank.mvp.presenter.ReadingPresenter;
import com.example.horselai.gank.mvp.ui.fragment.CommReadingListFragment;

public class EquipmentActivity extends AppbarActivity
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
        getBarTitleView().setText("  装备");

        setupViewPager();
    }

    private void setupViewPager()
    {
        final TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        final ViewPager vpContainer = (ViewPager) findViewById(R.id.vp_container);
        tabs.setupWithViewPager(vpContainer);

        final BaseFragmentStatePagerAdapter adapter = new BaseFragmentStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CommReadingListFragment().setFragmentType(ReadingPresenter.TYPE_DIGITAL), "数字尾巴");
        adapter.addFragment(new CommReadingListFragment().setFragmentType(ReadingPresenter.TYPE_LIQI), "利器");
        adapter.addFragment(new CommReadingListFragment().setFragmentType(ReadingPresenter.TYPE_MAC_PLAY), "Mac玩法");
        adapter.addFragment(new CommReadingListFragment().setFragmentType(ReadingPresenter.TYPE_APP_MINORITY), "小众软件");
        adapter.addFragment(new CommReadingListFragment().setFragmentType(ReadingPresenter.TYPE_APP_ANTI), "反斗软件");
        vpContainer.setAdapter(adapter);

    }


}