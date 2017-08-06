package com.example.horselai.gank.mvp.ui.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.mvp.ui.adapter.GankNewsListAdapter;
import com.example.horselai.gank.mvp.ui.adapter.binder.CatTypeGridVhBinder;

/**
 * Created by horseLai on 2017/7/16.
 */

public class AndroidFragment extends CommGankNewsListFragment
{

    private static final String TAG = "AndroidFragment";

    @Override protected void doUpdateDataList()
    {
        updateData(GankUI.PAGE_ANDROID, ITEM_SIZE, ++mPageNum);
    }


    @Override protected BaseViewHolderBinder<GankNews> onCreateViewHolderBinder()
    {
        return new CatTypeGridVhBinder(mContext);
    }

    @Override public RecyclerView.LayoutManager onCreateLayoutManager()
    {
        return new GridLayoutManager(mContext, 2);
    }

    @Override public BaseMultipleTypeListAdapter<GankNews> onCreateAdapter()
    {
        return new GankNewsListAdapter(mContext);
    }
}
