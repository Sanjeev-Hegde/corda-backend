package com.api.config;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.etc")
public class ServerConfig {

    public static void main(String[] args) {
        SpringApplication.run(ServerConfig.class, args);
    }

}
