package com.example.horselai.gank.mvp.ui.activity.interf;

import android.webkit.JavascriptInterface;

public interface OnHtmlLoadedListener
{
    @JavascriptInterface void onLoaded(String html);
}