package com.example.horselai.gank.mvp.model;

import android.util.Log;

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
    private static final String TAG = "ReadingModel";

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
       /* elements = doc.getElementsByClass("question");
        if (Utils.isEmpty(elements)) return null;
        data.htmlSnap = elements.first().outerHtml();
        String content = elements.first().outerHtml();*/
        elements = doc.getElementsByClass("global-header");
        if (!Utils.isEmpty(elements)) {
            elements.remove();
        }
        elements = doc.getElementsByClass("header-for-mobile");
        if (!Utils.isEmpty(elements)) {
            elements.remove();
        }

        // TODO: 2017/8/27 处理图片
        // compatImage(doc);

        doc.getElementsByClass("header-for-mobile").remove();
        elements = doc.getElementsByClass("content");
        if (!Utils.isEmpty(elements) && elements.size() > 1) {
            elements.get(1).remove();
        }
        doc.getElementsByClass("bottom-recommend-download-link").remove();
        doc.getElementsByTag("link").first().attr("href", "css/zhiHu.css");
        data.htmlSnap = doc.outerHtml();
        return data;
    }

  /*  private void compatImage(Document doc)
    {
        final Elements elements = doc.getElementsByTag("img");
        if (!Utils.isEmpty(elements)) {
            String src;
            for (Element e : elements) {
                src = e.attr("src");
                e.attr("href", "");
            }
        }
    }*/

    private String formHtml(String content, String cssFileName)
    {
        return String.format("<!DOCTYPE html>" + "<html>" + "<head>" + "<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\">" + "</head>" + "<body>"
                // + "<div class='contentstyle' id='article_id' style='%s'>"
                + "%s"
                // + "</div>"
                + "</body>" + "</html>", cssFileName, content);
    }

    public CuriosityDaily parseCuriosityDaily(String url)
    {
        final Document doc = getDocument(url);
        if (doc == null) return null;

        Log.i(TAG, "parseCuriosityDaily: " + doc.outerHtml());
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
