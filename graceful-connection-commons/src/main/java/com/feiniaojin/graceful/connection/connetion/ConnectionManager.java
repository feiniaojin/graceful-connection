package com.feiniaojin.graceful.connection.connetion;

import com.feiniaojin.graceful.connection.lifecycle.LifeCycle;

import java.util.List;

public interface ConnectionManager extends LifeCycle {
    List<Connection> getConnections();
}
