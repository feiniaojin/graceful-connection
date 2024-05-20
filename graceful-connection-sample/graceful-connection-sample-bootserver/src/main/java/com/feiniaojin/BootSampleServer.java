package com.feiniaojin;

import com.feiniaojin.lc.sdk.server.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Hello world!
 */
@SpringBootApplication
public class BootSampleServer {

    @Resource
    private Server server;

    public static void main(String[] args) {
        SpringApplication.run(BootSampleServer.class, args);
    }

    @PostConstruct
    public void init() {
//        this.lcServer.initServer();
    }
}
