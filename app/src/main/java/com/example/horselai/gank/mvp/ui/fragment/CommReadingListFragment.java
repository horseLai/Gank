package com.example.horselai.gank.mvp.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.BaseListFragment;
import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.GankReading;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.RefresherHelper;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/8/8.
 */

public class CommReadingListFragment extends BaseListFragment<GankReading>
{
    @Override protected int provideLayoutId()
    {
        return R.layout.fragment_base_refresh_list;
    }

    @Override protected BaseViewHolderBinder<GankReading> onCreateViewHolderBinder()
    {
        return null;
    }


    @Override public AbsSuperPresenter onCreatePresenter(ISuperView view)
    {
        return null;
    }

    @Override public RecyclerView.LayoutManager onCreateLayoutManager()
    {
        return null;
    }

    @Override public BaseMultipleTypeListAdapter<GankReading> onCreateAdapter()
    {
        return null;
    }


    @Override
    protected void onFragmentListInitOk(View savedInstanceState, Bundle bundle, RecyclerView rvList, RefresherHelper refresherHelper, AbsSuperPresenter presenter, BaseMultipleTypeListAdapter<GankReading> adapter, BaseViewHolderBinder<GankReading> vhBinder)
    {

    }

    @Override public void onLoadOk(ArrayList<GankReading> data)
    {

    }

    @Override public void onRefresh()
    {

    }
}
