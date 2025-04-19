package com.ddbb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

/**
 * 1. application.properties文件中的路径配置如下：
 * cbs.imagesPath=file:/D:/images/
 * 在D:/images目录下放置需要的图片。
 *
 * 2. 添加配置类，实现WebMvcConfigurer的addResourceHandlers方法。
 * 启动项目，就可以获取路径下的图片了：访问地址例如：http://localhost:8080/images/1.jpg
 * ————————————————
 * 原文链接：https://blog.csdn.net/bingyuderizi/article/details/124109324
 */
@Slf4j
@Configuration
@EnableWebMvc
public class MyWebMvcConfiguration implements WebMvcConfigurer{

    //获取配置文件中图片的路径
    @Value("${cbs.imagesPath}")
    private String mImagesPath;
    @Value("${cbs.imagesUrlContext}")
    private String imagesUrlContext;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if(mImagesPath.equals("") || mImagesPath.equals("${cbs.imagesPath}")){

            String imagesPath = MyWebMvcConfiguration.class.getClassLoader().getResource("").getPath();

            if(imagesPath.indexOf(".jar")>0){
                imagesPath = imagesPath.substring(0, imagesPath.indexOf(".jar"));
            }else if(imagesPath.indexOf("classes")>0){
                imagesPath = "file:"+imagesPath.substring(0, imagesPath.indexOf("classes"));
            }

            imagesPath = imagesPath.substring(0, imagesPath.lastIndexOf("/"))+"/images/";
            mImagesPath = imagesPath;
        }

        log.info("------imagesPath="+mImagesPath);

        //registry.addResourceHandler("/images/**").addResourceLocations(mImagesPath);
        registry.addResourceHandler("/"+imagesUrlContext+"/**").addResourceLocations(mImagesPath);
    }
}
