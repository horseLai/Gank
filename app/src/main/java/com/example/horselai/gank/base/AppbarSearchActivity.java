package com.example.horselai.gank.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.app.App;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.http.api.GankApi;
import com.example.horselai.gank.impl.TabSelectedListener;
import com.example.horselai.gank.impl.TextWatcherAdapter;
import com.example.horselai.gank.mvp.presenter.SearchPresenter;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.mvp.ui.adapter.SearchListAdapter;
import com.example.horselai.gank.mvp.ui.adapter.binder.SearchVHBinder;
import com.example.horselai.gank.mvp.ui.iView.ISearchView;
import com.example.horselai.gank.util.RefresherHelper;
import com.example.horselai.gank.util.Utils;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/8/2.
 */

public abstract class AppbarSearchActivity extends AppbarActivity implements ISearchView
{

    private PopupWindow mSearchPopup;
    private EditText etSearchText;
    private TabLayout mTabs;
    private String mSearchUrl;
    private static final String TAG = "AppbarSearchActivity";
    private SearchPresenter mPresenter;
    public static final int SEARCH_SIZE = 20;
    private int mCurrPage = 1;
    private SearchListAdapter mAdapter;
    private boolean mFromRefresher;
    private RefresherHelper mRefresher;


    @Override public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mPresenter = new SearchPresenter(this);
    }

    public void showSearchPopup()
    {
        if (mSearchPopup == null) {
            final View popupView = getLayoutInflater().inflate(R.layout.search_popup_layout, getAppBarLayout(), false);
            initPopupView(popupView);
            mSearchPopup = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if (Build.VERSION.SDK_INT >= 21) mSearchPopup.setElevation(2);
            mSearchPopup.setOutsideTouchable(true);
            mSearchPopup.setFocusable(true);
            mSearchPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mSearchPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_FROM_FOCUSABLE);
            //mSearchPopup.setAnimationStyle(R.style.searchPopupAnimationStyle);
            initListView(popupView);
        }

        //清理状态，使得每次都跟第一次显示一样
        //mTabs.getTabAt(0).select();
        //etSearchText.setText("");
        mSearchPopup.showAtLocation(getAppBarLayout(), Gravity.NO_GRAVITY, 0, 0);
    }

    protected void initListView(View popupView)
    {
        final RecyclerView rvSearchList = (RecyclerView) popupView.findViewById(R.id.rv_search_list);

        mRefresher = new RefresherHelper(popupView.findViewById(R.id.root_layout), R.id.search_result_refresher, new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                loadMore();
            }
        });

        mAdapter = new SearchListAdapter(this);
        final SearchVHBinder vhBinder = new SearchVHBinder(this);
        mAdapter.setViewHolderBinder(vhBinder);
        rvSearchList.setLayoutManager(new LinearLayoutManager(this));
        rvSearchList.setAdapter(mAdapter);
        vhBinder.setOnItemClickListener(new BaseViewHolderBinder.OnItemClickListener()
        {
            @Override public void onItemClicked(View v, int position)
            {
                GankUI.startWebActivity(AppbarSearchActivity.this, mAdapter.getDataList().get(position));
            }
        });

        addListListener(rvSearchList);
    }

    private void loadMore()
    {
        ++mCurrPage;
        formSearchUrl();
        search();
    }


    private void addListListener(RecyclerView rvSearchList)
    {
        rvSearchList.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                checkIfLoadMore((LinearLayoutManager) recyclerView.getLayoutManager(), dy);
            }
        });
    }


    private void checkIfLoadMore(LinearLayoutManager layoutManager, int dy)
    {
        final int last = layoutManager.findLastVisibleItemPosition();
        final boolean toLoad = last >= mAdapter.getItemCount() - 2;

        if (dy > 20 && toLoad && !mRefresher.isRefreshing()) {
            App.toastShort("正在加载更多...ฅʕ•̫͡•ʔฅ ");
            loadMore();
        }
    }

    private void initPopupView(View popupView)
    {
        popupView.findViewById(R.id.ib_back_up).setOnClickListener(mClickListener);
        final ImageButton ibClear = (ImageButton) popupView.findViewById(R.id.ib_clear);
        final ImageButton ibSearch = (ImageButton) popupView.findViewById(R.id.ib_search);

        ibClear.setOnClickListener(mClickListener);
        ibSearch.setOnClickListener(mClickListener);
        ibClear.setVisibility(View.GONE);
        ibSearch.setVisibility(View.GONE);

        etSearchText = (EditText) popupView.findViewById(R.id.et_search_text);
        initEditTextListener(ibClear, ibSearch);

        initTabs(popupView);
    }

    private void initTabs(View popupView)
    {
        mTabs = (TabLayout) popupView.findViewById(R.id.search_cat_tabs);
        final String[] categories = getResources().getStringArray(R.array.categories);
        final String[] categoryTabs = getResources().getStringArray(R.array.categoryTabs);

        //添加tab选项，绑定类别到对应的tab，这样即使顺序调换了也没影响
        TabLayout.Tab tab;
        for (int i = 0; i < categoryTabs.length; i++) {
            tab = mTabs.newTab();
            tab.setText(categoryTabs[i]);
            tab.setTag(categories[i]);
            mTabs.addTab(tab, i);
        }
        mTabs.addOnTabSelectedListener(new TabSelectedListener()
        {
            @Override public void onTabSelected(TabLayout.Tab tab)
            {
                //选中tab，更新一次用于搜索的url
                mCurrPage = 1;
                formSearchUrl();
                mFromRefresher = true;
                search();
            }
        });
        mTabs.getTabAt(0).select();
        mTabs.setVisibility(View.GONE);
    }


    private void initEditTextListener(final ImageButton ibClear, final ImageButton ibSearch)
    {
        etSearchText.addTextChangedListener(new TextWatcherAdapter()
        {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (etSearchText.getText().length() > 0) {
                    ibClear.setVisibility(View.VISIBLE);
                    ibSearch.setVisibility(View.VISIBLE);
                    mTabs.setVisibility(View.VISIBLE);
                } else {
                    ibClear.setVisibility(View.GONE);
                    ibSearch.setVisibility(View.GONE);
                    mTabs.setVisibility(View.GONE);
                }
            }

            @Override public void afterTextChanged(Editable s)
            {
                // TODO: 2017/8/2  考虑是否在每次字符改变时都搜索
                //打完字后，更新一次用于搜索的url
                mCurrPage = 1;
                formSearchUrl();
            }
        });

        etSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    //用户点击回车确认后，更新一次用于搜索的url
                    mCurrPage = 1;
                    formSearchUrl();
                    search();
                }
                return true;
            }
        });
    }

    private void search()
    {
        mPresenter.doSearch(mSearchUrl);
        mRefresher.startRefreshing();
    }

    private void formSearchUrl()
    {
        mSearchUrl = GankApi.apiSearch(etSearchText.getText().toString(), (String) mTabs.getTabAt(mTabs.getSelectedTabPosition()).getTag(), SEARCH_SIZE, mCurrPage);
    }


    private View.OnClickListener mClickListener = new View.OnClickListener()
    {
        @Override public void onClick(View v)
        {
            switch (v.getId()) {
                case R.id.ib_back_up: {
                    mSearchPopup.dismiss();
                    break;
                }
                case R.id.ib_clear: {
                    etSearchText.setText("");
                    break;
                }
                case R.id.ib_search: {
                    search();
                    break;
                }
            }
        }
    };


    @Override public void onBackPressed()
    {
        if (mSearchPopup != null && mSearchPopup.isShowing()) {
            mSearchPopup.dismiss();
        } else {
            super.onBackPressed();
        }
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
                //GankUI.startSearchActivity(this);
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
                Utils.shareTextPlain(AppbarSearchActivity.this, msg, msg);
                return true;
            }
        });
    }

    @Override public void onSearchFailed(Exception e)
    {
        App.toastShort(e.getMessage());
        mRefresher.stopRefreshing();
    }

    @Override public void onSearchOk(ArrayList<GankNews> data)
    {
        if (mFromRefresher) {
            mAdapter.removeAllItems();
            mFromRefresher = false;
        }
        mAdapter.insertItemsIntoFootPos(data);
        App.toastShort("数据加载成功（￣︶￣）↗　");
        mRefresher.stopRefreshing();
    }


}
