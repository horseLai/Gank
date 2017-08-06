package com.example.horselai.gank.http.loader;

import android.graphics.Bitmap;

import com.example.horselai.gank.comm.ICallback;

/**
 * Created by laixiaolong on 2017/3/27.
 */

public abstract class ImageCallback implements ICallback<String, Bitmap>
{
    @Override public abstract void onSuccess(String key, Bitmap data);

    @Override public void onFail(Exception e, String key)
    {
    }
}
