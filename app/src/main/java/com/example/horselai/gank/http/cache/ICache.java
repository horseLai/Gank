package com.example.horselai.gank.http.cache;

import android.support.annotation.NonNull;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by laixiaolong on 2017/3/15.
 */

public interface ICache<K, V> extends Closeable
{
    V getFromCache(@NonNull K key) throws IOException;

    void putIntoCache(@NonNull K key, @NonNull V value) throws IOException;

    void removeFromCache(@NonNull K key) throws IOException;

    boolean contains(K key);

    void clearCache() throws IOException;

    void close() throws IOException;
}
