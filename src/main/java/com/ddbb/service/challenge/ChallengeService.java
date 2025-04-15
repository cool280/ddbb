package com.ddbb.service.challenge;

import com.ddbb.controller.request.ChallengeRequest;
import com.ddbb.enums.ChallengeRole;
import com.ddbb.enums.ChallengeStatus;
import com.ddbb.mongo.entity.ChallengeEntity;
import com.ddbb.mongo.entity.HallEntity;
import com.ddbb.mongo.entity.UserEntity;
import com.ddbb.mongo.repo.ChallengeRepo;
import com.ddbb.mongo.repo.HallRepo;
import com.ddbb.mongo.repo.UserRepo;
import com.ddbb.sms.Sioo;
import com.ddbb.utils.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;

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
    public boolean launchChallenge(ChallengeRequest request)  {
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
    private void createChallengeRecord(ChallengeRequest request)throws ParseException{
        long now = System.currentTimeMillis();
        String challengeId = "ch_"+SnowflakeIdUtil.getInstance().nextId();

        ChallengeEntity from = new ChallengeEntity();
        from.setAid(SnowflakeIdUtil.getInstance().nextId());
        from.setChallengeId(challengeId);
        from.setOwner(request.getFrom());
        from.setFrom(request.getFrom());
        from.setTo(request.getTo());
        from.setChallengeDateStr(request.getChallengeDateStr());
        from.setChallengeDateInt(Integer.valueOf(request.getChallengeDateStr().replace("-","")));
        from.setEndTime(request.getEndTime());
        from.setHallId(request.getHallId());
        from.setStartTime(request.getStartTime());
        from.setCreateTime(now);
        from.setUpdateTime(now);
        from.setStatus(ChallengeStatus.INIT.getCode());
        from.setChallengeRole(ChallengeRole.LAUNCH.getCode());

        ChallengeEntity to = new ChallengeEntity();
        BeanUtils.copyProperties(from,to);
        to.setOwner(request.getTo());
        to.setAid(SnowflakeIdUtil.getInstance().nextId());
        to.setChallengeRole(ChallengeRole.RECEIVE.getCode());



        challengeRepo.insert(from);
        challengeRepo.insert(to);
        log.info("[createChallengeRecord] >>> from:{},to:{}, done",from,to);
    }

    /**
     * 给被挑战者发短信
     * @param request
     */
    private void sendSms(ChallengeRequest request) throws Exception {
        Long to = request.getTo();
        UserEntity userTo = userRepo.findByQid(to);
        String phone = userTo.getPhone();


        UserEntity userFrom = userRepo.findByQid(request.getFrom());

        HallEntity hall = hallRepo.findByHallId(request.getHallId());

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

    /**
     * 接受挑战
     * @param request
     */
    public Pair<Boolean,String> refuseChallenge(ChallengeRequest request){
        ChallengeEntity entity = challengeRepo.findByChallengeIdAndTo(request.getChallengeId(), request.getQid());
        if(entity == null){
            return Pair.of(false,"挑战不存在");
        }
        if(entity.getStatus() == null || ChallengeStatus.INIT.getCode() != entity.getStatus()){
            return Pair.of(false,"不是发起状态，不能拒绝");
        }

        challengeRepo.updateChallengeStatus(request.getChallengeId(), ChallengeStatus.TO_REFUSED);
        return Pair.of(true,"ok");
    }
    /**
     * 接受挑战
     * @param request
     */
    public Pair<Boolean,String> acceptChallenge(ChallengeRequest request){
        ChallengeEntity entity = challengeRepo.findByChallengeIdAndTo(request.getChallengeId(), request.getQid());
        if(entity == null){
            return Pair.of(false,"挑战不存在");
        }
        if(entity.getStatus() == null || ChallengeStatus.INIT.getCode() != entity.getStatus()){
            return Pair.of(false,"不是发起状态，不能接受");
        }

        challengeRepo.updateChallengeStatus(request.getChallengeId(), ChallengeStatus.TO_ACCEPTED);
        return Pair.of(true,"ok");
    }
}
