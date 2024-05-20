package com.feiniaojin.graceful.connection.pack;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ResponsePacket extends Packet {
    private long requestId;
    private String method;
    private Integer status;
    private String serviceName;
    private String url;

    private String queryString;

    private List<Map<String, String>> headers;

    @Override
    public Short getCommand() {
        return 2;
    }
}
