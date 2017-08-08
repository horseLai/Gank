package com.example.horselai.gank.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.horselai.gank.R;
import com.example.horselai.gank.http.loader.ImageLoader;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.RefresherHelper;

import java.util.ArrayList;

/**
 * Created by laixiaolong on 2016/11/5.
 * <p>
 * 这个类集成了刷新列表的功能，使用时，注意布局中一定要包含
 * <code>provideContentViewId()</code>注释中所提到的控件id，否则会崩溃
 * ，这是必然的
 */

public abstract class BaseListFragment<T extends BeanEntry> extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, ISuperView<ArrayList<T>>
{

    private AbsSuperPresenter mPresenter;
    private static final String TAG = "BaseListFragment";

    private RecyclerView mRvList;
    private RefresherHelper mRefresherHelper;
    private BaseViewHolderBinder<T> mBinder;
    private Snackbar mSnackBar;


    @Override public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mPresenter = onCreatePresenter(this);
    }

    /**
     * <p>
     * 布局文件中需要同时含有包含id为<code>fragment_base_refresher</code>的wipeRefreshLayout和
     * id为<code>fragment_base_refresh_list</code>的RecyclerView
     * <p>
     * </p>
     *
     * @return
     */
    @Override protected abstract int provideLayoutId();


    @Override protected void initView(View rootView, Bundle savedInstanceState)
    {
        mRefresherHelper = new RefresherHelper(rootView, R.id.fragment_base_refresher, BaseListFragment.this);
        mRvList = (RecyclerView) rootView.findViewById(R.id.fragment_base_list);


        mRvList.setLayoutManager(onCreateLayoutManager());
        final BaseMultipleTypeListAdapter<T> adapter = onCreateAdapter();

        mBinder = onCreateViewHolderBinder();
        adapter.setViewHolderBinder(mBinder);
        mRvList.setAdapter(adapter);

        mSnackBar = Snackbar.make(mRvList, "", Snackbar.LENGTH_SHORT);
        mSnackBar.getView().setBackgroundColor(getResources().getColor(R.color.grayBlue));

        onFragmentListInitOk(rootView, savedInstanceState, mRvList, mRefresherHelper, mPresenter, adapter, mBinder);
    }


    public void showSnackBar(String msg)
    {
        mSnackBar.setText(msg);
        mSnackBar.show();
    }

    protected abstract BaseViewHolderBinder<T> onCreateViewHolderBinder();



    public void setOnScrollListener(RecyclerView.OnScrollListener listener)
    {
        if (listener != null) mRvList.addOnScrollListener(listener);
    }


    /**
     * 更新列表数据
     *
     * @param params 请求数据时所需要的参数
     */
    public void updateData(Object... params)
    {
        mRefresherHelper.startRefreshing();
        mPresenter.update(params);
    }


    @Override public void onPause()
    {
        super.onPause();
        ImageLoader.getImageLoader().getThreadPoolHandler().clearTaskQueue();
    }

    /**
     * @param view
     * @return
     */
    public abstract AbsSuperPresenter onCreatePresenter(ISuperView view);


    /**
     * @return
     */
    public abstract RecyclerView.LayoutManager onCreateLayoutManager();


    public abstract BaseMultipleTypeListAdapter<T> onCreateAdapter();


    protected abstract void onFragmentListInitOk(View savedInstanceState, Bundle bundle, RecyclerView rvList, RefresherHelper refresherHelper, AbsSuperPresenter presenter, BaseMultipleTypeListAdapter<T> adapter, BaseViewHolderBinder<T> vhBinder);

    /**
     * @param data
     */
    @Override public abstract void onLoadOk(ArrayList<T> data);


    @Override public abstract void onRefresh();


    @Override public void onLoadFailed(Exception e)
    {
        mRefresherHelper.stopRefreshing();
        showSnackBar(e.getMessage());
    }


    @Override public void onDestroy()
    {
        super.onDestroy();
        if (mBinder != null) mBinder.release();
    }


}
