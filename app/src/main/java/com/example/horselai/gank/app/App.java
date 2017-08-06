package com.example.horselai.gank.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.example.horselai.gank.R;
import com.example.horselai.gank.http.loader.ImageLoader;
import com.example.horselai.gank.http.loader.ImageLoaderConfig;
import com.example.horselai.gank.util.FileManager;

import java.io.File;


/**
 * Created by laixiaolong on 2016/12/3.
 */

public class App extends Application
{
    private static ConnectivityManager manager;
    private static Context mContext;
    private static long lastToastTime;
    private static String lastToastMsg;
    private static final String TAG = "App >>> ";
    public static final boolean DEBUG = true;

    //private static final String APP_KEY = "3a1c665fb6bc7d43f95e4f3c95d19e25";

    public static Context context()
    {
        return mContext;
    }

    @Override public void onCreate()
    {
        super.onCreate();
        mContext = this;

        initBmobSdk();

        initImageLoader();
        // initContentLoader();


        //初始化主目录
        getAppHomePath();

    }

    public static File getAppHomePath()
    {
        return FileManager.getInstance().initHomeDir(mContext, "GankIo");
    }

    public static String getAppPicturePath()
    {
        final File file = FileManager.getInstance().initPicturePath(mContext, "pictures");
        return file.getAbsolutePath();
    }


    public static File getAppCacheDir()
    {
        return FileManager.getInstance().createCacheDir(mContext, "AppCache");
    }

    public static File getHttpCacheDir()
    {
        return FileManager.getInstance().createCacheDir(mContext, "HttpCache");
    }

    private void initImageLoader()
    {
        ImageLoaderConfig config = new ImageLoaderConfig.Builder().setCompressFormat(Bitmap.CompressFormat.JPEG).setCacheMethod(ImageLoaderConfig.CACHE_DOUBLE).setDiskCacheSize(1024 * 1024 * 50).showImageWhenFailed(R.drawable.ic_load_image_fail).showImageWhenLoading(R.drawable.ic_emoji_frame_loading).build();
        try {
            ImageLoader.getImageLoader().init(config, mContext);
        } catch (Exception e) {
            e.printStackTrace();
            if (DEBUG) Log.i(TAG, "initImageLoader: ImageLoader 初始化失败");
        }
    }


    private void initBmobSdk()
    {
        /*BmobConfig config =
                new BmobConfig.Builder(this).setApplicationId(APP_KEY).setConnectTimeout(30) //30s
                        .setFileExpiration(2500) //文件过期时间
                        .setUploadBlockSize(1024 * 1024) //设置文件分片上传时每片的大小（字节）
                        .build();
        Bmob.initialize(config);*/
    }


    /**
     * 当前是否联网
     *
     * @return
     */
    public static boolean isOnline()
    {
        if (manager == null) {
            manager = (ConnectivityManager) mContext.getSystemService(CONNECTIVITY_SERVICE);
        }

        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.isConnected();

    }

    /**
     * 当前是否wifi在线
     *
     * @return
     */
    public static boolean isWifiOnline()
    {
        return isOnline() && manager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    /**
     * 当前是否移动在线
     *
     * @return
     */
    public static boolean isMobileOnline()
    {
        return isOnline() && manager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE;
    }


    public static void toast(String msg, int duration)
    {
        if (msg == null || TextUtils.isEmpty(msg)) return;

        long currentTimeM = System.currentTimeMillis();

        if (!msg.equals(lastToastMsg) || Math.abs(currentTimeM - lastToastTime) > 1500) //3s时差
        {
            Toast.makeText(mContext, msg, duration).show();
        }

        lastToastTime = currentTimeM;
        lastToastMsg = msg;
    }

    public static void toastShort(String msg)
    {
        toast(msg, Toast.LENGTH_SHORT);
    }

    public static void toastLong(String msg)
    {
        toast(msg, Toast.LENGTH_LONG);
    }


    public static DisplayMetrics getScreenMetrics()
    {
        return Resources.getSystem().getDisplayMetrics();
    }


}
