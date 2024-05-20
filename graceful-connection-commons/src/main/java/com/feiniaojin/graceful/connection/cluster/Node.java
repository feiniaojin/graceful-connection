package com.feiniaojin.graceful.connection.cluster;

import lombok.Data;

import java.util.Objects;

@Data
public class Node {
    private String ip;
    private int port;

    public Node() {
    }

    public Node(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return port == node.port && Objects.equals(ip, node.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
