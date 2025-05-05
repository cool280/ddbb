package com.ddbb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class Swagger2Config {
    @Bean
    public Docket h5Api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("h5")
                .select()
                // 指定扫描的包路径，该路径下的控制器类会被 Swagger 处理
                .apis(RequestHandlerSelectors.basePackage("com.ddbb.controller"))
                .paths(PathSelectors.any())
                .build()
                .protocols(getProtocols())
                .apiInfo(apiInfo("H5 API"));
    }

    /*
    @Bean
    public Docket adminApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("admin")
                .select()
                // 指定扫描的包路径，该路径下的控制器类会被 Swagger 处理
                .apis(RequestHandlerSelectors.basePackage("com.rent.mall.admin.controller"))
                .paths(PathSelectors.any())
                .build()
                .protocols(getProtocols())
                .apiInfo(apiInfo("Admin API"));
    }

     */



    private ApiInfo apiInfo(String title) {
        return new ApiInfoBuilder()
                .title(title)
                .description("API Documentation")
                .version("1.0.0")
                .build();
    }

    private Set<String> getProtocols(){
        Set<String> protocols = new HashSet<>();
        protocols.add("http");
        protocols.add("https");

        return protocols;
    }
}
