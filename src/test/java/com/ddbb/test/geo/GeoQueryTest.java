package com.ddbb.test.geo;

import com.ddbb.controller.request.NearbyAssistantCoachRequest;
import com.ddbb.controller.response.NearbyAssistantCoachResponse;
import com.ddbb.controller.request.NearbyHallRequest;
import com.ddbb.controller.response.NearbyHallResponse;
import com.ddbb.service.nearby.NearbyService;
import com.ddbb.utils.ObjectConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
/**
 * java-springboot下使用websocket运行单侧时报异常：javax.websocket.server.ServerContainer not available
 * 一：原因：spring boot内带tomcat，tomcat中的websocket会有冲突出现问题
 * 二：解决方法：
 *
 * 1. 为SpringbootTest注解指定参数classes和webEnvironment：@SpringBootTest(classes = WebsocketServerTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
 * 或者
 * 2. 指定webEnvironment也可以：@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
 */
 @RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class GeoQueryTest {
    @Autowired
    private NearbyService nearbyService;

    @Test
    public void testQueryCoach(){
        NearbyAssistantCoachRequest request = new NearbyAssistantCoachRequest();
        request.setMaxDistanceKm(10.1);
        List<NearbyAssistantCoachResponse> list = nearbyService.getNearbyAssistantCoach(request);
        System.out.println("=========================== nearby coaches are: ===========================");
        list.forEach(e->{
            System.out.println(ObjectConverter.o2s(e));
        });
    }

    @Test
    public void testQueryHall(){
        NearbyHallRequest request = new NearbyHallRequest();
        request.setMaxDistanceKm(100.1);
        List<NearbyHallResponse> list = nearbyService.getNearbyHall(request);
        System.out.println("=========================== nearby halls are: ===========================");
        list.forEach(e->{
            System.out.println(ObjectConverter.o2s(e));
        });
    }
}
