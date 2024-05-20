package com.feiniaojin.graceful.connection.protocol;

import com.feiniaojin.graceful.connection.pack.Packet;
import com.feiniaojin.graceful.connection.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * +---------------------------------------------------------------+
 * | 魔数 4byte | 协议版本号 1byte | 序列化算法 1byte | 压缩算法 1byte  |
 * +---------------------------------------------------------------+
 * | 报文指令 2byte | 状态 1byte | 保留字段 4byte | 数据长度 4byte     |
 * +---------------------------------------------------------------+
 * |                   数据内容 （长度不定）                          |
 * +---------------------------------------------------------------+
 */
public class ProtocolEncoder extends MessageToByteEncoder<Packet> {

    private Serializer serializer;

    public ProtocolEncoder(Serializer serializer) {
        this.serializer = serializer;
    }


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          Packet packet,
                          ByteBuf byteBuf) throws Exception {
        //魔数
        byteBuf.writeInt(ProtocolDefaultValue.MAGIC_NUMBER);
        //协议版本号
        byteBuf.writeByte(ProtocolDefaultValue.VERSION);
        //序列化算法
        byteBuf.writeByte(ProtocolDefaultValue.SERIALIZE);
        //压缩算法
        byteBuf.writeByte(ProtocolDefaultValue.COMPRESS);
        //命令
        Short command = packet.getCommand();
        byteBuf.writeShort(command);
        //状态
        byteBuf.writeByte(ProtocolDefaultValue.STATUS);
        //保留字段
        byteBuf.writeInt(ProtocolDefaultValue.RETAIN);

        byte[] serialize = serializer.serialize(packet);
        //长度
        byteBuf.writeInt(serialize.length);
        //数据
        byteBuf.writeBytes(serialize);
    }
}
