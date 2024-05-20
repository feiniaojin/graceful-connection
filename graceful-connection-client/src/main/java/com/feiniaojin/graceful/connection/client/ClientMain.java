package com.feiniaojin.graceful.connection.client;

import com.feiniaojin.AbstractSystemContext;
import com.feiniaojin.DefaultSenderImpl;
import com.feiniaojin.Sender;
import com.feiniaojin.pack.RequestPacket;
import com.feiniaojin.pack.ResponsePacket;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientMain {

    private static ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        AbstractSystemContext systemContext = client.getClientSystemContext();
        final Sender<RequestPacket, ResponsePacket> sender = new DefaultSenderImpl(systemContext);
        client.start();

        scheduledExecutor.scheduleWithFixedDelay(() -> {
            try {
                if(!sender.isAvailable()){
                    System.out.println("不可用");
                    return;
                }
                System.out.println("clientMain:开始向客户端写数据");
                RequestPacket requestPacket = new RequestPacket();
                requestPacket.setRequestId(System.currentTimeMillis());
                requestPacket.setServiceName("from client: graceful long connection");
                ResponsePacket responsePacket = sender.sendWaitResponse(requestPacket);
                System.out.println("clientMain:收到回复" + responsePacket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 10, 10, TimeUnit.SECONDS);
    }
}
