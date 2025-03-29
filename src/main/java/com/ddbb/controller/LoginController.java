package com.ddbb.controller;

import com.ddbb.annotate.DdbbController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@DdbbController
public class LoginController {
    @GetMapping("/hi")
    public String hi(){
        return "hello world";
    }
}
