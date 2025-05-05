package com.ddbb.test.profile;

import com.ddbb.controller.request.WorkplaceRequest;
import com.ddbb.controller.response.ImagePath;
import com.ddbb.service.profile.ImagePathService;
import com.ddbb.service.profile.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
@Slf4j
public class ProfileTest {

    @Autowired
    private ProfileService profileService;
    @Autowired
    private ImagePathService imagePathService;

    @Test
    public void testSendMsg() throws Exception {
        WorkplaceRequest request = new WorkplaceRequest();
        request.setQid(134L);
        request.setHallId(1L);

        profileService.addWorkplace(request);
        System.out.println("============= done ==============");
    }
    @Test
    public void testGetPath(){
        ImagePath u = imagePathService.getUserImageFolderPath(65143423516446720L);
        System.out.println(u);
        System.out.println("==================================");
        ImagePath h = imagePathService.getHallImageFolderPath(1L);
        System.out.println(h);
    }
}
