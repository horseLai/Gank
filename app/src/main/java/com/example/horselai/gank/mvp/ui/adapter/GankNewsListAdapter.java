package com.example.horselai.gank.mvp.ui.adapter;

import android.content.Context;

import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.bean.GankNews;

/**
 * Created by horseLai on 2017/7/25.
 */

public class GankNewsListAdapter extends BaseMultipleTypeListAdapter<GankNews>
{

    private static final String TAG = "GankNewsListAdapter";

    public GankNewsListAdapter(Context context)
    {
        super(context);
    }

    @Override public boolean hasFooterView()
    {
        return false;
    }


}
