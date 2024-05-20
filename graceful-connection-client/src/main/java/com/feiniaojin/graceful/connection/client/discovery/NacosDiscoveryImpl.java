package com.feiniaojin.graceful.connection.client.discovery;

import com.feiniaojin.cluster.ClusterChangeCallback;
import com.feiniaojin.discovery.Discovery;
import com.feiniaojin.discovery.NacosRegister;
import com.feiniaojin.cluster.Node;

import java.util.Set;

/**
 * 服务发现
 */
public class NacosDiscoveryImpl implements Discovery {
    public NacosDiscoveryImpl(String serverAddr) {
        NacosRegister.init(serverAddr);
    }

    public Set<Node> getAllInstances(String serviceName) {
        return NacosRegister.getAllInstances(serviceName);
    }

    public void addListener(ClusterChangeCallback callback) {
        NacosRegister.addClusterChangeCallback(callback);
    }

    public void subscribe() {
        NacosRegister.clusterListener();
    }
}
