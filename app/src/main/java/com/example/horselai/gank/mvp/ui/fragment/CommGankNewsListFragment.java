package com.example.horselai.gank.mvp.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewConfiguration;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.BaseListFragment;
import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.mvp.presenter.AllCatPresenter;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.RefresherHelper;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by horseLai on 2017/7/26.
 */

public abstract class CommGankNewsListFragment extends BaseListFragment<GankNews>
{

    protected int mPageNum = 0;
    protected static final int ITEM_SIZE = 20;
    protected static final int MAX_ITEM_SIZE = ITEM_SIZE * 10;

    private RefresherHelper mRefresherHelper;
    private BaseMultipleTypeListAdapter<GankNews> mAdapter;
    private int mTouchSlop;


    @Override protected int provideLayoutId()
    {
        return R.layout.fragment_base_refresh_list;
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

        //恢复数据
        if (savedInstanceState == null) {
            doUpdateDataList();
            showSnackBar("正在加载数据！ฅʕ•̫͡•ʔฅ");
        } else {
            mAdapter.removeAllItems();
            mAdapter.insertItemsIntoFootPos((LinkedList<GankNews>) savedInstanceState.getSerializable("data"));
        }
    }

    protected abstract void doUpdateDataList();

    private void loadMore(RecyclerView.LayoutManager layoutManager, int dy)
    {
        checkIfLoadMore((LinearLayoutManager) layoutManager, dy);
    }

    private void checkIfLoadMore(LinearLayoutManager layoutManager, int dy)
    {
        final int last = layoutManager.findLastVisibleItemPosition();
        final boolean toLoad = last >= mAdapter.getItemCount() - 2;

        if (dy > 20 && toLoad && !mRefresherHelper.isRefreshing()) {

            showSnackBar("正在加载更多...ฅʕ•̫͡•ʔฅ ");
            doUpdateDataList();
        }
    }


    @Override public AbsSuperPresenter onCreatePresenter(ISuperView view)
    {
        return new AllCatPresenter(view);
    }


    @Override public void onLoadOk(ArrayList<GankNews> data)
    {
        if (mFromRefresher) {
            mFromRefresher = false;
            mAdapter.removeAllItems();
        }

        //限制列表条目数量，省内存
        /*if (mAdapter.getItemCount() > MAX_ITEM_SIZE) {
            mAdapter.removeItems(0, MAX_ITEM_SIZE / 2);  //去除前一半的数据
        }*/
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
        showSnackBar(e.getMessage());
        mRefresherHelper.stopRefreshing();
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
