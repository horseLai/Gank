package com.example.horselai.gank.mvp.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.mvp.ui.adapter.GankNewsListAdapter;
import com.example.horselai.gank.mvp.ui.adapter.binder.CatTypeLinearVhBinder;

/**
 * Created by horseLai on 2017/7/16.
 */

public class IOSFragment extends CommGankNewsListFragment
{

    @Override protected void doUpdateDataList()
    {
        updateData(GankUI.PAGE_IOS, ITEM_SIZE, ++mPageNum);
    }


    @Override protected BaseViewHolderBinder<GankNews> onCreateViewHolderBinder()
    {
        return new CatTypeLinearVhBinder(mContext);
    }

    @Override public RecyclerView.LayoutManager onCreateLayoutManager()
    {
        return new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
    }

    @Override public BaseMultipleTypeListAdapter<GankNews> onCreateAdapter()
    {
        return new GankNewsListAdapter(mContext);
    }
}

