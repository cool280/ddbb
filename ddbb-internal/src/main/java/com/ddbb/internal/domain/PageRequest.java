package com.ddbb.internal.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {

    // 当前页码，默认第1页
    private int pageNum = 1;
    // 每页显示数量，默认10条
    private int pageSize = 10;
}
