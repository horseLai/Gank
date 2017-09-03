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

public class ReadingActivity extends AppbarActivity
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
        getBarTitleView().setText("  闲读");


        setupViewPager();
    }

    private void setupViewPager()
    {

        final TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        final ViewPager vpContainer = (ViewPager) findViewById(R.id.vp_container);
        tabs.setupWithViewPager(vpContainer);

        final BaseFragmentStatePagerAdapter adapter = new BaseFragmentStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CommReadingListFragment().setFragmentType(ReadingPresenter.TYPE_36KR), "36Kr");
        adapter.addFragment(new CommReadingListFragment().setFragmentType(ReadingPresenter.TYPE_ZHIHU_DAILY), "知乎日报");
        adapter.addFragment(new CommReadingListFragment().setFragmentType(ReadingPresenter.TYPE_CURIOSITY), "好奇心");
        adapter.addFragment(new CommReadingListFragment().setFragmentType(ReadingPresenter.TYPE_BUSINESS), "创业邦");
        adapter.addFragment(new CommReadingListFragment().setFragmentType(ReadingPresenter.TYPE_ENGLAND_LIFE), "英国那些事");
        // adapter.addFragment(new CommReadingListFragment()  .setFragmentType(ReadingPresenter.TYPE_YUEGUANG), "月光博客");
        adapter.addFragment(new CommReadingListFragment().setFragmentType(ReadingPresenter.TYPE_IDEAL_LIF), "理想生活");
        adapter.addFragment(new CommReadingListFragment().setFragmentType(ReadingPresenter.TYPE_JIANDAN), "煎蛋");
        vpContainer.setAdapter(adapter);

    }


}
