package com.example.horselai.gank.mvp.ui.adapter;

import android.content.Context;

import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.bean.home.CommHomeItem;

/**
 * Created by horseLai on 2017/7/31.
 */

public class DayRecommendAdapter extends BaseMultipleTypeListAdapter<CommHomeItem<GankNews>>
{
    public DayRecommendAdapter(Context context)
    {
        super(context);
    }

    @Override public boolean hasFooterView()
    {
        return false;
    }
}
