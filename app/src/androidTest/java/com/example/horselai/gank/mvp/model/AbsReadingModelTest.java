package com.example.horselai.gank.mvp.model;

import com.example.horselai.gank.bean.GankReading;
import com.example.horselai.gank.http.api.GankReadingUrls;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/8/7.
 */
public class AbsReadingModelTest
{

    private final BusinessModel mModel;

    public AbsReadingModelTest()
    {
        mModel = new BusinessModel();
    }

    @Test public void fetchDisplayList() throws Exception
    {
        String rootUrl = GankReadingUrls.URL_AUTO_IO;


        final ArrayList<GankReading> gankReadings = mModel.fetchDisplayList(rootUrl, 2);

        System.out.println(gankReadings);
    }

}