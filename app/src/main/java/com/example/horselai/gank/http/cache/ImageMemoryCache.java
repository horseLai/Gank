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

    // private Map<String, SoftReference<Bitmap>> mSoftReferences;
    private static final int DEFAULT_SIZE;

    static {
        final long max = Runtime.getRuntime().maxMemory() / 8;
        DEFAULT_SIZE = (int) (max > 25 * 1024 ? 25 * 1024 : max);
    }

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public ImageMemoryCache(int maxSize)
    {
        super(maxSize);
        // mSoftReferences = Collections.synchronizedMap(new HashMap<String, SoftReference<Bitmap>>());
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
            // mSoftReferences.put(md5, new SoftReference<Bitmap>(value));
        }
    }


    @Override public void removeFromCache(@NonNull String key)
    {
        final String md5 = EncryptionUtil.generateMd5(String.valueOf(key));

        if (md5 != null && get(md5) != null) {
            Bitmap bitmap = get(md5);
            if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
            remove(md5);
            //mSoftReferences.remove(md5);
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
            Bitmap bitmap = snapshot.get(e);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            // mSoftReferences.remove(e);
            snapshot.remove(e);
        }
    }


    @Override
    protected void entryRemoved(boolean evicted, Object key, Bitmap oldValue, Bitmap newValue)
    {

        // TODO: 2017/8/1 这里图片回收之后，会出现其他地方图片引用错误的问题
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
