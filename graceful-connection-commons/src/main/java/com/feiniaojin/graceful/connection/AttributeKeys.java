package com.feiniaojin.graceful.connection;

import com.feiniaojin.graceful.connection.connetion.Connection;
import io.netty.util.AttributeKey;

public class AttributeKeys {

    public static final AttributeKey<Connection> CONNECTION = AttributeKey.valueOf("connection");
}
