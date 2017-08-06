package com.example.horselai.gank.mvp.ui.adapter.binder;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;

import com.example.horselai.gank.R;
import com.example.horselai.gank.bean.GankBeauty;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.util.Utils;

/**
 * Created by horseLai on 2017/7/23.
 * <p>
 * 统一处理ViewHolder Binder中的一些点击事件
 */

public class BinderClickEventListener implements View.OnClickListener
{
    private Context mContext;

    @Override public void onClick(View v)
    {
        mContext = v.getContext();
        switch (v.getId()) {
            case R.id.iv_other_play: {
                GankUI.startWebActivity(mContext, (GankNews) v.getTag());
                break;
            }
            case R.id.tv_grid_desc:
            case R.id.tv_load_content: {
                GankUI.startWebActivity(mContext, (GankNews) v.getTag());
                break;
            }
            case R.id.btn_other_more: {
                showPopupMenu(v, (GankNews) v.getTag());
                break;
            }
            case R.id.btn_grid_more: {
                showPopupMenu(v, (GankNews) v.getTag());
                break;
            }
            case R.id.tv_see_beauty: {
                GankUI.startImageActivity(mContext, (GankBeauty) v.getTag());
                break;
            }
            default: {
                final Object tag = v.getTag();
                if (tag instanceof GankNews) GankUI.startWebActivity(mContext, (GankNews) tag);
                else if (tag instanceof GankBeauty)
                    GankUI.startImageActivity(mContext, (GankBeauty) tag);
                break;
            }
        }
    }


    private void showPopupMenu(View v, final GankNews news)
    {
        Utils.popupMenu(v, R.menu.item_more, false, new PopupMenu.OnMenuItemClickListener()
        {
            @Override public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId()) {
                    case R.id.menu_load: {
                        GankUI.startWebActivity(mContext, news);
                        break;
                    }
                    case R.id.menu_download: {
                        GankUI.startImageActivity(mContext, new GankBeauty(null, news.image, null));
                        break;
                    }
                    case R.id.menu_load_other: {
                        GankUI.startExternalBrowser(mContext, news.url);
                        break;
                    }
                }
                return true;
            }
        });
    }


}
