package com.feiniaojin.graceful.connection.server;

import com.feiniaojin.AbstractSystemContext;
import com.feiniaojin.DefaultSenderImpl;
import com.feiniaojin.Sender;
import com.feiniaojin.pack.RequestPacket;
import com.feiniaojin.pack.ResponsePacket;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerMain {

    private static ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {

        Server server = new Server();
        AbstractSystemContext systemContext = server.getSystemContext();
        final Sender<RequestPacket, ResponsePacket> sender = new DefaultSenderImpl(systemContext);

        scheduledExecutor.scheduleWithFixedDelay(() -> {
            try {
                if(!sender.isAvailable()){
                    System.out.println("不可用");
                    return;
                }
                System.out.println("ServerMain:开始向客户端写数据");
                RequestPacket requestPacket = new RequestPacket();
                requestPacket.setServiceName("from server: graceful long connection");
                ResponsePacket responsePacket = sender.sendWaitResponse(requestPacket);
                System.out.println("ServerMain:收到回复" + responsePacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 5, 5, TimeUnit.SECONDS);
        server.start();
    }
}
