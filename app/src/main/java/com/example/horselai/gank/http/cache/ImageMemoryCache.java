package com.example.horselai.gank.http.cache;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.LruCache;

import com.example.horselai.gank.util.EncryptionUtil;

import java.io.IOException;
import java.util.Map;

/**
 * Created by laixiaolong on 2017/3/19.
 */

public class ImageMemoryCache extends LruCache<Object, Bitmap> implements ICache<String, Bitmap>
{

    private static final int DEFAULT_SIZE;

    static {
        final int max = (int) (Runtime.getRuntime().maxMemory() / 8);
        DEFAULT_SIZE = max > 50 * 1024 * 1024 ? 50 * 1024 * 1024 : max;
    }


    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public ImageMemoryCache(int maxSize)
    {
        super(maxSize);
    }

    public ImageMemoryCache()
    {
        super(DEFAULT_SIZE);
    }


    @Override protected int sizeOf(Object key, Bitmap value)
    {
        return value.getHeight() * value.getRowBytes() / 1024;
    }


    @Override public Bitmap getFromCache(@NonNull String key)
    {
        final String md5 = EncryptionUtil.generateMd5(String.valueOf(key));
        if (md5 != null) return get(md5);
        return null;
    }

    @Override public void putIntoCache(@NonNull String key, @NonNull Bitmap value)
    {
        final String md5 = EncryptionUtil.generateMd5(String.valueOf(key));
        if (md5 != null && get(md5) == null) {
            put(md5, value);
        }
    }


    @Override public void removeFromCache(@NonNull String key)
    {
        final String md5 = EncryptionUtil.generateMd5(String.valueOf(key));

        if (md5 != null && get(md5) != null) {
            remove(md5);
        }
    }


    @Override public boolean contains(@NonNull String key)
    {
        final String md5 = EncryptionUtil.generateMd5(String.valueOf(key));
        return md5 != null && get(md5) != null;
    }

    @Override public void clearCache()
    {
        final Map<Object, Bitmap> snapshot = snapshot();
        for (Object e : snapshot.entrySet()) {
            snapshot.remove(e);
        }
    }


    @Override
    protected void entryRemoved(boolean evicted, Object key, Bitmap oldValue, Bitmap newValue)
    {

        // TODO: 2017/8/1 这里图片回收之后，会出现其他地方图片引用错误的问题,shifo

        /*if (evicted && oldValue != null && !oldValue.isRecycled()) {
            oldValue.recycle();
            oldValue = null;
        }*/

    }


    @Override public void close() throws IOException
    {
        clearCache();
    }
}
