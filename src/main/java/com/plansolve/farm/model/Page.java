package com.plansolve.farm.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/4/30
 * @Description: 分页对象
 **/
@Data
public class Page<T> {

    // 页码
    private Integer pageNo = 0;
    // 每页大小
    private Integer pageSize = 20;
    // 总数
    private Long total = 0l;
    // 内容
    private List<T> rows = new ArrayList<>();

}
