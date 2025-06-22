package com.ddbb.service.challenge;

import com.ddbb.controller.request.ChallengeRequest;
import com.ddbb.controller.request.challenge.*;
import com.ddbb.internal.constants.DdbbConstant;
import com.ddbb.internal.enums.ChallengeRole;
import com.ddbb.internal.enums.ChallengeStatus;
import com.ddbb.infra.data.mongo.entity.ChallengeEntity;
import com.ddbb.infra.data.mongo.entity.HallEntity;
import com.ddbb.infra.data.mongo.entity.UserEntity;
import com.ddbb.infra.data.mongo.repo.ChallengeRepo;
import com.ddbb.infra.data.mongo.repo.HallRepo;
import com.ddbb.infra.data.mongo.repo.UserRepo;
import com.ddbb.extapi.sms.Sioo;
import com.ddbb.internal.enums.UserType;
import com.ddbb.internal.utils.DateUtilPlus;
import com.ddbb.internal.utils.DdbbUtil;
import com.ddbb.internal.utils.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

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
    public Pair<Boolean,String> launchChallenge(LaunchChallengeRequest request)  {
        try {
            String challengeId = createChallengeRecord(request);
            sendLaunchChallengeSms(request);
            return Pair.of(true,challengeId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[launchChallenge] >>> from:{}, to:{}, with error: {}",request.getFrom(),request.getTo(),e.getMessage(),e);
            return Pair.of(false,e.getMessage());
        }
    }

    /**
     * 生成两条挑战记录
     * @param request
     * @throws ParseException
     */
    private String createChallengeRecord(LaunchChallengeRequest request)throws ParseException{
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

        ChallengeEntity sys = new ChallengeEntity();
        BeanUtils.copyProperties(from,sys);
        sys.setOwner(DdbbConstant.SYS_UID);
        sys.setAid(SnowflakeIdUtil.getInstance().nextId());
        sys.setChallengeRole(ChallengeRole.SYSTEM.getCode());



        challengeRepo.insert(from);
        challengeRepo.insert(to);
        challengeRepo.insert(sys);
        log.info("[createChallengeRecord] >>> from:{},to:{}, done",from,to);

        return challengeId;
    }

    /**
     * 给被挑战者发短信
     * @param request
     */
    private void sendLaunchChallengeSms(LaunchChallengeRequest request) throws Exception {
        Long to = request.getTo();
        UserEntity userTo = userRepo.findByUid(to);
        String phone = userTo.getPhone();


        UserEntity userFrom = userRepo.findByUid(request.getFrom());

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
    public Pair<Boolean,String> refuseChallenge(RefuseChallengeRequest request){
        Long uid = request.getUid();
        if(!Objects.equals(UserType.ASSISTANT_COACH,userRepo.getUserType(uid))){
            return Pair.of(false,"只能是助教做拒绝挑战的动作");
        }
        ChallengeEntity challenge = challengeRepo.findByChallengeIdAndTo(request.getChallengeId(), request.getUid());
        ChallengeStatus changeTo = ChallengeStatus.TO_REFUSED;
        Pair<Boolean,String> check = canChangeStatus(uid,challenge,changeTo);
        if(!check.getLeft()){
            return check;
        }

        challengeRepo.updateChallengeStatusAndFinish(request.getChallengeId(), changeTo);

        //发短信
        UserEntity userFrom = userRepo.findByUid(challenge.getFrom());
        try {
            sioo.sendMsg("亲，《DD打球》很抱歉通知您，助教拒绝了您的打球邀请",userFrom.getPhone());
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("拒绝挑战");
        return Pair.of(true,"ok");
    }
    /**
     * 接受挑战
     * @param request
     */
    public Pair<Boolean,String> acceptChallenge(AcceptChallengeRequest request){
        UserEntity user = userRepo.findByUid(request.getUid());
        if(user == null){
            return Pair.of(false,"uid is wrong");
        }
        if(user.getUserType() != UserType.ASSISTANT_COACH.getCode()){
            return Pair.of(false,"只能是助教做接受挑战的动作");
        }

        ChallengeEntity challenge = challengeRepo.findByChallengeIdAndTo(request.getChallengeId(), request.getUid());

        ChallengeStatus changeTo = ChallengeStatus.TO_ACCEPTED;
        Pair<Boolean,String> check = canChangeStatus(request.getUid(),challenge,changeTo);
        if(!check.getLeft()){
            return check;
        }

        challengeRepo.updateChallengeStatus(request.getChallengeId(), changeTo);

        //发短信
        UserEntity userFrom = userRepo.findByUid(challenge.getFrom());
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
    public Pair<Boolean,String> cancelChallenge(CancelChallengeRequest request){
        ChallengeEntity challenge = challengeRepo.findOneByChallengeId(request.getChallengeId());
        if(challenge == null){
            return Pair.of(false,"challengeId is wrong");
        }
        ChallengeStatus changeTo;
        Long uid = request.getUid();
        Long notifyUid;//要发短信通知的对方uid
        if(challenge.getFrom().equals(uid)){//发起方取消
            changeTo = ChallengeStatus.FROM_CANCELED;
            notifyUid = challenge.getTo();
        }else{//接受方取消
            changeTo = ChallengeStatus.TO_CANCELED;
            notifyUid = challenge.getFrom();
        }

        Pair<Boolean,String> check = canChangeStatus(uid,challenge,changeTo);
        if(!check.getLeft()){
            return check;
        }

        challengeRepo.updateChallengeStatusAndFinish(request.getChallengeId(), changeTo);

        //发短信
        UserEntity user2Notify = userRepo.findByUid(notifyUid);
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
    public Pair<Boolean,String> signIn(SignInChallengeRequest request){
        ChallengeEntity challenge = challengeRepo.findOneByChallengeId(request.getChallengeId());
        if(challenge == null){
            return Pair.of(false,"challengeId is wrong");
        }else if(challenge.getAlive() == null || challenge.getAlive().equals(false)){
            return Pair.of(false,"挑战已关闭");
        }
        ChallengeStatus changeTo;
        Long uid = request.getUid();
        Long notifyUid;//要发短信通知的对方uid
        boolean isFromSign;

        if(challenge.getFrom().equals(uid)){//发起方签到
            if(BooleanUtils.isTrue(challenge.getFromSignIn())){
                return Pair.of(false,"您已经签到，无需再签");
            }
            notifyUid = challenge.getTo();
            isFromSign = true;
        }else{//接受方签到
            if(BooleanUtils.isTrue(challenge.getToSignIn())){
                return Pair.of(false,"您已经签到，无需再签");
            }
            notifyUid = challenge.getFrom();
            isFromSign = false;
        }

        //临时赋值，做对比
        if(challenge.getFromSignIn() == null){
            challenge.setFromSignIn(false);
        }
        if(challenge.getToSignIn() == null){
            challenge.setToSignIn(false);
        }

        if(BooleanUtils.isFalse(challenge.getFromSignIn()) && BooleanUtils.isFalse(challenge.getToSignIn())){
            changeTo = ChallengeStatus.SINGLE_SIGN_IN;
        }else if(BooleanUtils.isTrue(challenge.getFromSignIn()) && BooleanUtils.isFalse(challenge.getToSignIn())){
            changeTo = ChallengeStatus.ALL_SIGN_IN;
        }else if(BooleanUtils.isFalse(challenge.getFromSignIn()) && BooleanUtils.isTrue(challenge.getToSignIn())){
            changeTo = ChallengeStatus.ALL_SIGN_IN;
        }else{
            return Pair.of(false,"双方都已经签到，无需再签");
        }

        Pair<Boolean,String> check = canChangeStatus(uid,challenge,changeTo);
        if(!check.getLeft()){
            return check;
        }

        //检查签到时间与经纬度
        String challengeStartDateTime = challenge.getChallengeDateStr() + " "+challenge.getStartTime()+":00:00";
        LocalDateTime cStart = DateUtilPlus.string2LocalDateTime(challengeStartDateTime);
        LocalDateTime now = LocalDateTime.now();
        long betweenHours = Duration.between(now,cStart).toHours();
        if(betweenHours > challengeConfig.getSignInAheadHours()) {
            return Pair.of(false,"比赛开始前"+challengeConfig.getSignInAheadHours()+"小时才能签到");
        }
        //距离球房500m才能签到
        HallEntity hall = hallRepo.findByHallId(challenge.getHallId());
        if(hall == null){
            return Pair.of(false,"hallId is wrong");
        }
        Double[] hallPos = hall.getCoordinate();
        double km = DdbbUtil.calculateDistanceKm(hallPos[0],hallPos[1]
                ,Double.valueOf(request.getLongitude()),Double.valueOf(request.getLatitude()));
        int miInt = (int)(km * 1000);
        if(miInt > challengeConfig.getSignInDistanceMi()){
            return Pair.of(false,"距离球房"+challengeConfig.getSignInDistanceMi()+"米内才能签到");
        }


        challengeRepo.signIn(request.getChallengeId(), changeTo,isFromSign);

        //发短信
        UserEntity user2Notify = userRepo.findByUid(notifyUid);
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
    public Pair<Boolean,String> saveScore(SaveScoreRequest request){
        ChallengeEntity challenge = challengeRepo.findOneByChallengeId(request.getChallengeId());

        //临时赋值，做对比
        if(challenge.getScoreSaved() == null){
            challenge.setScoreSaved(false);
        }

        if(BooleanUtils.isTrue(challenge.getScoreSaved())){
            return Pair.of(false,"比分已登记完成！无需重复登记");
        }

        Pair<Boolean,String> check = canChangeStatus(request.getUid(),challenge,ChallengeStatus.SCORE_SAVED);
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
    public Pair<Boolean,String> commentCoach(CommentCoachRequest request){
        Long uid = request.getUid();
        if(!Objects.equals(UserType.COMMON_USER,userRepo.getUserType(uid))){
            return Pair.of(false,"球友才能评价助教");
        }

        ChallengeEntity challenge = challengeRepo.findOneByChallengeId(request.getChallengeId());
        if(challenge == null){
            return Pair.of(false,"challengeId is wrong");
        }
        ChallengeStatus changeTo = ChallengeStatus.COACH_COMMENTED;
        //临时赋值，做对比
        if(challenge.getCoachCommented() == null){
            challenge.setCoachCommented(false);
        }

        if(BooleanUtils.isTrue(challenge.getCoachCommented())){
            return Pair.of(false,"助教已评价完成！无需重复评价");
        }

        Pair<Boolean,String> check = canChangeStatus(uid,challenge,changeTo);
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
    public Pair<Boolean,String> commentHall(CommentHallRequest request){
        Long uid = request.getUid();
        if(!Objects.equals(UserType.COMMON_USER,userRepo.getUserType(uid))){
            return Pair.of(false,"球友才能评价球房");
        }

        ChallengeEntity challenge = challengeRepo.findOneByChallengeId(request.getChallengeId());
        if(challenge == null){
            return Pair.of(false,"challengeId is wrong");
        }
        ChallengeStatus changeTo = ChallengeStatus.HALL_COMMENTED;
        //临时赋值，做对比
        if(challenge.getHallCommented() == null){
            challenge.setHallCommented(false);
        }

        if(BooleanUtils.isTrue(challenge.getHallCommented())){
            return Pair.of(false,"球房已评价完成！无需重复评价");
        }

        Pair<Boolean,String> check = canChangeStatus(uid,challenge,changeTo);
        if(!check.getLeft()){
            return check;
        }

        challengeRepo.commentHall(request.getChallengeId());

        return Pair.of(true,"ok");
    }

    /**
     * 判断一个挑战书是否能被修改状态
     * @param challenge
     * @param newStatus  要修改成什么状态
     * @return
     */
    private Pair<Boolean,String> canChangeStatus(Long uid,ChallengeEntity challenge,ChallengeStatus newStatus){
        if(uid == null){
            return Pair.of(false,"uid is wrong");
        }
        if(challenge == null){
            return Pair.of(false,"挑战不存在，challengeId is wrong");
        }

        if(!uid.equals(challenge.getFrom()) && !uid.equals(challenge.getTo()) ){
            return Pair.of(false,"您和本次比赛无关，无权操作");
        }

        if(challenge.getAlive() == null || challenge.getAlive() == false){
            return Pair.of(false,"挑战已关闭");
        }
        ChallengeStatus old = ChallengeStatus.of(challenge.getStatus());
        if(! ChallengeStatusSequence.canChange(old,newStatus)){
            return Pair.of(false,"不能从"+old+"状态 -> 修改成指定状态："+newStatus);
        }


        Pair<LocalDateTime,LocalDateTime> se = challengeKit.getChallengeStartAndEnd(challenge);

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



        LocalDateTime cStart = DateUtilPlus.string2LocalDateTime("2025-06-22 22:00:00");
        LocalDateTime now = LocalDateTime.now();
        long between = Duration.between(now,cStart).toMillis() / 1000;
        System.out.println(between);
    }
}
