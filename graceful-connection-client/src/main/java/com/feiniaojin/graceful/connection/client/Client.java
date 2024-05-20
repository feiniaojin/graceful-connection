package com.feiniaojin.graceful.connection.client;

import com.feiniaojin.PrintProcessor;
import com.feiniaojin.discovery.Discovery;
import com.feiniaojin.graceful.connection.client.discovery.NacosDiscoveryImpl;
import com.feiniaojin.lb.Loadbalancer;
import com.feiniaojin.lb.RandomLoadbalancer;
import com.feiniaojin.graceful.connection.client.connection.ClientConnectionManager;
import com.feiniaojin.graceful.connection.client.discovery.ClusterChangeListener;
import com.feiniaojin.graceful.connection.client.lifecycle.BeforeClientCreate;
import com.feiniaojin.graceful.connection.client.lifecycle.ClientCallbackRegister;
import com.feiniaojin.graceful.connection.client.lifecycle.SubscribeAfterClientStart;
import com.feiniaojin.lifecycle.LifeCycle;

public class Client implements LifeCycle {

    private ClientConnectionManager connectionManager;

    private Discovery discovery;

    private ClientSystemContext clientSystemContext;

    public Client() {

        clientSystemContext = new ClientSystemContext();
        //回调注册器
        ClientCallbackRegister callbackRegister = new ClientCallbackRegister();
        clientSystemContext.setClientCallbackRegister(callbackRegister);
        //注册创建前的回调
        callbackRegister.registerCallback(new BeforeClientCreate());
        callbackRegister.registerCallback(new SubscribeAfterClientStart());

        //创建前的回调
        callbackRegister.invokeBeforeCreateCallback(clientSystemContext);
        create();
        //创建后的回调
        callbackRegister.invokeAfterCreateCallback(clientSystemContext);
    }

    @Override
    public void create() {
        connectionManager = new ClientConnectionManager();
        //双向绑定
        connectionManager.setClientSystemContext(clientSystemContext);
        clientSystemContext.setClientConnectionManager(connectionManager);

        //服务发现
        discovery = new NacosDiscoveryImpl("localhost:8848");
        discovery.addListener(new ClusterChangeListener(this.connectionManager));
        clientSystemContext.setDiscovery(discovery);

        //负载均衡
        Loadbalancer loadbalancer = new RandomLoadbalancer();
        clientSystemContext.setLoadbalancer(loadbalancer);

        clientSystemContext.setProcessor(new PrintProcessor());
    }

    @Override
    public void start() {

        //执行启动前处理
        ClientCallbackRegister callbackRegister = clientSystemContext.getClientCallbackRegister();
        callbackRegister.invokeBeforeStartCallback(this.clientSystemContext);

        //配置初始集群节点
        connectionManager.addClusterNodes(this.clientSystemContext.getInitClusterNodes());
        connectionManager.start();

        //订阅注册中心
        callbackRegister.invokeAfterStartCallback(clientSystemContext);
    }

    public ClientSystemContext getClientSystemContext() {
        return clientSystemContext;
    }

    public void setClientSystemContext(ClientSystemContext clientSystemContext) {
        this.clientSystemContext = clientSystemContext;
    }
}
