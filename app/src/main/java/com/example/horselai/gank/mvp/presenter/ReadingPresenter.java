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
    public static final int TYPE_CURIOSITY = 13;
    public static final int TYPE_YUEGUANG = 20;

    //********************博客*******************
    public static final int TYPE_MEITUAN = 7;
    public static final int TYPE_GLOWING = 8;
    public static final int TYPE_O2IO = 9;
    public static final int TYPE_REALM = 10;
    public static final int TYPE_MOGU = 11;
    public static final int TYPE_PRODUCT = 12;
    public static final int TYPE_LUANXIANG = 19;

    //********************装备*******************
    public static final int TYPE_DIGITAL = 14;
    public static final int TYPE_LIQI = 15;
    public static final int TYPE_APP_MINORITY = 16;
    public static final int TYPE_MAC_PLAY = 17;
    public static final int TYPE_APP_ANTI = 18;


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
        final String url = decideUrl(type);
        return mModel.fetchDisplayList(url, pageNum);
    }

    private String decideUrl(int type)
    {
        switch (type) {

            //*********************************************
            case TYPE_36KR:
                return GankReadingUrls.URL_36KR;
            case TYPE_BUSINESS:
                return GankReadingUrls.URL_BUSINESS_GROUP;
            case TYPE_ZHIHU_DAILY:
                return GankReadingUrls.URL_ZHIHU_DAILY;
            case TYPE_IDEAL_LIF:
                return GankReadingUrls.URL_IDEAL_LIFE;
            case TYPE_JIANDAN:
                return GankReadingUrls.URL_JIANDAN;
            case TYPE_ENGLAND_LIFE:
                return GankReadingUrls.URL_ENGLAND_LIFE;
            case TYPE_CURIOSITY:
                return GankReadingUrls.URL_CURIOSITY_DAILY;

            //**************************************************
            case TYPE_MEITUAN:
                return GankReadingUrls.URL_MEITUAN;
            case TYPE_PRODUCT:
                return GankReadingUrls.URL_PRODUCT;
            case TYPE_GLOWING:
                return GankReadingUrls.URL_GLOWING;
            case TYPE_O2IO:
                return GankReadingUrls.URL_AUTO_IO;
            case TYPE_REALM:
                return GankReadingUrls.URL_REALM;
            case TYPE_MOGU:
                return GankReadingUrls.URL_MOGU;
            case TYPE_LUANXIANG:
                return GankReadingUrls.URL_LUANXIANG;
            case TYPE_YUEGUANG:
                return GankReadingUrls.URL_YUEGUANG;

            //**************************************************
            case TYPE_APP_MINORITY:
                return GankReadingUrls.URL_APP_MINORITY;
            case TYPE_LIQI:
                return GankReadingUrls.URL_LIQI;
            case TYPE_DIGITAL:
                return GankReadingUrls.URL_DIGITAL;
            case TYPE_MAC_PLAY:
                return GankReadingUrls.URL_MAC_PLAY;
            case TYPE_APP_ANTI:
                return GankReadingUrls.URL_APP_ANTI;

            default:
                return GankReadingUrls.URL_36KR;
        }
    }


}
