package com.example.horselai.gank.mvp.ui.adapter.binder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.horselai.gank.R;
import com.example.horselai.gank.app.App;
import com.example.horselai.gank.bean.GankBeauty;
import com.example.horselai.gank.bean.home.CommHomeItem;
import com.example.horselai.gank.http.loader.BitmapManager;
import com.example.horselai.gank.http.loader.ImageLoader;
import com.example.horselai.gank.mvp.ui.GankUI;
import com.example.horselai.gank.util.Utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by horseLai on 2017/7/16.
 */

public class HomeViewHolderBinder extends CommMultipleVHBinder<CommHomeItem>
{

    public HomeViewHolderBinder(Context mContext)
    {
        super(mContext);
    }


    @Override
    protected RecyclerView.ViewHolder onCreateCategoryBarViewHolder(ViewGroup parent, int viewType)
    {
        View view = mLayoutInflater.inflate(R.layout.home_category_bar, parent, false);
        StaggeredGridLayoutManager.LayoutParams staggeredLayoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        staggeredLayoutParams.setFullSpan(true);
        view.setLayoutParams(staggeredLayoutParams);
        return new CategoryBarVH(view);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateSliderRotationViewHolder(ViewGroup parent, int viewType)
    {
        View view = mLayoutInflater.inflate(R.layout.home_type_top_slide, parent, false);
        StaggeredGridLayoutManager.LayoutParams staggeredLayoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
        staggeredLayoutParams.setFullSpan(true);
        view.setLayoutParams(staggeredLayoutParams);

        return new SlideRotationVH(view);
    }


    @Override
    protected void onBindSliderRotationViewHolder(RecyclerView.ViewHolder holder, boolean mLoadImageNow, final CommHomeItem itemData, int position)
    {
        super.onBindSliderRotationViewHolder(holder, mLoadImageNow, itemData, position);

        if (itemData == null) return;

        final ArrayList<GankBeauty> dataList = itemData.dataList;
        if (Utils.isEmpty(dataList)) {
            return;
        }
        final SlideRotationVH vh = (SlideRotationVH) holder;

        vh.setBeauties(dataList);  //轮播时用到
        mPagerAdapter.updateAll(dataList);
        mPagerAdapter.isLoadImgNow = mLoadImageNow;

        vh.viewPager.removeAllViewsInLayout();
        vh.viewPager.setAdapter(mPagerAdapter);

    }


    private Timer mTimer;
    private Handler mHandler;
    private static final String TAG = "HomeViewHolderBinder";

    private class SlideRotationVH extends RecyclerView.ViewHolder
    {
        private final ViewPager viewPager;
        private int itemCount = 0;
        private ArrayList<GankBeauty> beauties;

        public void setBeauties(ArrayList<GankBeauty> beauties)
        {
            this.beauties = beauties;
            initHandler();
            // setupSlideRotation();
        }

        public SlideRotationVH(View itemView)
        {
            super(itemView);
            viewPager = (ViewPager) itemView.findViewById(R.id.vp_top_slider);
            addListener();
        }

        private void initHandler()
        {
            if (mHandler == null) mHandler = new Handler(Looper.getMainLooper())
            {
                @Override public void handleMessage(Message msg)
                {
                    if (msg.what == 0 && !Utils.isEmpty(beauties)) {
                        viewPager.setCurrentItem(itemCount < beauties.size() ? itemCount++ : 0, true);
                    }
                    if (App.DEBUG) Log.i(TAG, "handleMessage: itemCount : " + itemCount);
                }
            };
        }

        private void addListener()
        {
            viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
            {
                @Override public void onPageSelected(int position)
                {
                    itemCount = position;
                }
            });
        }

    }

    /**
     * 建立轮播滑动
     */
    public void setupSlideRotation()
    {
        if (mTimer == null) {
            mTimer = new Timer("vpTimer");

            mTimer.scheduleAtFixedRate(new TimerTask()
            {
                @Override public void run()
                {
                    if (mHandler != null) mHandler.sendEmptyMessage(0);
                }
            }, 0, 2000);
        }
    }

