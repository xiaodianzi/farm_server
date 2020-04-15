package com.plansolve.farm.model.client;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/4
 * @Description:
 **/

@Data
public class PageDTO<T> {

    private Long total = 0l;

    private List<T> rows = new ArrayList<>();

}
