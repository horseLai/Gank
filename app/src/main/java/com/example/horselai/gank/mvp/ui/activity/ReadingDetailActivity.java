package com.example.horselai.gank.mvp.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.app.App;
import com.example.horselai.gank.bean.ZhiHuDaily;
import com.example.horselai.gank.mvp.model.ReadingModel;

/**
 * Created by horseLai on 2017/8/8.
 */

public class ReadingDetailActivity extends AppCompatActivity
{

    private static final String TAG = "ReadingDetailActivity";
    private WebView mWebview;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reading_detail);


        initWidgets();


        requestData();

    }

    private void requestData()
    {
        new AsyncTask<String, Void, ZhiHuDaily>()
        {
            @Override protected ZhiHuDaily doInBackground(String... params)
            {
                final ReadingModel model = new ReadingModel();
                final String url = "http://daily.zhihu.com/story/9563136?utm_medium=website&utm_source=gank.io%2Fxiandu";

                return model.parseZhiHuDaily(url);
            }

            @Override protected void onPostExecute(ZhiHuDaily data)
            {
                Log.i(TAG, "onPostExecute: " + data.htmlSnap);
                mWebview.loadDataWithBaseURL("http://daily.zhihu.com", "<html>" + data.htmlSnap + "</html>", "text/html", "utf-8", null);
            }
        }.execute();

    }

    private void initWidgets()
    {
        mWebview = (WebView) findViewById(R.id.web_view);
        final WebSettings settings = mWebview.getSettings();
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
        mWebview.requestFocusFromTouch();
        //settings.setTextZoom(2);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);

        mWebview.setLayerType(View.LAYER_TYPE_NONE, null);

    }


    @Override protected void onPause()
    {
        super.onPause();
        mWebview.onPause();
    }

    @Override protected void onResume()
    {
        super.onResume();
        mWebview.onResume();
    }


    @Override protected void onDestroy()
    {
        super.onDestroy();
        mWebview.destroy();
    }
}
