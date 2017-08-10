package com.example.horselai.gank.base;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.app.App;
import com.example.horselai.gank.util.Utils;

/**
 * Created by horseLai on 2017/8/2.
 */

public abstract class BaseWebViewActivity extends AppbarActivity implements View.OnClickListener
{

    private WebView mWebView;
    private Snackbar mSnackbar;
    private TextView mBarTitle;
    private String mCurUrl;
    //private int mLastPosition = 0;
    private NestedScrollView mScrollParent;

    @Override protected abstract boolean homeAsUpEnable();

    @Override protected View.OnClickListener onToolbarClick()
    {
        return null;
    }


    /**
     * <p>
     * 所提供的layoutId 必须包含<code>R.id.toolbar</code>
     * 和<code>R.id.app_bar_layout</code>，分别代表Toolbar
     * 和AppBarLayout
     * <p>
     * 必须包含id为web_view 的WebView
     * <p>
     * </p>
     *
     * @return
     */
    @Override public abstract int provideContentViewId();


    public NestedScrollView getScrollParent()
    {
        return mScrollParent;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getToolbar().setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        mCurUrl = parseIntentResForUrl();
        setupWebView();
        initWidget();
    }

    protected abstract String parseIntentResForUrl();

    public WebView getWebView()
    {
        return mWebView;
    }

    private void initWidget()
    {
        //***************************************************************

        mSnackbar = Snackbar.make(mWebView, "", Snackbar.LENGTH_INDEFINITE);
        mSnackbar.getView().setBackgroundColor(getResources().getColor(R.color.grayBlue));

        //***************************************************************
        mBarTitle = getBarTitleView();
        mBarTitle.setTextColor(Color.WHITE);

        mScrollParent = (NestedScrollView) findViewById(R.id.nest_scroll_parent);

    }


    private void setupWebView()
    {
        mWebView = (WebView) findViewById(R.id.web_view);
        final WebSettings settings = mWebView.getSettings();
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setAppCachePath(App.getAppCacheDir().getPath());
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        //settings.setLoadsImagesAutomatically(true);

        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDisplayZoomControls(false);
        mWebView.requestFocusFromTouch();
        //settings.setTextZoom(2);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);

        mWebView.setLayerType(View.LAYER_TYPE_NONE, null);

        setupWebChromeClient();
        setupWebClient();

        if (!TextUtils.isEmpty(mCurUrl)) {
            mWebView.loadUrl(mCurUrl);
        }
    }

    protected void setupWebChromeClient()
    {
        mWebView.setWebChromeClient(new WebChromeClient()
        {
            @Override public void onProgressChanged(WebView view, int newProgress)
            {
                mSnackbar.setText("loading... " + newProgress + "%");
            }

            @Override public void onReceivedTitle(WebView view, String title)
            {
                mBarTitle.setText(title);
            }

            @Override public View getVideoLoadingProgressView()
            {
                //在这里自定义LoadingProgressView
                return provideVideoLoadingProgressView();
            }

            @Override public void onShowCustomView(View viewToShow, CustomViewCallback callback)
            {
                //视频全屏播放
                doShowCustomView(viewToShow, callback);
            }

            @Override public void onHideCustomView()
            {
                doHideCustomView();
            }
        });
    }

    protected abstract View provideVideoLoadingProgressView();

    protected abstract void doHideCustomView();

    protected abstract void doShowCustomView(View view, WebChromeClient.CustomViewCallback callback);

    public void showSnackBar(String msg)
    {
        mSnackbar.setText(msg);
        mSnackbar.show();
    }

    private void setupWebClient()
    {
        mWebView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
            {
                return false;
            }

            @Override public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);
                if (!mSnackbar.isShown()) {
                    mSnackbar.show();
                }
            }


            @Override public void onPageFinished(WebView view, String url)
            {
                mCurUrl = url;
                mSnackbar.dismiss();
            }

            @Override public void onScaleChanged(WebView view, float oldScale, float newScale)
            {
                // mWebView.setInitialScale((int) newScale * 100);
            }

            @Override
            public void onReceivedLoginRequest(WebView view, String realm, String account, String args)
            {

            }

        });
    }


    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.web_menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_refresh: {
                if (mWebView != null) mWebView.reload();
                break;
            }
            case R.id.menu_share: {
                final String title = mBarTitle.getText().toString();
                Utils.shareTextPlain(this, "来自‘干货集中营’的分享：" + title + "\r\n" + mCurUrl, title);
                break;
            }
            case R.id.web_menu_main: {
                showPopupMenu();
                break;
            }
        }
        return true;
    }

    private void showPopupMenu()
    {
        Utils.popupMenu(getToolbar(), R.menu.web_setting, false, new PopupMenu.OnMenuItemClickListener()
        {
            @Override public boolean onMenuItemClick(MenuItem item)
            {
                return onPopupMenuItemClick(item);
            }
        });
    }

    protected abstract boolean onPopupMenuItemClick(MenuItem item);


    @Override protected void onPostResume()
    {
        super.onPostResume();
        if (mWebView != null) mWebView.onResume();
    }

    @Override protected void onPause()
    {
        super.onPause();
        if (mWebView != null) mWebView.onPause();
    }

    @Override protected void onDestroy()
    {
        super.onDestroy();
        if (mWebView != null) mWebView.destroy();
    }


    @Override public void onBackPressed()
    {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }


}
