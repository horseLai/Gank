package com.example.horselai.gank.mvp.ui.adapter.binder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.GankBeauty;
import com.example.horselai.gank.http.loader.ImageLoader;

/**
 * Created by horseLai on 2017/7/24.
 */

public class BeautyGalleryVhBinder extends BaseViewHolderBinder<GankBeauty>
{
    public BeautyGalleryVhBinder(Context mContext)
    {
        super(mContext);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateGridViewHolder(ViewGroup parent, int viewType)
    {
        final View view = mLayoutInflater.inflate(R.layout.gallery_grid_item, parent, false);

        view.setOnClickListener(this);
        return new VH(view);
    }


    @Override
    protected void onBindGridViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, GankBeauty itemData, int position)
    {
        super.onBindGridViewHolder(holder, mLoadImageNow, itemData, position);

        VH vh = (VH) holder;
        if (!TextUtils.isEmpty(itemData.url))
            ImageLoader.getImageLoader().displayImageAsync(vh.ivImage, itemData.url + "?imageView2/0/w/320", true, mLoadImageNow, 320, 400);

    }

    @Override public void release()
    {
    }


    public static class VH extends RecyclerView.ViewHolder
    {

        public final ImageView ivImage;

        public VH(View itemView)
        {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_grid_image);
        }
    }
}
