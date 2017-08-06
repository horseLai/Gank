package com.example.horselai.gank.bean;

import com.example.horselai.gank.base.BeanEntry;

/**
 * Created by horseLai on 2017/7/21.
 */

public class GankSearch extends BeanEntry
{

    public String readability;
    public String publishedAt;
    public String desc;
    public String type;
    public String url;
    public String who;


    @Override public String toString()
    {
        return "GankSearch{" + " readability='" + readability + '\'' + ", publishedAt='" + publishedAt + '\'' + ", desc='" + desc + '\'' + ", type='" + type + '\'' + ", url='" + url + '\'' + ", who='" + who + '\'' + '}';
    }
}
