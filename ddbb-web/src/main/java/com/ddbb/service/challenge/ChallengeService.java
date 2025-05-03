package com.ddbb.service.challenge;

import com.ddbb.controller.request.ChallengeRequest;
import com.ddbb.internal.enums.ChallengeRole;
import com.ddbb.internal.enums.ChallengeStatus;
import com.ddbb.infra.data.mongo.entity.ChallengeEntity;
import com.ddbb.infra.data.mongo.entity.HallEntity;
import com.ddbb.infra.data.mongo.entity.UserEntity;
import com.ddbb.infra.data.mongo.repo.ChallengeRepo;
import com.ddbb.infra.data.mongo.repo.HallRepo;
import com.ddbb.infra.data.mongo.repo.UserRepo;
import com.ddbb.extapi.sms.Sioo;
import com.ddbb.internal.utils.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;

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
    @Autowired
    private ChallengeKit challengeKit;
    @Autowired
    private ChallengeConfig challengeConfig;
    /**
     * 发起挑战
     * @param request
     */
    public boolean launchChallenge(ChallengeRequest request)  {
        try {
            createChallengeRecord(request);
            sendLaunchChallengeSms(request);
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
        from.setCreateTimeTs(now);
        from.setUpdateTimeTs(now);
        from.setStatus(ChallengeStatus.INIT.getCode());
        from.setChallengeRole(ChallengeRole.LAUNCH.getCode());
        from.setAlive(true);
        from.setFromSignIn(false);
        from.setToSignIn(false);

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
    private void sendLaunchChallengeSms(ChallengeRequest request) throws Exception {
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
     * 拒绝挑战
     * @param request
     */
    public Pair<Boolean,String> refuseChallenge(ChallengeRequest request){
        ChallengeEntity entity = challengeRepo.findByChallengeIdAndTo(request.getChallengeId(), request.getQid());
        ChallengeStatus changeTo = ChallengeStatus.TO_REFUSED;
        Pair<Boolean,String> check = canChangeStatus(entity,changeTo);
        if(!check.getLeft()){
            return check;
        }

        challengeRepo.updateChallengeStatusAndFinish(request.getChallengeId(), changeTo);

        //发短信
        UserEntity userFrom = userRepo.findByQid(entity.getFrom());
        try {
            sioo.sendMsg("亲，《DD打球》很抱歉通知您，助教拒绝了您的打球邀请",userFrom.getPhone());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Pair.of(true,"ok");
    }
    /**
     * 接受挑战
     * @param request
     */
    public Pair<Boolean,String> acceptChallenge(ChallengeRequest request){
        ChallengeEntity entity = challengeRepo.findByChallengeIdAndTo(request.getChallengeId(), request.getQid());

        ChallengeStatus changeTo = ChallengeStatus.TO_ACCEPTED;
        Pair<Boolean,String> check = canChangeStatus(entity,changeTo);
        if(!check.getLeft()){
            return check;
        }

        challengeRepo.updateChallengeStatus(request.getChallengeId(), changeTo);

        //发短信
        UserEntity userFrom = userRepo.findByQid(entity.getFrom());
        try {
            sioo.sendMsg("亲，《DD打球》很高兴通知您，助教接受了您的打球邀请，请按时赴约哦",userFrom.getPhone());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Pair.of(true,"ok");
    }



    /**
     * 取消挑战
     * @param request
     */
    public Pair<Boolean,String> cancelChallenge(ChallengeRequest request){
        ChallengeEntity entity = challengeRepo.findOneByChallengeId(request.getChallengeId());
        ChallengeStatus changeTo;
        Long qid = request.getQid();
        Long notifyQid;//要发短信通知的对方qid
        if(entity.getFrom().equals(qid)){//发起方取消
            changeTo = ChallengeStatus.FROM_CANCELED;
            notifyQid = entity.getTo();
        }else{//接受方取消
            changeTo = ChallengeStatus.TO_CANCELED;
            notifyQid = entity.getFrom();
        }

        Pair<Boolean,String> check = canChangeStatus(entity,changeTo);
        if(!check.getLeft()){
            return check;
        }

        challengeRepo.updateChallengeStatusAndFinish(request.getChallengeId(), changeTo);

        //发短信
        UserEntity user2Notify = userRepo.findByQid(notifyQid);
        try {
            sioo.sendMsg("亲，《DD打球》很抱歉通知您，对方取消了打球邀请",user2Notify.getPhone());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Pair.of(true,"ok");
    }

    /**
     * 签到
     * @param request
     */
    public Pair<Boolean,String> signIn(ChallengeRequest request){
        ChallengeEntity entity = challengeRepo.findOneByChallengeId(request.getChallengeId());
        ChallengeStatus changeTo;
        Long qid = request.getQid();
        Long notifyQid;//要发短信通知的对方qid
        boolean isFromSign;

        if(entity.getFrom().equals(qid)){//发起方签到
            if(BooleanUtils.isTrue(entity.getFromSignIn())){
                return Pair.of(false,"您已经签到，无需再签");
            }
            notifyQid = entity.getTo();
            isFromSign = true;
        }else{//接受方签到
            if(BooleanUtils.isTrue(entity.getToSignIn())){
                return Pair.of(false,"您已经签到，无需再签");
            }
            notifyQid = entity.getFrom();
            isFromSign = false;
        }

        //临时赋值，做对比
        if(entity.getFromSignIn() == null){
            entity.setFromSignIn(false);
        }
        if(entity.getToSignIn() == null){
            entity.setToSignIn(false);
        }

        if(BooleanUtils.isFalse(entity.getFromSignIn()) && BooleanUtils.isFalse(entity.getToSignIn())){
            changeTo = ChallengeStatus.SINGLE_SIGN_IN;
        }else if(BooleanUtils.isTrue(entity.getFromSignIn()) && BooleanUtils.isFalse(entity.getToSignIn())){
            changeTo = ChallengeStatus.ALL_SIGN_IN;
        }else if(BooleanUtils.isFalse(entity.getFromSignIn()) && BooleanUtils.isTrue(entity.getToSignIn())){
            changeTo = ChallengeStatus.ALL_SIGN_IN;
        }else{
            return Pair.of(false,"双方都已经签到，无需再签");
        }

        Pair<Boolean,String> check = canChangeStatus(entity,changeTo);
        if(!check.getLeft()){
            return check;
        }

        challengeRepo.signIn(request.getChallengeId(), changeTo,isFromSign);

        //发短信
        UserEntity user2Notify = userRepo.findByQid(notifyQid);
        try {
            sioo.sendMsg("亲，《DD打球》通知您，对方已签到",user2Notify.getPhone());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Pair.of(true,"ok");
    }



    /**
     * 登记比分
     * @param request
     */
    public Pair<Boolean,String> saveScore(ChallengeRequest request){
        ChallengeEntity entity = challengeRepo.findOneByChallengeId(request.getChallengeId());

        //临时赋值，做对比
        if(entity.getScoreSaved() == null){
            entity.setScoreSaved(false);
        }

        if(BooleanUtils.isTrue(entity.getScoreSaved())){
            return Pair.of(false,"比分已登记完成！无需重复登记");
        }

        Pair<Boolean,String> check = canChangeStatus(entity,ChallengeStatus.SCORE_SAVED);
        if(!check.getLeft()){
            return check;
        }

        challengeRepo.saveScore(request.getChallengeId(), request.getFromWinRound() + "_" + request.getToWinRound());

        return Pair.of(true,"ok");
    }

    /**
     * 评价助教
     * @param request
     */
    public Pair<Boolean,String> commentCoach(ChallengeRequest request){
        ChallengeEntity entity = challengeRepo.findOneByChallengeId(request.getChallengeId());
        ChallengeStatus changeTo = ChallengeStatus.COACH_COMMENTED;
        //临时赋值，做对比
        if(entity.getCoachCommented() == null){
            entity.setCoachCommented(false);
        }

        if(BooleanUtils.isTrue(entity.getCoachCommented())){
            return Pair.of(false,"助教已评价完成！无需重复评价");
        }

        Pair<Boolean,String> check = canChangeStatus(entity,changeTo);
        if(!check.getLeft()){
            return check;
        }

        challengeRepo.commentCoach(request.getChallengeId());

        return Pair.of(true,"ok");
    }

    /**
     * 评价球房
     * @param request
     */
    public Pair<Boolean,String> commentHall(ChallengeRequest request){
        ChallengeEntity entity = challengeRepo.findOneByChallengeId(request.getChallengeId());
        ChallengeStatus changeTo = ChallengeStatus.HALL_COMMENTED;
        //临时赋值，做对比
        if(entity.getHallCommented() == null){
            entity.setHallCommented(false);
        }

        if(BooleanUtils.isTrue(entity.getHallCommented())){
            return Pair.of(false,"球房已评价完成！无需重复评价");
        }

        Pair<Boolean,String> check = canChangeStatus(entity,changeTo);
        if(!check.getLeft()){
            return check;
        }

        challengeRepo.commentHall(request.getChallengeId());

        return Pair.of(true,"ok");
    }

    /**
     * 判断一个挑战书是否能被修改状态
     * @param entity
     * @param newStatus  要修改成什么状态
     * @return
     */
    private Pair<Boolean,String> canChangeStatus(ChallengeEntity entity,ChallengeStatus newStatus){
        if(entity == null){
            return Pair.of(false,"挑战不存在");
        }
        if(entity.getAlive() == null || entity.getAlive() == false){
            return Pair.of(false,"挑战已关闭");
        }
        ChallengeStatus old = ChallengeStatus.of(entity.getStatus());
        if(! ChallengeStatusSequence.canChange(old,newStatus)){
            return Pair.of(false,"不能从"+old+"状态 -> 修改成指定状态："+newStatus);
        }


        Pair<LocalDateTime,LocalDateTime> se = challengeKit.getChallengeStartAndEnd(entity);

        int ss = challengeConfig.getCloseWhenSecondsAfterEndTime();
        //比分登记、评价助教、评价球房可在比赛结束后24h内进行
        if(newStatus.equals(ChallengeStatus.SCORE_SAVED) || newStatus.equals(ChallengeStatus.COACH_COMMENTED)
                || newStatus.equals(ChallengeStatus.HALL_COMMENTED)){
            if(LocalDateTime.now().isAfter(se.getRight().plusSeconds(ss))){
                return Pair.of(false,"挑战已结束"+(ss/3600)+"小时，不可操作");
            }
        }else{
            if(LocalDateTime.now().isAfter(se.getRight())){
                return Pair.of(false,"挑战已结束");
            }
        }

        return Pair.of(true,"ok");
    }

    public static void main(String[] args) {
        System.out.println(BooleanUtils.isFalse(null));
        System.out.println(BooleanUtils.isTrue(null));
    }
}
