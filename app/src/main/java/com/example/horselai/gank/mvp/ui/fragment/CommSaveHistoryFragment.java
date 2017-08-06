package com.example.horselai.gank.mvp.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.example.horselai.gank.R;
import com.example.horselai.gank.app.App;
import com.example.horselai.gank.base.BaseListFragment;
import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.mvp.presenter.SaveHistoryPresenter;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.mvp.ui.adapter.HistoryListAdapter;
import com.example.horselai.gank.mvp.ui.adapter.binder.HistoryVHBinder;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.RefresherHelper;
import com.example.horselai.gank.util.Utils;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/7/24.
 */

public abstract class CommSaveHistoryFragment extends BaseListFragment<GankNews>
{


    private BaseViewHolderBinder<GankNews> mVHBinder;
    protected RefresherHelper mRefresherHelper;
    private BaseMultipleTypeListAdapter<GankNews> mAdapter;
    private boolean mFromDelete;
    private AbsSuperPresenter mPresenter;
    private ClipboardManager.OnPrimaryClipChangedListener mClipboardListener;

    @Override
    protected void onFragmentListInitOk(View rootView, Bundle savedInstanceState, RecyclerView rvList, RefresherHelper refresherHelper, AbsSuperPresenter presenter, final BaseMultipleTypeListAdapter<GankNews> adapter, BaseViewHolderBinder<GankNews> binder)
    {
        this.mPresenter = presenter;
        this.mAdapter = adapter;
        this.mRefresherHelper = refresherHelper;
        this.mVHBinder = binder;

        addClickListener(adapter, binder);

        doUpdate();
        mRefresherHelper.startRefreshing();


    }

    public abstract void doUpdate();

    private void addClickListener(final BaseMultipleTypeListAdapter<GankNews> adapter, BaseViewHolderBinder<GankNews> binder)
    {
        binder.setOnItemClickListener(new BaseViewHolderBinder.OnItemClickListener()
        {
            @Override public void onItemClicked(View v, int position)
            {
                final GankNews news = adapter.getDataList().get(position);
                GankUI.startWebActivity(mContext, news);
            }
        });

        binder.setOnItemLongClickListener(new BaseViewHolderBinder.OnItemLongClickListener()
        {
            @Override public void onItemLongClicked(View v, final int position)
            {
                Utils.popupMenu(v, R.menu.menu_popup, false, new PopupMenu.OnMenuItemClickListener()
                {
                    @Override public boolean onMenuItemClick(MenuItem item)
                    {
                        return dealWithMenuItemClick(item, position);
                    }
                });
            }
        });
    }

    /**
     * 处理
     *
     * @param item
     * @param position
     * @return
     */
    private boolean dealWithMenuItemClick(MenuItem item, int position)
    {

        final GankNews news = mAdapter.getDataList().get(position);
        switch (item.getItemId()) {
            case R.id.menu_delete: {
                mFromDelete = true;
                if (this instanceof SaveFragment)
                    mPresenter.update(SaveHistoryPresenter.DELETE_SAVE, news);
                else mPresenter.update(SaveHistoryPresenter.DELETE_HISTORY, news);
                mAdapter.removeItem(position);
                break;
            }
            case R.id.menu_copy_title: {
                doCopy(news.desc);
                break;
            }
            case R.id.menu_copy_url: {
                doCopy(news.url);
                break;
            }

        }
        return true;
    }

    private void doCopy(String content)
    {
        if (mClipboardListener == null)
            mClipboardListener = new ClipboardManager.OnPrimaryClipChangedListener()
            {
                @Override public void onPrimaryClipChanged()
                {
                    showSnackBar("已复制到剪切板ˋ( ° ▽、° ) ");
                    final ClipData.Item item = Utils.extractPrimaryFromClipboard(mContext);
                    App.toastShort(item.getText().toString());
                }
            };
        Utils.copyTextIntoClipboard(mContext, content, mClipboardListener);
    }

    @Override protected int provideLayoutId()
    {
        return R.layout.fragment_base_refresh_list;
    }

    @Override protected BaseViewHolderBinder<GankNews> onCreateViewHolderBinder()
    {
        return new HistoryVHBinder(mContext);
    }


    @Override public AbsSuperPresenter onCreatePresenter(ISuperView view)
    {
        return new SaveHistoryPresenter(view, mContext);
    }

    @Override public RecyclerView.LayoutManager onCreateLayoutManager()
    {
        return new LinearLayoutManager(mContext);
    }

    @Override public BaseMultipleTypeListAdapter<GankNews> onCreateAdapter()
    {
        return new HistoryListAdapter(mContext);
    }

    @Override public void onLoadOk(ArrayList<GankNews> data)
    {
        if (mFromDelete && data == null) {
            mFromDelete = false;
            showSnackBar("删除成功！(●'◡'●)");
            return;
        }
        mAdapter.removeAllItems();
        mAdapter.insertItemsIntoFootPos(data);
        mRefresherHelper.stopRefreshing();
        if (mIsFromFresher) {
            mIsFromFresher = false;
            showSnackBar("浏览记录加载完成！(●'◡'●)");
        }
    }

    @Override public void onLoadFailed(Exception e)
    {
        if (mIsFromFresher) {
            mIsFromFresher = false;
            super.onLoadFailed(e);
        }
        mRefresherHelper.stopRefreshing();
    }

    boolean mIsFromFresher = false;

    @Override public void onRefresh()
    {
        mIsFromFresher = true;
        doUpdate();
    }


}
