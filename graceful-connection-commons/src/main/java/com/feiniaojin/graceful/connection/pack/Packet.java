package com.feiniaojin.graceful.connection.pack;

import lombok.Data;

@Data
public abstract class Packet {
    /**
     * 协议版本
     */

    private Byte version = 1;

    /**
     * 命令
     *
     * @return
     */
    public abstract Short getCommand();

}
