package com.ddbb.service.challenge;

import com.ddbb.controller.request.LaunchChallengeRequest;
import com.ddbb.mongo.entity.Challenge;
import com.ddbb.mongo.repo.ChallengeRepo;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class ChallengeService {
    @Autowired
    private ChallengeRepo challengeRepo;
    /**
     * 发起挑战
     * @param request
     */
    public void launchChallenge(LaunchChallengeRequest request) throws ParseException {
        Date challengeDate = DateUtils.parseDate(request.getChallengeDateStr(),"yyyy-MM-dd");
        long now = System.currentTimeMillis();

        Challenge from = new Challenge();
        from.setOwner(request.getFrom());
        from.setFrom(request.getFrom());
        from.setTo(request.getTo());
        from.setChallengeDate(challengeDate);
        from.setEndTime(request.getEndTime());
        from.setHallId(request.getHallId());
        from.setStartTime(request.getStartTime());
        from.setCreateTime(now);
        from.setUpdateTime(now);

        Challenge to = new Challenge();
        BeanUtils.copyProperties(from,to);
        to.setOwner(request.getTo());

        challengeRepo.insert(from);
        challengeRepo.insert(to);
    }
}
