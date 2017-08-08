package com.example.horselai.gank.mvp.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.horselai.gank.R;

/**
 * Created by horseLai on 2017/8/8.
 */

public class ReadingViewHolder extends RecyclerView.ViewHolder
{

    public final TextView tvTitle;
    public final TextView tvLabel;
    public final TextView tvTime;

    public ReadingViewHolder(View itemView)
    {
        super(itemView);
        tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
        tvLabel = (TextView) itemView.findViewById(R.id.tv_label);
        tvTime = (TextView) itemView.findViewById(R.id.tv_time);
    }

}
