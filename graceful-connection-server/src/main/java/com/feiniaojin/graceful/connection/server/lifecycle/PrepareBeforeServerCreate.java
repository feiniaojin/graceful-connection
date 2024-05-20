package com.feiniaojin.graceful.connection.server.lifecycle;

import com.feiniaojin.discovery.NacosRegister;
import com.feiniaojin.graceful.connection.server.ServerSystemContext;
import com.feiniaojin.lifecycle.SystemLifecycleCallback;

import java.net.InetSocketAddress;

/**
 * 服务创建前的准备
 */
public class PrepareBeforeServerCreate implements SystemLifecycleCallback<ServerSystemContext> {

    @Override
    public void beforeCreate(ServerSystemContext systemContext) {

        String serverHost = System.getProperty("serverHost");
        if (serverHost == null) {
            serverHost = "127.0.0.1";
        }
        String port = System.getProperty("serverPort");
        int serverPort = 9999;
        if (port != null) {
            serverPort = Integer.valueOf(port);
        }
        InetSocketAddress inetSocketAddress = new InetSocketAddress(serverHost, serverPort);
        systemContext.setInetSocketAddress(inetSocketAddress);

        //初始化注册中心
        NacosRegister.init("127.0.0.1:8848");
    }
}
