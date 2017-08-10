package com.example.horselai.gank.mvp.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.BaseListFragment;
import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.bean.GankReading;
import com.example.horselai.gank.mvp.presenter.ReadingPresenter;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.mvp.ui.adapter.ReadingListAdapter;
import com.example.horselai.gank.mvp.ui.adapter.binder.ReadingVhBinder;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.RefresherHelper;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by horseLai on 2017/8/8.
 */

public abstract class CommReadingListFragment extends BaseListFragment<GankReading>
{
    private int mPageNum = 1;
    private RefresherHelper mRefresher;
    private BaseMultipleTypeListAdapter<GankReading> mAdapter;

    @Override protected int provideLayoutId()
    {
        return R.layout.fragment_base_refresh_list;
    }

    @Override protected BaseViewHolderBinder<GankReading> onCreateViewHolderBinder()
    {
        return new ReadingVhBinder(mContext);
    }


    @Override public AbsSuperPresenter onCreatePresenter(ISuperView view)
    {
        return new ReadingPresenter(view);
    }

    @Override public RecyclerView.LayoutManager onCreateLayoutManager()
    {
        return new LinearLayoutManager(mContext);
    }

    @Override public BaseMultipleTypeListAdapter<GankReading> onCreateAdapter()
    {
        return new ReadingListAdapter(mContext);
    }


    @Override
    protected void onFragmentListInitOk(View savedInstanceState, Bundle bundle, RecyclerView rvList, RefresherHelper refresherHelper, AbsSuperPresenter presenter, BaseMultipleTypeListAdapter<GankReading> adapter, BaseViewHolderBinder<GankReading> vhBinder)
    {
        this.mAdapter = adapter;
        this.mRefresher = refresherHelper;

        addVhBinderListener(vhBinder);
        addListListener(rvList);

        //恢复数据
        if (bundle == null) {
            updateData(getFragmentType(), mPageNum);
        } else {
            mAdapter.removeAllItems();
            mAdapter.insertItemsIntoFootPos((LinkedList<GankReading>) bundle.getSerializable("data"));
        }

    }

    protected abstract int getFragmentType();

    private void addListListener(RecyclerView rvList)
    {
        rvList.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                mAdapter.updateListScrollState(recyclerView.getLayoutManager(), newState);
            }

            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                loadMore(recyclerView.getLayoutManager(), dy);
            }
        });
    }


    private void loadMore(RecyclerView.LayoutManager layoutManager, int dy)
    {
        checkIfLoadMore((LinearLayoutManager) layoutManager, dy);
    }

    private void checkIfLoadMore(LinearLayoutManager layoutManager, int dy)
    {
        final int last = layoutManager.findLastVisibleItemPosition();
        final boolean toLoad = last >= mAdapter.getItemCount() - 2;

        if (dy > 20 && toLoad && !mRefresher.isRefreshing()) {

            showSnackBar("正在加载更多...ฅʕ•̫͡•ʔฅ ");
            updateData(getFragmentType(), ++mPageNum);
        }
    }


    private void addVhBinderListener(BaseViewHolderBinder<GankReading> vhBinder)
    {
        vhBinder.setOnItemClickListener(new BaseViewHolderBinder.OnItemClickListener()
        {
            @Override public void onItemClicked(View v, int position)
            {
                final GankReading reading = mAdapter.getDataList().get(position);
                /*if ("知乎日报".equalsIgnoreCase(reading.source)) {
                    Utils.startActivity(mContext, ReadingDetailActivity.class);
                    return;
                }*/
                //App.toastShort(reading.title);
                final GankNews news = new GankNews();
                news.url = reading.url;
                news.desc = reading.title;
                news.source = reading.source;
                news.type = reading.source;

                GankUI.startWebActivity(mContext, news);
            }
        });
    }

    boolean mFromRefresher = false;

    @Override public void onLoadOk(ArrayList<GankReading> data)
    {
        if (mFromRefresher) {
            mFromRefresher = false;
            mAdapter.removeAllItems();
        }
        mAdapter.insertItemsIntoFootPos(data);
        showSnackBar("加载完成！(●'◡'●)");
        mRefresher.stopRefreshing();
    }

    @Override public void onRefresh()
    {
        mPageNum = 1;
        mFromRefresher = true;
        updateData(getFragmentType(), mPageNum);
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putSerializable("data", mAdapter.getDataList());
    }

}
