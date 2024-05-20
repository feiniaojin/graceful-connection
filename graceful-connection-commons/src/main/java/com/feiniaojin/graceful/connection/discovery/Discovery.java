package com.feiniaojin.graceful.connection.discovery;

import com.feiniaojin.graceful.connection.cluster.ClusterChangeCallback;
import com.feiniaojin.graceful.connection.cluster.Node;

import java.util.Set;

public interface Discovery {

    /**
     * 获得所有节点
     *
     * @return
     */
    Set<Node> getAllInstances(String serviceName);

    /**
     * 集群变更监听
     *
     * @param callback
     */
    void addListener(ClusterChangeCallback callback);

    /**
     * 监听集群
     */
    void subscribe();
}
