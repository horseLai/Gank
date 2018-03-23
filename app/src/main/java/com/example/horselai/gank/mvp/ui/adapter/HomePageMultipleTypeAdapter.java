package com.example.horselai.gank.mvp.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.bean.home.CommHomeItem;
import com.example.horselai.gank.mvp.ui.adapter.binder.HomeViewHolderBinder;

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

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder)
    {
        if (holder instanceof HomeViewHolderBinder.SlideRotationVH){
            HomeViewHolderBinder binder = (HomeViewHolderBinder) this.mViewHolderBinder;
            binder.setupSlideRotation();
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder)
    {
        if (holder instanceof HomeViewHolderBinder.SlideRotationVH){
            HomeViewHolderBinder binder = (HomeViewHolderBinder) this.mViewHolderBinder;
            binder.stopSlideRotation();
        }
    }
}
