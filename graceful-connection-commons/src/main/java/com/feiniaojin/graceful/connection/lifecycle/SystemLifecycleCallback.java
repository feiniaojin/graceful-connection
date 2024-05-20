package com.feiniaojin.graceful.connection.lifecycle;

import com.feiniaojin.graceful.connection.AbstractSystemContext;

public interface SystemLifecycleCallback<T extends AbstractSystemContext> {
    default void beforeCreate(T t) {
    }

    default void afterCreate(T t) {
    }

    default void beforeStart(T t) {
    }

    default void afterStart(T t) {
    }
}
