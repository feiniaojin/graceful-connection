package com.feiniaojin.graceful.connection.protocol;

import com.feiniaojin.graceful.connection.CommandPacketMapping;
import com.feiniaojin.graceful.connection.pack.Packet;
import com.feiniaojin.graceful.connection.serializer.ProtostuffSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * +---------------------------------------------------------------+
 * | 魔数 4byte | 协议版本号 1byte | 序列化算法 1byte | 压缩算法 1byte  |
 * +---------------------------------------------------------------+
 * | 报文指令 2byte | 状态 1byte | 保留字段 4byte | 数据长度 4byte     |
 * +---------------------------------------------------------------+
 * |                   数据内容 （长度不定）                          |
 * +---------------------------------------------------------------+
 */
public class ProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {

    ProtostuffSerializer serializer;

    public ProtocolDecoder(ProtostuffSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext,
                          ByteBuf byteBuf,
                          List<Object> list) throws Exception {
        //跳过魔数和协议版本，一共5字节
        byteBuf.skipBytes(5);
        //序列化算法
        byte ser = byteBuf.readByte();
        System.out.println("序列化算法为ser=" + ser);
        //压缩算法
        byte com = byteBuf.readByte();
        System.out.println("压缩算法为com=" + com);
        short command = byteBuf.readShort();
        System.out.println("command=" + command);
        //跳过状态和保留字段，一共5字节
        byteBuf.skipBytes(5);
        int length = byteBuf.readInt();
        byte[] data = new byte[length];
        byteBuf.readBytes(data);
        Class<? extends Packet> aClass = CommandPacketMapping.MAP.get(command);
        Object deserialized = serializer.deserialize(data, aClass);
        list.add(deserialized);
    }
}
