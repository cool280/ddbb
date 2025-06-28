package com.ddbb.controller.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class PointQueryRequest implements Serializable {

    private Long uid;

    public boolean validate() {
        if (uid == null || uid <= 0L) {
            return false;
        }
        return true;
    }
}
