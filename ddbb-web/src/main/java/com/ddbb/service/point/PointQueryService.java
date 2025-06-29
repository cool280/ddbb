package com.ddbb.service.point;

import com.ddbb.controller.response.point.PointDetailResponse;
import com.ddbb.controller.response.point.PointQueryResponse;
import com.ddbb.controller.response.point.SignInStatusResponse;
import com.ddbb.infra.data.mongo.entity.PointRecordEntity;
import com.ddbb.infra.data.mongo.repo.PointRecordRepository;
import com.ddbb.internal.enums.PointActionType;
import com.ddbb.internal.utils.DateUtilPlus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PointQueryService {

    @Autowired
    private PointRecordRepository pointRecordRepository;

    public PointQueryResponse getPoint(Long uid) {

        List<PointRecordEntity> list = pointRecordRepository.findByUidOrderByCtsDesc(uid);

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        int totalPointsEarned = list.stream().filter(point -> point.getPoints() > 0).mapToInt(PointRecordEntity::getPoints).sum();
        int totalPointsUsed = list.stream().filter(point -> point.getPoints() < 0).mapToInt(PointRecordEntity::getPoints).sum();
        int availablePoints = totalPointsEarned - totalPointsUsed;

        List<PointDetailResponse> details = list.stream().map(record -> {
            PointActionType pointType = PointActionType.of(record.getActionType());
            PointDetailResponse response = new PointDetailResponse();
            response.setPoint(record.getPoints());
            response.setPointActionType(pointType.type);
            response.setPointActionTypeName(pointType.desc);
            response.setCts(record.getCts());
            return response;
        }).collect(Collectors.toList());

        PointQueryResponse queryResponse = new PointQueryResponse();
        queryResponse.setAvailablePoints(availablePoints);
        queryResponse.setTotalPointsEarned(totalPointsEarned);
        queryResponse.setTotalPointsUsed(totalPointsUsed);
        queryResponse.setDetails(details);

        return queryResponse;
    }

    public SignInStatusResponse getSignInStatus(Long uid) {

        LocalDateTime now = LocalDateTime.now();

        LocalDate firstDayOfMonth = now.toLocalDate().withDayOfMonth(1);

        Long startTime = DateUtilPlus.localDateTime2UtilDate(LocalDateTime.of(firstDayOfMonth, LocalTime.MIN)).getTime();
        Long endTime = DateUtilPlus.localDateTime2UtilDate(now).getTime();

        List<PointRecordEntity> records = pointRecordRepository.findByUidAndCtsBetween(uid, startTime, endTime);

        if (CollectionUtils.isEmpty(records)) {
            return SignInStatusResponse.init();
        }

        Map<LocalDate, Boolean> signInRecords = records.stream().filter(record -> PointActionType.isSignIn(record.getActionType()))
                .collect(Collectors.toMap(record -> DateUtilPlus.utilDate2LocalDateTime(new Date(record.getCts())).toLocalDate(), record -> true));

        Map<LocalDate, Boolean> monthRecords = Stream.iterate(firstDayOfMonth, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(firstDayOfMonth, now) + 1)
                .collect(Collectors.toMap(Function.identity(), d -> signInRecords.getOrDefault(d, false)));

        SignInStatusResponse response = new SignInStatusResponse();
        response.setTodaySignedIn(signInRecords.getOrDefault(now.toLocalDate(), false));
        response.setMonthRecords(monthRecords);

        return response;
    }
}
