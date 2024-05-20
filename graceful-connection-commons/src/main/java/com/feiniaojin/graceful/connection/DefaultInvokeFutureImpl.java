package com.feiniaojin.graceful.connection;

import com.feiniaojin.graceful.connection.invoke.InvokeContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DefaultInvokeFutureImpl<T> implements InvokeFuture<T> {

    private Long invokeId;

    private InvokeContext invokeContext;

    private volatile T t;

    /**
     * 异步转同步
     */
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public long getInvokeId() {
        return this.invokeId;
    }

    @Override
    public T get(long timeout, TimeUnit timeUnit) throws InterruptedException {
        countDownLatch.await(timeout, timeUnit);
        return t;
    }

    @Override
    public T get() throws InterruptedException {
        countDownLatch.await();
        return t;
    }

    @Override
    public void done(T t) {
        this.t = t;
        countDownLatch.countDown();
    }

    @Override
    public boolean isDone() {
        return countDownLatch.getCount() <= 0;
    }

    public void setInvokeId(Long invokeId) {
        this.invokeId = invokeId;
    }
}
