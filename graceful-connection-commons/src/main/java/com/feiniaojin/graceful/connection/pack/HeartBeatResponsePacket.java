package com.feiniaojin.graceful.connection.pack;

import lombok.Data;

/**
 * 心跳响应
 */
@Data
public class HeartBeatResponsePacket extends Packet {

    private long pingTime;

    private long pongTime;

    @Override
    public Short getCommand() {
        return 4;
    }
}
