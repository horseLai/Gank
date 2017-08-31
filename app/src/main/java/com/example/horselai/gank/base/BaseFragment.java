package com.example.horselai.gank.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.horselai.gank.http.loader.ImageLoader;
import com.example.horselai.gank.http.service.AsyncService;

/**
 * Created by laixiaolong on 2016/11/3.
 */

public abstract class BaseFragment extends Fragment
{
    protected Context mContext;
    private Bundle mBundle;
    protected FragmentCallback mCallback;

    public BaseFragment setCallback(FragmentCallback callback)
    {
        this.mCallback = callback;
        return this;
    }

    @Override public void onPause()
    {
        super.onPause();
        ImageLoader.getImageLoader().getThreadPoolHandler().clearTaskQueue();
        AsyncService.getService().getPoolHandler().clearTaskQueue();
    }


    public BaseFragment setArgs(Bundle args)
    {
        setArguments(args);
        return this;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
        initBundle(mBundle);
        mContext = getActivity();
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(provideLayoutId(), container, false);
    }


    protected abstract int provideLayoutId();

    protected void initBundle(Bundle mBundle)
    {

    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initView(view, savedInstanceState);
    }

    protected abstract void initView(View rootView, Bundle savedInstanceState);

    @Override public void onDestroy()
    {
        super.onDestroy();
        mBundle = null;
        //清空请求队列
        ImageLoader.getImageLoader().getThreadPoolHandler().cancelAndClearTaskQueue();
        AsyncService.getService().getPoolHandler().cancelAndClearTaskQueue();
    }


}
