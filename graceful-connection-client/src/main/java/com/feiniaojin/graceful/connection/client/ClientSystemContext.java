package com.feiniaojin.graceful.connection.client;

import com.feiniaojin.AbstractSystemContext;
import com.feiniaojin.connetion.ConnectionManager;
import com.feiniaojin.discovery.Discovery;
import com.feiniaojin.lb.Loadbalancer;
import com.feiniaojin.graceful.connection.client.connection.ClientConnectionManager;
import com.feiniaojin.graceful.connection.client.lifecycle.ClientCallbackRegister;
import lombok.Data;

@Data
public class ClientSystemContext extends AbstractSystemContext {
    private ClientCallbackRegister clientCallbackRegister;
    private ClientConnectionManager clientConnectionManager;
    private Discovery discovery;

    private Loadbalancer loadbalancer;

    @Override
    public ConnectionManager getConnectionManager() {
        return this.clientConnectionManager;
    }

    public Loadbalancer getLoadbalancer() {
        return loadbalancer;
    }

    public void setLoadbalancer(Loadbalancer loadbalancer) {
        this.loadbalancer = loadbalancer;
    }
}
