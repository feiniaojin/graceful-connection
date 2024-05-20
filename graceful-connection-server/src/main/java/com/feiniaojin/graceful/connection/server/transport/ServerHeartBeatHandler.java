package com.feiniaojin.graceful.connection.server.transport;

import com.feiniaojin.pack.HeartBeatRequestPacket;
import com.feiniaojin.pack.HeartBeatResponsePacket;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ServerHeartBeatHandler extends SimpleChannelInboundHandler<HeartBeatRequestPacket> {

    private static Logger logger = LoggerFactory.getLogger(ServerHeartBeatHandler.class);

    private static ServerHeartBeatHandler instance = new ServerHeartBeatHandler();

    private ServerHeartBeatHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HeartBeatRequestPacket heartBeatPacket) throws Exception {
        System.out.println("收到心跳包：" + heartBeatPacket);
        HeartBeatResponsePacket beatResponsePacket = new HeartBeatResponsePacket();
        beatResponsePacket.setPingTime(heartBeatPacket.getPingTime());
        beatResponsePacket.setPongTime(System.currentTimeMillis());
        ctx.writeAndFlush(beatResponsePacket);
    }

    public static ServerHeartBeatHandler getInstance() {
        return instance;
    }
}
