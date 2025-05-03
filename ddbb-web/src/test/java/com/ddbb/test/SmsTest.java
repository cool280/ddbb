package com.ddbb.test;

import com.ddbb.extapi.sms.Sioo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * java-springboot下使用websocket运行单侧时报异常：javax.websocket.server.ServerContainer not available
 * 一：原因：spring boot内带tomcat，tomcat中的websocket会有冲突出现问题
 * 二：解决方法：
 *
 * 1. 为SpringbootTest注解指定参数classes和webEnvironment：@SpringBootTest(classes = WebsocketServerTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
 *
 * 或者
 *
 * 2. 指定webEnvironment也可以：@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
 * 三：解析
 *
 * 因为WebSocket是servlet容器所支持的，所以需要加载servlet容器：
 * webEnvironment参数为springboot指定ApplicationContext类型。
 * webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT表示内嵌的服务器将会在一个随机的端口启动。
 * webEnvironment 主要的值可以参考SpringbootTest源码：
 */
///**
// * An enumeration web environment modes.
// */
//enum WebEnvironment {
//
//    /**
//     * Creates a {@link WebApplicationContext} with a mock servlet environment if
//     * servlet APIs are on the classpath, a {@link ReactiveWebApplicationContext} if
//     * Spring WebFlux is on the classpath or a regular {@link ApplicationContext}
//     * otherwise.
//     */
//    MOCK(false),
//
//    /**
//     * Creates a web application context (reactive or servlet based) and sets a
//     * {@code server.port=0} {@link Environment} property (which usually triggers
//     * listening on a random port). Often used in conjunction with a
//     * {@link LocalServerPort} injected field on the test.
//     */
//    RANDOM_PORT(true),
//
//    /**
//     * Creates a (reactive) web application context without defining any
//     * {@code server.port=0} {@link Environment} property.
//     */
//    DEFINED_PORT(true),
//
//    /**
//     * Creates an {@link ApplicationContext} and sets
//     * {@link SpringApplication#setWebApplicationType(WebApplicationType)} to
//     * {@link WebApplicationType#NONE}.
//     */
//    NONE(false);
//
//    private final boolean embedded;
//
//    WebEnvironment(boolean embedded) {
//        this.embedded = embedded;
//    }
//
//    /**
//     * Return if the environment uses an {@link ServletWebServerApplicationContext}.
//     * @return if an {@link ServletWebServerApplicationContext} is used.
//     */
//    public boolean isEmbedded() {
//        return this.embedded;
//    }
//
//}
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
@Slf4j
public class SmsTest {

    @Autowired
    private Sioo sioo;

    @Test
    public void testSendMsg() throws Exception {

        List<String> phone = new ArrayList<>();
        phone.add("13472644829");
        sioo.sendMsg("a test message 123",phone);
        System.out.println("============= done ==============");
    }

}
