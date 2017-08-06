package com.example.horselai.gank.bean;

import com.example.horselai.gank.base.BeanEntry;

/**
 * Created by horseLai on 2017/7/15.
 */

public class GankNews extends BeanEntry
{
    public String desc;
    public String publishedAt;
    public String source;
    public String type;
    public String url;
    public String who;
    public String image;

    public GankNews()
    {
    }

    public GankNews(String desc, String publishedAt, String source, String type, String url, String who, String image)
    {
        this.desc = desc;
        this.publishedAt = publishedAt;
        this.source = source;
        this.type = type;
        this.url = url;
        this.who = who;
        this.image = image;
    }

    @Override public String toString()
    {
        return "GankNews{ 'desc='" + desc + '\'' + ", publishedAt='" + publishedAt + '\'' + ", source='" + source + '\'' + ", type='" + type + '\'' + ", url='" + url + '\'' + ", who='" + who + '\'' + ", image='" + image + '\'' + '}';
    }
}
