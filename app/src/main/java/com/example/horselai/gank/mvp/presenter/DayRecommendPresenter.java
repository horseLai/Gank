package com.example.horselai.gank.mvp.presenter;

import android.content.Context;
import android.util.SparseArray;

import com.example.horselai.gank.http.api.GankApi;
import com.example.horselai.gank.http.loader.DownloadListener;
import com.example.horselai.gank.http.service.AsyncService;
import com.example.horselai.gank.mvp.model.GankFetcher;
import com.example.horselai.gank.mvp.model.ImageModel;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;

import java.util.Date;

/**
 * Created by horseLai on 2017/7/30.
 */

public class DayRecommendPresenter extends AbsSuperPresenter<SparseArray<Object>>
{
    private final Context mContext;
    private final ImageModel mModel;

    public DayRecommendPresenter(ISuperView view, Context context)
    {
        super(view);
        this.mContext = context;
        mModel = new ImageModel();
    }

    @Override public void update(Object... params)
    {
        if (params != null && params.length != 0) {
            doDownload((String) params[0]);
        } else {
            doUpdate();
        }
    }

    private void doDownload(String url)
    {
        mModel.downloadImage(url, new DownloadListener()
        {

            @Override public void onSuccess(String url)
            {
                getHandler().obtainMessage(SUCCESS, null).sendToTarget();
            }

            @Override public void onFailed(String url, Exception e)
            {
                getHandler().obtainMessage(FAIL, new Exception("图片下载失败(ˉ▽ˉ；)...")).sendToTarget();
            }
        });
    }


    public void doUpdate()
    {

        AsyncService.getService().addRequestTask(new Runnable()
        {
            @Override public void run()
            {
                int offset = 0;

                SparseArray<Object> commHomeItems = null;
                String url;
                //一周内的最近一次的数据
                while ((commHomeItems == null || commHomeItems.size() == 0) && offset < 7) {
                    url = GankApi.apiByDate(new Date(System.currentTimeMillis()), offset++);
                    commHomeItems = GankFetcher.getFetcher().fetchGankByDate(url, false);

                }
                if (commHomeItems == null || commHomeItems.size() == 0) {
                    System.out.println("fetched nothing !!");
                    getHandler().obtainMessage(FAIL, new Exception("没有得到任何数据(。_。)")).sendToTarget();
                    return;
                }

                getHandler().obtainMessage(SUCCESS, commHomeItems).sendToTarget();

            }
        });

    }


}
