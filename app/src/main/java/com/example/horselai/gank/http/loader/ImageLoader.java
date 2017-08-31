package com.example.horselai.gank.http.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.http.cache.ICache;
import com.example.horselai.gank.http.cache.ImageDiskLruCache;
import com.example.horselai.gank.http.cache.ImageDoubleCache;
import com.example.horselai.gank.http.cache.ImageMemoryCache;
import com.example.horselai.gank.http.cache.ObjectCachePool;
import com.example.horselai.gank.http.request.HttpRequest;
import com.example.horselai.gank.http.service.ThreadPoolHandler;
import com.example.horselai.gank.util.FileManager;
import com.example.horselai.gank.util.Utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by laixiaolong on 2017/3/12.
 */

public class ImageLoader extends AbsImageLoader implements Closeable
{
    private static final String TAG = "ImageLoader >>> ";
    private static final ThreadPoolHandler THREAD_POOL_HANDLER = new ThreadPoolHandler();
    public static final boolean DEBUG = App.DEBUG;

    private ObjectCachePool<Result> mObjectCachePool;
    private ICache<String, Bitmap> mCache;

    private static int imageLoading;
    private static int imageLoadFailed;
    private boolean mIsCacheInitialized = false;


    private final Handler mMainHandler = new Handler()
    {
        @Override public void handleMessage(Message msg)
        {
            final Result result = (Result) msg.obj;
            switch (msg.what) {

                case MSG_LOADING:
                    if (imageLoading != 0) result.imageView.setImageResource(imageLoading);
                    break;

                case MSG_LOAD_OK:
                    if (result.bitmap == null) return;
                    if (result.imageView.getTag().equals(result.url)) {
                        result.imageView.setImageBitmap(result.bitmap);
                    }
                    break;

                case MSG_LOAD_FAILED:
                    if (imageLoadFailed != 0) {
                        result.imageView.setImageResource(imageLoadFailed);
                    }
                    break;

                case MSG_SET_IMG_GONE:
                    if (result.imageView.getTag() != null && result.imageView.getTag().equals(result.url))
                        result.imageView.setVisibility(View.GONE);
                    break;
            }
            //使用完成，收录到缓存
            recycleResult(result);

        }

    };

    private void recycleResult(Result result)
    {
        result.bitmap = null;
        result.url = null;
        result.imageView = null;
        mObjectCachePool.recycle(result);
    }


    public ThreadPoolHandler getThreadPoolHandler()
    {
        return THREAD_POOL_HANDLER;
    }

    public static ImageLoader getImageLoader()
    {
        return InstanceBuilder.loader;
    }

    @Override public void close() throws IOException
    {
        THREAD_POOL_HANDLER.cancelAndClearTaskQueue();
        THREAD_POOL_HANDLER.closePoolNow();
        mObjectCachePool.close();
        mCache.close();
        BitmapManager.getInstance().close();
    }

