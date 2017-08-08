package com.example.horselai.gank.bean;

import com.example.horselai.gank.base.BeanEntry;

/**
 * Created by horseLai on 2017/8/7.
 */

public class GankReading extends BeanEntry
{
    public String title;
    public String time;
    public String url;
    public String source;


    @Override public String toString()
    {
        return "GankReading{" + "title='" + title + '\'' + ", time='" + time + '\'' + ", url='" + url + '\'' + ", source='" + source + '\'' + '}';
    }
}
