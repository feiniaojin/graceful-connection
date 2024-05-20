package com.feiniaojin.graceful.connection.lb;

import com.feiniaojin.graceful.connection.connetion.Connection;

import java.util.List;

/**
 * 负载均衡
 */
public interface Loadbalancer {
    Connection select(List<Connection> connectionList);
}
