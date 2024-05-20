package com.feiniaojin.graceful.connection.lifecycle;

public interface LifeCycle {

    /**
     * 创建
     */
    default void create() {
    }

    /**
     * 启动
     */
    default void start() {
    }

    /**
     * 停止
     */
    default void shutdown() {

    }

    /**
     * 销毁
     */
    default void destroy() {

    }
}
