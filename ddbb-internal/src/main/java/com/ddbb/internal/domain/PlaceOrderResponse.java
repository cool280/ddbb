package com.ddbb.internal.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 下单给前端返回的对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderResponse {
    private String timeStamp;
    private String nonceStr;
    private String package1;
    private String signType;
    private String paySign;
}
