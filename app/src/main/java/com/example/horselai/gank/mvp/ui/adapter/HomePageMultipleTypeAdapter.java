package com.example.horselai.gank.mvp.ui.adapter;

import android.content.Context;

import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.bean.home.CommHomeItem;

/**
 * Created by laixiaolong on 2017/4/10.
 */

public class HomePageMultipleTypeAdapter extends BaseMultipleTypeListAdapter<CommHomeItem>
{


    public HomePageMultipleTypeAdapter(Context context)
    {
        super(context);
    }

    @Override public boolean hasFooterView()
    {
        return false;
    }


}
