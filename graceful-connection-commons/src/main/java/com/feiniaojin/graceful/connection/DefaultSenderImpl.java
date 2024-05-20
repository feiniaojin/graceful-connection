package com.feiniaojin.graceful.connection;

import com.feiniaojin.graceful.connection.connetion.Connection;
import com.feiniaojin.graceful.connection.connetion.ConnectionManager;
import com.feiniaojin.graceful.connection.lb.Loadbalancer;
import com.feiniaojin.graceful.connection.pack.RequestPacket;
import com.feiniaojin.graceful.connection.pack.ResponsePacket;
import io.netty.channel.ChannelFuture;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class DefaultSenderImpl implements Sender<RequestPacket, ResponsePacket> {

    private AbstractSystemContext systemContext;

    public DefaultSenderImpl(AbstractSystemContext systemContext) {
        this.systemContext = systemContext;
    }

    @Override
    public void sendOneway(RequestPacket requestPacket) {
        if (Objects.isNull(requestPacket)) {
            throw new IllegalArgumentException();
        }
        try {
            //TODO 负载均衡
            ConnectionManager connectionManager = systemContext.getConnectionManager();
            List<Connection> connections = connectionManager.getConnections();
            Loadbalancer loadbalancer = systemContext.getLoadbalancer();
            Connection connection = loadbalancer.select(connections);
            if (Objects.isNull(connection)) {
                throw new RuntimeException("未匹配到连接");
            }
            //通过连接发送请求
            ChannelFuture sync = connection.writeAndFlush(requestPacket);
            if (sync.isSuccess()) {

            } else {
                //没有发送成功
                throw new RuntimeException();
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public ResponsePacket sendWaitResponse(RequestPacket requestPacket) {
        if (Objects.isNull(requestPacket)) {
            throw new IllegalArgumentException();
        }
        DefaultInvokeFutureImpl<ResponsePacket> invokeFuture = new DefaultInvokeFutureImpl<>();
        invokeFuture.setInvokeId(requestPacket.getRequestId());
        ConnectionManager connectionManager = systemContext.getConnectionManager();
        List<Connection> connections = connectionManager.getConnections();
        Loadbalancer loadbalancer = systemContext.getLoadbalancer();
        Connection connection = loadbalancer.select(connections);
        if (Objects.isNull(connection)) {
            throw new RuntimeException("未匹配到连接");
        }
        try {
            //TODO 负载均衡
            //通过连接发送请求
            ChannelFuture sync = connection.writeAndFlush(requestPacket);

            if (sync.isSuccess()) {
                connection.addInvokeFuture(invokeFuture);
                ResponsePacket responsePacket = invokeFuture.get();
                connection.removeInvokeFuture(requestPacket.getRequestId());
                return responsePacket;
            }
            //没有发送成功
            throw new RuntimeException();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        } finally {
            //清理，避免内存泄漏
            connection.removeInvokeFuture(requestPacket.getRequestId());
        }
    }

    @Override
    public ResponsePacket sendWaitResponse(RequestPacket requestPacket, long timeout, TimeUnit timeUnit) {
        if (Objects.isNull(requestPacket)) {
            throw new IllegalArgumentException();
        }
        DefaultInvokeFutureImpl<ResponsePacket> invokeFuture = new DefaultInvokeFutureImpl<>();
        invokeFuture.setInvokeId(requestPacket.getRequestId());
        ConnectionManager connectionManager = systemContext.getConnectionManager();
        List<Connection> connections = connectionManager.getConnections();
        Loadbalancer loadbalancer = systemContext.getLoadbalancer();
        Connection connection = loadbalancer.select(connections);
        if (Objects.isNull(connection)) {
            throw new RuntimeException("未匹配到连接");
        }
        try {
            //TODO 负载均衡
            ConcurrentHashMap<Long, InvokeFuture> invokeFutureMap = connection.getInvokeFutureMap();
            //通过连接发送请求
            ChannelFuture sync = connection.writeAndFlush(requestPacket);

            if (sync.isSuccess()) {
                connection.addInvokeFuture(invokeFuture);
                ResponsePacket responsePacket = invokeFuture.get(timeout, TimeUnit.MILLISECONDS);
                connection.removeInvokeFuture(requestPacket.getRequestId());
                return responsePacket;
            }
            //没有发送成功
            throw new RuntimeException();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        } finally {
            //清理，避免内存泄漏
            connection.removeInvokeFuture(requestPacket.getRequestId());
        }
    }

    @Override
    public boolean isAvailable() {
        ConnectionManager connectionManager = systemContext.getConnectionManager();
        List<Connection> connections = connectionManager.getConnections();
        if (connections == null
                || connections.isEmpty()) {
            return false;
        }
        return true;
    }
}