    /**
     * 停止轮播滑动
     */
    public void stopSlideRotation()
    {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private class CategoryBarVH extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        CategoryBarVH(View itemView)
        {
            super(itemView);
            itemView.findViewById(R.id.tv_cat_all).setOnClickListener(this);
            itemView.findViewById(R.id.tv_cat_day_recommend).setOnClickListener(this);
            itemView.findViewById(R.id.tv_cat_beauty).setOnClickListener(this);
            //itemView.findViewById(R.id.tv_cat_reading).setOnClickListener(this);
            itemView.findViewById(R.id.tv_cat_reading).setOnClickListener(this);
            itemView.findViewById(R.id.tv_cat_equipment).setOnClickListener(this);
            itemView.findViewById(R.id.tv_cat_blog).setOnClickListener(this);
        }

        @Override public void onClick(View v)
        {
            switch (v.getId()) {
                case R.id.tv_cat_all: {
                    GankUI.startAllCategoriesActivity(mContext, 0);
                    break;
                }
                case R.id.tv_cat_beauty: {
                    GankUI.startBeautyGallaryActivity(mContext);
                    break;
                }
                case R.id.tv_cat_day_recommend: {
                    GankUI.startDayRecommendActivity(mContext);
                    break;
                }
                /*case R.id.tv_cat_reading: {
                    GankUI.startBusinessActivity(mContext);
                    break;
                }*/
                case R.id.tv_cat_reading: {
                    GankUI.startBusinessActivity(mContext);
                    break;
                }
                case R.id.tv_cat_blog: {
                    GankUI.startBlogActivity(mContext);
                    break;
                }
                case R.id.tv_cat_equipment: {
                    GankUI.startThoughtActivity(mContext);
                    break;
                }
            }

        }
    }


    private final VpPagerAdapter mPagerAdapter = new VpPagerAdapter();

    class VpPagerAdapter extends PagerAdapter
    {
        ArrayList<GankBeauty> beauties = new ArrayList<>();
        boolean isLoadImgNow = true;
        int counter = 0;

        public void updateAll(ArrayList<GankBeauty> items)
        {
            if (Utils.isEmpty(items)) return;
            beauties.clear();
            beauties.addAll(items);
            notifyDataSetChanged();
        }

        @Override public int getCount()
        {
            return beauties.size();
        }

        @Override public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }


        @Override public Object instantiateItem(ViewGroup container, final int position)
        {
            View view = mLayoutInflater.inflate(R.layout.vp_slide_item, container, false);
            ImageView ivImage = (ImageView) view.findViewById(R.id.iv_slide_image);

            ivImage.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    GankUI.startImageActivity(mContext, beauties.get(position));
                }
            });
            final String url = beauties.get(position).url;
            if (!TextUtils.isEmpty(url))
                ImageLoader.getImageLoader().displayImageAsync(ivImage, url + "?imageView2/0/w/320", false, isLoadImgNow, 320, 400);

            container.addView(view);
            return view;
        }

        @Override public void destroyItem(ViewGroup container, int position, Object object)
        {
            if (object != null) {
                container.removeView((View) object);

                //释放图片资源
                ImageView ivImage = (ImageView) ((View) object).findViewById(R.id.iv_slide_image);
                BitmapManager.getInstance().releaseImage(ivImage);
                final View view = (View) object;
                view.setBackground(null);
                view.setBackgroundColor(0);

                if (++counter > 3) {
                    counter = 0;
                    Runtime.getRuntime().gc();
                    if (App.DEBUG)
                        Log.d(TAG, "HomeViewHolderBinder:VpPagerAdapter >> destroyItem: do gc");
                }
            } else {
                super.destroyItem(container, position, object);
            }
        }

    }


    /**
     * 释放资源，在Fragment或Activity的onDestroy()或onStop()中调用
     */
    @Override public void release()
    {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
