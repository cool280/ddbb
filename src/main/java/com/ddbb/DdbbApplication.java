package com.ddbb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class DdbbApplication {
    public static void main(String[] args) {
        SpringApplication.run(DdbbApplication.class, args);
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+" =============== ddbb-service startup ok! ===============");
    }
}
