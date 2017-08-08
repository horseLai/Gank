package com.example.horselai.gank.mvp.ui.adapter;

import android.content.Context;

import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.bean.GankReading;

/**
 * Created by horseLai on 2017/8/8.
 */

public class ReadingListAdapter extends BaseMultipleTypeListAdapter<GankReading>
{
    public ReadingListAdapter(Context context)
    {
        super(context);
    }

    @Override public boolean hasFooterView()
    {
        return false;
    }
}
