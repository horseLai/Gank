package com.example.horselai.gank.mvp.ui.adapter.binder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.GankReading;

/**
 * Created by horseLai on 2017/8/8.
 */

public class ReadingVhBinder extends BaseViewHolderBinder<GankReading>
{
    public ReadingVhBinder(Context mContext)
    {
        super(mContext);
    }


    @Override
    public RecyclerView.ViewHolder onCreateOrdinaryVieHolder(ViewGroup parent, int viewType)
    {
        return super.onCreateOrdinaryVieHolder(parent, viewType);
    }

    @Override
    public void onBindOrdinaryViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, GankReading itemData, int position)
    {
        super.onBindOrdinaryViewHolder(holder, mLoadImageNow, itemData, position);
    }


    @Override public void release()
    {

    }
}
