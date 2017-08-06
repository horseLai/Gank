package com.example.horselai.gank.http.cache;

import android.support.annotation.NonNull;

import com.example.horselai.gank.util.EncryptionUtil;
import com.example.horselai.gank.util.Utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by laixiaolong on 2016/12/6.
 */

public class ContentDiskCache implements ICache<String, String>
{

    private static final String TAG = "ContentDiskCache >>> ";
    private DiskLruCache mDiskLruCache;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;

    public ContentDiskCache(File diskCacheDir, long diskCacheSize) throws IOException
    {
        mDiskLruCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize);
    }

    @Override public String getFromCache(@NonNull String key)
    {
        final String md5 = EncryptionUtil.generateMd5(key);
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskLruCache.get(md5);
            if (snapshot == null) return null;

            final InputStream is = snapshot.getInputStream(0);
            InputStreamReader isr = new InputStreamReader(is);
            StringBuilder sb = new StringBuilder();
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) sb.append(line).append("\n");

            Utils.close(br, isr, is);

            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) snapshot.close();
        }

        return null;
    }

    @Override public void putIntoCache(@NonNull String key, @NonNull String value)
    {

        final String md5 = EncryptionUtil.generateMd5(key);
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskLruCache.edit(md5);
            if (editor == null) return;

            OutputStream os = editor.newOutputStream(0);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            bos.write(value.getBytes());
            mDiskLruCache.flush();
            editor.commit();

            Utils.close(bos, os);

        } catch (IOException e) {
            e.printStackTrace();
            if (editor != null) {
                try {
                    editor.abort();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override public void removeFromCache(@NonNull String key)
    {
        final String md5 = EncryptionUtil.generateMd5(key);
        try {
            if (contains(key)) mDiskLruCache.remove(md5);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public boolean contains(@NonNull String key)
    {
        final String md5 = EncryptionUtil.generateMd5(key);
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskLruCache.get(md5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (snapshot == null) {
            return false;
        } else {
            snapshot.close();
            return true;
        }
    }

    @Override public void clearCache()
    {
        try {
            mDiskLruCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void close() throws IOException
    {
        mDiskLruCache.close();
    }

    public boolean remove(String key)
    {
        final String md5 = EncryptionUtil.generateMd5(key);
        try {
            if (contains(key)) return mDiskLruCache.remove(md5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
