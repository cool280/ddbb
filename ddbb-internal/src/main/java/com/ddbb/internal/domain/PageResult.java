package com.ddbb.internal.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {
    // 当前页码
    private int pageNum = 1;
    // 每页数量
    private int pageSize = 10;
    // 总记录数
    private long totalRecord = 0L;
    // 总页数
    private long totalPages = 0L;
    // 当前页数据
    private List<T> records;
}
