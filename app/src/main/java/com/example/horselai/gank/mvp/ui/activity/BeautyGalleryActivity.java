package com.example.horselai.gank.mvp.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.BaseAppbarListActivity;
import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.GankBeauty;
import com.example.horselai.gank.http.loader.BitmapManager;
import com.example.horselai.gank.mvp.presenter.GalleryPresenter;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.mvp.ui.adapter.BeautyGalleryAdapter;
import com.example.horselai.gank.mvp.ui.adapter.binder.BeautyGalleryVhBinder;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.RefresherHelper;
import com.example.horselai.gank.util.Utils;

import java.util.ArrayList;

public class BeautyGalleryActivity extends BaseAppbarListActivity<GankBeauty>
{

    private int mPageNum = 0;
    private static final int ITEM_SIZE = 20;

    private BaseMultipleTypeListAdapter<GankBeauty> mAdapter;
    private RefresherHelper mRefresherHelper;
    private RecyclerView mRvList;
    private ImageView mImageBg;

    @Override protected boolean homeAsUpEnable()
    {
        return true;
    }

    @Override protected View.OnClickListener onToolbarClick()
    {
        return null;
    }

    @Override public int provideContentViewId()
    {
        return R.layout.activity_beauty_gallary;
    }

    @NonNull @Override protected View provideRootLayoutForRefresher()
    {
        return findViewById(R.id.refresher_root_layout);
    }

    @Override protected BaseViewHolderBinder<GankBeauty> onCreateViewHolderBinder()
    {
        return new BeautyGalleryVhBinder(this);
    }

    @Override
    protected void onActivityListInitOk(View view, Bundle savedInstanceState, RecyclerView mRvList, RefresherHelper mRefresherHelper, AbsSuperPresenter mPresenter, BaseMultipleTypeListAdapter adapter, BaseViewHolderBinder binder)
    {

        this.mRefresherHelper = mRefresherHelper;
        this.mRvList = mRvList;
        this.mAdapter = adapter;

        getToolbar().setLogo(R.drawable.logo_work);
        getToolbar().setLongClickable(true);
        getToolbar().setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override public boolean onLongClick(View v)
            {
                Utils.alertDialog(BeautyGalleryActivity.this, getResources().getString(R.string.app_name), getResources().getString(R.string.logo_desc), R.drawable.logo_work);
                return true;
            }
        });

        mImageBg = (ImageView) findViewById(R.id.iv_bg);
        getBarTitleView().setText("   妹纸");


        dealWithBinderEvents(binder);
        addListListener((StaggeredGridLayoutManager) mRvList.getLayoutManager());

        updateData(ITEM_SIZE, ++mPageNum);

    }


    private void dealWithBinderEvents(BaseViewHolderBinder vhBinder)
    {
        vhBinder.setOnItemClickListener(new BaseViewHolderBinder.OnItemClickListener()
        {
            @Override public void onItemClicked(View v, int position)
            {
                // App.toastShort("position:" + position);
                GankUI.startImageActivity(BeautyGalleryActivity.this, mAdapter.getDataList().get(position));
            }
        });
    }


    private void addListListener(final StaggeredGridLayoutManager layoutManager)
    {
        mRvList.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                if (!mRefresherHelper.isRefresherShow())
                    mAdapter.updateListScrollState(layoutManager, newState);
            }

            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                loadMore(dy, layoutManager);
            }
        });
    }

    @Override public AbsSuperPresenter onCreatePresenter(ISuperView view)
    {
        return new GalleryPresenter(view);
    }

    @Override public RecyclerView.LayoutManager onCreateLayoutManager()
    {
        return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    }

    private void loadMore(int dy, StaggeredGridLayoutManager layoutManager)
    {
        final int[] positions = layoutManager.findLastVisibleItemPositions(new int[2]);
        final boolean needLoad = positions[1] >= mAdapter.getItemCount() - 2;

        if (dy > 20 && needLoad && !mRefresherHelper.isRefreshing()) {

            updateData(ITEM_SIZE, ++mPageNum);
        }
    }


    @Override public BaseMultipleTypeListAdapter<GankBeauty> onCreateAdapter()
    {
        return new BeautyGalleryAdapter(this);
    }

    @Override public void onLoadOk(ArrayList<GankBeauty> data)
    {
        showSnackBar(getResources().getString(R.string.tvLoadSuccess));

        if (mToClear) {
            mAdapter.removeAllItems();
            mToClear = false;
        }
        mAdapter.insertItemsIntoFootPos(data);
        mRefresherHelper.stopRefreshing();
    }


    boolean mToClear = false;

    @Override public void onRefresh()
    {
        mToClear = true;
        updateData(ITEM_SIZE, ++mPageNum);
    }

    @Override public void onLoadFailed(Exception e)
    {
        showSnackBar(e.getMessage());

        mRefresherHelper.stopRefreshing();
    }

    private static final String TAG = "BeautyGalleryActivity";

    @Override protected void onDestroy()
    {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        BitmapManager.getInstance().releaseImage(mImageBg);
        gc();
    }


    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_search: {
                showSearchPopup();
                break;
            }
            case R.id.menu_menu: {
                showMenu(getToolbar());
                break;
            }
        }
        return true;
    }


    private void showMenu(View anchorView)
    {
        Utils.popupMenu(anchorView, R.menu.menu_main_more, true, new PopupMenu.OnMenuItemClickListener()
        {
            @Override public boolean onMenuItemClick(MenuItem item)
            {
                //App.toastShort("" + item.getTitle());
                final String msg = getResources().getString(R.string.share_text);
                Utils.shareTextPlain(BeautyGalleryActivity.this, msg, msg);
                return true;
            }
        });
    }

}
