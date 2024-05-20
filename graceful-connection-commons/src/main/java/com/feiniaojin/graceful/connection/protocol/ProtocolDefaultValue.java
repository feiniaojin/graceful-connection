package com.feiniaojin.graceful.connection.protocol;

/**
 * 协议默认值
 * +---------------------------------------------------------------+
 * | 魔数 4byte | 协议版本号 1byte | 序列化算法 1byte | 压缩算法 1byte  |
 * +---------------------------------------------------------------+
 * | 报文指令 2byte | 状态 1byte | 保留字段 4byte | 数据长度 4byte     |
 * +---------------------------------------------------------------+
 * |                   数据内容 （长度不定）                          |
 * +---------------------------------------------------------------+
 */
public class ProtocolDefaultValue {

    public static final int MAGIC_NUMBER = 0x19562024;

    /**
     * 协议版本号
     */
    public static final byte VERSION = 0;

    /**
     * 序列化算法，默认为protobuf
     */
    public static final byte SERIALIZE = 0;

    /**
     * 压缩算法，0不压缩
     */
    public static final byte COMPRESS = 0;

    /**
     * 状态
     */
    public static final byte STATUS = 0;

    /**
     * 保留字段
     */
    public static final int RETAIN = 0;

}
