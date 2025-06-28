package com.ddbb.service.point;

import com.ddbb.controller.response.PointDetailResponse;
import com.ddbb.controller.response.PointQueryResponse;
import com.ddbb.infra.data.mongo.entity.PointRecordEntity;
import com.ddbb.infra.data.mongo.repo.PointRecordRepository;
import com.ddbb.internal.enums.PointType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PointQueryService {

    @Autowired
    private PointRecordRepository pointRecordRepository;

    public PointQueryResponse getPoint(Long uid) {

        PointRecordEntity record = new PointRecordEntity();
        record.setUid(uid);

        List<PointRecordEntity> list = pointRecordRepository.findAll(Example.of(record), Sort.by(Sort.Direction.DESC, "createTime"));

        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        int totalPointsEarned = list.stream().filter(point -> point.getPoint() > 0).mapToInt(PointRecordEntity::getPoint).sum();
        int totalPointsUsed = list.stream().filter(point -> point.getPoint() < 0).mapToInt(PointRecordEntity::getPoint).sum();
        int availablePoints = totalPointsEarned - totalPointsEarned;

        List<PointDetailResponse> details = list.stream().map(point -> {
            PointType pointType = PointType.of(point.getPointType());
            PointDetailResponse response = new PointDetailResponse();
            response.setPoint(point.getPoint());
            response.setPointName(pointType.desc);
            response.setCreateTime(point.getCreateTime());
            response.setPointType(pointType.type);
            return response;
        }).collect(Collectors.toList());

        PointQueryResponse queryResponse = new PointQueryResponse();
        queryResponse.setAvailablePoints(availablePoints);
        queryResponse.setTotalPointsEarned(totalPointsEarned);
        queryResponse.setTotalPointsUsed(totalPointsUsed);
        queryResponse.setDetails(details);

        return queryResponse;
    }
}
