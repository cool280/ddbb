package com.ddbb.service.evaluation;

import com.ddbb.controller.BaseResult;
import com.ddbb.controller.request.evaluation.CoachEvalDimensionRequest;
import com.ddbb.infra.data.mongo.entity.CoachEvalEntity;
import com.ddbb.infra.data.mongo.repo.CoachEvalRepository;
import com.ddbb.internal.enums.PointActionType;
import com.ddbb.internal.utils.SnowflakeIdUtil;
import com.ddbb.service.point.PointUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CoachEvalUpdateService {

    @Autowired
    private CoachEvalRepository coachEvalRepository;

    @Autowired
    private PointUpdateService pointUpdateService;

    //add 评论
    public BaseResult addEvaluation(CoachEvalDimensionRequest coachRequet) {

        CoachEvalEntity entity = new CoachEvalEntity();
        entity.setCoachUid(coachRequet.getCoachUid());
        entity.setEvalUid(coachRequet.getEvalUid());
        entity.setSatisfaction(coachRequet.getSatisfaction());
        entity.setAppearance(coachRequet.getAppearance());
        entity.setSkill(coachRequet.getSkill());
        entity.setAttitude(coachRequet.getAttitude());
        entity.setComment(coachRequet.getComment());
        entity.setChallengeId(coachRequet.getChallengeId());
        entity.setAid(SnowflakeIdUtil.getInstance().nextId());

        try {
            coachEvalRepository.insert(entity);
            //赠送积分
            pointUpdateService.updatePoints(entity.getEvalUid(), PointActionType.COACH_COMMENT);
            return BaseResult.OK();
        } catch (Exception e) {
            return BaseResult.ERROR("添加评价失败");
        }
    }
}
