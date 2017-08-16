package com.example.horselai.gank.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.example.horselai.gank.http.loader.ImageLoader;
import com.example.horselai.gank.http.service.AsyncService;
import com.example.horselai.gank.util.StatusBarCompat;

/**
 * 包含页面交互的基本方法
 */
public abstract class BaseActivity extends AppCompatActivity
{

    @Override protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(provideContentViewId());

        StatusBarCompat.transparentStatusBar(this);

    }


    public abstract int provideContentViewId();

    @Override protected void onPause()
    {
        //清空请求队列
        ImageLoader.getImageLoader().getThreadPoolHandler().cancelAndClearTaskQueue();
        AsyncService.getService().getPoolHandler().cancelAndClearTaskQueue();
        super.onPause();
    }

    /**
     * 获取屏幕像素宽高
     *
     * @return wh[0]: widthPixels,  wh[1]: heightPixels
     */
    public int[] getScreenWh()
    {
        int[] wh = new int[2];
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        wh[0] = displayMetrics.widthPixels;
        wh[1] = displayMetrics.heightPixels;
        return wh;
    }


    /**
     * 主动申请 gc
     */
    protected void gc()
    {
        Runtime.getRuntime().gc();
    }
}
