package com.example.horselai.gank.mvp.model;

import org.junit.Test;

/**
 * Created by horseLai on 2017/8/30.
 */
public class ReadingModelTest
{
    @Test public void parseCuriosityDaily() throws Exception
    {
        final String url = "http://www.qdaily.com/articles/44636.html?utm_medium=website&utm_source=gank.io%2Fxiandu&source=feed";
        new ReadingModel().parseCuriosityDaily(url);
    }

}