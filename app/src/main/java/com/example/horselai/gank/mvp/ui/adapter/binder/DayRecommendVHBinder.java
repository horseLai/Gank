package com.example.horselai.gank.mvp.ui.adapter.binder;

import android.content.Context;

import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.bean.home.CommHomeItem;

/**
 * Created by horseLai on 2017/7/31.
 */

public class DayRecommendVHBinder extends CommMultipleVHBinder<CommHomeItem<GankNews>>
{
    public DayRecommendVHBinder(Context mContext)
    {
        super(mContext);
    }


    @Override public void release()
    {
    }
}
