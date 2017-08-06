package com.example.horselai.gank.mvp.ui.adapter;

import android.content.Context;

import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.bean.GankNews;

/**
 * Created by horseLai on 2017/8/2.
 */

public class SearchListAdapter extends BaseMultipleTypeListAdapter<GankNews>
{
    public SearchListAdapter(Context context)
    {
        super(context);
    }

    @Override public boolean hasFooterView()
    {
        return false;
    }


}
