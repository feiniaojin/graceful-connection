package com.feiniaojin.graceful.connection.server.connection;

import com.feiniaojin.connetion.Connection;
import com.feiniaojin.connetion.ConnectionManager;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 服务端连接管理器
 */
@Data
public class ServerConnectionManager implements ConnectionManager {

    public final List<Connection> connections = new CopyOnWriteArrayList<>();

    /**
     * 新增连接
     *
     * @param connection
     */
    public void addConnection(Connection connection) {
        this.connections.add(connection);
    }

    /**
     * 移除某个连接
     *
     * @param connection
     */
    public void removeConnection(Connection connection) {
        this.connections.remove(connection);
    }

    public List<Connection> getConnections() {
        return new ArrayList<>(this.connections);
    }
}
