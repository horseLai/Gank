package com.example.horselai.gank.mvp.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.BaseListFragment;
import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.home.CommHomeItem;
import com.example.horselai.gank.mvp.presenter.MainViewPresenter;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.mvp.ui.adapter.HomePageMultipleTypeAdapter;
import com.example.horselai.gank.mvp.ui.adapter.binder.HomeViewHolderBinder;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.RefresherHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by horseLai on 2017/7/16.
 */

public class HomeFragment extends BaseListFragment<CommHomeItem>
{

    private static final String TAG = "HomeFragment";

    private RefresherHelper mRefresherHelper;
    private HomePageMultipleTypeAdapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;
    private HomeViewHolderBinder mBinder;
    private int mTouchSlop;


    @Override protected int provideLayoutId()
    {
        return R.layout.fragment_base_refresh_list;
    }


    @Override
    protected void onFragmentListInitOk(View savedInstanceState, Bundle bundle, RecyclerView rvList, RefresherHelper refresherHelper, AbsSuperPresenter presenter, BaseMultipleTypeListAdapter<CommHomeItem> adapter, BaseViewHolderBinder<CommHomeItem> vhBinder)
    {

        this.mRefresherHelper = refresherHelper;
        this.mBinder = (HomeViewHolderBinder) vhBinder;
        this.mAdapter = (HomePageMultipleTypeAdapter) adapter;


        setItemClickListener();
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (mRefresherHelper.isRefresherShow()) return;
                mAdapter.updateListScrollState(mLayoutManager, newState);
            }
        });

        if (bundle == null) {
            updateData(MainViewPresenter.HOME);
        } else {
            mAdapter.removeAllItems();
            mAdapter.insertItemsIntoFootPos((List<CommHomeItem>) bundle.getSerializable("data"));
        }


    }

    @Override protected BaseViewHolderBinder<CommHomeItem> onCreateViewHolderBinder()
    {
        return new HomeViewHolderBinder(mContext);
    }


    @Override public AbsSuperPresenter onCreatePresenter(ISuperView view)
    {
        return new MainViewPresenter(view);
    }

    @Override public RecyclerView.LayoutManager onCreateLayoutManager()
    {
        return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    }

    @Override public BaseMultipleTypeListAdapter<CommHomeItem> onCreateAdapter()
    {
        return new HomePageMultipleTypeAdapter(mContext);
    }

    private void setItemClickListener()
    {
        mBinder.setOnItemClickListener(new BaseViewHolderBinder.OnItemClickListener()
        {
            @Override public void onItemClicked(View v, int position)
            {
                //App.toastShort("" + position);
                final CommHomeItem item = mAdapter.getDataList().get(position);

                //点击header时跳转
                if (item.data == null && item.dataList == null)
                    GankUI.startActivityByHeaderLabel(mContext, item.headerLabel);
            }
        });
    }


    @Override public void onLoadOk(ArrayList<CommHomeItem> data)
    {
        mAdapter.removeAllItems();
        mAdapter.addItemsToHeadPos(data);

        showSnackBar("数据加载成功！(￣▽￣)");
        mRefresherHelper.stopRefreshing();

    }


    @Override public void onRefresh()
    {
        updateData(MainViewPresenter.HOME);
    }

    @Override public void onLoadFailed(Exception e)
    {
        e.printStackTrace();
        showSnackBar(e.getMessage());
        mRefresherHelper.stopRefreshing();
    }

    @Override public void onResume()
    {
        super.onResume();
        mBinder.setupSlideRotation();
    }

    @Override public void onPause()
    {
        super.onPause();
        mBinder.stopSlideRotation();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putSerializable("data", mAdapter.getDataList());
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }
}
