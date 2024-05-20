package com.feiniaojin.graceful.connection.client.lifecycle;

import com.feiniaojin.cluster.Node;
import com.feiniaojin.graceful.connection.client.ClientSystemContext;
import com.feiniaojin.graceful.connection.client.discovery.NacosDiscoveryImpl;
import com.feiniaojin.lifecycle.SystemLifecycleCallback;

import java.util.Set;

/**
 * 客户端创建之前，从注册中心拉取服务配置
 */
public class BeforeClientCreate implements SystemLifecycleCallback<ClientSystemContext> {

    @Override
    public void beforeCreate(ClientSystemContext clientSystemContext) {
        NacosDiscoveryImpl discovery = new NacosDiscoveryImpl("127.0.0.1:8848");
        clientSystemContext.setDiscovery(discovery);
        Set<Node> nodes = discovery.getAllInstances("LongConnection");
        System.out.println("从注册中心获得服务端节点" + nodes);
        clientSystemContext.setInitClusterNodes(nodes);
    }
}
