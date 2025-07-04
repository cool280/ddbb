package com.ddbb.service.evaluation;

import com.ddbb.controller.BaseResult;
import com.ddbb.controller.request.evaluation.HallEvalDimensionRequest;
import com.ddbb.infra.data.mongo.entity.HallEvalEntity;
import com.ddbb.infra.data.mongo.repo.HallEvalRepository;
import com.ddbb.internal.enums.PointActionType;
import com.ddbb.internal.utils.SnowflakeIdUtil;
import com.ddbb.service.point.PointUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HallEvalUpdateService {

    @Autowired
    private HallEvalRepository hallEvalRepository;

    @Autowired
    private PointUpdateService pointUpdateService;

    public BaseResult addEvaluation(HallEvalDimensionRequest hallRequest) {

        HallEvalEntity entity = new HallEvalEntity();
        entity.setHallId(hallRequest.getHallId());
        entity.setEvalUid(hallRequest.getEvalUid());
        entity.setSatisfaction(hallRequest.getSatisfaction());
        entity.setEnvironment(hallRequest.getEnvironment());
        entity.setFacilities(hallRequest.getFacilities());
        entity.setService(hallRequest.getService());
        entity.setAssistantScore(hallRequest.getAssistantScore());
        entity.setCostPerformance(hallRequest.getCostPerformance());
        entity.setComment(hallRequest.getComment());
        entity.setChallengeId(hallRequest.getChallengeId());
        entity.setAid(SnowflakeIdUtil.getInstance().nextId());

        try {
            hallEvalRepository.insert(entity);

            pointUpdateService.updatePoints(entity.getEvalUid(), PointActionType.HALL_COMMENT);

            return BaseResult.OK();
        } catch (Exception e) {
            return BaseResult.ERROR("添加评价失败");
        }
    }

}
