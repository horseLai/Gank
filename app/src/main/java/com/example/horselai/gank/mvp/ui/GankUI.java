package com.example.horselai.gank.mvp.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.example.horselai.gank.R;
import com.example.horselai.gank.app.App;
import com.example.horselai.gank.bean.GankBeauty;
import com.example.horselai.gank.bean.GankNews;
import com.example.horselai.gank.http.api.GankApi;
import com.example.horselai.gank.mvp.ui.activity.AllCategoriesActivity;
import com.example.horselai.gank.mvp.ui.activity.BeautyGalleryActivity;
import com.example.horselai.gank.mvp.ui.activity.BlogActivity;
import com.example.horselai.gank.mvp.ui.activity.DayRecommendActivity;
import com.example.horselai.gank.mvp.ui.activity.ImageActivity;
import com.example.horselai.gank.mvp.ui.activity.ReadingActivity;
import com.example.horselai.gank.mvp.ui.activity.ThoughtActivity;
import com.example.horselai.gank.mvp.ui.activity.WebActivity;
import com.example.horselai.gank.service.ScanHistoryService;
import com.example.horselai.gank.util.Utils;

/**
 * Created by horseLai on 2017/7/24.
 */

public class GankUI
{

    public static final int PAGE_RECOMMEND = 0;
    public static final int PAGE_ANDROID = 1;
    public static final int PAGE_IOS = 2;
    public static final int PAGE_WEB = 3;
    public static final int PAGE_EXPANDS = 4;
    public static final int PAGE_APP = 5;
    public static final int PAGE_VIDEO = 6;


    public static int getAssociatedPageIndex(String category)
    {
        int index = PAGE_ANDROID;
        switch (category) {
            case GankApi.RECOMMEND:
                index = PAGE_RECOMMEND;
                break;
            case GankApi.ANDROID:
                index = PAGE_ANDROID;
                break;
            case GankApi.IOS:
                index = PAGE_IOS;
                break;
            case GankApi.WEB:
                index = PAGE_WEB;
                break;
            case GankApi.EXPENDS:
                index = PAGE_EXPANDS;
                break;
            case GankApi.APP:
                index = PAGE_APP;
                break;
            case GankApi.VIDEO:
                index = PAGE_VIDEO;
                break;
        }
        return index;
    }


    public static void startExternalBrowser(Context context, String url)
    {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            App.toastShort("链接打开失败！(⊙ˍ⊙)");
        }
    }

    public static void startShare(Context context, String textMsg)
    {
        Utils.shareTextPlain(context, textMsg);
    }

    public static void startActivityByHeaderLabel(Context context, String label)
    {
        startAllCategoriesActivity(context, getAssociatedPageIndex(label));
    }


    public static void startImageActivity(Context context, GankBeauty beauty)
    {
        Intent starter = new Intent(context, ImageActivity.class);
        starter.putExtra(ImageActivity.KEY_BEAUTY, beauty);
        context.startActivity(starter);
    }


    public static void startWebActivity(Context context, GankNews news)
    {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(WebActivity.KEY_DATA, news);
        context.startActivity(intent);

        //打开一个链接表示一次浏览
        intent = new Intent(context, ScanHistoryService.class);
        intent.setAction(ScanHistoryService.ACTION_HISTORY);
        intent.putExtra(ScanHistoryService.ADD_ONE, news);
        context.startService(intent);
    }


    public static void startWebActivity(Context context)
    {
        Utils.startActivity(context, WebActivity.class);
    }

    public static void startImageActivity(Context context)
    {
        Utils.startActivity(context, ImageActivity.class);
    }

    public static void startBeautyGallaryActivity(Context context)
    {
        Utils.startActivity(context, BeautyGalleryActivity.class);
    }

    public static void startAllCategoriesActivity(Context context, int whichPage)
    {
        Intent starter = new Intent(context, AllCategoriesActivity.class);
        starter.putExtra(AllCategoriesActivity.KEY_PAGE, whichPage);
        context.startActivity(starter);
    }

    public static void startDayRecommendActivity(Context context)
    {
        Utils.startActivity(context, DayRecommendActivity.class);
    }

    public static int getCategoryDrawableResId(String category)
    {
        int id = R.drawable.ic_news_teal;
        switch (category) {
            case GankApi.ANDROID:
                id = R.drawable.ic_android_teal;
                break;
            case GankApi.IOS:
                id = R.drawable.ic_ios_teal;
                break;
            case GankApi.WEB:
                id = R.drawable.ic_web_teal;
                break;
            case GankApi.EXPENDS:
                id = R.drawable.ic_expand_teal;
                break;
            case GankApi.APP:
                id = R.drawable.ic_app_teal;
                break;
            case GankApi.VIDEO:
                id = R.drawable.ic_video_teal;
                break;
            case GankApi.RECOMMEND:
                id = R.drawable.ic_bottom_recommand_teal;
                break;
        }
        return id;
    }

    public static void startBusinessActivity(Context mContext)
    {
        Utils.startActivity(mContext, ReadingActivity.class);
    }

    public static void startBlogActivity(Context mContext)
    {
        Utils.startActivity(mContext, BlogActivity.class);

    }

    public static void startThoughtActivity(Context mContext)
    {
        Utils.startActivity(mContext, ThoughtActivity.class);
    }
}
