package com.ddbb.service.evaluation;

import com.ddbb.controller.BaseResult;
import com.ddbb.controller.request.evaluation.HallEvalDimensionRequest;
import com.ddbb.infra.data.mongo.entity.HallEvalEntity;
import com.ddbb.infra.data.mongo.repo.HallEvalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HallEvalUpdateService {

    @Autowired
    private HallEvalRepository hallEvalRepository;

    public BaseResult addEval(HallEvalDimensionRequest hallRequest) {

        if (null == hallRequest || !hallRequest.validate()) {
            return BaseResult.ERROR("参数不合法");
        }

        HallEvalEntity entity = new HallEvalEntity();
        entity.setHallId(hallRequest.getHallId());
        entity.setEvaluatorId(hallRequest.getEvaluatorId());
        entity.setSatisfaction(hallRequest.getSatisfaction());
        entity.setEnvironment(hallRequest.getEnvironment());
        entity.setFacilities(hallRequest.getFacilities());
        entity.setService(hallRequest.getService());
        entity.setAssistantScore(hallRequest.getAssistantScore());
        entity.setCostPerformance(hallRequest.getCostPerformance());
        entity.setComment(hallRequest.getComment());
        entity.setChallengeId(hallRequest.getChallengeId());

        try {
            hallEvalRepository.insert(entity);
            return BaseResult.OK();
        } catch (Exception e) {
            return BaseResult.ERROR("添加评价失败");
        }
    }

}
