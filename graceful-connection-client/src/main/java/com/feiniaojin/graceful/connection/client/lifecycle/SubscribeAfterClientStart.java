package com.feiniaojin.graceful.connection.client.lifecycle;

import com.feiniaojin.discovery.Discovery;
import com.feiniaojin.graceful.connection.client.ClientSystemContext;
import com.feiniaojin.lifecycle.SystemLifecycleCallback;

/**
 * 客户端启动后，要订阅注册中心集群的变化
 */
public class SubscribeAfterClientStart implements SystemLifecycleCallback<ClientSystemContext> {

    @Override
    public void afterStart(ClientSystemContext clientSystemContext) {
        //订阅注册中心
        Discovery discovery = clientSystemContext.getDiscovery();
        discovery.subscribe();
        System.out.println("订阅注册中心");
    }
}
