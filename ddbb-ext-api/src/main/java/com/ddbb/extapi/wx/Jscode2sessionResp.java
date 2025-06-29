package com.ddbb.extapi.wx;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Jscode2sessionResp {
    private String openId;
    private String sessionKey;
    private String unionId;
}
