package com.plansolve.farm.model.console;

import lombok.Data;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/4
 * @Description:
 **/

@Data
public class PageDTO<T> {

    private Long total;

    private List<T> rows;

}
