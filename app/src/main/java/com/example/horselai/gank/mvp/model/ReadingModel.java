package com.example.horselai.gank.mvp.model;

import com.example.horselai.gank.bean.CuriosityDaily;
import com.example.horselai.gank.bean.ZhiHuDaily;
import com.example.horselai.gank.util.Utils;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by horseLai on 2017/8/7.
 */

public class ReadingModel extends AbsReadingModel
{

    // TODO: 2017/8/10 真的有必要解析这些吗？
    public ZhiHuDaily parseZhiHuDaily(String url)
    {

        final Document doc = getDocument(url);
        if (doc == null) return null;

        Elements elements = doc.getElementsByClass("img-wrap");
        if (Utils.isEmpty(elements)) return null;
        //标题
        Elements tmpEs = elements.first().getElementsByClass("headline-title");
        if (Utils.isEmpty(tmpEs)) return null;

        final ZhiHuDaily data = new ZhiHuDaily();
        data.headTitle = tmpEs.first().text();

        //图片来源
        tmpEs = elements.first().getElementsByClass("img-source");
        if (Utils.isEmpty(tmpEs)) return null;
        data.imgSource = tmpEs.first().text();

        //图片
        tmpEs = elements.first().select("img");
        if (Utils.isEmpty(tmpEs)) return null;
        data.imgUrl = tmpEs.first().attr("src");

        //正文
        elements = doc.getElementsByClass("question");
        if (Utils.isEmpty(elements)) return null;
        data.htmlSnap = elements.first().outerHtml();

        return data;
    }

    public CuriosityDaily parseCuriosityDaily(String url)
    {

        final Document doc = getDocument(url);
        if (doc == null) return null;

        //image, title
        Elements elements = doc.getElementsByClass("banner lazyloaded");
        if (Utils.isEmpty(elements)) return null;
        final CuriosityDaily data = new CuriosityDaily();
        data.imgUrl = elements.first().attr("src");
        data.headTitle = elements.first().attr("alt");
        //正文


        return data;
    }

}
