package com.ddbb.service.point;

import com.ddbb.controller.BaseResult;
import com.ddbb.infra.data.mongo.entity.PointRecordEntity;
import com.ddbb.infra.data.mongo.repo.PointRecordRepository;
import com.ddbb.internal.enums.PointActionType;
import com.ddbb.internal.utils.DateUtilPlus;
import com.ddbb.internal.utils.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
public class PointUpdateService {

    private static final int KEEP_SIGN_IN_DAYS = 10;

    @Autowired
    private PointRecordRepository pointRecordRepository;


    public BaseResult signIn(Long uid) {

        LocalDateTime now = LocalDateTime.now();
        long todayTime = DateUtilPlus.localDateTime2UtilDate(LocalDateTime.of(now.toLocalDate(), LocalTime.MIN)).getTime();

        long startTime = DateUtilPlus.localDateTime2UtilDate(LocalDateTime.of(now.minusDays(KEEP_SIGN_IN_DAYS).toLocalDate(), LocalTime.MIN)).getTime();
        long endTime = DateUtilPlus.localDateTime2UtilDate(now).getTime();

        List<PointRecordEntity> pointRecords = pointRecordRepository.findByUidAndCtsBetween(uid, startTime, endTime);

        if (CollectionUtils.isEmpty(pointRecords)) {
            if (updatePoints(uid, PointActionType.SIGN_IN)) {
                return BaseResult.OK("签到成功");
            }
            return BaseResult.ERROR("签到失败");
        }

        //判断当天是否签到
        boolean isTodaySignIn = pointRecords.stream()
                .filter(record -> PointActionType.isSignIn(record.getActionType()))
                .anyMatch(record -> record.getCts().longValue() >= todayTime);

        if (isTodaySignIn) {
            return BaseResult.ERROR("当天已签到");
        }

        //判断是否连续签到
        boolean isKeepSignIn = pointRecords.size() == KEEP_SIGN_IN_DAYS - 1;

        PointActionType actionType = isKeepSignIn ? PointActionType.KEEP_SIGN_IN : PointActionType.SIGN_IN;

        if (updatePoints(uid, actionType)) {
            return BaseResult.OK();
        }
        return BaseResult.ERROR("签到失败");
    }

    private boolean updatePoints(Long uid, PointActionType pointType) {
        try {
            PointRecordEntity entity = new PointRecordEntity();
            entity.setUid(uid);
            entity.setActionType(pointType.type);
            entity.setPoints(pointType.points);
            entity.setCts(System.currentTimeMillis());
            entity.setAid(SnowflakeIdUtil.getInstance().nextId());
            pointRecordRepository.insert(entity);
            return true;
        } catch (Exception e) {
            log.error("uid:{} pointType:{} updatePoints fail:{}", uid, pointType, ExceptionUtils.getMessage(e), e);
            return false;
        }
    }
}
