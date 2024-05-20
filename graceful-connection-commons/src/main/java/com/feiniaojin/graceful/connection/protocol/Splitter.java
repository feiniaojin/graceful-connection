package com.feiniaojin.graceful.connection.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 粘包分包解决，且不支持的协议直接关掉
 * +---------------------------------------------------------------+
 * | 魔数 4byte | 协议版本号 1byte | 序列化算法 1byte | 压缩算法 1byte  |
 * +---------------------------------------------------------------+
 * | 报文指令 2byte | 状态 1byte | 保留字段 4byte | 数据长度 4byte     |
 * +---------------------------------------------------------------+
 * |                   数据内容 （长度不定）                          |
 * +---------------------------------------------------------------+
 */
public class Splitter extends LengthFieldBasedFrameDecoder {
    private static final int LENGTH_FIELD_OFFSET = 14;

    private static final int LENGTH_FIELD_LENGTH = 4;

    public Splitter() {
        super(Integer.MAX_VALUE, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 屏蔽非本协议的客户端
        in.markReaderIndex();
        if (in.readableBytes() < 4) {
            in.resetReaderIndex();
            return null;
        }
        if (in.readInt() != ProtocolDefaultValue.MAGIC_NUMBER) {
            ctx.channel().close();
            return null;
        }
        in.resetReaderIndex();
        return super.decode(ctx, in);
    }
}
