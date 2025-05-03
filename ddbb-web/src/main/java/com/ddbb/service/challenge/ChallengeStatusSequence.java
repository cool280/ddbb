package com.ddbb.service.challenge;

import com.ddbb.internal.enums.ChallengeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 挑战书序列，挑战必须处于parent状态时，才能执行child操作（变成child状态）
 * 当前序列情况：
 * INIT(1,"发起方发起") - TO_REFUSED(2,"接受方拒绝") --终结
 *                    - FROM_CANCELED(4,"发起方取消") -- 终结
 *                    - TO_ACCEPTED(3,"接受方接受") - TO_CANCELED(5,"接受方取消") -- 终结
 *                                                - FROM_CANCELED(4,"发起方取消") -- 终结
 *                                                - SINGLE_SIGN_IN(6,"单方签到") - FROM_CANCELED(4,"发起方取消") -- 终结
 *                                                                             - TO_CANCELED(5,"接受方取消") -- 终结
 *                                                                             - ALL_SIGN_IN(7,"双方签到") - FROM_CANCELED(4,"发起方取消") -- 终结
 *                                                                                                       - TO_CANCELED(5,"接受方取消") -- 终结
 *                                                                                                       - SCORE_SAVED(8,"比分已登记")      - COACH_COMMENTED(9,"助教已评价")
 *  *                                                                                                                                     - HALL_COMMENTED(10,"球房已评价")
 *                                                                                                       - COACH_COMMENTED(9,"助教已评价")  - SCORE_SAVED(8,"比分已登记")
 *                                                                                                                                        - HALL_COMMENTED(10,"球房已评价")
 *                                                                                                       - HALL_COMMENTED(10,"球房已评价")  - SCORE_SAVED(8,"比分已登记")
 *                                                                                                                                        - COACH_COMMENTED(9,"助教已评价")
 */
@Data
public class ChallengeStatusSequence {
    private static Map<ChallengeStatus,SequenceNode> map = new HashMap<>();
    private static SequenceNode INIT = new SequenceNode(ChallengeStatus.INIT);
    private static SequenceNode TO_REFUSED = new SequenceNode(ChallengeStatus.TO_REFUSED);
    private static SequenceNode TO_ACCEPTED = new SequenceNode(ChallengeStatus.TO_ACCEPTED);
    private static SequenceNode FROM_CANCELED = new SequenceNode(ChallengeStatus.FROM_CANCELED);
    private static SequenceNode TO_CANCELED = new SequenceNode(ChallengeStatus.TO_CANCELED);
    private static SequenceNode SINGLE_SIGN_IN = new SequenceNode(ChallengeStatus.SINGLE_SIGN_IN);
    private static SequenceNode ALL_SIGN_IN = new SequenceNode(ChallengeStatus.ALL_SIGN_IN);
    private static SequenceNode SCORE_SAVED = new SequenceNode(ChallengeStatus.SCORE_SAVED);
    private static SequenceNode COACH_COMMENTED = new SequenceNode(ChallengeStatus.COACH_COMMENTED);
    private static SequenceNode HALL_COMMENTED = new SequenceNode(ChallengeStatus.HALL_COMMENTED);
    static {
        INIT.addChild(TO_REFUSED);
        INIT.addChild(TO_ACCEPTED);
        INIT.addChild(FROM_CANCELED);


        TO_ACCEPTED.addChild(TO_CANCELED);
        TO_ACCEPTED.addChild(FROM_CANCELED);
        TO_ACCEPTED.addChild(SINGLE_SIGN_IN);

        SINGLE_SIGN_IN.addChild(ALL_SIGN_IN);
        SINGLE_SIGN_IN.addChild(FROM_CANCELED);
        SINGLE_SIGN_IN.addChild(TO_CANCELED);

        ALL_SIGN_IN.addChild(SCORE_SAVED);
        ALL_SIGN_IN.addChild(COACH_COMMENTED);
        ALL_SIGN_IN.addChild(HALL_COMMENTED);
        ALL_SIGN_IN.addChild(FROM_CANCELED);
        ALL_SIGN_IN.addChild(TO_CANCELED);


        //以下3个状态可随意转化，先变成谁都行
        SCORE_SAVED.addChild(COACH_COMMENTED);
        SCORE_SAVED.addChild(HALL_COMMENTED);

        COACH_COMMENTED.addChild(SCORE_SAVED);
        COACH_COMMENTED.addChild(HALL_COMMENTED);

        HALL_COMMENTED.addChild(SCORE_SAVED);
        HALL_COMMENTED.addChild(COACH_COMMENTED);
    }

    /**
     * 能否从状态old变成newer
     * @param old
     * @param newer
     * @return
     */
    public static boolean canChange(ChallengeStatus old,ChallengeStatus newer){
        List<ChallengeStatus> children = ChallengeStatusSequence.getChildren(old);
        return children.contains(newer);
    }


    /**
     * 获取子状态
     * @param current
     * @return
     */
    public static List<ChallengeStatus> getChildren(ChallengeStatus current){
        if(map.get(current) == null){
            return Collections.emptyList();
        }
        if(CollectionUtils.isEmpty(map.get(current).getChildren())){
            return Collections.emptyList();
        }

        return map.get(current).getChildren().stream().map(SequenceNode::getChallengeStatus).collect(Collectors.toList());
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static class SequenceNode{
        private ChallengeStatus challengeStatus;
        private List<SequenceNode> children = new ArrayList<>();

        public SequenceNode(ChallengeStatus me){
            this.challengeStatus = me;
            map.put(me,this);
        }

        public void addChild(SequenceNode sequenceNode){
            children.add(sequenceNode);
        }
    }

    public static void main(String[] args) {
        System.out.println(getChildren(ChallengeStatus.ALL_SIGN_IN));
    }
}
