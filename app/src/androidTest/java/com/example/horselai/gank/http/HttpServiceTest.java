package com.example.horselai.gank.http;

import android.util.Log;

import com.example.horselai.gank.comm.ICallback;
import com.example.horselai.gank.http.service.AsyncService;
import com.google.gson.JsonElement;

import org.junit.Test;

/**
 * Created by horseLai on 2017/7/15.
 */
public class HttpServiceTest
{
    private static final String TAG = "HttpServiceTest";

    @Test public void fetchJson() throws Exception
    {
        final String url = "http://gank.io/api/data/Android/10/1";
        AsyncService.getService().fetchJsonAsync(url, "UTF-8", new ICallback<String, JsonElement>()
        {
            @Override public void onSuccess(String key, JsonElement data)
            {
                Log.i(TAG, "onSuccess: key : " + key);
                Log.i(TAG, "onSuccess: " + data.toString());
                System.out.println(data.toString());
            }

            @Override public void onFail(Exception e, String key)
            {
                Log.i(TAG, "onFail: " + e.getMessage() + " : " + key);
                e.printStackTrace();
            }
        });
    }

    @Test public void fetchHtml() throws Exception
    {
        System.out.println(" >>>>>> fetchHtml");
    }

    @Test public void fetchXml() throws Exception
    {

    }

}