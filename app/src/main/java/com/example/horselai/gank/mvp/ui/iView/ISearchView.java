package com.example.horselai.gank.mvp.ui.iView;

import com.example.horselai.gank.bean.GankNews;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/8/2.
 */

public interface ISearchView
{
    void onSearchOk(ArrayList<GankNews> data);

    void onSearchFailed(Exception e);
}
