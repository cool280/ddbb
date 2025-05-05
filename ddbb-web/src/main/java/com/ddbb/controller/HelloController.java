package com.ddbb.controller;

import com.ddbb.internal.annotate.DdbbController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@DdbbController("helloController")
@Slf4j
@Api(tags = "忽略，Hello world")
public class HelloController extends BaseController{
    @GetMapping("/hi")
    @ApiOperation(value = "example")
//    @ApiResponses(value = {@ApiResponse(code = 200, message = "ok", response = String.class)})
    public String hi(){
        log.info("hello com.ddbb");
        return "hello world";
    }
}
