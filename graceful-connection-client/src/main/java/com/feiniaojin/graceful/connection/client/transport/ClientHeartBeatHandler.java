package com.feiniaojin.graceful.connection.client.transport;

import com.feiniaojin.cluster.Node;
import com.feiniaojin.graceful.connection.client.connection.ClientConnectionImpl;
import com.feiniaojin.graceful.connection.client.connection.ClientConnectionManager;
import com.feiniaojin.pack.HeartBeatRequestPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

import java.util.Collections;

public class ClientHeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            // send heartbeat
            Channel channel = ctx.channel();
            sendHeartBeat(channel);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    public void sendHeartBeat(Channel channel) {
        HeartBeatRequestPacket beatRequestPacket =
                new HeartBeatRequestPacket(System.currentTimeMillis());
        channel.writeAndFlush(beatRequestPacket);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端链接断开，即将开始重连");
        Channel channel = ctx.channel();
        ClientConnectionImpl clientConnection = (ClientConnectionImpl) channel.attr(AttributeKey.valueOf("connection")).get();
        if (clientConnection != null) {
            Node node = clientConnection.getNode();
            ClientConnectionManager connectionManager = (ClientConnectionManager) clientConnection.getConnectionManager();
            if (connectionManager != null) {
                connectionManager.removeConnection(node);
                connectionManager.addReConnectList(Collections.singletonList(node));
            }
        }
    }
}
