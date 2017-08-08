package com.example.horselai.gank.mvp.presenter;

import com.example.horselai.gank.bean.GankReading;
import com.example.horselai.gank.http.api.GankReadingUrls;
import com.example.horselai.gank.http.service.AsyncService;
import com.example.horselai.gank.mvp.model.ReadingModel;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.Utils;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/8/8.
 */

public class ReadingPresenter extends AbsSuperPresenter<ArrayList<GankReading>>
{
    //********************闲读*******************
    public static final int TYPE_36KR = 1;
    public static final int TYPE_BUSINESS = 2;
    public static final int TYPE_ZHIHU_DAILY = 3;
    public static final int TYPE_IDEAL_LIF = 4;
    public static final int TYPE_JIANDAN = 5;
    public static final int TYPE_ENGLAND_LIFE = 6;

    //********************博客*******************
    public static final int TYPE_MEITUAN = 7;
    public static final int TYPE_GLOWING = 8;
    public static final int TYPE_O2IO = 9;
    public static final int TYPE_REALM = 10;
    public static final int TYPE_MOGU = 11;
    public static final int TYPE_PRODUCT = 12;


    private final ReadingModel mModel;


    public ReadingPresenter(ISuperView mView)
    {
        super(mView);
        mModel = new ReadingModel();
    }

    @Override public void update(Object... params)
    {
        if (!checkIsOnline()) return;
        final int type = (int) params[0];
        final int pageNum = (int) params[1];


        AsyncService.getService().addRequestTask(new Runnable()
        {
            @Override public void run()
            {
                final ArrayList<GankReading> gankReadings = getGankReadings(type, pageNum);

                if (Utils.isEmpty(gankReadings)) {
                    getHandler().obtainMessage(FAIL, new Exception("没有加载到数据(*/ω＼*)")).sendToTarget();
                    return;
                }

                getHandler().obtainMessage(SUCCESS, gankReadings).sendToTarget();
            }
        });


    }


    private ArrayList<GankReading> getGankReadings(int type, int pageNum)
    {
        switch (type) {

            case TYPE_36KR:
                return mModel.fetchDisplayList(GankReadingUrls.URL_36KR, pageNum);

            case TYPE_BUSINESS:
                return mModel.fetchDisplayList(GankReadingUrls.URL_BUSINESS_GROUP, pageNum);

            case TYPE_ZHIHU_DAILY:
                return mModel.fetchDisplayList(GankReadingUrls.URL_ZHIHU_DAILY, pageNum);

            case TYPE_IDEAL_LIF:
                return mModel.fetchDisplayList(GankReadingUrls.URL_IDEAL_LIFE, pageNum);

            case TYPE_JIANDAN:
                return mModel.fetchDisplayList(GankReadingUrls.URL_JIANDAN, pageNum);

            case TYPE_ENGLAND_LIFE:
                return mModel.fetchDisplayList(GankReadingUrls.URL_ENGLAND_LIFE, pageNum);

            case TYPE_MEITUAN:
                return mModel.fetchDisplayList(GankReadingUrls.URL_MEITUAN, pageNum);
            case TYPE_PRODUCT:
                return mModel.fetchDisplayList(GankReadingUrls.URL_PRODUCT, pageNum);
            case TYPE_GLOWING:
                return mModel.fetchDisplayList(GankReadingUrls.URL_GLOWING, pageNum);

            case TYPE_O2IO:
                return mModel.fetchDisplayList(GankReadingUrls.URL_AUTO_IO, pageNum);

            case TYPE_REALM:
                return mModel.fetchDisplayList(GankReadingUrls.URL_REALM, pageNum);
            case TYPE_MOGU:
                return mModel.fetchDisplayList(GankReadingUrls.URL_MOGU, pageNum);

        }
        return null;
    }


}
