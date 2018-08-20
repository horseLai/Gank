package com.example.horselai.gank.mvp.model;

import android.util.ArrayMap;
import android.util.Log;

import com.example.horselai.gank.base.BaseMultipleTypeListAdapter;
import com.example.horselai.gank.bean.GankSearch;
import com.example.horselai.gank.bean.home.CommHomeItem;
import com.example.horselai.gank.http.api.GankApi;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by horseLai on 2017/8/1.
 */
public class GankFetcherTest
{
    private static final String TAG = "GankFetcherTest";

    @Test public void search() throws Exception
    {
        final ArrayMap<String, Object> search = GankFetcher.getFetcher().search(GankApi.ANDROID, BaseMultipleTypeListAdapter.ItemType.TYPE_LINEAR);
        if (search == null) {
            Log.i(TAG, "search: nothing fetched !!");
            return;
        }

        Log.i(TAG, "search: source: " + search.get(GankFetcher.SEARCH_CATEGORY));
        Log.i(TAG, "search: total : " + search.get(GankFetcher.SEARCH_TOTAL));
        ArrayList<GankSearch> result = (ArrayList<GankSearch>) search.get(GankFetcher.SEARCH_DATA);

        System.out.println("**********************desc********************");
        System.out.println("desc >>: " + result.get(0).desc);
        System.out.println("**********************type********************");
        System.out.println("type >>: " + result.get(0).type);

        final Document doc = Jsoup.parse(result.get(0).readability);
        if (doc == null) return;
        System.out.println("**********************title********************");
        System.out.println("text >>: " + doc.title());
        System.out.println("**********************outerHtml********************");
        System.out.println("text >>: " + doc.outerHtml());
        System.out.println("**********************text********************");
        System.out.println("text >>: " + doc.text());
        System.out.println("**********************sub text********************");
        System.out.println("text >>: " + doc.text().substring(0, 200));

    }

    @Test
    public void fetchHome()
    {
        ArrayList<CommHomeItem> commHomeItems = GankFetcher.getFetcher().formHomePageGank();
        System.out.println(commHomeItems);
    }

}