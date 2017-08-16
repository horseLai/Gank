package com.example.horselai.gank.mvp.ui.adapter.binder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.http.loader.ImageLoader;
import com.example.horselai.gank.mvp.ui.adapter.viewholder.OtherViewHolder;

/**
 * Created by horseLai on 2017/7/24.
 */

public class CatTypeOtherVhBinder extends BaseViewHolderBinder<GankNews>
{
    public CatTypeOtherVhBinder(Context mContext)
    {
        super(mContext);
    }

    private BinderClickEventListener mEventListener = new BinderClickEventListener();

    @Override
    public RecyclerView.ViewHolder onCreateOrdinaryVieHolder(ViewGroup parent, int viewType)
    {
        final View view = mLayoutInflater.inflate(R.layout.type_other_item, parent, false);
        final OtherViewHolder holder = new OtherViewHolder(view);

        holder.ivPlay.setOnClickListener(mEventListener);
        holder.btnMenuMore.setOnClickListener(mEventListener);
        return holder;
    }

    @Override
    public void onBindOrdinaryViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, GankNews itemData, int position)
    {
        super.onBindOrdinaryViewHolder(holder, mLoadImageNow, itemData, position);

        OtherViewHolder vh = (OtherViewHolder) holder;
        vh.tvDesc.setText(itemData.desc);

        //作为点击事件数据源
        vh.ivPlay.setTag(itemData);
        vh.btnMenuMore.setTag(itemData);

        if (!TextUtils.isEmpty(itemData.image) && mLoadImageNow)
            ImageLoader.getImageLoader().displayImageAsync(vh.ivImage, itemData.image + "?imageView2/0/w/320", false, 320, 400);
    }


    @Override public void release()
    {
    }

}
