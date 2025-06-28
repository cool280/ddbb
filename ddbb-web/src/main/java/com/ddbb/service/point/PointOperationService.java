package com.ddbb.service.point;

import com.ddbb.infra.data.mongo.repo.PointRecordRepository;
import com.ddbb.internal.enums.PointType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointOperationService {

    @Autowired
    private PointRecordRepository pointRecordRepository;

    public boolean addPoint(Long uid, PointType pointType) {
return true;
    }
}
