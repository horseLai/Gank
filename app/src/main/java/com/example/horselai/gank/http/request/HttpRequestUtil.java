package com.example.horselai.gank.http.request;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.util.Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by laixiaolong on 2016/11/3.
 * <p>
 * Http请求通用工具类，用于一般的网页请求，如果需要自定义请求头等信息，
 * 那么还是使用<code>HttpRequest</code>类吧
 * </p>
 */

public final class HttpRequestUtil
{
    private HttpRequestUtil()
    {
    }

    private static final String TAG = "HttpRequestUtil >>> ";
    public static final String REQUEST_METHOD_GET = "GET";
    public static final String REQUEST_METHOD_POST = "POST";
    public static final String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
    public static final String ACCEPT_LANGUAGE = "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3";
    public static final String CONNECTION = "keep-alive";
    public static final String Upgrade_Insecure_Requests = "1";
    public static final String ACCEPT_ENCODING = "gzip, deflate";
    public static final int REQUEST_TIMEOUT_MS = 15000;
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:52.0) Gecko/20100101 Firefox/52.0";
    private static final int MAX_STALE = 60 * 60 * 24 * 7; // tolerate 1-week  stale


    /**
     * 获取网络数据流
     *
     * @param method 方法名称
     * @param strUrl 目标接口地址
     * @param params 目标参数
     * @return 访问成功，返回数据流，不成功则返回null
     * @throws IOException
     */
    public static InputStream getHttpInputStream(String method, @NonNull String strUrl, @Nullable Map<String, Object> params) throws IOException
    {
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) initConnection(method, strUrl, params);
            if (!conn.getURL().getHost().equals(new URL(strUrl).getHost())) {
                //网页跳转了
                return null;
            }
            checkRequestMethod(method, params, conn);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return conn.getInputStream();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取网络数据流
     *
     * @param method 方法名称
     * @param strUrl 目标接口地址
     * @param params 目标参数
     * @return 访问成功，返回数据流，不成功则返回null
     * @throws IOException
     */
    public static InputStream getHttpsInputStream(String method, @NonNull String strUrl, @Nullable Map<String, Object> params) throws IOException
    {
        HttpsURLConnection conn = null;

        conn = (HttpsURLConnection) initConnection(method, strUrl, params);
        if (!conn.getURL().getHost().equals(new URL(strUrl).getHost())) {
            //网页跳转了
            return null;
        }
        checkRequestMethod(method, params, conn);
        if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }

        return null;
    }


    private static URLConnection initConnection(String method, @NonNull String strUrl, @Nullable Map<String, Object> params) throws IllegalArgumentException, IOException
    {
        if (method == null) throw new IllegalArgumentException("method could not be null !");
        if (URLDecoder.decode(strUrl, "UTF-8") == null) {
            throw new IllegalArgumentException("strUrl is not illegal !");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(strUrl);
        if (method.equalsIgnoreCase("") || method.equalsIgnoreCase(REQUEST_METHOD_GET)) {
            sb.append("?").append(decodePostArgs(params));
        }
        URL url = new URL(sb.toString());
        final URLConnection conn = url.openConnection();
        addCommRequestProperty(conn);
        return conn;
    }


    private static void addCommRequestProperty(URLConnection conn)
    {
        conn.addRequestProperty("User-Agent", USER_AGENT);
        conn.addRequestProperty("Accept", ACCEPT);
        conn.addRequestProperty("Accept-Language", ACCEPT_LANGUAGE);
        conn.addRequestProperty("Connection", CONNECTION);
        conn.addRequestProperty("Upgrade-Insecure-Requests", Upgrade_Insecure_Requests);
        conn.setReadTimeout(REQUEST_TIMEOUT_MS);
    }


    private static void checkRequestMethod(String method, @Nullable Map<String, Object> params, HttpURLConnection conn) throws IOException
    {
        if (method.equalsIgnoreCase("") || method.equalsIgnoreCase(REQUEST_METHOD_GET)) {
            conn.setRequestMethod(REQUEST_METHOD_GET);
        } else if (method.equalsIgnoreCase(REQUEST_METHOD_POST)) {
            conn.setRequestMethod(REQUEST_METHOD_POST);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            if (params != null) {

                PrintWriter pw = new PrintWriter(conn.getOutputStream());
                pw.write(decodePostArgs(params));
                pw.flush();

                pw.close();
            }
        }
        conn.connect();
    }


    /**
     * 获取网络 数据
     *
     * @param method 方法名称
     * @param strUrl 目标接口地址
     * @param params 目标参数
     * @return 访问成功，返回数据，不成功则返回null
     */
    public static String getContentFromNetWithCache(@NonNull String method, @NonNull String charset, @NonNull String strUrl, @Nullable Map<String, Object> params) throws IOException
    {
        Log.i(TAG, "getContentFromNetWithCache: 从网络中读取文档！# " + strUrl);
        InputStream httpInputStream = null;
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) initConnection(method, strUrl, params);
            conn.addRequestProperty("Cache-Control", "only-if-cached");
            conn.addRequestProperty("Cache-Control", "max-stale=" + MAX_STALE);
            httpInputStream = conn.getInputStream();
            return parseString(charset, httpInputStream);
        } catch (IOException e) {
            try {
                conn = (HttpURLConnection) initConnection(method, strUrl, params);
                if (!conn.getURL().getHost().equals(new URL(strUrl).getHost())) {
                    //网页跳转了
                    return null;
                }
                conn.addRequestProperty("Cache-Control", "no-cache");
                httpInputStream = conn.getInputStream();
            } catch (IOException e1) {
                throw new IOException(e1);
            }
            return parseString(charset, httpInputStream);
        } finally {
            Utils.close(httpInputStream);
        }

    }

    /**
     * 从数据流中解析出文本数据
     *
     * @param charset
     * @param httpInputStream
     * @return
     * @throws IOException
     */
    @Nullable
    public static String parseString(@NonNull String charset, InputStream httpInputStream) throws IOException
    {
        InputStreamReader isr = null;
        BufferedReader br = null;
        if (httpInputStream != null) {
            try {
                isr = new InputStreamReader(httpInputStream, Charset.forName(charset));
                br = new BufferedReader(isr);
                String line;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                return sb.toString();
            } finally {
                Utils.close(br, isr);
            }
        }
        return null;
    }


    /**
     * 获取网络数据，并且不缓存数据
     *
     * @param method 方法名称
     * @param strUrl 目标接口地址
     * @param params 目标参数
     * @return 访问成功，返回数据，不成功则返回null
     */
    public static String getContentFromNetWithNoCache(@NonNull String method, @NonNull String charset, @NonNull String strUrl, @Nullable Map<String, Object> params) throws IOException
    {
        //Log.i(TAG, "getContentFromNetWithCache: 从网络中读取文档！# " + strUrl);
        InputStream httpInputStream = null;
        try {
            httpInputStream = getHttpInputStream(method, strUrl, params);
            if (httpInputStream == null) return null;

            return parseString(charset, httpInputStream);
        } finally {
            Utils.close(httpInputStream);
        }
    }

    /**
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String decodePostArgs(Map<String, Object> params) throws UnsupportedEncodingException
    {
        if (params == null) return "";
        StringBuilder sb = new StringBuilder();

        int index = 0;
        final int size = params.size() - 1;

        for (ArrayMap.Entry entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue() + "", "UTF-8"));
            if (index++ < size) sb.append("&");
        }
        return sb.toString();
    }


    /**
     * 初始化HttpResponseCache缓存机制
     *
     * @param context
     */
    public static void installHttpResponseCache(Context context, long httpCacheSize) throws IOException
    {
        if (httpCacheSize <= 0) httpCacheSize = 20 * 1024 * 1024; // 20 MiB
        HttpResponseCache.install(App.getHttpCacheDir(), httpCacheSize);
    }

    /**
     * 刷新HttpResponseCache缓存到内存，在onStop()回调中调用即可
     */
    public static void flushHttpResponseCache()
    {
        final HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }


    /**
     * 删除所有缓存
     */
    public static void deleteHttpResponseCache() throws IOException
    {
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.delete();
        }
    }
}
