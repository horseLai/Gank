package com.example.horselai.gank.mvp.presenter;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.bean.home.CommHomeItem;
import com.example.horselai.gank.http.service.AsyncService;
import com.example.horselai.gank.mvp.model.GankFetcher;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.Utils;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/7/15.
 */

public class MainViewPresenter extends AbsSuperPresenter<ArrayList<CommHomeItem>>
{

    private final GankFetcher mFetcher;
    public static final int HOME = 0;

    public MainViewPresenter(ISuperView mView)
    {
        super(mView);
        mFetcher = GankFetcher.getFetcher();
    }

    @Override public void update(Object... params)
    {
        if (!App.isOnline()) {
            getHandler().obtainMessage(FAIL, new Exception("网络异常!_〆(´Д｀ )")).sendToTarget();
            return;
        }
        switch ((int) params[0]) {

            case HOME:
                fetchGankHome();
                break;
        }

    }

    private void fetchGankHome()
    {
        AsyncService.getService().addRequestTask(new Runnable()
        {
            @Override public void run()
            {
                final ArrayList<CommHomeItem> data = mFetcher.formHomePageGank();
                if (Utils.isEmpty(data)) {
                    getHandler().obtainMessage(FAIL, new Exception(" no data fetched !")).sendToTarget();
                    return;
                }
                getHandler().obtainMessage(SUCCESS, data).sendToTarget();
            }
        });
    }


}
