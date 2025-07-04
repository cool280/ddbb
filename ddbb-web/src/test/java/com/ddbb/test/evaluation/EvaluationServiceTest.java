package com.ddbb.test.evaluation;

import com.ddbb.controller.request.evaluation.CoachEvalDimensionRequest;
import com.ddbb.controller.request.evaluation.HallEvalDimensionRequest;
import com.ddbb.infra.data.mongo.repo.CoachEvalRepository;
import com.ddbb.service.evaluation.CoachEvalQueryService;
import com.ddbb.service.evaluation.CoachEvalUpdateService;
import com.ddbb.service.evaluation.HallEvalQueryService;
import com.ddbb.service.evaluation.HallEvalUpdateService;
import com.ddbb.test.BaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class EvaluationServiceTest extends BaseTest {

    @Autowired
    private HallEvalQueryService hallEvalQueryService;

    @Autowired
    private CoachEvalQueryService coachEvalQueryService;

    @Autowired
    private HallEvalUpdateService hallEvalUpdateService;

    @Autowired
    private CoachEvalUpdateService coachEvalUpdateService;

    @Autowired
    private CoachEvalRepository coachEvalRepository;


    @Test
    public void testGetCoachEvaluation() {
        coachEvalQueryService.getByCoachUid(65143423516446721L, 0, 10);
    }

    @Test
    public void testGetHallEvaluation() {
        hallEvalQueryService.getByHallId(1L, 0, 10);
    }

    @Test
    public void testAddCoachEvaluation() {
        CoachEvalDimensionRequest dimensionRequest = new CoachEvalDimensionRequest();
        dimensionRequest.setAppearance(5);
        dimensionRequest.setAttitude(5);
        dimensionRequest.setSatisfaction(5);
        dimensionRequest.setSkill(5);
        dimensionRequest.setComment("糖糖贼漂亮，nice ");
        dimensionRequest.setChallengeId(RandomStringUtils.randomAlphanumeric(7));
        dimensionRequest.setEvalUid(65143423516446722L);
        dimensionRequest.setCoachUid(65143423516446721L);
        coachEvalUpdateService.addEvaluation(dimensionRequest);
    }

    @Test
    public void testAddHallEvaluation() {

        HallEvalDimensionRequest dimensionRequest = new HallEvalDimensionRequest();
        dimensionRequest.setHallId(1L);
        dimensionRequest.setEvalUid(65143423516446722L); //评论者UID
        dimensionRequest.setSatisfaction(5);    // 满意度
        dimensionRequest.setEnvironment(5);     // 环境
        dimensionRequest.setFacilities(5);      // 设施
        dimensionRequest.setService(5);         // 服务
        dimensionRequest.setAssistantScore(5);  // 助教整体评分
        dimensionRequest.setCostPerformance(5); // 性价比
        dimensionRequest.setComment("干净整洁氛围相当不错"); //评价内容
        dimensionRequest.setChallengeId(RandomStringUtils.randomAlphanumeric(7)); //订单ID

        hallEvalUpdateService.addEvaluation(dimensionRequest);
    }

    @Test
    public void testCalculateDynamicScore() {

       double v=  coachEvalQueryService.calculateDynamicScore(65143423516446721L);

        System.out.println();
    }
}