    public void clearCache()
    {
        try {
            mCache.clearCache();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class InstanceBuilder
    {
        private static ImageLoader loader = new ImageLoader();
    }

    private ImageLoader()
    {
    }


    public void init(ImageLoaderConfig config, Context context) throws Exception
    {
        if (mIsCacheInitialized) throw new Exception("ImageLoader has already been initialized ! ");

        final long diskCacheSize = config.getDiskCacheSize();
        final int maxThreadPoolSize = config.getMaxThreadPoolSize();
        final int compressQuality = config.getCompressQuality();
        final int cacheMethod = config.getCacheMethod();
        final Bitmap.CompressFormat compressFormat = config.getCompressFormat();

        imageLoadFailed = config.getImageLoadFailed();
        imageLoading = config.getImageLoading();

        THREAD_POOL_HANDLER.setMaxPoolSize(maxThreadPoolSize);

        initCache(cacheMethod, diskCacheSize, context, compressFormat, compressQuality);
        //创建缓冲池
        mObjectCachePool = new ObjectCachePool<>(25, 20, Result.class);
    }


    private void initCache(int method, long diskCacheSize, Context context, Bitmap.CompressFormat compressFormat, int compressQuality)
    {
        if (mCache == null) {
            try {
                switch (method) {
                    case ImageLoaderConfig.CACHE_DOUBLE:
                        mCache = new ImageDoubleCache(getDiskCacheDir(context), diskCacheSize, compressFormat, compressQuality);
                        break;
                    case ImageLoaderConfig.CACHE_DISK:
                        mCache = new ImageDiskLruCache(getDiskCacheDir(context), diskCacheSize, compressFormat, compressQuality);
                        break;
                    case ImageLoaderConfig.CACHE_MEMORY:
                        mCache = new ImageMemoryCache();
                        break;
                }

                mIsCacheInitialized = true;
            } catch (IOException e) {
                mIsCacheInitialized = false;
                e.printStackTrace();
                Log.i(TAG, "init: " + e.getMessage());
            }
        }
    }


    public File getDiskCacheDir(Context context)
    {
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(context.getExternalCacheDir(), "imageCache");
        } else {
            file = new File(context.getCacheDir(), "imageCache");
        }
        if (!file.exists()) file.mkdirs();
        return file;
    }


    //************************************************************************
    //                    小小分割线 ,,ԾㅂԾ,,
    //************************************************************************

    /**
     * 从缓存中加载并显示一张图片
     *
     * @param iv
     * @param url
     * @param goneIfNull 是否在图片不存在时把ImageView设置成View.GONE
     */
    @Override public void displaySyncFromCache(final ImageView iv, String url, boolean goneIfNull)
    {

        if (iv == null) return;
        if (TextUtils.isEmpty(url)) {
            if (!goneIfNull) iv.setImageResource(imageLoadFailed);
            else iv.setVisibility(View.GONE);
            return;
        }

        iv.setVisibility(View.VISIBLE);
        iv.setImageResource(imageLoading);

        iv.setTag(url);
        final Bitmap bitmap = fetchSyncFromCache(url);
        if (bitmap != null) {
            iv.setImageBitmap(bitmap);
            return;
        }
        if (goneIfNull) iv.setVisibility(View.GONE);
    }


    /**
     * 从缓存中抓取图片
     *
     * @param url
     * @return 可能为 null
     */
    @Override public Bitmap fetchSyncFromCache(String url)
    {
        try {
            return mCache.getFromCache(url);
        } catch (IOException e) {
            if (DEBUG) {
                Log.i(TAG, "fetchSyncFromCache: url >>: " + url);
            }
            Log.i(TAG, "fetchSyncFromCache: " + e.getMessage());
            return null;
        }
    }

    /**
     * <p>
     * 如果图片在服务器已经处理好了尺寸，
     * 那么可以用这个方法直接下载显示到ImageView，
     * 而不需要额外的图片处理工作
     * </p>
     *
     * @param iv
     * @param url
     * @param goneIfNull
     */
    public void displayImageAsync(final ImageView iv, final String url, final boolean goneIfNull)
    {
        displayImageAsync(iv, url, goneIfNull, 0, 0);
    }


    /**
     * 异步加载图片
     *
     * @param iv
     * @param url
     * @param goneIfNull 是否在图片不存在时把ImageView设置成View.GONE
     * @param targetW
     * @param targetH
     */
    @Override
    public void displayImageAsync(final ImageView iv, final String url, final boolean goneIfNull, final int targetW, final int targetH)
    {
        if (iv == null) return;
        if (TextUtils.isEmpty(url)) {
            if (!goneIfNull) iv.setImageResource(imageLoadFailed);
            else iv.setVisibility(View.GONE);
            return;
        }


        iv.setTag(url);
        iv.setImageResource(imageLoading);

        THREAD_POOL_HANDLER.addToPool(new FetchTask(url, targetW, targetH, iv, goneIfNull));
    }

    final class FetchTask implements Runnable
    {
        private String url;
        private int targetW;
        private int targetH;
        private ImageView iv;
        private boolean goneIfNull;

        public FetchTask(String url, int targetW, int targetH, ImageView iv, boolean goneIfNull)
        {
            this.url = url;
            this.targetW = targetW;
            this.targetH = targetH;
            this.iv = iv;
            this.goneIfNull = goneIfNull;
        }

        @Override public void run()
        {
            final Bitmap bitmap = loadImage(url, targetW, targetH);

            if (bitmap != null) {
                mMainHandler.obtainMessage(MSG_LOAD_OK, getResult(iv, bitmap, url)).sendToTarget();
                return;
            }

            int msg;
            if (goneIfNull) {
                msg = MSG_SET_IMG_GONE;
            } else {
                msg = MSG_LOAD_FAILED;
            }
            mMainHandler.obtainMessage(msg, getResult(iv, null, url)).sendToTarget();

        }
    }

    private Result getResult(ImageView iv, Bitmap bitmap, String url)
    {
        final Result result = mObjectCachePool.get();
        result.imageView = iv;
        result.url = url;
        result.bitmap = bitmap;
        return result;
    }


    private Bitmap loadImage(String url, int targetW, int targetH)
    {
        //缓存中看看有没有
        Bitmap bitmap = fetchSyncFromCache(url);
        if (bitmap != null) return bitmap;

        //检测过程中，线程有没有被终止
        if (Thread.currentThread().isInterrupted()) return null;

        //既然没有，那就网络下载呗
        bitmap = fetchFromNet(url, true, targetW, targetH);
        return bitmap;
    }


    /**
     * 网上获取图片
     *
     * @param url
     * @param targetW
     * @param targetH
     * @return
     */
    @Override public Bitmap fetchFromNet(String url, boolean canCache, int targetW, int targetH)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
            throw new RuntimeException(" Don't try visiting net in UI Thread !");
        InputStream is = null;
        HttpRequest httpRequest = null;
        try {

            httpRequest = HttpRequest.newNormalRequest(url, false);
            is = httpRequest.getInputStream();
            Bitmap bitmap;

            if (targetH == 0 && targetW == 0) {
                bitmap = BitmapFactory.decodeStream(is);
            } else {
                bitmap = BitmapManager.getInstance().decodeBitmap(is, targetW, targetH);
                //最大50kb
                bitmap = BitmapManager.getInstance().compressBitmap(bitmap, 50);

            }
            if (bitmap != null && canCache)
                //得到图片后别着急，先缓存先
                mCache.putIntoCache(url, bitmap);
            //ok
            return bitmap;

        } catch (Exception e) {
            if (DEBUG) {
                Log.i(TAG, "fetchFromNet: url >>: " + url);
                e.printStackTrace();
            }
            return null;
        } finally {
            Utils.close(is);
            if (httpRequest != null) httpRequest.disconnect();
        }

    }


