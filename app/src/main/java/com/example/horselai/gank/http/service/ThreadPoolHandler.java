package com.example.horselai.gank.http.service;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
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
    private ThreadPoolExecutor executor = null;
    private static final LinkedBlockingQueue QUEUE = new LinkedBlockingQueue(128);
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(Runtime.getRuntime().availableProcessors() - 1, 4));
    private static final int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 15; //存活时间（超时时间）

    public ThreadPoolHandler()
    {
        executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS, QUEUE, new Factory());
        executor.allowCoreThreadTimeOut(true);
    }

    public synchronized void setMaxPoolSize(int maxPoolSize)
    {
        if (maxPoolSize > 0 && maxPoolSize < Integer.MAX_VALUE)
            executor.setMaximumPoolSize(maxPoolSize);
    }


    public synchronized Object addToPool(Callable<?> task)
    {
        if (stillWork() && !QUEUE.contains(task)) {
            try {
                return executor.submit(task).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public synchronized void removeTaskFromQueue(Runnable task)
    {
        if (contains(task)) QUEUE.remove(task);
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
        if (contains(task)) QUEUE.remove(task);
    }

    public synchronized void removeTaskFromQueue(FutureTask<?> task)
    {
        if (contains(task)) QUEUE.remove(task);
    }

    public synchronized void clearTaskQueue()
    {
        if (!QUEUE.isEmpty()) QUEUE.clear();
    }


    /**
     * 添加一个任务到线程池，如果包含此任务，那么将删旧添新
     *
     * @param task
     */
    public synchronized void addToPool(Runnable task)
    {
        if (!stillWork()) return;
        if (QUEUE.contains(task)) {
            QUEUE.remove(task);
        }
        executor.execute(task);
    }

    /**
     * 添加一个任务到线程池，如果包含此任务，那么将删旧添新
     *
     * @param task
     */
    public synchronized void addToPool(FutureTask<?> task)
    {
        if (!stillWork()) return;

        if (QUEUE.contains(task)) {
            task.cancel(false);
            QUEUE.remove(task);
        }
        executor.execute(task);
    }

    public synchronized void closePool()
    {
        if (stillWork()) executor.shutdown();
    }

    public synchronized void closePoolNow()
    {
        if (stillWork()) executor.shutdownNow();
    }

    public int getActiveTaskSize()
    {
        if (stillWork()) return executor.getActiveCount();
        return 0;
    }

    /**
     * Blocks until all tasks have completed execution after a shutdown
     * request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return {@code true} if this executor terminated and
     * {@code false} if the timeout elapsed before termination
     * @throws InterruptedException if interrupted while waiting
     */
    public synchronized void closeUntilAllCompleted(long timeout, TimeUnit unit) throws InterruptedException
    {
        if (stillWork()) executor.awaitTermination(timeout, unit);
    }

    /**
     * @return true if executor is still Work, false otherwise
     */
    private boolean stillWork()
    {
        return executor != null && (!executor.isShutdown() || !executor.isTerminated() || !executor.isTerminating());
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
