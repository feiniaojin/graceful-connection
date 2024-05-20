package com.feiniaojin.graceful.connection.server.connection;

import com.feiniaojin.InvokeFuture;
import com.feiniaojin.connetion.Connection;
import com.feiniaojin.connetion.ConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接的实现
 */
public class ServerConnectionImpl implements Connection {

    private Channel channel;

    private ServerConnectionManager serverConnectionManager;

    private final ConcurrentHashMap<Long, InvokeFuture> invokeFutureMap = new ConcurrentHashMap<>();

    public ServerConnectionImpl(Channel channel, ServerConnectionManager serverConnectionManager) {
        this.channel = channel;
        this.serverConnectionManager = serverConnectionManager;
        this.start();
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public ServerConnectionManager getConnectionManager() {
        return this.serverConnectionManager;
    }

    @Override
    public void setConnectionManager(ConnectionManager connectionManager) {
        this.serverConnectionManager = (ServerConnectionManager) connectionManager;
    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    public ChannelFuture writeAndFlush(Object object) throws InterruptedException {
        ChannelFuture future = this.channel.writeAndFlush(object).sync();
        return future;
    }

    @Override
    public ConcurrentHashMap<Long, InvokeFuture> getInvokeFutureMap() {
        return this.invokeFutureMap;
    }

    @Override
    public void addInvokeFuture(InvokeFuture invokeFuture) {
        this.invokeFutureMap.putIfAbsent(invokeFuture.getInvokeId(), invokeFuture);
    }

    @Override
    public void removeInvokeFuture(Long requestId) {
        this.invokeFutureMap.remove(requestId);
    }
}
