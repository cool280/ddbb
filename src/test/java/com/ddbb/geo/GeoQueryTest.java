package com.ddbb.geo;

import com.ddbb.controller.request.NearbyAssistantCoachRequest;
import com.ddbb.controller.request.NearbyAssistantCoachResponse;
import com.ddbb.controller.request.NearbyHallRequest;
import com.ddbb.controller.request.NearbyHallResponse;
import com.ddbb.service.nearby.NearbyService;
import com.ddbb.utils.ObjectConverter;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
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
