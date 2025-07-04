package com.ddbb.controller.request.evaluation;

import com.ddbb.internal.domain.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class HallEvalQueryRequest extends PageRequest implements Serializable {

    private Long hallId; //助教UID

    public boolean validate() {
        if (null == hallId || hallId <= 0L) {
            return false;
        }
        return true;
    }
}
