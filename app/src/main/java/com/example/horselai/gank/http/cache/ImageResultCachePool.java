package com.example.horselai.gank.http.cache;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.http.loader.ImageLoader;

import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by horseLai on 2017/8/15.
 * <p>
 * ImageLoader中并发请求时， Result的缓冲队列，用于并发操作时减少对象创建次数，降低GC频率
 */
public class ImageResultCachePool
{
    private static final String TAG = "ConcurObjectCacheQueue";

    //以下两队列用来作为Result缓冲池，减少创建对象的次数以及GC频率
    //1.用来记录正在使用的Result
    private ConcurrentLinkedQueue<ImageLoader.Result> mInUseResultQueue;
    //2.用来记录已回收的Result
    private ConcurrentLinkedQueue<ImageLoader.Result> mFreeResultQueue;

    /**
     * @param initialObjCount 初始化创建的对象数量
     */
    public ImageResultCachePool(int initialObjCount)
    {
        mInUseResultQueue = new ConcurrentLinkedQueue<>();
        mFreeResultQueue = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < initialObjCount; i++) {
            mFreeResultQueue.add(new ImageLoader.Result());
        }
    }


    /**
     * 回收Result对象
     */
    public void recycleResult(ImageLoader.Result result)
    {
        if (result == null) return;
        result.imageView = null;
        result.bitmap = null;
        result.url = null;
        if (mInUseResultQueue.contains(result)) mInUseResultQueue.remove(result);
        mFreeResultQueue.add(result);
    }


    public ImageLoader.Result getResult(ImageView iv, Bitmap bitmap, String url)
    {
        ImageLoader.Result result = null;
        if (!mFreeResultQueue.isEmpty()) {
            while (result == null) {
                result = mFreeResultQueue.poll();
            }
        } else {
            result = new ImageLoader.Result();
        }
        mInUseResultQueue.add(result);
        result.imageView = iv;
        result.bitmap = bitmap;
        result.url = url;
        if (App.DEBUG)
            Log.i(TAG, String.format(Locale.getDefault(), "getResult: inUse = %d , free = %d", mInUseResultQueue.size(), mFreeResultQueue.size()));
        return result;
    }

    //清空Result缓存对象
    public void clearResultCache()
    {
        mFreeResultQueue.clear();
        mInUseResultQueue.clear();
    }

}
