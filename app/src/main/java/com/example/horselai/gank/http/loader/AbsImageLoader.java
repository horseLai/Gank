package com.example.horselai.gank.http.loader;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by horseLai on 2017/7/14.
 * <p>
 * 抽象图像加载类，包含图片加载所共性的抽象方法
 */

public abstract class AbsImageLoader
{
    protected static final int MSG_LOADING = 1;
    protected static final int MSG_LOAD_OK = 2;
    protected static final int MSG_LOAD_FAILED = 3;
    protected static final int MSG_SET_IMG_GONE = 4;
    protected static final int MSG_SET_IMG_VISIBLE = 5;

    public abstract void init(ImageLoaderConfig config, Context context) throws Exception;

    public abstract void displaySyncFromCache(ImageView iv, String url, boolean showWhenImgUrlIsEmpty, boolean loadNow);

    public abstract Bitmap fetchSyncFromCache(String url);

    public abstract void displayImageAsync(ImageView iv, String url, boolean showWhenImgUrlIsEmpty, boolean loadNow, int targetW, int targetH);

    public abstract Bitmap fetchFromNet(String url, boolean canCache, int targetW, int targetH);

    public abstract void downloadAsyncAndSave(String url, String absPathSaveTo, DownloadListener listener);

    public abstract void downloadAsync(String url, int targetW, int targetH, ImageCallback callback);

    public abstract void downloadAsync(String url, ImageCallback callback);

}
