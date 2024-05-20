package com.feiniaojin.graceful.connection.client.connection;

import com.feiniaojin.AttributeKeys;
import com.feiniaojin.InvokeFuture;
import com.feiniaojin.Processor;
import com.feiniaojin.cluster.Node;
import com.feiniaojin.connetion.ConnectionContext;
import com.feiniaojin.connetion.ConnectionManager;
import com.feiniaojin.graceful.connection.client.transport.ClientHandler;
import com.feiniaojin.graceful.connection.client.transport.ClientHeartBeatHandler;
import com.feiniaojin.pack.RequestPacket;
import com.feiniaojin.pack.ResponsePacket;
import com.feiniaojin.protocol.ProtocolDecoder;
import com.feiniaojin.protocol.ProtocolEncoder;
import com.feiniaojin.protocol.Splitter;
import com.feiniaojin.serializer.ProtostuffSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;

@Data
public class ClientConnectionImpl implements ClientConnection {

    private Node node;

    private Channel channel;

    private Processor<RequestPacket, ResponsePacket> processor;

    private final ConcurrentHashMap<Long, InvokeFuture> invokeFutureMap = new ConcurrentHashMap<>();

    private ConnectionManager connectionManager;

    private ConnectionContext connectionContext;

    public ClientConnectionImpl(ConnectionContext connectionContext) {
        this.connectionContext = connectionContext;
    }

    public void start() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();
            bootstrap.group(workerGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline()
                            .addLast("clientIdleHandler", new IdleStateHandler(5, 0, 0))
                            .addLast(new Splitter())
                            .addLast(new ProtocolEncoder(protostuffSerializer))
                            .addLast(new ProtocolDecoder(protostuffSerializer))
                            .addLast(new ClientHeartBeatHandler())
                            .addLast(new ClientHandler(connectionContext.getSystemContext()));
                }
            });

            ChannelFuture channelFuture = bootstrap.connect(node.getIp(), node.getPort()).sync().addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("连接成功！");
                } else {
                    System.err.println("连接失败！");
                }
            });
            this.channel = channelFuture.channel();
            //channel关联连接，连接关联连接管理器
            channel.attr(AttributeKeys.CONNECTION).set(this);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("连接失败，等待下次重试");
            throw new RuntimeException(e);
        }
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
