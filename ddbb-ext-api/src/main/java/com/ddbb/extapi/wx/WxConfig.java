package com.ddbb.extapi.wx;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "ddbb.wx")
@Data
public class WxConfig {
    /**
     * 证书文件文件夹
     */
    private String pemFolder;
}
