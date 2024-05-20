package com.feiniaojin.graceful.connection.server.transport;

import com.feiniaojin.AttributeKeys;
import com.feiniaojin.InvokeFuture;
import com.feiniaojin.Processor;
import com.feiniaojin.connetion.Connection;
import com.feiniaojin.graceful.connection.server.ServerSystemContext;
import com.feiniaojin.graceful.connection.server.connection.ServerConnectionImpl;
import com.feiniaojin.graceful.connection.server.connection.ServerConnectionManager;
import com.feiniaojin.pack.Packet;
import com.feiniaojin.pack.RequestPacket;
import com.feiniaojin.pack.ResponsePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerHandler extends SimpleChannelInboundHandler<Packet> {

    private ServerSystemContext systemContext;

    ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 30,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(64));

    public ServerHandler(ServerSystemContext systemContext) {
        this.systemContext = systemContext;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("已连接到服务端");
        Channel channel = ctx.channel();
        ServerConnectionManager connectionManager = systemContext.getConnectionManager();
        Connection connection = new ServerConnectionImpl(channel, connectionManager);
        connectionManager.addConnection(connection);
        //关联连接
        channel.attr(AttributeKeys.CONNECTION).set(connection);
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        if (packet instanceof ResponsePacket) {
            //推送场景：如果收到客户端的响应
            ResponsePacket responsePacket = (ResponsePacket) packet;
            System.out.println("收到来自客户端的响应：" + packet);
            long requestId = responsePacket.getRequestId();
            Connection connection = channelHandlerContext.channel().attr(AttributeKeys.CONNECTION).get();
            InvokeFuture invokeFuture = connection.getInvokeFutureMap().get(requestId);
            if (invokeFuture != null) {
                invokeFuture.done(responsePacket);
            }
        } else if (packet instanceof RequestPacket) {
            //服务端接受请求的场景
            executor.submit(() -> {
                RequestPacket requestPacket = (RequestPacket) packet;
                System.out.println("收到来自客户端的请求：" + packet);
                Processor<RequestPacket, ResponsePacket> processor = systemContext.getProcessor();
                ResponsePacket responsePacket = processor.process(requestPacket);
                channelHandlerContext.channel().writeAndFlush(responsePacket);
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
