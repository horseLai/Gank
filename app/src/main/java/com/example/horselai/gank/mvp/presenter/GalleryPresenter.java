package com.example.horselai.gank.mvp.presenter;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.bean.GankBeauty;
import com.example.horselai.gank.http.api.GankApi;
import com.example.horselai.gank.http.service.AsyncService;
import com.example.horselai.gank.mvp.model.GankFetcher;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.Utils;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/7/24.
 */

public class GalleryPresenter extends AbsSuperPresenter<ArrayList<GankBeauty>>
{


    public GalleryPresenter(ISuperView mView)
    {
        super(mView);
    }

    @Override public void update(Object... params)
    {
        if (!App.isOnline()) {
            getHandler().obtainMessage(FAIL, new Exception("网络异常!_〆(´Д｀ )")).sendToTarget();
            return;
        }
        doUpdate((int) params[0], (int) params[1]);
    }


    private void doUpdate(final int itemNum, final int pageNum)
    {
        AsyncService.getService().addRequestTask(new Runnable()
        {
            @Override public void run()
            {
                final ArrayList<GankBeauty> beauties = GankFetcher.getFetcher().fetchGankBeauty(GankApi.encodeNormalApiUrl(GankApi.BEAUTY, itemNum, pageNum), BaseMultipleTypeListAdapter.ItemType.TYPE_GRID);

                if (Utils.isEmpty(beauties)) {

                    final Exception e = App.isOnline() ? new Exception("没有更多到数据(ง •_•)ง") : new Exception("网络异常!(ง •_•)ง");

                    getHandler().obtainMessage(FAIL, e).sendToTarget();
                } else {
                    getHandler().obtainMessage(SUCCESS, beauties).sendToTarget();
                }
            }
        });
    }


}
