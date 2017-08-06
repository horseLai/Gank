package com.example.horselai.gank.mvp.presenter;

import android.util.Log;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.comm.ICallback;
import com.example.horselai.gank.http.api.GankApi;
import com.example.horselai.gank.mvp.model.GankFetcher;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/7/26.
 */

public class AllCatPresenter extends AbsSuperPresenter<GankNews>
{

    private static final String TAG = "AllCatPresenter";

    public AllCatPresenter(ISuperView mView)
    {
        super(mView);
    }

    @Override public void update(Object... params)
    {
        if (!App.isOnline()) {
            getHandler().obtainMessage(FAIL, new Exception("网络异常!_〆(´Д｀ )")).sendToTarget();
            return;
        }
        switch ((int) params[0]) {
            case GankUI.PAGE_RECOMMEND:
                fetchRecommend((int) params[1], (int) params[2]);
                break;
            case GankUI.PAGE_ANDROID:
                fetchAndroid((int) params[1], (int) params[2]);
                break;
            case GankUI.PAGE_IOS:
                fetchIOS((int) params[1], (int) params[2]);
                break;
            case GankUI.PAGE_WEB:
                fetchWeb((int) params[1], (int) params[2]);
                break;
            case GankUI.PAGE_EXPANDS:
                fetchExpands((int) params[1], (int) params[2]);
                break;
            case GankUI.PAGE_APP:
                fetchApp((int) params[1], (int) params[2]);
                break;
            case GankUI.PAGE_VIDEO:
                fetchVideo((int) params[1], (int) params[2]);
                break;
            default:
                break;
        }
    }

    private void fetchRecommend(final int itemNum, final int pageNum)
    {
        GankFetcher.getFetcher().fetchGankByTypeDefaultAsync(GankApi.RECOMMEND, itemNum, pageNum, mCallback);
    }

    private void fetchVideo(int itemNum, int pageNum)
    {
        GankFetcher.getFetcher().fetchGankByTypeDefaultAsync(GankApi.VIDEO, itemNum, pageNum, mCallback);
    }

    private void fetchApp(int itemNum, int pageNum)
    {
        GankFetcher.getFetcher().fetchGankByTypeDefaultAsync(GankApi.APP, itemNum, pageNum, mCallback);
    }

    private void fetchExpands(int itemNum, int pageNum)
    {
        GankFetcher.getFetcher().fetchGankByTypeDefaultAsync(GankApi.EXPENDS, itemNum, pageNum, mCallback);
    }

    private void fetchWeb(int itemNum, int pageNum)
    {
        GankFetcher.getFetcher().fetchGankByTypeDefaultAsync(GankApi.WEB, itemNum, pageNum, mCallback);
    }

    private void fetchIOS(int itemNum, int pageNum)
    {
        GankFetcher.getFetcher().fetchGankByTypeDefaultAsync(GankApi.IOS, itemNum, pageNum, mCallback);
    }

    private void fetchAndroid(int itemNum, int pageNum)
    {
        GankFetcher.getFetcher().fetchGankByTypeDefaultAsync(GankApi.ANDROID, itemNum, pageNum, mCallback);

    }

    private final ICallback<String, ArrayList<GankNews>> mCallback = new ICallback<String, ArrayList<GankNews>>()
    {
        @Override public void onSuccess(String url, ArrayList<GankNews> data)
        {
            getHandler().obtainMessage(SUCCESS, data).sendToTarget();
        }

        @Override public void onFail(Exception e, String url)
        {
            getHandler().obtainMessage(FAIL, e).sendToTarget();

            Log.i(TAG, "run: url >> " + url);
        }
    };


}
