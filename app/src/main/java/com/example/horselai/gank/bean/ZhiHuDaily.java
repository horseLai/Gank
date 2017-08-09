package com.example.horselai.gank.bean;

import com.example.horselai.gank.base.BeanEntry;

/**
 * Created by horseLai on 2017/8/9.
 */

public class ZhiHuDaily extends BeanEntry
{

    public String imgUrl;
    public String imgSource;
    public String headTitle;
    public String htmlSnap;

    @Override public String toString()
    {
        return "ZhiHuDaily{" + "imgUrl='" + imgUrl + '\'' + ", imgSource='" + imgSource + '\'' + ", headTitle='" + headTitle + '\'' + ", htmlSnap='" + htmlSnap + '\'' + '}';
    }
}
