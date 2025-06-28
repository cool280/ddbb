package com.ddbb.service.evaluation;

import com.ddbb.controller.BaseResult;
import com.ddbb.controller.request.evaluation.CoachEvalDimensionRequest;
import com.ddbb.infra.data.mongo.entity.CoachEvalEntity;
import com.ddbb.infra.data.mongo.repo.CoachEvalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoachEvalUpdateService {

    @Autowired
    private CoachEvalRepository coachEvalRepository;

    public BaseResult addEval(CoachEvalDimensionRequest coachRequet) {

        if (null == coachRequet || !coachRequet.validate()) {
            return BaseResult.ERROR("参数不合法");
        }

        CoachEvalEntity entity = new CoachEvalEntity();
        entity.setCoachUid(coachRequet.getCoachUid());
        entity.setEvaluatorId(coachRequet.getEvaluatorId());
        entity.setSatisfaction(coachRequet.getSatisfaction());
        entity.setAppearance(coachRequet.getAppearance());
        entity.setSkill(coachRequet.getSkill());
        entity.setAttitude(coachRequet.getAttitude());
        entity.setComment(coachRequet.getComment());
        entity.setChallengeId(coachRequet.getChallengeId());

        try {
            coachEvalRepository.insert(entity);
            return BaseResult.OK();
        } catch (Exception e) {
            return BaseResult.ERROR("添加评价失败");
        }
    }


}
