package com.ddbb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EnableWebSocket
@SpringBootApplication
public class DdbbApplication {
    public static void main(String[] args) {
        SpringApplication.run(DdbbApplication.class, args);
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+" =============== ddbb-service startup ok! ===============");
    }
}
