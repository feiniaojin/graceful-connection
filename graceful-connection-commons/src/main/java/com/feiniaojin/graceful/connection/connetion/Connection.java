package com.feiniaojin.graceful.connection.connetion;

import com.feiniaojin.graceful.connection.InvokeFuture;
import com.feiniaojin.graceful.connection.lifecycle.LifeCycle;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.ConcurrentHashMap;

public interface Connection extends LifeCycle {

    /**
     * 连接对应的Channel
     *
     * @return
     */
    Channel getChannel();

    /**
     * 连接管理器
     *
     * @return
     */
    ConnectionManager getConnectionManager();

    void setConnectionManager(ConnectionManager connectionManager);

    default ChannelFuture writeAndFlush(Object object) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    ConcurrentHashMap<Long, InvokeFuture> getInvokeFutureMap();

    void addInvokeFuture(InvokeFuture invokeFuture);

    void removeInvokeFuture(Long requestId);
}
