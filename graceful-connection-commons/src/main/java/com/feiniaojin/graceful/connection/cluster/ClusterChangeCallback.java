package com.feiniaojin.graceful.connection.cluster;

import java.util.List;

public interface ClusterChangeCallback {
    void callback(List<Node> nodeList);
}
