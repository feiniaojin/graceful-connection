package com.feiniaojin.graceful.connection.server.lifecycle;

import com.feiniaojin.discovery.NacosRegister;
import com.feiniaojin.graceful.connection.server.ServerSystemContext;
import com.feiniaojin.lifecycle.SystemLifecycleCallback;

import java.net.InetSocketAddress;

/**
 * 服务端启动后把自己注册到注册中心
 */
public class RegisterDiscoveryAfterServerStart implements SystemLifecycleCallback<ServerSystemContext> {

    @Override
    public void afterStart(ServerSystemContext systemContext) {
        InetSocketAddress inetSocketAddress = systemContext.getInetSocketAddress();

        NacosRegister.register(inetSocketAddress.getAddress().getHostAddress(),
                inetSocketAddress.getPort());
        System.out.println("服务已注册");
    }
}
