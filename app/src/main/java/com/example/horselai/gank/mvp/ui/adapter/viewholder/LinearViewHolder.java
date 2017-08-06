package com.example.horselai.gank.mvp.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.horselai.gank.R;


/**
 * Created by laixiaolong on 2017/5/1.
 */
public class LinearViewHolder extends RecyclerView.ViewHolder
{
    public final ImageView ivImage;
    public final TextView tvDesc;
    public final TextView tvWho;
    public final TextView tvSrcFrom;
    public final TextView tvSeeBeauty;
    public final TextView tvLoadContent;

    public LinearViewHolder(View itemView)
    {
        super(itemView);
        tvDesc = (TextView) itemView.findViewById(R.id.tv_linear_desc);
        ivImage = (ImageView) itemView.findViewById(R.id.iv_linear_image);
        tvSrcFrom = (TextView) itemView.findViewById(R.id.tv_linear_src_from);
        tvWho = (TextView) itemView.findViewById(R.id.tv_linear_who);
        tvSeeBeauty = (TextView) itemView.findViewById(R.id.tv_see_beauty);
        tvLoadContent = (TextView) itemView.findViewById(R.id.tv_load_content);
    }
}
