package com.example.horselai.gank.mvp.ui.adapter.binder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.GankReading;
import com.example.horselai.gank.mvp.ui.adapter.viewholder.ReadingViewHolder;

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
        final View view = mLayoutInflater.inflate(R.layout.reading_list_item, parent, false);

        view.setOnClickListener(this);
        return new ReadingViewHolder(view);
    }

    @Override
    public void onBindOrdinaryViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, GankReading itemData, int position)
    {
        super.onBindOrdinaryViewHolder(holder, mLoadImageNow, itemData, position);

        ReadingViewHolder vh = (ReadingViewHolder) holder;

        vh.tvTitle.setText(itemData.title);
        vh.tvLabel.setText(itemData.source);
        vh.tvTime.setText(itemData.time);

    }


    @Override public void release()
    {

    }
}
