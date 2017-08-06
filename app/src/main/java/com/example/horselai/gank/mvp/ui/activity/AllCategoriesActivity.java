package com.example.horselai.gank.mvp.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.AppbarSearchActivity;
import com.example.horselai.gank.base.BaseFragmentStatePagerAdapter;
import com.example.horselai.gank.http.api.GankApi;
import com.example.horselai.gank.http.loader.BitmapManager;
import com.example.horselai.gank.mvp.ui.fragment.AndroidFragment;
import com.example.horselai.gank.mvp.ui.fragment.AppFragment;
import com.example.horselai.gank.mvp.ui.fragment.ExpandsFragment;
import com.example.horselai.gank.mvp.ui.fragment.IOSFragment;
import com.example.horselai.gank.mvp.ui.fragment.RandomRecommendFragment;
import com.example.horselai.gank.mvp.ui.fragment.VideoFragment;
import com.example.horselai.gank.mvp.ui.fragment.WebFragment;
import com.example.horselai.gank.util.Utils;

public class AllCategoriesActivity extends AppbarSearchActivity
{
    public static final String KEY_PAGE = "page";
    private ImageView mImageBg;
    private ViewPager mViewPager;

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
        getToolbar().setLongClickable(true);
        getToolbar().setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override public boolean onLongClick(View v)
            {
                Utils.alertDialog(AllCategoriesActivity.this, getResources().getString(R.string.app_name), getResources().getString(R.string.logo_desc), R.drawable.logo_work);
                return true;
            }
        });

        setupViewPager();
        initOthers();
    }

    private void initOthers()
    {
        final TextView barTitle = getBarTitleView();
        barTitle.setText("  干货分类");
        barTitle.setTextColor(getResources().getColor(R.color.lightBlack));

        mImageBg = (ImageView) findViewById(R.id.iv_bg);

    }

    void setupViewPager()
    {
        mViewPager = (ViewPager) findViewById(R.id.vp_container);

        final BaseFragmentStatePagerAdapter pagerAdapter = new BaseFragmentStatePagerAdapter(getSupportFragmentManager());
        final TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);
        /*tabs.setBackgroundColor(Color.TRANSPARENT);
        tabs.setSelectedTabIndicatorColor(Color.WHITE);
*/
        pagerAdapter.addFragment(new RandomRecommendFragment(), GankApi.RECOMMEND);
        pagerAdapter.addFragment(new AndroidFragment(), GankApi.ANDROID);
        pagerAdapter.addFragment(new IOSFragment(), GankApi.IOS);
        pagerAdapter.addFragment(new WebFragment(), GankApi.WEB);
        pagerAdapter.addFragment(new ExpandsFragment(), GankApi.EXPENDS);
        pagerAdapter.addFragment(new AppFragment(), GankApi.APP);
        pagerAdapter.addFragment(new VideoFragment(), GankApi.VIDEO);

        mViewPager.setAdapter(pagerAdapter);

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final int pageIndex = extras.getInt(KEY_PAGE);
            mViewPager.setCurrentItem(pageIndex);
        }

    }

    @Override protected void onDestroy()
    {
        super.onDestroy();

        BitmapManager.getInstance().releaseImage(mImageBg);
        gc();
    }


}
