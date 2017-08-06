package com.example.horselai.gank.mvp.ui.adapter;

import android.content.Context;

import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.bean.GankNews;

/**
 * Created by horseLai on 2017/7/28.
 */

public class HistoryListAdapter extends BaseMultipleTypeListAdapter<GankNews>
{


    public HistoryListAdapter(Context context)
    {
        super(context);
    }

    @Override public boolean hasFooterView()
    {
        return false;
    }

}
