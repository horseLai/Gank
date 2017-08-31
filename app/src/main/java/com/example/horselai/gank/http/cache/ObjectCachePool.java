package com.example.horselai.gank.http.cache;

import android.util.Log;

import com.example.horselai.gank.app.App;

import java.io.Closeable;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by horseLai on 2017/8/15.
 * <p>
 * ImageLoader中并发请求时， Result的缓冲队列，用于并发操作时减少对象创建次数，降低GC频率
 */
public class ObjectCachePool<T extends Closeable> implements Closeable
{
    private static final String TAG = "ObjectCachePool";
    private final Class<T> tClass;
    private final int mPoolSize;

    //以下两队列用来作为Result缓冲池，减少创建对象的次数以及GC频率
    //1.用来记录正在使用的Result
    private ConcurrentLinkedQueue<T> mInUseObjQueue;
    //2.用来记录已回收的Result
    private ConcurrentLinkedQueue<T> mFreeObjQueue;

    /**
     * @param poolSize 指定对象池大小
     * @param tClass   对应类必须包含无参构造方法，否则会出错
     */
    public ObjectCachePool(int poolSize, Class<T> tClass)
    {
        this.mPoolSize = poolSize;
        this.tClass = tClass;
    }

    /**
     * @param poolSize        指定对象池大小
     * @param initialObjCount 初始化创建的对象数量，这个可以减轻启动时的对象创建压力（注意： initialObjCount <= poolSize）
     * @param tClass          对应类必须包含无参构造方法，否则会出错
     */
    public ObjectCachePool(int poolSize, int initialObjCount, Class<T> tClass)
    {
        this.mPoolSize = poolSize;
        this.tClass = tClass;
        mInUseObjQueue = new ConcurrentLinkedQueue<>();
        mFreeObjQueue = new ConcurrentLinkedQueue<>();

        for (int i = 0; i < initialObjCount; i++) {
            try {
                mFreeObjQueue.add(tClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    }


    /**
     * 回收Result对象
     */
    public void recycle(T obj)
    {
        if (obj == null) return;
        if (mInUseObjQueue.contains(obj)) mInUseObjQueue.remove(obj);
        mFreeObjQueue.add(obj);

        // 检测容量
        checkCapacity();
    }

    private void checkCapacity()
    {
        //在空闲的时候检测，高使用时不做处理
        if (mInUseObjQueue.isEmpty()) return;
        //取出多余项释放掉
        int needFree = mFreeObjQueue.size() - mPoolSize;
        while ((needFree--) > 0) {
            try {
                mFreeObjQueue.poll().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public T get()
    {
        T obj = null;
        if (!mFreeObjQueue.isEmpty()) {
            obj = mFreeObjQueue.poll();
        } else {
            try {
                obj = tClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        mInUseObjQueue.add(obj);
        if (App.DEBUG)
            Log.i(TAG, String.format(Locale.getDefault(), "get: %s inUse = %d , free = %d", obj.getClass().getSimpleName(), mInUseObjQueue.size(), mFreeObjQueue.size()));
        return obj;
    }

    @Override public void close() throws IOException
    {
        for (int i = 0; i < mFreeObjQueue.size(); i++) {
            try {
                mFreeObjQueue.poll().close();
            } catch (IOException e) {
                // do nothing
            }
        }
        for (int i = 0; i < mInUseObjQueue.size(); i++) {
            try {
                mInUseObjQueue.poll().close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

}
