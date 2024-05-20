package com.feiniaojin.graceful.connection;

import java.util.concurrent.TimeUnit;

public interface InvokeFuture<T> {


    long getInvokeId();

    /**
     * 会超时的等待
     *
     * @param timeout
     * @return
     * @throws InterruptedException
     */
    T get(final long timeout, TimeUnit timeUnit) throws InterruptedException;

    T get() throws InterruptedException;

    /**
     * 设置执行成功的结果
     *
     * @param t
     */
    void done(final T t);


    boolean isDone();
}
