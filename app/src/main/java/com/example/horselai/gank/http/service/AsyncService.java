package com.example.horselai.gank.http.service;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.example.horselai.gank.comm.ICallback;
import com.example.horselai.gank.http.request.HttpRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by horseLai on 2017/7/14.
 * <p>
 * 该类用于HTTP请求，所有请求均在线程池中进行
 */

public class AsyncService implements Closeable
{

    private static final ThreadPoolHandler THREAD_POOL_HANDLER = new ThreadPoolHandler();

    @Override public void close() throws IOException
    {
        THREAD_POOL_HANDLER.cancelAndClearTaskQueue();
        THREAD_POOL_HANDLER.closePoolNow();
    }


    static final class Builder
    {
        static final AsyncService SERVICE = new AsyncService();
    }

    public static AsyncService getService()
    {
        return Builder.SERVICE;
    }

    /**
     * 获取线程池管理器，通过这个管理器可以进一步管理线程池中的线程
     *
     * @return
     */
    public ThreadPoolHandler getPoolHandler()
    {
        return THREAD_POOL_HANDLER;
    }


    /**
     * GET方法获取json数据
     *
     * @param url
     * @param charset
     * @param callback 异步回调，请勿直接在方法中操作UI
     */
    public void fetchJsonAsync(final String url, final String charset, final ICallback<String, JsonElement> callback)
    {
        if (callback == null) {
            throw new IllegalArgumentException("callback can not be null !!");
        }
        requestGET(url, charset, null, new ICallback<String, String>()
        {
            @Override public void onSuccess(String key, String data)
            {
                JsonElement element = null;
                try {
                    final JsonParser parser = new JsonParser();
                    element = parser.parse(data);
                } catch (JsonSyntaxException e) {
                    element = null;
                    callback.onFail(new Exception("the content is not json !!"), url);
                }
                if (element != null) callback.onSuccess(url, element);

            }

            @Override public void onFail(Exception e, String key)
            {
                callback.onFail(e, url);
            }
        });
    }


    private Runnable newContentRequest(final String url, final String charset, final String method, final ArrayMap<String, String> postParams, final ArrayMap<String, String> heads, final ICallback<String, String> callback)
    {
        return new Runnable()
        {
            @Override public void run()
            {
                String content = null;
                try {

                    content = new HttpRequest.Creator().url(url).addRequestHeader(heads).postParams(postParams).create(method).doRequest("UTF-8");

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (TextUtils.isEmpty(content)) {
                    callback.onFail(new Exception("no content fetched !"), url);
                    return;
                }
                callback.onSuccess(url, content);

            }
        };
    }


    /**
     * @param url
     * @param charset
     * @param postParams
     * @param heads
     * @param callback   异步回调，请勿直接在方法中操作UI
     */
    public void requestPost(final String url, final String charset, ArrayMap<String, String> postParams, final ArrayMap<String, String> heads, final ICallback<String, String> callback)
    {
        if (callback == null) {
            throw new IllegalArgumentException("callback can not be null !!");
        }

        final Runnable task = newContentRequest(url, charset, HttpRequest.POST, postParams, heads, callback);
        THREAD_POOL_HANDLER.addToPool(task);

    }

    /**
     * @param url
     * @param charset
     * @param heads
     * @param callback 异步回调，请勿直接在方法中操作UI
     */
    public void requestGET(final String url, final String charset, final ArrayMap<String, String> heads, final ICallback<String, String> callback)
    {
        if (callback == null) {
            throw new IllegalArgumentException("callback can not be null !!");
        }

        final Runnable task = newContentRequest(url, charset, HttpRequest.GET, null, heads, callback);
        THREAD_POOL_HANDLER.addToPool(task);

    }

    public void addRequestTask(Runnable task)
    {
        THREAD_POOL_HANDLER.addToPool(task);
    }

    public void addRequestTask(Callable task)
    {
        THREAD_POOL_HANDLER.addToPool(task);
    }


    public void fetchHtml(String url, ICallback<String, String> callback)
    {


    }

    public void fetchXml(String url, ICallback<String, String> callback)
    {


    }

}
