package com.ddbb.geo;

import com.ddbb.controller.request.NearbyAssistantCoachRequest;
import com.ddbb.controller.request.NearbyAssistantCoachResponse;
import com.ddbb.mongo.AssistantCoachRepo;
import com.ddbb.service.coach.AssistantCoachService;
import com.ddbb.utils.ObjectConerter;
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
    private AssistantCoachService assistantCoachService;

    @Test
    public void testQueryCoach(){
        NearbyAssistantCoachRequest request = new NearbyAssistantCoachRequest();
        List<NearbyAssistantCoachResponse> list = assistantCoachService.getNearbyAssistantCoach(request);
        list.forEach(e->{
            System.out.println(ObjectConerter.o2s(e));
        });
    }
}
