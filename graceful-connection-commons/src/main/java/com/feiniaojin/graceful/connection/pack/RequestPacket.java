package com.feiniaojin.graceful.connection.pack;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class RequestPacket extends Packet {
    private long requestId;
    private String method;
    private String serviceName;
    private String url;

    private String queryString;

    private List<Map<String, String>> headers;

    @Override
    public Short getCommand() {
        return 1;
    }

    @Override
    public String toString() {
        return "RequestPacket{" +
                "requestId=" + requestId +
                ", method='" + method + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", url='" + url + '\'' +
                ", queryString='" + queryString + '\'' +
                ", headers=" + headers +
                '}';
    }
}
