package com.example.horselai.gank.mvp.ui.adapter.binder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.base.BaseViewHolderBinder;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.mvp.ui.GankUI;

/**
 * Created by horseLai on 2017/7/28.
 */

public class HistoryVHBinder extends BaseViewHolderBinder<GankNews>
{
    public HistoryVHBinder(Context mContext)
    {
        super(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateOrdinaryVieHolder(ViewGroup parent, int viewType)
    {
        final View view = mLayoutInflater.inflate(R.layout.history_list_item, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new VH(view);
    }

    @Override
    public void onBindOrdinaryViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, GankNews itemData, int position)
    {

        super.onBindOrdinaryViewHolder(holder, mLoadImageNow, itemData, position);

        VH vh = (VH) holder;
        vh.tvDesc.setText(itemData.desc);
        vh.ivType.setImageResource(GankUI.getCategoryDrawableResId(itemData.type));
    }


    @Override public void release()
    {
    }

    public class VH extends RecyclerView.ViewHolder
    {

        public final ImageView ivType;
        //public final ImageView ivEdit;
        public final TextView tvDesc;

        public VH(View itemView)
        {
            super(itemView);
            //  ivEdit = (ImageView) itemView.findViewById(R.id.iv_history_edit);
            ivType = (ImageView) itemView.findViewById(R.id.iv_history_type);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_history_desc);

        }

    }

}