    @Override
    public void downloadAsyncAndSave(final String url, final String absPathSaveTo, final DownloadListener listener)
    {
        THREAD_POOL_HANDLER.addToPool(new Runnable()
        {
            @Override public void run()
            {
                HttpRequest httpRequest = null;
                InputStream is = null;
                try {
                    if (listener != null) listener.onStart(url);

                    httpRequest = HttpRequest.newNormalRequest(url, false);
                    is = httpRequest.getInputStream();

                    //保存到文件文件
                    FileManager.getInstance().writeToFile(absPathSaveTo, is);

                    if (listener != null) listener.onSuccess(url);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null) listener.onFailed(url, e);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) listener.onFailed(url, e);
                } finally {
                    Utils.close(is);
                    if (httpRequest != null) httpRequest.disconnect();
                }
            }
        });

    }


    /**
     * @param url
     * @param callback
     */
    @Override public void downloadAsync(final String url, final ImageCallback callback)
    {
        downloadAsync(url, 0, 0, callback);
    }

    /**
     * @param url
     * @param callback 注意这个回调是异步回调，请勿在里面直接操作UI
     * @param targetW
     * @param targetH
     * @throws IllegalArgumentException
     */
    @Override
    public void downloadAsync(final String url, final int targetW, final int targetH, final ImageCallback callback) throws IllegalArgumentException
    {
        if (callback == null) {
            throw new IllegalArgumentException("callback can not be null !!");
        }

        THREAD_POOL_HANDLER.addToPool(new Runnable()
        {
            @Override public void run()
            {
                final Bitmap bitmap = fetchFromNet(url, true, targetW, targetH);
                if (bitmap != null) {
                    callback.onSuccess(url, bitmap);
                    return;
                }
                callback.onFail(new Exception("图片加载失败！"), url);
            }
        });
    }


    public static final class Result implements Closeable
    {
        public ImageView imageView;
        public Bitmap bitmap;
        public String url;

        public Result()
        {
        }

        public Result(ImageView imageView, Bitmap bitmap, String mainUrl)
        {
            this.imageView = imageView;
            this.bitmap = bitmap;
            this.url = mainUrl;
        }

        @Override public void close() throws IOException
        {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null;
            }
            imageView = null;
            url = null;
        }
    }
}
