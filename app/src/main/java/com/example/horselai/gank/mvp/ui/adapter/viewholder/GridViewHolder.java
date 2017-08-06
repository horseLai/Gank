package com.example.horselai.gank.mvp.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.horselai.gank.R;


public class GridViewHolder extends RecyclerView.ViewHolder
{
    public final TextView tvDesc;
    public final ImageView ivImage;
    public final ImageView btnMenuMore;
    public final TextView tvWho;
    public final TextView tvSrcFrom;

    public GridViewHolder(View itemView)
    {
        super(itemView);
        tvDesc = (TextView) itemView.findViewById(R.id.tv_grid_desc);
        tvSrcFrom = (TextView) itemView.findViewById(R.id.tv_grid_src_from);
        tvWho = (TextView) itemView.findViewById(R.id.tv_grid_who);
        ivImage = (ImageView) itemView.findViewById(R.id.iv_grid_image);
        btnMenuMore = (ImageView) itemView.findViewById(R.id.btn_grid_more);
    }
}
