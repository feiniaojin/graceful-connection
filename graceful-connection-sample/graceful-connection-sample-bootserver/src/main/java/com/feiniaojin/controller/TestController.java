package com.feiniaojin.controller;

import com.feiniaojin.pack.RequestPacket;
import com.feiniaojin.pack.ResponsePacket;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/")
public class TestController {

    private AtomicLong atomicLong = new AtomicLong(1);

//    @RequestMapping("/test0")
//    public Map<String, Object> test0() {
//        RequestSender serverSender = new RequestSender();
//        RequestPacket requestPacket = new RequestPacket();
//        requestPacket.setRequestId(atomicLong.incrementAndGet());
//        requestPacket.setMethod("POST");
//        requestPacket.setServiceName("serviceName");
//        ResponsePacket responsePacket = serverSender.send(requestPacket);
//        return Collections.singletonMap("key", responsePacket);
//    }
}
