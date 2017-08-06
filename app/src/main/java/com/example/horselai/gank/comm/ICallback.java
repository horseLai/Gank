package com.example.horselai.gank.comm;

/**
 * Created by horseLai on 2017/7/14.
 * <p>
 * <p>
 * 通用回掉接口， 泛型参数含义如下
 * <p>
 * <code>K</code> : 表示获取数据时所用的链接，或者id等；
 * 比方说,一个网络请求回调中<code>K</code>表示的就是那个网络链接的数据类型
 * </p>
 * <p>
 * <code>V</code> : 表示所得到的数据的数据类型
 * </p>
 * <p>
 * </p>
 */

public interface ICallback<K, V>
{
    void onSuccess(K key, V data);

    void onFail(Exception e, K key);

}
