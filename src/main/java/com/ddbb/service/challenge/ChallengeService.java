package com.ddbb.service.challenge;

import com.ddbb.controller.request.LaunchChallengeRequest;
import com.ddbb.enums.ChallengeStatus;
import com.ddbb.mongo.entity.Challenge;
import com.ddbb.mongo.entity.Hall;
import com.ddbb.mongo.entity.User;
import com.ddbb.mongo.repo.ChallengeRepo;
import com.ddbb.mongo.repo.HallRepo;
import com.ddbb.mongo.repo.UserRepo;
import com.ddbb.sms.Sioo;
import com.ddbb.utils.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
@Slf4j
public class ChallengeService {
    @Autowired
    private ChallengeRepo challengeRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private HallRepo hallRepo;
    @Autowired
    private Sioo sioo;
    /**
     * 发起挑战
     * @param request
     */
    public boolean launchChallenge(LaunchChallengeRequest request)  {
        try {
            createChallengeRecord(request);
            sendSms(request);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[launchChallenge] >>> from:{}, to:{}, with error: {}",request.getFrom(),request.getTo(),e.getMessage(),e);
        }
        return false;
    }

    /**
     * 生成两条挑战记录
     * @param request
     * @throws ParseException
     */
    private void createChallengeRecord(LaunchChallengeRequest request)throws ParseException{
        Date challengeDate = DateUtils.parseDate(request.getChallengeDateStr()+" 08:00:00","yyyy-MM-dd HH:mm:ss");
        long now = System.currentTimeMillis();
        String challengeId = "ch_"+SnowflakeIdUtil.getInstance().nextId();

        Challenge from = new Challenge();
        from.setAid(SnowflakeIdUtil.getInstance().nextId());
        from.setChallengeId(challengeId);
        from.setOwner(request.getFrom());
        from.setFrom(request.getFrom());
        from.setTo(request.getTo());
        from.setChallengeDate(challengeDate);
        from.setEndTime(request.getEndTime());
        from.setHallId(request.getHallId());
        from.setStartTime(request.getStartTime());
        from.setCreateTime(now);
        from.setUpdateTime(now);
        from.setStatus(ChallengeStatus.INIT.getCode());

        Challenge to = new Challenge();
        BeanUtils.copyProperties(from,to);
        to.setOwner(request.getTo());
        to.setAid(SnowflakeIdUtil.getInstance().nextId());


        challengeRepo.insert(from);
        challengeRepo.insert(to);
        log.info("[createChallengeRecord] >>> from:{},to:{}, done",from,to);
    }

    /**
     * 给被挑战者发短信
     * @param request
     */
    private void sendSms(LaunchChallengeRequest request) throws Exception {
        Long to = request.getTo();
        User userTo = userRepo.findByQid(to);
        String phone = userTo.getPhone();


        User userFrom = userRepo.findByQid(request.getFrom());

        Hall hall = hallRepo.findByHallId(request.getHallId());

        StringBuilder sb = new StringBuilder(200);
        sb.append("亲，您有一位球友")
                //.append("《").append(userFrom.getNickname()).append("》")
                .append("在《DD打球》上向您发起打球邀请，请您及时在微信小程序上回应。")
                .append("日期：")
                .append(request.getChallengeDateStr())
                .append("，时间：")
                .append(request.getStartTime()).append(":00")
                .append(" - ")
                .append(request.getEndTime()).append(":00")
                .append("，")
                .append("地点：").append(hall.getNickname());
        sioo.sendMsg(sb.toString(),phone);
        log.info("[sendSms] >>> to:{} , content:{}, done",phone,sb.toString());
    }

    public void getSchedule(Long qid){
//        challengeRepo.findAll()
    }
}
