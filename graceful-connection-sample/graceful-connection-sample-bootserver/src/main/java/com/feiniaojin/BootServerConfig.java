package com.feiniaojin;

import com.feiniaojin.lc.sdk.server.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootServerConfig {
    @Bean
    public Server lcServer() {
        return new Server();
    }
}
