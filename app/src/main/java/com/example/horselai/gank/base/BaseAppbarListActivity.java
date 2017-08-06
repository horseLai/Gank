package com.example.horselai.gank.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.horselai.gank.util.Utils;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/7/24.
 * <p>
 * 这个类集成了appbar以及刷新列表的功能，使用时，注意布局中一定要包含
 * <code>provideContentViewId()</code>注释中所提到的控件id，否则会崩溃
 * ，这是必然的
 */

public abstract class BaseAppbarListActivity<T extends BeanEntry> extends AppbarSearchActivity implements SwipeRefreshLayout.OnRefreshListener, ISuperView<ArrayList<T>>
{

    private Snackbar mSnackBar;

    @Override protected abstract boolean homeAsUpEnable();

    @Override protected abstract View.OnClickListener onToolbarClick();

    /**
     * <p>
     * 所提供的layoutId 必须包含
     * <code>R.id.toolbar</code>
     * 和
     * <code>R.id.app_bar_layout</code>，
     * 分别代表Toolbar和AppBarLayout
     * </p>
     * <p>
     * 以及<code>R.id.fragment_base_list</code>
     * 和
     * <code>R.id.fragment_base_refresher</code>，
     * 分别代表RecyclerView和SwipeRefreshLayout
     * </p>
     * </p>
     *
     * @return
     */
    @Override public abstract int provideContentViewId();


    private AbsSuperPresenter mPresenter;
    private static final String TAG = "BaseListFragment";

    private RecyclerView mRvList;
    private RefresherHelper mRefresherHelper;


    @Override public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mPresenter = onCreatePresenter(this);


        initView(provideRootLayoutForRefresher(), savedInstanceState);
    }

    /**
     * @return 返回一个根部布局容器对象（例如：CoordinatorLayout）给SwipeRefreshLayout， 不能为null,， 否则出错
     */
    @NonNull protected abstract View provideRootLayoutForRefresher();


    private void initView(View rootView, Bundle savedInstanceState)
    {
        mRefresherHelper = new RefresherHelper(rootView, R.id.fragment_base_refresher, this);
        mRvList = (RecyclerView) rootView.findViewById(R.id.fragment_base_list);

        mRvList.setLayoutManager(onCreateLayoutManager());
        final BaseMultipleTypeListAdapter<T> adapter = onCreateAdapter();

        BaseViewHolderBinder<T> mBinder = onCreateViewHolderBinder();
        adapter.setViewHolderBinder(mBinder);
        mRvList.setAdapter(adapter);

        mSnackBar = Snackbar.make(mRvList, "", Snackbar.LENGTH_SHORT);
        mSnackBar.getView().setBackgroundColor(getResources().getColor(R.color.grayBlue));

        onActivityListInitOk(rootView, savedInstanceState, mRvList, mRefresherHelper, mPresenter, adapter, mBinder);


    }

    public void showSnackBar(String msg)
    {
        mSnackBar.setText(msg);
        mSnackBar.show();
    }


    protected abstract BaseViewHolderBinder<T> onCreateViewHolderBinder();


    protected abstract void onActivityListInitOk(View rootView, Bundle savedInstanceState, RecyclerView rvList, RefresherHelper refresherHelper, AbsSuperPresenter presenter, BaseMultipleTypeListAdapter<T> adapter, BaseViewHolderBinder<T> binder);


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


    /**
     * @param data
     */
    @Override public abstract void onLoadOk(ArrayList<T> data);


    @Override public abstract void onRefresh();


    @Override public void onLoadFailed(Exception e)
    {
        mRefresherHelper.stopRefreshing();
        Utils.showDefaultSnackBar(mRefresherHelper.getRefresher(), e.getMessage());
    }


}
