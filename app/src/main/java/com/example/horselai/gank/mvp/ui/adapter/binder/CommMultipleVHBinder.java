package com.example.horselai.gank.mvp.ui.adapter.binder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.base.BeanEntry;
import com.example.horselai.gank.bean.GankBeauty;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.bean.home.CommHomeItem;
import com.example.horselai.gank.http.loader.ImageLoader;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.mvp.ui.adapter.viewholder.GridViewHolder;
import com.example.horselai.gank.mvp.ui.adapter.viewholder.HeaderVH;
import com.example.horselai.gank.mvp.ui.adapter.viewholder.LinearViewHolder;
import com.example.horselai.gank.mvp.ui.adapter.viewholder.OtherViewHolder;

/**
 * Created by horseLai on 2017/7/31.
 */

public abstract class CommMultipleVHBinder<T extends BeanEntry> extends BaseViewHolderBinder<T>
{
    public CommMultipleVHBinder(Context mContext)
    {
        super(mContext);
    }

    private BinderClickEventListener mClickEventListener = new BinderClickEventListener();

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType)
    {
        final View view = mLayoutInflater.inflate(R.layout.home_type_header_item, parent, false);
        final StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.setFullSpan(true);
        view.setLayoutParams(layoutParams);
        view.setOnClickListener(this);
        return new HeaderVH(view);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateLinearViewHolder(ViewGroup parent, int viewType)
    {
        final View view = mLayoutInflater.inflate(R.layout.type_linear_news_item, parent, false);

        final StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.setFullSpan(true);
        view.setLayoutParams(layoutParams);
        final LinearViewHolder vh = new LinearViewHolder(view);
        vh.tvLoadContent.setOnClickListener(mClickEventListener);
        vh.tvSeeBeauty.setOnClickListener(mClickEventListener);
        return vh;
    }


    @Override
    protected RecyclerView.ViewHolder onCreateGridViewHolder(ViewGroup parent, int viewType)
    {
        final View view = mLayoutInflater.inflate(R.layout.type_grid_news_item, parent, false);

        final StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.setFullSpan(false);
        view.setLayoutParams(layoutParams);
        final GridViewHolder vh = new GridViewHolder(view);

        vh.btnMenuMore.setOnClickListener(mClickEventListener);
        vh.tvDesc.setOnClickListener(mClickEventListener);
        return vh;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateOtherViewHolder(ViewGroup parent, int viewType)
    {
        final View view = mLayoutInflater.inflate(R.layout.type_other_item, parent, false);

        final StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.setFullSpan(true);
        view.setLayoutParams(layoutParams);
        final OtherViewHolder holder = new OtherViewHolder(view);

        holder.ivPlay.setOnClickListener(mClickEventListener);
        holder.btnMenuMore.setOnClickListener(mClickEventListener);
        return holder;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateStaggeredViewHolder(ViewGroup parent, int viewType)
    {
        View view = mLayoutInflater.inflate(R.layout.type_stagered_news_item, parent, false);
        StaggeredGridLayoutManager.LayoutParams staggeredLayoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        staggeredLayoutParams.setFullSpan(false);
        view.setLayoutParams(staggeredLayoutParams);
        final GridViewHolder vh = new GridViewHolder(view);

        vh.btnMenuMore.setOnClickListener(mClickEventListener);
        vh.tvDesc.setOnClickListener(mClickEventListener);
        return vh;
    }


    @Override
    protected void onBindGridViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, T itemData, int position)
    {

        if (itemData == null) return;

        final GridViewHolder vh = (GridViewHolder) holder;

        final GankNews data = getNews(itemData);

        vh.tvDesc.setText(data.desc);
        vh.tvSrcFrom.setText(data.source);
        vh.tvWho.setText(data.who);


        //作为点击事件数据源
        vh.btnMenuMore.setTag(data);
        vh.tvDesc.setTag(data);

        if (!TextUtils.isEmpty(data.image) && mLoadImageNow)
            ImageLoader.getImageLoader().displayImageAsync(vh.ivImage, data.image + "?imageView2/0/w/320", false, 320, 400);

    }


    @Override
    protected void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, T itemData, int position)
    {
        super.onBindHeaderViewHolder(holder, mLoadImageNow, itemData, position);
        if (itemData == null) return;

        if (itemData instanceof CommHomeItem) {
            final HeaderVH vh = (HeaderVH) holder;
            final String headerLabel = ((CommHomeItem) itemData).headerLabel;
            vh.tvDesc.setText(headerLabel);
            vh.tvDesc.setCompoundDrawablesWithIntrinsicBounds(GankUI.getCategoryDrawableResId(headerLabel), 0, 0, 0);

        }

    }

    @Override
    protected void onBindLinearViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, T itemData, int position)
    {
        super.onBindLinearViewHolder(holder, mLoadImageNow, itemData, position);
        if (itemData == null) return;
        final LinearViewHolder vh = (LinearViewHolder) holder;

        final GankNews data = getNews(itemData);

        vh.tvDesc.setText(data.desc);
        vh.tvSrcFrom.setText(data.source);
        vh.tvWho.setText(data.who);

        //作为点击事件数据源
        vh.tvSeeBeauty.setTag(new GankBeauty(null, data.image, null));
        vh.tvLoadContent.setTag(data);
        if (!TextUtils.isEmpty(data.image) && mLoadImageNow)
            ImageLoader.getImageLoader().displayImageAsync(vh.ivImage, data.image + "?imageView2/0/w/320", false, 320, 400);


    }


    private GankNews getNews(T itemData)
    {
        if (itemData instanceof CommHomeItem) {
            return (GankNews) ((CommHomeItem) itemData).data;
        } else {
            return (GankNews) itemData;
        }
    }


    @Override
    protected void onBindStaggeredViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, T itemData, int position)
    {
        super.onBindStaggeredViewHolder(holder, mLoadImageNow, itemData, position);
        if (itemData == null) return;
        final GridViewHolder vh = (GridViewHolder) holder;
        final GankNews data = getNews(itemData);
        vh.tvDesc.setText(data.desc);
        vh.tvSrcFrom.setText(data.source);
        vh.tvWho.setText(data.who);

        //作为点击事件数据源
        vh.tvDesc.setTag(data);
        vh.btnMenuMore.setTag(data);

        if (!TextUtils.isEmpty(data.image) && mLoadImageNow)
            ImageLoader.getImageLoader().displayImageAsync(vh.ivImage, data.image + "?imageView2/0/w/320", false, 320, 400);
    }

    @Override
    protected void onBindOtherViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, T itemData, int position)
    {
        super.onBindOtherViewHolder(holder, mLoadImageNow, itemData, position);

        if (itemData == null) return;
        final OtherViewHolder vh = (OtherViewHolder) holder;

        final GankNews data = getNews(itemData);

        vh.tvDesc.setText(data.desc);

        //作为点击事件数据源
        vh.btnMenuMore.setTag(data);
        vh.ivPlay.setTag(data);

        if (!TextUtils.isEmpty(data.image) && mLoadImageNow)
            ImageLoader.getImageLoader().displayImageAsync(vh.ivImage, data.image + "?imageView2/0/w/320", false, 320, 400);

    }


    @Override public  void release(){
        mClickEventListener = null;
    }
}
