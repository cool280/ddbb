package com.ddbb.test.point;

import com.ddbb.controller.BaseResult;
import com.ddbb.controller.response.point.PointQueryResponse;
import com.ddbb.controller.response.point.SignInStatusResponse;
import com.ddbb.service.point.PointUpdateService;
import com.ddbb.service.point.PointQueryService;
import com.ddbb.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PointServiceTest extends BaseTest {

    @Autowired
    private PointQueryService pointQueryService;

    @Autowired
    PointUpdateService pointUpdateService;

    private static final Long uid = 65143423516446721L;


    @Test
    public void testSignIn() {
        BaseResult baseResult = pointUpdateService.signIn(uid);
        System.out.println(baseResult.getResultCode());
    }

    @Test
    public void testGetPoint() {
        PointQueryResponse response = pointQueryService.getPoint(uid);
        System.out.println();
    }

    @Test
    public void testGetSignInStatus() {
        SignInStatusResponse response = pointQueryService.getSignInStatus(uid);
        System.out.println();
    }

}
