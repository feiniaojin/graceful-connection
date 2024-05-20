package com.feiniaojin.graceful.connection.client.discovery;

import com.feiniaojin.cluster.ClusterChangeCallback;
import com.feiniaojin.cluster.Node;
import com.feiniaojin.graceful.connection.client.connection.ClientConnectionManager;

import java.util.List;

public class ClusterChangeListener implements ClusterChangeCallback {

    private ClientConnectionManager connectionManager;

    public ClusterChangeListener(ClientConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public void callback(List<Node> nodeList) {
        connectionManager.checkAndConnect(nodeList);
    }
}
