package com.feiniaojin.graceful.connection;

import com.feiniaojin.graceful.connection.pack.RequestPacket;
import com.feiniaojin.graceful.connection.pack.ResponsePacket;

public class PrintProcessor implements Processor<RequestPacket, ResponsePacket> {
    @Override
    public ResponsePacket process(RequestPacket requestPacket) {

        ResponsePacket responsePacket = new ResponsePacket();
        responsePacket.setStatus(200);
        responsePacket.setRequestId(requestPacket.getRequestId());
        responsePacket.setServiceName(requestPacket.getServiceName());
        return responsePacket;
    }
}
