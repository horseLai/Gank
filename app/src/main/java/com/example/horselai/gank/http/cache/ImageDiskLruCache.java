package com.example.horselai.gank.http.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.example.horselai.gank.util.EncryptionUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by laixiaolong on 2017/3/19.
 */

public class ImageDiskLruCache implements ICache<String, Bitmap>
{

    private static final String TAG = "ImageDiskLruCache >>> ";
    private DiskLruCache mDiskLruCache;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private int mCompressQuality = 70;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final int STREAM_INDEX = 0;

    public ImageDiskLruCache(File diskCacheDir, long diskCacheSize, Bitmap.CompressFormat compressFormat, int compressQuality) throws IOException
    {
        if (!diskCacheDir.exists()) diskCacheDir.mkdirs();
        mDiskLruCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize);
        mCompressFormat = compressFormat;
        mCompressQuality = compressQuality;
    }

    public ImageDiskLruCache(File diskCacheDir, long diskCacheSize) throws IOException
    {
        this(diskCacheDir, diskCacheSize, Bitmap.CompressFormat.JPEG, 70);
    }

    @Override public Bitmap getFromCache(@NonNull String url) throws IOException
    {
        DiskLruCache.Snapshot snapshot = null;
        final String key = EncryptionUtil.generateMd5(url);
        try {
            snapshot = mDiskLruCache.get(key);
            if (snapshot == null) return null;
            //获取快照流数据
            final InputStream is = snapshot.getInputStream(STREAM_INDEX);
            Bitmap bitmap = null;
            if (is != null) {
                bitmap = BitmapFactory.decodeStream(is);
                DiskLruCache.closeQuietly(is);
            }

            return bitmap;
        } finally {
            if (snapshot != null) snapshot.close();
        }
    }

    @Override
    public void putIntoCache(@NonNull String key, @NonNull Bitmap value) throws IOException
    {
        final String md5 = EncryptionUtil.generateMd5(key);
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskLruCache.edit(md5);
            if (editor == null) return;
            OutputStream os = editor.newOutputStream(STREAM_INDEX);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            final boolean compress = value.compress(mCompressFormat, mCompressQuality, bos);

            if (compress) {
                mDiskLruCache.flush(); //刷新缓冲到文件系统
                editor.commit();
            } else {
                editor.abort(); //终止当前编辑
            }
            DiskLruCache.closeQuietly(bos);
            DiskLruCache.closeQuietly(os);

        } catch (IOException e) {
            if (editor != null) {
                try {
                    editor.abort();
                } catch (IOException e1) { //忽略此异常
                }
            }
            throw new IOException(e);
        }
    }

    @Override public void removeFromCache(@NonNull String key) throws IOException
    {
        final String md5 = EncryptionUtil.generateMd5(key);
        if (contains(key)) mDiskLruCache.remove(md5);
    }

    @Override public boolean contains(@NonNull String key)
    {
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskLruCache.get(key);
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

    /**
     * 关闭，并清空缓存
     *
     * @throws IOException
     */
    @Override public void clearCache() throws IOException
    {
        mDiskLruCache.delete();
    }

    /**
     * 只关闭，不删除缓存
     *
     * @throws IOException
     */
    @Override public void close() throws IOException
    {
        mDiskLruCache.close();
    }
}
