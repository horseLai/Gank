package com.example.horselai.gank.mvp.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.app.App;
import com.example.horselai.gank.base.AppbarSearchActivity;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.GankBeauty;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.bean.home.CommHomeItem;
import com.example.horselai.gank.http.loader.ImageLoader;
import com.example.horselai.gank.mvp.model.GankFetcher;
import com.example.horselai.gank.mvp.presenter.DayRecommendPresenter;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.mvp.ui.adapter.DayRecommendAdapter;
import com.example.horselai.gank.mvp.ui.adapter.binder.DayRecommendVHBinder;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.RefresherHelper;

import java.util.ArrayList;

public class DayRecommendActivity extends AppbarSearchActivity implements ISuperView<SparseArray<Object>>, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener
{

    private RefresherHelper mRefresher;
    private AbsSuperPresenter<SparseArray<Object>> mPresenter;
    private RecyclerView mRvList;
    private Snackbar mSnackBar;
    private DayRecommendVHBinder mBinder;
    private DayRecommendAdapter mAdapter;
    private ImageView mBarImage;
    private GankBeauty mBeauty;


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
        return R.layout.activity_day_recommend;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mPresenter = new DayRecommendPresenter(this, this);


        getToolbar().setLogo(R.drawable.logo_work);
        getBarTitleView().setText("   日推");

        initView(findViewById(R.id.refresher_root_layout), savedInstanceState);
    }


    private void initView(View rootView, Bundle savedInstanceState)
    {
        mRefresher = new RefresherHelper(rootView, R.id.fragment_base_refresher, this);
        mRvList = (RecyclerView) rootView.findViewById(R.id.fragment_base_list);

        mRvList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        mAdapter = new DayRecommendAdapter(this);
        mBinder = new DayRecommendVHBinder(this);
        addBindListener();
        mAdapter.setViewHolderBinder(mBinder);
        mRvList.setAdapter(mAdapter);

        //*************************************************************
        mSnackBar = Snackbar.make(mRvList, "", Snackbar.LENGTH_SHORT);
        mSnackBar.getView().setBackgroundColor(getResources().getColor(R.color.grayBlue));

        //*************************************************************
        mBarImage = (ImageView) findViewById(R.id.bar_img);
        mBarImage.setOnClickListener(this);


        //*************************************************************
        FloatingActionButton fabDownload = (FloatingActionButton) findViewById(R.id.fab_download);
        fabDownload.setOnClickListener(this);

        updateData();
    }

    private void addBindListener()
    {
        mBinder.setOnItemClickListener(new BaseViewHolderBinder.OnItemClickListener()
        {
            @Override public void onItemClicked(View v, int position)
            {
                final CommHomeItem<GankNews> item = mAdapter.getDataList().get(position);
                if (item.data == null && item.dataList == null) {
                    final int pageIndex = GankUI.getAssociatedPageIndex(item.headerLabel);
                    GankUI.startAllCategoriesActivity(DayRecommendActivity.this, pageIndex);
                }
            }
        });
    }

    public void showSnackBar(String msg)
    {
        mSnackBar.setText(msg);
        mSnackBar.show();
    }

    @Override public void onLoadOk(SparseArray data)
    {
        if (data == null) {
            App.toastLong("图片下载成功（￣︶￣）↗　");
            return;
        }
        mAdapter.removeAllItems();
        mAdapter.insertItemsIntoFootPos((ArrayList<CommHomeItem<GankNews>>) data.get(GankFetcher.DAY_RECOMMEND_NEWS));

        mBeauty = (GankBeauty) data.get(GankFetcher.DAY_RECOMMEND_BEAUTY);
        final String url = mBeauty.url + "?imageView2/0/w/320";
        ImageLoader.getImageLoader().displayImageAsync(mBarImage, url, false, true, 320, 420);

        mRefresher.stopRefreshing();
        showSnackBar("数据加载成功 ԅ(¯﹃¯ԅ)");
    }


    @Override public void onRefresh()
    {
        updateData();
    }

    private void updateData()
    {
        mPresenter.update();
        mRefresher.startRefreshing();
    }

    @Override protected void onStop()
    {
        super.onStop();
        // 刷新缓存
        // HttpRequestUtil.flushHttpResponseCache();
    }


    @Override public void onLoadFailed(Exception e)
    {
        mRefresher.stopRefreshing();
        showSnackBar(e.getMessage());
    }

    @Override public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.bar_img: {

                if (mBeauty != null) GankUI.startImageActivity(DayRecommendActivity.this, mBeauty);
                else App.toastShort("图片链接异常！⊙﹏⊙∥");
                break;
            }
            case R.id.fab_download: {
                if (mBeauty != null) {
                    mPresenter.update(mBeauty.url);
                    App.toastLong("马上就下载 (●'◡'●)");
                    break;
                }
                App.toastLong("这是张假图片(╯‵□′)╯︵┻━┻");
                break;
            }
        }
    }


}
