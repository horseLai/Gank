package com.example.horselai.gank.mvp.ui.adapter;

import android.content.Context;

import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.bean.GankBeauty;

/**
 * Created by horseLai on 2017/7/25.
 */

public class BeautyGalleryAdapter extends BaseMultipleTypeListAdapter<GankBeauty>
{

    private static final String TAG = "BeautyGalleryAdapter";

    public BeautyGalleryAdapter(Context context)
    {
        super(context);
    }

    @Override public boolean hasFooterView()
    {
        return false;
    }


}
