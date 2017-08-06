package com.example.horselai.gank.impl;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by horseLai on 2017/7/23.
 */

public class TitleTextView extends AppCompatTextView
{
    public TitleTextView(Context context)
    {
        super(context);
    }

    public TitleTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TitleTextView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override public boolean isFocused()
    {
        //让它一直处于聚焦状态聚焦
        return true;
    }

}
