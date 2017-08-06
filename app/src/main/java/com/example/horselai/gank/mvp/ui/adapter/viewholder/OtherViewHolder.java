package com.example.horselai.gank.mvp.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.horselai.gank.R;

public class OtherViewHolder extends RecyclerView.ViewHolder
{

    public final ImageView ivImage;
    public final ImageView ivPlay;
    public final ImageView btnMenuMore;
    public final TextView tvDesc;

    public OtherViewHolder(View view)
    {
        super(view);
        ivImage = (ImageView) itemView.findViewById(R.id.iv_other_image);
        ivPlay = (ImageView) itemView.findViewById(R.id.iv_other_play);
        btnMenuMore = (ImageView) view.findViewById(R.id.btn_other_more);
        tvDesc = (TextView) view.findViewById(R.id.tv_other_desc);

    }
}
