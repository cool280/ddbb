package com.ddbb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@EnableWebSocket
@EnableConfigurationProperties
@Slf4j
@SpringBootApplication
public class DdbbApplication {
    public static void main(String[] args) {
        SpringApplication.run(DdbbApplication.class, args);
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(" =============== "+time+" ddbb-service startup ok! ===============");
        log.info(" =============== "+time+" ddbb-service startup ok! ===============");
    }

}
