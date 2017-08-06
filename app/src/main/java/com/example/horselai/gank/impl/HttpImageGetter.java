package com.example.horselai.gank.impl;

import android.graphics.drawable.Drawable;
import android.text.Html;

/**
 * Created by laixiaolong on 2016/12/19.
 */

public class HttpImageGetter implements Html.ImageGetter
{
    private final int targetW;

    public HttpImageGetter(int parentWh)
    {
        targetW = parentWh;
    }


    @Override public Drawable getDrawable(final String source)
    {
        return null;
    }

}
