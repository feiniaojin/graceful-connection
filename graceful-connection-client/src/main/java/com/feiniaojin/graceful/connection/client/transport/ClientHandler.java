package com.feiniaojin.graceful.connection.client.transport;

import com.feiniaojin.AbstractSystemContext;
import com.feiniaojin.AttributeKeys;
import com.feiniaojin.InvokeFuture;
import com.feiniaojin.Processor;
import com.feiniaojin.connetion.Connection;
import com.feiniaojin.pack.Packet;
import com.feiniaojin.pack.RequestPacket;
import com.feiniaojin.pack.ResponsePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClientHandler extends SimpleChannelInboundHandler<Packet> {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private AbstractSystemContext systemContext;

    ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 30,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(64));

    public ClientHandler(AbstractSystemContext systemContext) {
        this.systemContext = systemContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
        if (packet instanceof ResponsePacket) {
            //推送场景：如果收到客户端的响应
            ResponsePacket responsePacket = (ResponsePacket) packet;
            System.out.println("收到来自服务端的响应：" + packet);
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
                System.out.println("收到来自服务端的请求：" + packet);
                Processor<RequestPacket, ResponsePacket> processor = systemContext.getProcessor();
                ResponsePacket responsePacket = processor.process(requestPacket);
                channelHandlerContext.channel().writeAndFlush(responsePacket);
            });
        }
    }
}
