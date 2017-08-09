package com.example.horselai.gank.impl;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.horselai.gank.util.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by horseLai on 2017/8/9.
 */

public class MyWebView extends WebView
{

    private static final String TAG = "MyWebView";

    public ImageExtractListener mImageExtractListener;

    public void setImageExtractEntry(ImageExtractListener listener)
    {
        this.mImageExtractListener = listener;
    }

    public MyWebView(Context context)
    {
        this(context, null);
    }

    public MyWebView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        setupJsInterface();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupJsInterface();
    }

    // TODO: 2017/8/9
    private void setupJsInterface()
    {
        final WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);

        addJavascriptInterface(mImageExtractListener, "mImageExtractListener");
    }


    /**
     * 重组html文件，用于更好的显示效果
     *
     * @param htmlSnap
     * @param cssFilePath
     * @return
     */
    public String reformHtmlContent(@NonNull String htmlSnap, String cssFilePath)
    {
        // TODO: 2017/8/9 重组html
        final Document doc = Jsoup.parse(htmlSnap);
        final Elements imgEs = doc.getElementsByTag("img");
        if (!Utils.isEmpty(imgEs)) {

        }


        return null;
    }


    public interface ImageExtractListener
    {

        @JavascriptInterface void showImage(String imgUrl);
    }
}
