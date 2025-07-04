package com.ddbb.controller.request.evaluation;

import com.ddbb.internal.domain.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class CoachEvalQueryRequest extends PageRequest implements Serializable {

    private Long coachUid; //助教UID

    public boolean validate(){
        if(null==coachUid || coachUid<=0L){
            return false;
        }
        return true;
    }
}
