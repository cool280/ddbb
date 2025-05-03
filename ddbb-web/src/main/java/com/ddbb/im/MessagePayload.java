package com.ddbb.im;

import lombok.Data;

@Data
public class MessagePayload {

    private String mid;

    private String fromUserId;

    private String toUserId;

    private String content;

    private Long timestamp = System.currentTimeMillis();
}
