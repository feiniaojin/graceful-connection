package com.feiniaojin.graceful.connection.lb;

import com.feiniaojin.graceful.connection.connetion.Connection;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机负载均衡
 */
public class RandomLoadbalancer implements Loadbalancer {

    @Override
    public Connection select(List<Connection> list) {
        if (null == list || list.isEmpty()) {
            return null;
        }
        ThreadLocalRandom current = ThreadLocalRandom.current();
        int index = current.nextInt(list.size());
        return list.get(index);
    }
}
