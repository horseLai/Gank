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
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.mvp.presenter.AllCatPresenter;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.mvp.ui.adapter.binder.RandomRecommendVHBinder;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.RefresherHelper;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/7/16.
 */

public class RandomRecommendFragment extends BaseListFragment<GankNews>
{

    private static final String TAG = "AndroidFragment";

    protected int mPageNum = 0;
    protected static final int ITEM_SIZE = 20;
    private RefresherHelper mRefresherHelper;
    private BaseMultipleTypeListAdapter<GankNews> mAdapter;
    private int mTouchSlop;

    protected void doUpdateDataList()
    {
        updateData(GankUI.PAGE_RECOMMEND, ITEM_SIZE, ++mPageNum);
    }

    @Override protected int provideLayoutId()
    {
        return R.layout.fragment_base_refresh_list;
    }

    @Override protected BaseViewHolderBinder<GankNews> onCreateViewHolderBinder()
    {
        return new RandomRecommendVHBinder(mContext);
    }

    private void addListener(final RefresherHelper mRefresherHelper)
    {
        setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                if (mRefresherHelper.isRefresherShow()) return;
                mAdapter.updateListScrollState(recyclerView.getLayoutManager(), newState);
            }

            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);

                loadMore(recyclerView.getLayoutManager(), dy);
            }
        });
    }

    @Override
    protected void onFragmentListInitOk(View rootView, Bundle savedInstanceState, RecyclerView rvList, final RefresherHelper refresherHelper, AbsSuperPresenter presenter, BaseMultipleTypeListAdapter<GankNews> adapter, BaseViewHolderBinder<GankNews> binder)
    {
        this.mRefresherHelper = refresherHelper;
        mTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        this.mAdapter = adapter;

        addListener(refresherHelper);

        doUpdateDataList();
        showSnackBar("正在加载数据！ฅʕ•̫͡•ʔฅ");
    }

    private void loadMore(RecyclerView.LayoutManager layoutManager, int dy)
    {
        final int[] last = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(new int[2]);
        final boolean toLoad = last[0] >= mAdapter.getItemCount() - 2;

        if (dy > 20 && toLoad && !mRefresherHelper.isRefreshing()) {

            showSnackBar("正在加载更多...ฅʕ•̫͡•ʔฅ ");
            doUpdateDataList();
        }
    }


    @Override public AbsSuperPresenter onCreatePresenter(ISuperView view)
    {
        return new AllCatPresenter(view);
    }

    @Override public RecyclerView.LayoutManager onCreateLayoutManager()
    {
        return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    }

    @Override public BaseMultipleTypeListAdapter<GankNews> onCreateAdapter()
    {
        return new BaseMultipleTypeListAdapter<GankNews>(mContext)
        {
            @Override public boolean hasFooterView()
            {
                return false;
            }
        };
    }


    @Override public void onLoadOk(ArrayList<GankNews> data)
    {
        if (mFromRefresher) {
            mFromRefresher = false;
            mAdapter.removeAllItems();
        }

        mAdapter.insertItemsIntoFootPos(data);

        mRefresherHelper.stopRefreshing();
        showSnackBar("加载完成！(●'◡'●)");
    }


    boolean mFromRefresher = false;

    @Override public void onRefresh()
    {
        mFromRefresher = true;
        doUpdateDataList();
    }

    @Override public void onLoadFailed(Exception e)
    {
        e.printStackTrace();
        showSnackBar("没有加载到数据！(*/ω＼*)");
        mRefresherHelper.stopRefreshing();
    }


}
