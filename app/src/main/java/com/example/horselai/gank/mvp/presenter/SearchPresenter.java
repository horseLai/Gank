package com.example.horselai.gank.mvp.presenter;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.http.service.AsyncService;
import com.example.horselai.gank.mvp.model.GankFetcher;
import com.example.horselai.gank.mvp.ui.iView.ISearchView;
import com.example.horselai.gank.util.Utils;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/8/2.
 */

public class SearchPresenter
{
    private final ISearchView mView;
    static final int SUCCESS = 0;
    static final int FAIL = 1;
    private final Handler mHandler = new Handler(Looper.getMainLooper())
    {
        @Override public void handleMessage(Message msg)
        {
            Log.i(TAG, "handleMessage: " + msg.obj);
            switch (msg.what) {
                case SUCCESS:
                    mView.onSearchOk((ArrayList<GankNews>) msg.obj);
                    break;
                case FAIL:
                    mView.onSearchFailed((Exception) msg.obj);
                    break;
            }
        }
    };

    private static final String TAG = "SearchPresenter";

    public SearchPresenter(ISearchView view)
    {
        this.mView = view;
    }


    public boolean checkIsOnline()
    {
        if (App.isOnline()) {
            return true;
        } else {
            mHandler.obtainMessage(FAIL, new Exception("网络未连接_〆(´Д｀ )")).sendToTarget();
            return false;
        }
    }

    public void doSearch(final Object... params)
    {
        if (!checkIsOnline()) return;
        AsyncService.getService().addRequestTask(new Runnable()
        {
            @Override public void run()
            {
                final ArrayMap<String, Object> arrayMap = GankFetcher.getFetcher().search((String) params[0], BaseMultipleTypeListAdapter.ItemType.TYPE_LINEAR);

                if (Utils.isEmpty(arrayMap)) {

                    Log.i(TAG, "run:  数据加载失败_〆(´Д｀ ) ");
                    mHandler.obtainMessage(FAIL, new Exception("数据加载失败_〆(´Д｀ )")).sendToTarget();
                    return;
                }
                mHandler.obtainMessage(SUCCESS, arrayMap.get(GankFetcher.SEARCH_DATA)).sendToTarget();
            }
        });

    }

}
