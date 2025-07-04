package com.ddbb.service.evaluation;

import com.ddbb.controller.response.evaluation.CoachEvalQueryResponse;
import com.ddbb.infra.data.mongo.entity.CoachEvalEntity;
import com.ddbb.infra.data.mongo.entity.UserEntity;
import com.ddbb.infra.data.mongo.repo.CoachEvalRepository;
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
public class CoachEvalQueryService {

    @Autowired
    private CoachEvalRepository coachEvalRepository;

    @Autowired
    private UserRepo userRepo;

    public PageResult<CoachEvalQueryResponse> getByCoachUid(Long coachUid, int pageNum, int pageSize) {

        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("cts").descending());

        Page<CoachEvalEntity> page = coachEvalRepository.findAllByCoachUid(coachUid, pageable);

        if (page.isEmpty()) {
            PageResult pageResult = new PageResult<>();
            pageResult.setPageNum(pageNum);
            pageResult.setPageSize(pageSize);
            return pageResult;
        }

        Set<Long> uids = page.get().map(CoachEvalEntity::getEvalUid).collect(Collectors.toSet());

        Map<Long, UserEntity> users = userRepo.findByUids(uids);

        List<CoachEvalQueryResponse> list = page.get().map(ce -> {
            CoachEvalQueryResponse response = new CoachEvalQueryResponse();
            response.setSatisfaction(ce.getSatisfaction());
            response.setAppearance(ce.getAppearance());
            response.setSkill(ce.getSkill());
            response.setAttitude(ce.getAttitude());
            response.setComment(ce.getComment());
            response.setCreateTime(ce.getCts());
            UserEntity userEntity = users.getOrDefault(ce.getEvalUid(), null);
            if (null != userEntity) {
                response.setNickname(userEntity.getNickname());
            }
            return response;
        }).collect(Collectors.toList());

        PageResult<CoachEvalQueryResponse> result = new PageResult();
        result.setTotalPages(page.getTotalPages());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setRecords(list);
        result.setTotalRecord(page.getTotalElements());
        return result;
    }
}
