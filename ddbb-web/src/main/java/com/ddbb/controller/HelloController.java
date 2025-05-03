package com.ddbb.controller;

import com.ddbb.internal.annotate.DdbbController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@DdbbController("helloController")
@Slf4j
public class HelloController extends BaseController{
    @GetMapping("/hi")
    public String hi(){
        log.info("hello com.ddbb");
        return "hello world";
    }
}
