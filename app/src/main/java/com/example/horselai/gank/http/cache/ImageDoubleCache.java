package com.example.horselai.gank.http.cache;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;

/**
 * Created by laixiaolong on 2017/3/23.
 */

public class ImageDoubleCache implements ICache<String, Bitmap>
{
    private ImageDiskLruCache mDiskLCache;
    private ImageMemoryCache mMemoryCache;

    public ImageDoubleCache(File diskCacheDir, long diskCacheSize, Bitmap.CompressFormat compressFormat, int compressQuality) throws IOException
    {
        mDiskLCache = new ImageDiskLruCache(diskCacheDir, diskCacheSize, compressFormat, compressQuality);
        mMemoryCache = new ImageMemoryCache();
    }

    @Override public Bitmap getFromCache(@NonNull String key) throws IOException
    {
        final Bitmap fromCache = mMemoryCache.getFromCache(key);
        return fromCache == null ? mDiskLCache.getFromCache(key) : fromCache;
    }

    @Override
    public void putIntoCache(@NonNull String key, @NonNull Bitmap value) throws IOException
    {
        mMemoryCache.putIntoCache(key, value);
        mDiskLCache.putIntoCache(key, value);
    }

    @Override public void removeFromCache(@NonNull String key) throws IOException
    {
        mMemoryCache.remove(key);
        mDiskLCache.removeFromCache(key);
    }

    @Override public boolean contains(String key)
    {
        return mMemoryCache.contains(key) || mDiskLCache.contains(key);
    }

    @Override public void clearCache() throws IOException
    {
        mMemoryCache.clearCache();
        mDiskLCache.clearCache();
    }

    @Override public void close() throws IOException
    {
        mDiskLCache.close();
        mMemoryCache.close();
    }


}
