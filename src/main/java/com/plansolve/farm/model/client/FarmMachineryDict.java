package com.plansolve.farm.model.client;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2018/6/11
 * @Description:
 **/
@Data
public class FarmMachineryDict implements Serializable {

    private Integer idMachineryType; // 农机id

    private String value; // 农机名称

}
