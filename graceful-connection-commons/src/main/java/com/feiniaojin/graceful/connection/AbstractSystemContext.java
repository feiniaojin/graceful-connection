package com.feiniaojin.graceful.connection;

import com.feiniaojin.graceful.connection.cluster.Node;
import com.feiniaojin.graceful.connection.connetion.ConnectionManager;
import com.feiniaojin.graceful.connection.lb.Loadbalancer;
import com.feiniaojin.graceful.connection.pack.RequestPacket;
import com.feiniaojin.graceful.connection.pack.ResponsePacket;
import lombok.Data;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * 抽象的系统上下文
 */
@Data
public abstract class AbstractSystemContext extends AbstractContext {

    InetSocketAddress inetSocketAddress;

    /**
     * 初始化集群
     */
    private Set<Node> initClusterNodes;

    private ConnectionManager connectionManager;

    private Processor<RequestPacket, ResponsePacket> processor;

    public Loadbalancer getLoadbalancer() {
        throw new UnsupportedOperationException();
    }

}
