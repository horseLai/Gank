package com.example.horselai.gank.bean;

import com.example.horselai.gank.base.BeanEntry;

/**
 * Created by horseLai on 2017/7/21.
 */

public class GankBeauty extends BeanEntry
{

    public String who;
    public String url;
    public String publishedAt;

    public GankBeauty()
    {
    }

    public GankBeauty(String who, String url, String publishedAt)
    {
        this.who = who;
        this.url = url;
        this.publishedAt = publishedAt;
    }

    @Override public String toString()
    {
        return "GankBeauty{" + "who='" + who + '\'' + ", url='" + url + '\'' + ", publishedAt='" + publishedAt + '\'' + '}';
    }
}
