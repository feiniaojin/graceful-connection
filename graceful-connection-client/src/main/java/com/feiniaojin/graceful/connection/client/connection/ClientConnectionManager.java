package com.feiniaojin.graceful.connection.client.connection;

import com.alibaba.nacos.common.utils.ConcurrentHashSet;
import com.feiniaojin.connetion.Connection;
import com.feiniaojin.connetion.ConnectionContext;
import com.feiniaojin.connetion.ConnectionManager;
import com.feiniaojin.cluster.Node;
import com.feiniaojin.graceful.connection.client.ClientSystemContext;
import lombok.Data;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 连接管理器
 * 单一长连接
 */
@Data
public class ClientConnectionManager implements ConnectionManager {

    private ClientSystemContext clientSystemContext;

    /**
     * 集群节点
     */
    private ConcurrentHashSet<Node> clusterNodes = new ConcurrentHashSet<>();

    private CopyOnWriteArrayList<Node> reConnectList = new CopyOnWriteArrayList<>();

    private CopyOnWriteArrayList<ClientConnection> connections = new CopyOnWriteArrayList<>();

    private ConcurrentHashMap<Node, ClientConnection> nodeConnectionMap = new ConcurrentHashMap<>();

    /**
     * 断线重连线程池
     */
    private static ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    private ReentrantLock lock = new ReentrantLock();

    /**
     * 增加集群节点
     *
     * @param nodeList
     */
    public void addClusterNodes(Set<Node> nodeList) {
        lock.lock();
        try {
            clusterNodes.addAll(nodeList);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 增加重连队列
     *
     * @param nodeList
     */
    public void addReConnectList(List<Node> nodeList) {
        lock.lock();
        try {
            reConnectList.addAll(nodeList);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 初始化连接池
     */
    public void start() {
        lock.lock();
        try {
            //正常发起的连接
            Set<Node> nodeList = this.clusterNodes;
            for (Node n : nodeList) {
                doConnect(n);
            }

            //触发重连任务
            scheduledExecutor.scheduleWithFixedDelay(new ReConnectTask(this), 10, 10, TimeUnit.SECONDS);
        } finally {
            lock.unlock();
        }
    }

    private boolean doConnect(Node node) {
        try {
            //以及初始化过了，就不要再连了，除非移除该连接
            if (nodeConnectionMap.containsKey(node)) {
                return true;
            }
            ConnectionContext connectionContext = new ConnectionContext();
            connectionContext.setSystemContext(clientSystemContext);
            ClientConnection connection = new ClientConnectionImpl(connectionContext);
            connection.setNode(node);
            //channel关联连接，连接关联连接管理器
            connection.setConnectionManager(this);
            connection.start();
            this.connections.addIfAbsent(connection);
            this.nodeConnectionMap.putIfAbsent(node, connection);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            this.reConnectList.addIfAbsent(node);
            return false;
        }
    }

    public void removeConnection(Node node) {
        lock.lock();
        try {
            if (node == null) {
                return;
            }
            ClientConnection connection = nodeConnectionMap.get(node);
            connections.remove(connection);
            nodeConnectionMap.remove(node);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 检查并重新连接
     *
     * @param nodeList
     */
    public void checkAndConnect(List<Node> nodeList) {
        lock.lock();
        try {
            //空列表不处理
            if (Objects.isNull(nodeList)) {
                return;
            }
            //清理连接
            if (nodeList.isEmpty()) {
                this.nodeConnectionMap.clear();
                this.reConnectList.clear();
                this.clusterNodes.clear();
                return;
            }
            //连接管理器中没有任何集群节点
            Set<Node> c = this.clusterNodes;
            if (c.isEmpty()) {
                c.addAll(nodeList);
                for (Node n : c) {
                    doConnect(n);
                }
                return;
            }

            HashSet<Node> newClusterSet = new HashSet<>(nodeList);

            Iterator<Node> it = c.iterator();
            while (it.hasNext()) {
                Node node = it.next();
                //新集群没有这个节点，移除掉
                if (!newClusterSet.contains(node)) {
                    it.remove();
                    ClientConnection connection = nodeConnectionMap.get(node);
                    if (connection == null
                            || connection.getChannel() == null
                            || !connection.getChannel().isActive()) {
                        nodeConnectionMap.remove(node);
                        this.connections.remove(connection);
                    }
                }
            }

            //筛选出新的节点
            List<Node> newNodes = nodeList.stream()
                    .filter(n -> !c.contains(n)).collect(Collectors.toList());

            for (Node n : newNodes) {
                doConnect(n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Connection> getConnections() {
        return new ArrayList<>(connections);
    }

    private class ReConnectTask implements Runnable {

        private ConnectionManager connectionManager;

        public ReConnectTask(ConnectionManager connectionManager) {
            this.connectionManager = connectionManager;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                if (clusterNodes.isEmpty()
                        || reConnectList == null
                        || reConnectList.isEmpty()) {
                    return;
                }
                System.out.println("重连队列不为空，开始重连");
                List<Node> nodeList = reConnectList;
                for (Node node : nodeList) {
                    try {
                        //集群中已经没有这个节点，则不再重连
                        if (!clusterNodes.contains(node)) {
                            nodeList.remove(node);
                            System.out.println("集群无该节点，取消重连");
                            continue;
                        }
                        //重连成功
                        if (doConnect(node)) {
                            nodeList.remove(node);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
