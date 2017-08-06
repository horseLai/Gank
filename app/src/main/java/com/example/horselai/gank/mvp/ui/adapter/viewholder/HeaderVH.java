package com.example.horselai.gank.mvp.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.horselai.gank.R;


public class HeaderVH extends RecyclerView.ViewHolder
{
    public final TextView tvMore;
    public final TextView tvDesc;

    public HeaderVH(View itemView)
    {
        super(itemView);
        tvDesc = (TextView) itemView.findViewById(R.id.tv_header_desc);
        tvMore = (TextView) itemView.findViewById(R.id.tv_header_more);

    }
}
