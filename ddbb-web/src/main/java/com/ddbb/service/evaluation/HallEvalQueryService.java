package com.ddbb.service.evaluation;

import com.ddbb.controller.response.evaluation.HallEvalQueryResponse;
import com.ddbb.infra.data.mongo.entity.HallAggregationResult;
import com.ddbb.infra.data.mongo.entity.HallEvalEntity;
import com.ddbb.infra.data.mongo.entity.UserEntity;
import com.ddbb.infra.data.mongo.repo.HallEvalRepository;
import com.ddbb.infra.data.mongo.repo.UserRepo;
import com.ddbb.internal.domain.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HallEvalQueryService {

    @Autowired
    private HallEvalRepository hallEvalRepository;

    @Autowired
    private UserRepo userRepo;

    public PageResult<HallEvalQueryResponse> getByHallId(Long hallId, int pageNum, int pageSize) {

        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("cts").descending());

        Page<HallEvalEntity> page = hallEvalRepository.findByHallId(hallId, pageable);

        if (page.isEmpty()) {
            PageResult pageResult = new PageResult<>();
            pageResult.setPageNum(pageNum);
            pageResult.setPageSize(pageSize);
            return pageResult;
        }

        Set<Long> uids = page.get().map(HallEvalEntity::getEvalUid).collect(Collectors.toSet());

        Map<Long, UserEntity> users = userRepo.findByUids(uids);

        List<HallEvalQueryResponse> list = page.get().map(hall -> {
            HallEvalQueryResponse response = new HallEvalQueryResponse();
            response.setSatisfaction(hall.getSatisfaction());
            response.setEnvironment(hall.getEnvironment());
            response.setFacilities(hall.getFacilities());
            response.setService(hall.getService());
            response.setAssistantScore(hall.getAssistantScore());
            response.setCostPerformance(hall.getCostPerformance());
            response.setComment(hall.getComment());
            response.setCreateTime(hall.getCts());
            UserEntity userEntity = users.getOrDefault(hall.getEvalUid(), null);
            if (null != userEntity) {
                response.setNickname(userEntity.getNickname());
            }
            return response;
        }).collect(Collectors.toList());

        PageResult<HallEvalQueryResponse> result = new PageResult();
        result.setTotalPages(page.getTotalPages());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setRecords(list);
        result.setTotalRecord(page.getTotalElements());
        return result;
    }

    public Double calculateDynamicScore(long hallId) {

        HallAggregationResult aggregationResult = hallEvalRepository.sumAndCountByHallId(hallId);

        if (null == aggregationResult) {
            return 5.0;
        }

        double v = (aggregationResult.getSatisfaction() * 0.2 + aggregationResult.getEnvironment() * 0.2 + aggregationResult.getFacilities() * 0.1 + aggregationResult.getService() * 0.15 + aggregationResult.getAssistantScore()*0.2 + aggregationResult.getCostPerformance() * 0.15) / aggregationResult.getCount();

        return Math.round(v * 100) / 100.0;
    }
}
