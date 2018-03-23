package com.example.horselai.gank.mvp.presenter;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.http.loader.BitmapManager;
import com.example.horselai.gank.http.loader.DownloadListener;
import com.example.horselai.gank.http.service.AsyncService;
import com.example.horselai.gank.mvp.model.ImageModel;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.FileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by horseLai on 2017/7/22.
 */

public class ImagePresenter extends AbsSuperPresenter<String>
{

    private static final String TAG = "ImagePresenter";
    private final ImageModel mModel;

    public ImagePresenter(ISuperView mView)
    {
        super(mView);
        mModel = new ImageModel();
    }

    @Override public void update(Object... params)
    {
        if (!App.isOnline()) {
            getHandler().obtainMessage(FAIL, new Exception("网络异常!_〆(´Д｀ )")).sendToTarget();
            return;
        }
        downloadImage((String) params[0]);
    }

    private void downloadImage(String url)
    {

        if (FileManager.getInstance().exists(getPicPath(url))) {
            getHandler().obtainMessage(SUCCESS, "文件已存在！").sendToTarget();
            return;
        }

        mModel.downloadImage(url, new DownloadListener()
        {
            @Override public void onSuccess(String url)
            {
                getHandler().obtainMessage(SUCCESS, "下载成功！").sendToTarget();

            }

            @Override public void onFailed(String url, Exception e)
            {
                super.onFailed(url, e);
                getHandler().obtainMessage(FAIL, e).sendToTarget();
            }
        });
    }


    public void settingWallpaper(final WallpaperManager wpManager, final String url)
    {
        AsyncService.getService().addRequestTask(new Runnable()
        {
            @Override public void run()
            {
                final String picPath = getPicPath(url);
                final DisplayMetrics wh = App.getScreenMetrics();
                try {
                    final Bitmap bitmap = BitmapManager.getInstance().decodeBitmap(picPath, wh.widthPixels, wh.heightPixels);

                    wpManager.setBitmap(bitmap);
                    getHandler().obtainMessage(SUCCESS, "壁纸设置成功！").sendToTarget();
                } catch (IOException e) {
                    getHandler().obtainMessage(FAIL, new Exception("壁纸设置失败!")).sendToTarget();
                }
            }
        });

    }

    public String getPicPath(String url)
    {
        return mModel.generatePicPath(url);
    }
}
