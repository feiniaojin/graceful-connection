package com.feiniaojin.graceful.connection.server;

import com.feiniaojin.PrintProcessor;
import com.feiniaojin.graceful.connection.server.connection.ServerConnectionManager;
import com.feiniaojin.lb.Loadbalancer;
import com.feiniaojin.lb.RandomLoadbalancer;
import com.feiniaojin.graceful.connection.server.lifecycle.PrepareBeforeServerCreate;
import com.feiniaojin.graceful.connection.server.lifecycle.RegisterDiscoveryAfterServerStart;
import com.feiniaojin.graceful.connection.server.lifecycle.ServerCallbackRegister;
import com.feiniaojin.graceful.connection.server.transport.ServerHandler;
import com.feiniaojin.graceful.connection.server.transport.ServerHeartBeatHandler;
import com.feiniaojin.graceful.connection.server.transport.ServerIdleHandler;
import com.feiniaojin.lifecycle.LifeCycle;
import com.feiniaojin.protocol.ProtocolDecoder;
import com.feiniaojin.protocol.ProtocolEncoder;
import com.feiniaojin.protocol.Splitter;
import com.feiniaojin.serializer.ProtostuffSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class Server implements LifeCycle {

    ServerSystemContext systemContext;

    public Server() {
        //系统上下文
        systemContext = new ServerSystemContext();

        //配置连接管理器
        ServerConnectionManager serverConnectionManager = new ServerConnectionManager();
        systemContext.setConnectionManager(serverConnectionManager);
    }

    @Override
    public void start() {

        try {

            //负载均衡
            Loadbalancer loadbalancer = new RandomLoadbalancer();
            systemContext.setLoadbalancer(loadbalancer);

            //回调注册
            ServerCallbackRegister register = new ServerCallbackRegister();
            systemContext.setServerCallbackRegister(register);

            //创建服务前的回调
            register.registerCallback(new PrepareBeforeServerCreate());

            //服务启动后的回调
            register.registerCallback(new RegisterDiscoveryAfterServerStart());

            //执行创建前回调
            register.invokeBeforeCreateCallback(systemContext);
            systemContext.setProcessor(new PrintProcessor());
            //创建并启动服务
            doCrateServer(systemContext);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {
        //TODO 服务关闭
    }

    private void doCrateServer(ServerSystemContext systemContext) throws InterruptedException {

        EventLoopGroup boss;
        EventLoopGroup worker;
        Class<? extends ServerSocketChannel> serverSocketChannelClazz;
        //对于Epoll的处理
        if (Epoll.isAvailable()) {
            boss = new EpollEventLoopGroup();
            worker = new EpollEventLoopGroup();
            serverSocketChannelClazz = EpollServerSocketChannel.class;
        } else {
            boss = new NioEventLoopGroup();
            worker = new NioEventLoopGroup();
            serverSocketChannelClazz = NioServerSocketChannel.class;
        }

        try {
            ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                    .channel(serverSocketChannelClazz)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline()
                                    .addLast(new ServerIdleHandler())
                                    .addLast(new Splitter())
                                    .addLast(new ProtocolEncoder(protostuffSerializer))
                                    .addLast(new ProtocolDecoder(protostuffSerializer))
                                    .addLast(ServerHeartBeatHandler.getInstance())
                                    .addLast(new ServerHandler(systemContext));
                        }
                    });
            InetSocketAddress inetSocketAddress = systemContext.getInetSocketAddress();
            systemContext.setInetSocketAddress(inetSocketAddress);
            ChannelFuture channelFuture = serverBootstrap.bind(inetSocketAddress).sync();
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    //执行服务创建后的回调
                    systemContext.getServerCallbackRegister()
                            .invokeAfterStartCallback(systemContext);
                    System.out.println("Server start!");
                }
            });
            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    public ServerSystemContext getSystemContext() {
        return systemContext;
    }
}
