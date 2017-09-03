package com.example.horselai.gank.http.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.horselai.gank.app.App;

import java.util.ArrayDeque;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by laixiaolong on 2016/11/5.
 */

public class ThreadPoolHandler
{
    private static final String TAG = "ThreadPoolHandler >>> ";
    private ThreadPoolExecutor mExecutor = null;
    private static final LinkedBlockingQueue QUEUE = new LinkedBlockingQueue(128);
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(Runtime.getRuntime().availableProcessors() - 1, 4));
    private static final int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 15; //存活时间（超时时间）
    private ArrayDeque<Future> mFutureTasks;

    public ThreadPoolHandler()
    {
        mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, QUEUE, new Factory());
        mExecutor.allowCoreThreadTimeOut(true);
        mFutureTasks = new ArrayDeque<>();
    }

    public synchronized void setMaxPoolSize(int maxPoolSize)
    {
        if (maxPoolSize > 0 && maxPoolSize < Integer.MAX_VALUE)
            mExecutor.setMaximumPoolSize(maxPoolSize);
    }


    public synchronized Object addToPool(Callable<?> task)
    {
        if (stillWork() && !QUEUE.contains(task)) {
            try {
                return mExecutor.submit(task).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public synchronized void removeTaskFromQueue(Runnable task)
    {
        if (stillWork() && contains(task)) QUEUE.remove(task);
    }

    public boolean contains(Runnable task)
    {
        return QUEUE.contains(task);
    }

    public boolean contains(Callable<?> task)
    {
        return QUEUE.contains(task);
    }

    public synchronized void removeTaskFromQueue(Callable<?> task)
    {
        if (stillWork() && contains(task)) QUEUE.remove(task);
    }

    public synchronized void removeTaskFromQueue(FutureTask<?> task)
    {
        if (stillWork()) {
            task.cancel(true);
            mExecutor.purge();
        }
        if (mFutureTasks.contains(task)) mFutureTasks.remove(task);
    }

    public synchronized void clearTaskQueue()
    {
        if (stillWork() && !QUEUE.isEmpty()) {
            QUEUE.clear();
        }
    }

    public synchronized void cancelAndClearTaskQueue()
    {
        if (!stillWork()) {
            mFutureTasks.clear();
            return;
        }

        if (App.DEBUG)
            Log.i(TAG, String.format(Locale.getDefault(), "cancelAndClearTaskQueue: mFutureTasks size = %d", mFutureTasks.size()));
        Future f;
        for (int i = 0; i < mFutureTasks.size(); i++) {
            f = mFutureTasks.poll();
            if (f != null && !f.isCancelled() && !f.isDone()) {
                f.cancel(true);
            }
        }
        mExecutor.purge();
    }


    /**
     * 添加一个任务到线程池，如果包含此任务，那么将删旧添新
     *
     * @param task
     */
    public synchronized Future<?> addToPool(Runnable task)
    {
        if (!stillWork()) return null;
        final Future<?> future = mExecutor.submit(task);
        mFutureTasks.push(future);
        return future;
    }

    /**
     * 添加一个任务到线程池，如果包含此任务，那么将删旧添新
     *
     * @param task
     */
    public synchronized Future<?> addToPool(FutureTask<?> task)
    {
        if (!stillWork()) return null;

        if (QUEUE.contains(task)) {
            task.cancel(false);
            QUEUE.remove(task);
        }
        final Future<?> future = mExecutor.submit(task);
        mFutureTasks.add(future);
        return future;
    }

    public synchronized void closePool()
    {
        if (stillWork()) mExecutor.shutdown();
    }

    public synchronized void closePoolNow()
    {
        if (stillWork()) mExecutor.shutdownNow();
    }

    public int getActiveTaskSize()
    {
        if (stillWork()) return mExecutor.getActiveCount();
        return 0;
    }

    /**
     * Blocks until all tasks have completed execution after a shutdown
     * request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return {@code true} if this mExecutor terminated and
     * {@code false} if the timeout elapsed before termination
     * @throws InterruptedException if interrupted while waiting
     */
    public synchronized void closeUntilAllCompleted(long timeout, TimeUnit unit) throws InterruptedException
    {
        if (stillWork()) mExecutor.awaitTermination(timeout, unit);
    }

    /**
     * @return true if mExecutor is still Work, false otherwise
     */
    private boolean stillWork()
    {
        return mExecutor != null && (!mExecutor.isShutdown() || !mExecutor.isTerminated() || !mExecutor.isTerminating());
    }


    /**
     * 线程异常捕获器
     */
    private final class PoolExceptionCatcher implements Thread.UncaughtExceptionHandler
    {
        @Override public void uncaughtException(Thread t, Throwable e)
        {
            Log.i(TAG, "uncaughtException: " + e.getMessage());
            e.printStackTrace();
            //异常处理的逻辑
            // TODO: 2017/7/15  看看需要怎么处理

        }
    }

    /**
     * 自定义TreadFactory，在其中为每个线程设置线程异常捕获器
     */
    private final class Factory implements ThreadFactory
    {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override public Thread newThread(@NonNull Runnable r)
        {
            Thread thread = new Thread(r, "Thread in pool#" + mCount.getAndIncrement());
            thread.setUncaughtExceptionHandler(new PoolExceptionCatcher());
            return thread;
        }

    }
}
