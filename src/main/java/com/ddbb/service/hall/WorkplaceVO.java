package com.ddbb.service.hall;

import lombok.Data;

@Data
public class WorkplaceVO {
    /**
     * 球房id
     */
    private Long hallId;
    /**
     * 球房名称
     */
    private String nickname;
    /**
     * 球房头像
     */
    private String avatar;
    private String address;
    private String tel;
    /**
     * 乘车路线
     */
    private String wayTo;
    /**
     * 自驾停车优惠
     */
    private String parkingDiscount;
}
