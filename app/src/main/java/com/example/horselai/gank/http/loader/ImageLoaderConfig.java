package com.example.horselai.gank.http.loader;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;

/**
 * Created by laixiaolong on 2017/3/21.
 */

public class ImageLoaderConfig
{
    public static final int CACHE_DOUBLE = 0X11;
    public static final int CACHE_MEMORY = 0X12;
    public static final int CACHE_DISK = 0X13;

    private long diskCacheSize;
    private int maxThreadPoolSize;
    private int imageLoading;
    private int imageLoadFailed;
    private Bitmap.CompressFormat compressFormat;
    private int compressQuality;
    private int cacheMethod = CACHE_DOUBLE;

    public int getCacheMethod()
    {
        return cacheMethod;
    }

    private ImageLoaderConfig(Builder builder)
    {
        this.diskCacheSize = builder.diskCacheSize;
        this.maxThreadPoolSize = builder.maxThreadPoolSize;
        this.imageLoadFailed = builder.imageLoadFailed;
        this.imageLoading = builder.imageLoading;
        this.compressQuality = builder.compressQuality;
        this.compressFormat = builder.compressFormat;
        this.cacheMethod = builder.cacheMethod;
    }

    public Bitmap.CompressFormat getCompressFormat()
    {
        return compressFormat;
    }

    public int getCompressQuality()
    {
        return compressQuality;
    }

    public long getDiskCacheSize()
    {
        return diskCacheSize;
    }

    public int getMaxThreadPoolSize()
    {
        return maxThreadPoolSize;
    }

    public int getImageLoading()
    {
        return imageLoading;
    }

    public int getImageLoadFailed()
    {
        return imageLoadFailed;
    }

    public static class Builder
    {
        private long diskCacheSize = 1024 * 1024 * 50; //50M
        private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        private int compressQuality = 70;
        private int maxThreadPoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        private int imageLoading = 0;
        private int imageLoadFailed = 0;
        private int cacheMethod;

        public Builder setCacheMethod(int cache)
        {
            this.cacheMethod = cache;
            return this;
        }

        public Builder setCompressFormat(Bitmap.CompressFormat compressFormat)
        {
            this.compressFormat = compressFormat;
            return this;
        }

        public Builder setImageCompressQuality(int compressQuality)
        {
            this.compressQuality = compressQuality;
            return this;
        }

        public Builder setMaxThreadPoolSize(int maxThreadPoolSize)
        {
            if (maxThreadPoolSize <= 0) {
                return this;
            }
            this.maxThreadPoolSize = maxThreadPoolSize;
            return this;
        }

        public Builder setDiskCacheSize(long diskCacheSize)
        {
            this.diskCacheSize = diskCacheSize;
            return this;
        }

        public Builder showImageWhenLoading(@DrawableRes int imageLoading)
        {
            this.imageLoading = imageLoading;
            return this;
        }

        public Builder showImageWhenFailed(@DrawableRes int imageLoadFailed)
        {
            this.imageLoadFailed = imageLoadFailed;
            return this;
        }

        public ImageLoaderConfig build()
        {
            return new ImageLoaderConfig(this);
        }
    }
}
