package com.example.horselai.gank.mvp.ui.adapter.binder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.GankNews;

/**
 * Created by horseLai on 2017/8/2.
 */

public class SearchVHBinder extends BaseViewHolderBinder<GankNews>
{
    public SearchVHBinder(Context mContext)
    {
        super(mContext);
    }


    @Override
    public RecyclerView.ViewHolder onCreateOrdinaryVieHolder(ViewGroup parent, int viewType)
    {
        final View view = mLayoutInflater.inflate(R.layout.search_list_item, parent, false);

        view.setOnClickListener(this);
        return new VH(view);
    }

    @Override
    public void onBindOrdinaryViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, GankNews itemData, int position)
    {
        super.onBindOrdinaryViewHolder(holder, mLoadImageNow, itemData, position);

        VH vh = (VH) holder;

        vh.tvDesc.setText(itemData.desc);
        vh.tvTime.setText(itemData.publishedAt);
        vh.tvWho.setText(itemData.who);

    }

    @Override public void release()
    {

    }


    class VH extends RecyclerView.ViewHolder
    {
        private final TextView tvDesc;
        private final TextView tvWho;
        private final TextView tvTime;

        public VH(View itemView)
        {
            super(itemView);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_desc);
            tvWho = (TextView) itemView.findViewById(R.id.tv_who);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
}
