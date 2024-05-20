package com.feiniaojin.graceful.connection.server.transport;

import com.feiniaojin.AttributeKeys;
import com.feiniaojin.connetion.Connection;
import com.feiniaojin.graceful.connection.server.connection.ServerConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 心跳检测，15s空闲，断开连接
 */
public class ServerIdleHandler extends IdleStateHandler {

    private static Logger logger = LoggerFactory.getLogger(ServerIdleHandler.class);

    private static int HEART_BEAT_TIME = 15;

    public ServerIdleHandler() {
        super(0, 0, HEART_BEAT_TIME);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {

        System.out.println("无数据读写，关闭连接..." + HEART_BEAT_TIME);
        Channel channel = ctx.channel();
        Connection serverConnection = channel.attr(AttributeKeys.CONNECTION).get();

        if (serverConnection != null
                && serverConnection.getConnectionManager() != null) {
            ServerConnectionManager connectionManager = (ServerConnectionManager) serverConnection.getConnectionManager();
            connectionManager.removeConnection(serverConnection);
        }
        ctx.channel().close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接关闭");
        Channel channel = ctx.channel();
        Connection connection = channel.attr(AttributeKeys.CONNECTION).get();
        ServerConnectionManager connectionManager = (ServerConnectionManager) connection.getConnectionManager();
        connectionManager.removeConnection(connection);
        super.channelInactive(ctx);
    }
}
