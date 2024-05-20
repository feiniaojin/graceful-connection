package com.feiniaojin.graceful.connection.pack;

import lombok.Data;

/**
 * 心跳请求
 */
@Data
public class HeartBeatRequestPacket extends Packet {

    public HeartBeatRequestPacket(long pingTime) {
        this.pingTime = pingTime;
    }

    /**
     * 心跳发送时间
     */
    private long pingTime;

    @Override
    public Short getCommand() {
        return 3;
    }

    @Override
    public String toString() {
        return "HeartBeatRequestPacket{" +
                "pingTime=" + pingTime +
                '}';
    }
}
