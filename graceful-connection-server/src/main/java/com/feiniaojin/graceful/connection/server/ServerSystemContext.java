package com.feiniaojin.graceful.connection.server;

import com.feiniaojin.AbstractSystemContext;
import com.feiniaojin.graceful.connection.server.connection.ServerConnectionManager;
import com.feiniaojin.lb.Loadbalancer;
import com.feiniaojin.graceful.connection.server.lifecycle.ServerCallbackRegister;
import lombok.Data;

@Data
public class ServerSystemContext extends AbstractSystemContext {
    private ServerCallbackRegister serverCallbackRegister;
    private ServerConnectionManager connectionManager;
    Loadbalancer loadbalancer;
}
