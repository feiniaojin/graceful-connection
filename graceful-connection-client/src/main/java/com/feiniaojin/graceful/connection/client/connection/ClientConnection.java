package com.feiniaojin.graceful.connection.client.connection;

import com.feiniaojin.connetion.Connection;
import com.feiniaojin.cluster.Node;
import com.feiniaojin.Processor;
import com.feiniaojin.pack.RequestPacket;
import com.feiniaojin.pack.ResponsePacket;

public interface ClientConnection extends Connection {
    void setNode(Node node);

    Node getNode();
    void setProcessor(Processor<RequestPacket, ResponsePacket> processor);

    Processor<RequestPacket, ResponsePacket> getProcessor();
}
