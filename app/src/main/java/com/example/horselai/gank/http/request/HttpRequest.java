package com.example.horselai.gank.http.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.example.horselai.gank.app.App;
import com.example.horselai.gank.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by laixiaolong on 2017/4/16.
 * <p>
 * <pre>
 *  //简单使用（读取字符串）
 *  String content =  new HttpRequest.Creator()
 * .addRequestHeader(generateReqParams()) //自定义请求消息头
 * .requestTimeOut(15000)  //默认15000
 * .canCache(false)  //是否缓存数据到本地，默认false
 * .url(...)
 * .create(HttpRequest.GET)   //创建连接（支持GET,POST请求方式）
 * .doRequest("UTF-8");  //发送请求，并指定数据编码格式
 *
 *
 *
 *
 * //读取数据流，如图片
 * InputStream is = new HttpRequest.Creator()
 * .canCache(false)
 * .url(...)
 * .create(HttpRequest.GET)   //创建连接（支持GET,POST请求方式）
 * .connect()       //开始连接
 * .getInputStream();
 *
 * //读取流之后记得断开连接
 * disconnect();
 * </pre>
 */


public class HttpRequest
{
    private static final String TAG = "HttpRequest";

    public static final String GET = "GET";
    public static final String POST = "POST";
    private static final int MAX_STALE = 60 * 60 * 24 * 7; // tolerate 1-week  stale
    private ArrayMap<String, String> requestParams;
    private String strPostParams;
    private URL url;
    private String method;
    //如果key已存在，则替换
    private ArrayMap<String, String> postParams;
    private HttpURLConnection conn;
    private int timeOut;
    private boolean canCache;

    private HttpRequest(Creator builder)
    {
        requestParams = builder.requestHeaders;
        this.url = builder.url;
        this.strPostParams = builder.strPostParams;
        this.postParams = builder.postParams;
        this.method = builder.method;
        this.timeOut = builder.timeOut;
        this.canCache = builder.canCache;
    }


    /**
     * 创建一个通用请求
     *
     * @param url
     * @param canCache 是否缓存
     * @return 返回一个已经连接好的请求
     * @throws Exception
     */
    public static HttpRequest newNormalRequest(String url, boolean canCache) throws Exception
    {
        Log.i(TAG, "newNormalRequest: " + url);
        return new HttpRequest.Creator().canCache(canCache).url(url).create(HttpRequest.GET);
    }

    /**
     * 记得在流处理完成后中断连接
     */
    public InputStream getInputStream()
    {
        if (conn != null) {
            //
            if (canCache) {
                conn.addRequestProperty("Cache-Control", "only-if-cached");
                conn.addRequestProperty("Cache-Control", "max-stale=" + MAX_STALE);
            }
            InputStream is = null;
            try {
                Log.i(TAG, "getInputStream: " + conn.getResponseMessage());
                is = canCache ? conn.getInputStream() : //读缓存
                        conn.getResponseCode() == HttpURLConnection.HTTP_OK ?//读网络
                                conn.getInputStream() : null;
            } catch (IOException e) {
                if (canCache) { // 缓存读取失败时读网络
                    if (App.DEBUG)
                        Log.i(TAG, "getInputStream: failed to read cache .. now fetch from net");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Cache-Control", "max-age=0");
                    try {
                        is = conn.getResponseCode() == HttpURLConnection.HTTP_OK ? conn.getInputStream() : null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            return is;
        }
        return null;
    }


    /**
     * 流处理完成后中断连接（配合getInputStream()使用）
     */
    public void disconnect()
    {
        if (conn != null) {
            conn.disconnect();
        }
    }

    // TODO: 2017/4/18 处理 301 网页重定向问题
    private HttpRequest connect()
    {
        try {

            conn = (HttpURLConnection) url.openConnection();
            if (!conn.getURL().getHost().equals(url.getHost())) {
                //网页重定向了
                Log.i(TAG, "connect: 重定向啦  " + conn.getResponseCode());
                Log.i(TAG, "connect: Error::" + parseString("utf-8", conn.getErrorStream()));
                conn = null;
                return this;
            }
            //添加请求消息头
            for (int i = 0; i < requestParams.size(); i++) {
                conn.addRequestProperty(requestParams.keyAt(i), requestParams.valueAt(i));
            }

            conn.setReadTimeout(timeOut);
            conn.setRequestMethod(method);

            if (method.equalsIgnoreCase(POST)) {
                String params = "";
                if (postParams != null && !postParams.isEmpty()) {
                    params = decodePostArgs(postParams);
                } else if (!TextUtils.isEmpty(strPostParams)) {
                    params = strPostParams;
                }
                conn.setDoOutput(true);
                conn.setChunkedStreamingMode(0);

                PrintWriter pw = new PrintWriter(conn.getOutputStream());
                pw.write(params);
                pw.flush();
                pw.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }


    public ArrayMap<String, String> getRequestParams()
    {
        return requestParams;
    }

    public URL getUrl()
    {
        return url;
    }

    /**
     * 文本类请求
     *
     * @param charset
     * @return 文本数据
     */
    public String doRequest(String charset)
    {
        final InputStream is = getInputStream();

        try {
            if (is != null) {
                return parseString(charset, is);
            }
        } finally {
            Utils.close(is);
            conn.disconnect();
        }
        return null;
    }

    @Nullable public static String parseString(@NonNull String charset, InputStream httpInputStream)
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
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Utils.close(br, isr);
            }
        }
        return null;
    }


    private String decodePostArgs(Map<String, String> params) throws UnsupportedEncodingException
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

    public static class Creator
    {
        private ArrayMap<String, String> requestHeaders;
        private ArrayMap<String, String> postParams;
        private String strPostParams;
        private URL url;
        private String method;
        private int timeOut = 10000;
        private boolean canCache = false;

        public Creator()
        {
            requestHeaders = new ArrayMap<>();
            //初始化默认消息头
            requestHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36");
            requestHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            requestHeaders.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
            requestHeaders.put("Connection", "keep-alive");
            requestHeaders.put("Upgrade-Insecure-Requests", "1");
        }

        public Creator canCache(boolean canCache)
        {
            this.canCache = canCache;
            return this;
        }

        public Creator addRequestHeader(String key, String value) throws IllegalArgumentException
        {
            if (TextUtils.isEmpty(key) && TextUtils.isEmpty(value)) {
                throw new IllegalArgumentException("参数错误！");
            }
            if (!requestHeaders.containsKey(key)) requestHeaders.put(key, value);
            return this;
        }

        public Creator addRequestHeader(ArrayMap<String, String> params) throws IllegalArgumentException
        {
            if (params != null && params.isEmpty()) {
                requestHeaders.putAll(params);
            }
            return this;
        }

        public Creator url(String url) throws MalformedURLException
        {
            this.url = URI.create(url).toURL();
            return this;
        }

        public Creator requestTimeOut(int timeOut)
        {
            this.timeOut = timeOut;
            return this;
        }


        public Creator postParams(ArrayMap<String, String> params)
        {
            this.postParams = params;
            return this;
        }

        public Creator postParams(String params)
        {
            this.strPostParams = params;
            return this;
        }


        public HttpRequest create(String method) throws Exception
        {
            this.method = method;
            if (TextUtils.isEmpty(method)) this.method = HttpRequest.GET;

            if (url == null) throw new Exception("请确认参数是否完整！");
            return new HttpRequest(this).connect();
        }


    }

}
