package com.example.horselai.gank.mvp.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.app.App;
import com.example.horselai.gank.base.BaseWebViewActivity;
import com.example.horselai.gank.bean.GankBeauty;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.http.loader.ImageLoader;
import com.example.horselai.gank.mvp.presenter.ImagePresenter;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.service.ScanHistoryService;
import com.example.horselai.gank.util.FileManager;

public class WebActivity extends BaseWebViewActivity implements ISuperView<String>
{

    private GankNews mData;
    private static final String TAG = "WebActivity";
    private ImagePresenter mPresenter;
    public static final String KEY_DATA = "data";
    private ImageView mIvBarImage;
    private NestedScrollView mScrollParent;
    private FrameLayout mWebViewContainer;
    private View mFullView;
    private WebChromeClient.CustomViewCallback mFullCallback;
    private CollapsingToolbarLayout mCollapseBar;


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
        return R.layout.activity_web;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mPresenter = new ImagePresenter(this);

        getToolbar().setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        initWidget();
    }

    @Override protected String parseIntentResForUrl()
    {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mData = (GankNews) extras.getSerializable(KEY_DATA);
            if (mData != null) return mData.url;
        }
        return null;
    }


    private void initWidget()
    {
        mScrollParent = getScrollParent();
        //***************************************************************
        FloatingActionButton fabFullUp = (FloatingActionButton) findViewById(R.id.fab_full_up);
        FloatingActionButton fabDownload = (FloatingActionButton) findViewById(R.id.fab_download);

        fabFullUp.setOnClickListener(this);
        fabDownload.setOnClickListener(this);


        //***************************************************************
        mWebViewContainer = (FrameLayout) findViewById(R.id.web_view_container);
        mCollapseBar = (CollapsingToolbarLayout) findViewById(R.id.bar_collapse);


        //***************************************************************
        mIvBarImage = (ImageView) findViewById(R.id.bar_img);
        mIvBarImage.setOnClickListener(this);


        final CollapsingToolbarLayout barCollapse = (CollapsingToolbarLayout) findViewById(R.id.bar_collapse);
        barCollapse.setTitleEnabled(false);


        if (TextUtils.isEmpty(mData.image)) {
            mIvBarImage.setVisibility(View.GONE);
            getToolbar().setBackgroundColor(getResources().getColor(R.color.grayBlue));
            return;
        }
        ImageLoader.getImageLoader().displayImageAsync(mIvBarImage, mData.image, true, true);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 处理全屏播放
    ///////////////////////////////////////////////////////////////////////////

    @Override protected View provideVideoLoadingProgressView()
    {
        return null;
    }

    @Override protected void doHideCustomView()
    {
        getWebView().setVisibility(View.VISIBLE);
        if (mFullView == null) return;

        mFullView.setVisibility(View.GONE);
        mWebViewContainer.removeView(mFullView);
        mFullCallback.onCustomViewHidden();
        mFullView = null;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


    }

    @Override
    protected void doShowCustomView(View viewToShow, WebChromeClient.CustomViewCallback callback)
    {
        App.toastShort("doShowCustomView");
        if (mFullView != null) {
            callback.onCustomViewHidden();
            return;
        }
        this.mFullView = viewToShow;
        this.mFullCallback = callback;

        if (viewToShow != null) {
            getWebView().setVisibility(View.GONE);
            final int[] wh = getScreenWh();
            mWebViewContainer.addView(viewToShow, wh[1], wh[0]);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }


    @Override public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.fab_download: {
                if (mData.image != null) {

                    final boolean exists = FileManager.getInstance().exists(mPresenter.getPicPath(mData.image));
                    if (exists) {
                        App.toastLong("图片已存在 (●'◡'●)");
                        break;
                    }
                    mPresenter.update(mData.image);
                    App.toastLong("马上就下载 (●'◡'●)");
                    break;
                }
                App.toastLong("这是张假图片(╯‵□′)╯︵┻━┻");
                break;
            }
            case R.id.fab_full_up: {
                mScrollParent.fullScroll(View.FOCUS_UP);
                //保持标题滚动
                getBarTitleView().requestFocus();
                break;
            }
            case R.id.bar_img: {
                if (TextUtils.isEmpty(mData.image)) {
                    App.toastShort("图片链接异常！⊙﹏⊙∥");
                    return;
                }
                final Intent intent = new Intent(this, ImageActivity.class);
                intent.putExtra("ic_beauty_teal", new GankBeauty("", mData.image, ""));
                startActivity(intent);
                break;
            }

        }
    }


    @Override public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        getAppBarLayout().setExpanded(false);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE: {
                getToolbar().setVisibility(View.GONE);
                mIvBarImage.setVisibility(View.GONE);
                mCollapseBar.setVisibility(View.GONE);
                getAppBarLayout().setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            }
            case Configuration.ORIENTATION_PORTRAIT: {
                getAppBarLayout().setEnabled(true);
                getAppBarLayout().setVisibility(View.VISIBLE);
                mCollapseBar.setVisibility(View.VISIBLE);
                mIvBarImage.setVisibility(View.VISIBLE);
                getToolbar().setVisibility(View.VISIBLE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
            }
        }

    }


    @Override protected boolean onPopupMenuItemClick(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_external_browser: {
                GankUI.startExternalBrowser(this, mData.url);
                break;
            }
            case R.id.menu_clear_history: {
                getWebView().clearHistory();
                App.toastShort("已清空！");
                break;
            }
            case R.id.menu_save: {
                startServiceToSave();
                break;
            }
        }
        return true;
    }


    private void startServiceToSave()
    {
        final Intent intent = new Intent(this, ScanHistoryService.class);
        intent.setAction(ScanHistoryService.ACTION_SAVE);
        intent.putExtra(ScanHistoryService.ADD_ONE, mData);
        startService(intent);
    }

    @Override public void onLoadOk(String data)
    {
        App.toastLong("下载成功（￣︶￣）↗　");
    }

    @Override public void onLoadFailed(Exception e)
    {
        App.toastLong("下载失败(＃°Д°)：" + e.getMessage());
    }
}
