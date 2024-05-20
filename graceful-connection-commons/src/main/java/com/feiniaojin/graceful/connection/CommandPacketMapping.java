package com.feiniaojin.graceful.connection;

import com.feiniaojin.graceful.connection.pack.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandPacketMapping {
    public static final Map<Short, Class<? extends Packet>> MAP = new ConcurrentHashMap<>();

    static {
        MAP.put((short) 1, RequestPacket.class);
        MAP.put((short) 2, ResponsePacket.class);
        MAP.put((short) 3, HeartBeatRequestPacket.class);
        MAP.put((short) 4, HeartBeatResponsePacket.class);
    }
}
