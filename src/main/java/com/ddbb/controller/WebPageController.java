package com.ddbb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 一般我们把需要通过controller跳转的页面放在templates中，接下来我们就创建一个Controller去跳转放在Templates文件夹中的资源：
 * 要实现页面的跳转，还需要导入一个依赖：
 * <dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-thymeleaf</artifactId>
 *         </dependency>
 */
@Controller
public class WebPageController {
    @GetMapping("/index888")
    public String home() {
        return "index888";
    }

}
