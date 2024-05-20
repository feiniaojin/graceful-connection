package com.feiniaojin.graceful.connection;

import com.feiniaojin.graceful.connection.pack.Packet;

import java.util.concurrent.TimeUnit;

public interface Sender<REQ extends Packet, RES extends Packet> {

    /**
     * 单向发送，发完就不管了
     *
     * @param req
     */
    default void sendOneway(REQ req) {
    }

    /**
     * 发送并等待回复
     *
     * @param req
     * @return
     */
    default RES sendWaitResponse(REQ req) {
        throw new UnsupportedOperationException();
    }

    /**
     * 发送并等待回复，带超时时间
     *
     * @param req
     * @return
     */
    default RES sendWaitResponse(REQ req, long timeout, TimeUnit timeUnit) {
        throw new UnsupportedOperationException();
    }

    /**
     * 是否可用
     *
     * @return
     */
    default boolean isAvailable() {
        throw new UnsupportedOperationException();
    }
}
