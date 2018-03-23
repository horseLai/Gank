package com.example.horselai.gank.mvp.model;

import android.provider.MediaStore;
import android.util.Log;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.http.loader.DownloadListener;
import com.example.horselai.gank.http.loader.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by horseLai on 2017/7/22.
 */

public class ImageModel
{
    private static final String TAG = "ImageModel";

    public void downloadImage(String url, DownloadListener listener)
    {
        //Log.i(TAG, "downloadImage: " + generatePicPath(url));
        File picFile = new File(generatePicPath(url) );
        ImageLoader.getImageLoader().downloadAsyncAndSave(url + "?imageView2/0/w/1924", picFile.getAbsolutePath(), listener);

    }


    public String generatePicPath(String url)
    {
        return App.getAppPicturePath() + "/saved_as_" + url.hashCode() + ".jpg";
    }


}
