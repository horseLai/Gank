package com.example.horselai.gank.mvp.model;

import android.text.TextUtils;

import com.example.horselai.gank.bean.GankReading;
import com.example.horselai.gank.http.request.HttpRequest;
import com.example.horselai.gank.util.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/8/7.
 */

public abstract class AbsReadingModel
{

    static final String CHARSET = "UTF-8";

    /**
     * 获取数据列表
     *
     * @param rootUrl 对应数据列表首页地址
     * @param pageNum 目标页码
     * @return
     */
    public ArrayList<GankReading> fetchDisplayList(String rootUrl, int pageNum)
    {
        final String url = formRequestUrl(rootUrl, pageNum);
        final Document doc = getDocument(url);
        if (doc == null) return null;

        Elements xianduItems = doc.getElementsByClass("xiandu_items");
        if (Utils.isEmpty(xianduItems)) return null;
        xianduItems = xianduItems.get(0).getElementsByClass("xiandu_item");
        if (Utils.isEmpty(xianduItems)) return null;

        final ArrayList<GankReading> gankReadings = new ArrayList<>();

        //now parse item
        Elements tmpEs;
        Element tmpE;
        GankReading reading;
        for (Element e : xianduItems) {

            reading = new GankReading();

            tmpEs = e.getElementsByClass("xiandu_left");
            tmpE = tmpEs.first().select("a").first();
            reading.url = tmpE.attr("href");
            reading.title = tmpE.text();
            reading.time = tmpEs.first().select("span").select("small").text();
            tmpEs = e.getElementsByClass("xiandu_right");
            reading.source = tmpEs.first().select("a").attr("title");

            gankReadings.add(reading);
        }

        gankReadings.trimToSize();
        return gankReadings;
    }

    protected String formRequestUrl(String rootUrl, int pageNum)
    {
        return pageNum > 1 ? (rootUrl + "/page/" + pageNum) : rootUrl;
    }


    protected Document getDocument(String url)
    {
        String content = null;
        try {
            content = HttpRequest.newNormalRequest(url, false).doRequest(CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(content)) return null;

        return Jsoup.parse(content);
    }
}
