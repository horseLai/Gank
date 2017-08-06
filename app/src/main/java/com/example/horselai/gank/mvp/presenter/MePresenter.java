package com.example.horselai.gank.mvp.presenter;

import android.content.Context;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.http.loader.ImageLoader;
import com.example.horselai.gank.http.request.HttpRequestUtil;
import com.example.horselai.gank.http.service.AsyncService;
import com.example.horselai.gank.mvp.presenter.iPresenter.AbsSuperPresenter;
import com.example.horselai.gank.mvp.ui.iView.ISuperView;
import com.example.horselai.gank.util.FileManager;

import java.io.File;
import java.io.IOException;

/**
 * Created by horseLai on 2017/7/29.
 */

public class MePresenter extends AbsSuperPresenter<String>
{
    private final Context mContext;

    public MePresenter(ISuperView mView, Context context)
    {
        super(mView);
        this.mContext = context;
    }

    @Override public void update(Object... params)
    {
        doClean();
    }


    private void doClean()
    {

        AsyncService.getService().addRequestTask(new Runnable()
        {
            @Override public void run()
            {
                final FileManager fileManager = FileManager.getInstance();
                final File diskCacheDir = ImageLoader.getImageLoader().getDiskCacheDir(mContext);
                long fileSize = fileManager.calculateFileSize(diskCacheDir);

                final File appCacheDir = App.getAppCacheDir();
                fileSize += fileManager.calculateFileSize(appCacheDir);

                fileSize += fileManager.calculateFileSize(App.getHttpCacheDir());

                if (fileSize == 0) {
                    getHandler().obtainMessage(FAIL, new Exception("没有更多缓存啦(ノ｀Д)ノ")).sendToTarget();
                    return;
                }

                fileManager.clearFilesInFolder(diskCacheDir.getPath(), false);
                fileManager.clearFilesInFolder(diskCacheDir.getPath(), false);
                try {
                    HttpRequestUtil.deleteHttpResponseCache();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                getHandler().obtainMessage(SUCCESS, fileManager.formatSize(fileSize)).sendToTarget();
            }
        });
    }
}
