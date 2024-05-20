package com.feiniaojin.graceful.connection.discovery;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.feiniaojin.graceful.connection.cluster.ClusterChangeCallback;
import com.feiniaojin.graceful.connection.cluster.Node;

import java.util.*;
import java.util.stream.Collectors;

public class NacosRegister {

    private static NamingService namingService;

    public static void init(String serveAddr) {
        try {
            namingService = NamingFactory.createNamingService(serveAddr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void register(String serverHost, int serverPort) {
        try {
            Instance instance = new Instance();

            instance.setIp(serverHost);
            instance.setPort(serverPort);
            instance.setInstanceId("123");
            instance.setHealthy(true);
            instance.setEnabled(true);
            namingService.registerInstance("LongConnection", instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从注册中心获得所有的阶段
     *
     * @param serviceName
     * @return
     */
    public static Set<Node> getAllInstances(String serviceName) {
        try {
            List<Instance> instanceList = namingService.getAllInstances(serviceName);
            if (Objects.isNull(instanceList)
                    || instanceList.isEmpty()) {
                return Collections.emptySet();
            }
            Set<Node> set = instanceList.stream().filter(e -> e.isEnabled() && e.isHealthy())
                    .map(e -> {
                        Node node = new Node();
                        String ip = e.getIp();
                        node.setIp(ip);
                        int port = e.getPort();
                        node.setPort(port);
                        return node;
                    })
                    .collect(Collectors.toSet());
            return set;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回一个空的
        return Collections.emptySet();
    }

    public static List<ClusterChangeCallback> callbacks = new ArrayList<>();

    public static void clusterListener() {
        try {
            namingService.subscribe("LongConnection", event -> {
                if (event instanceof NamingEvent) {
                    System.out.println(((NamingEvent) event).getInstances());

                    //找到变更的节点
                    List<Instance> instances = ((NamingEvent) event).getInstances();

                    List<Node> nodeList = instances.stream().map(e -> {
                        Node node = new Node();
                        node.setIp(e.getIp());
                        node.setPort(e.getPort());
                        return node;
                    }).collect(Collectors.toList());

                    for (ClusterChangeCallback callback : callbacks) {
                        callback.callback(nodeList);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addClusterChangeCallback(ClusterChangeCallback callback) {
        callbacks.add(callback);
    }
}
