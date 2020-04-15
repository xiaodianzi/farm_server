package com.plansolve.farm.model.bo.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: 高一平
 * @Date: 2019/5/9
 * @Description:
 **/
@Data
public class OrderChangeLogDTO {

    private String username; // 工作人员姓名

    private String mobile; // 工作人员联系方式


    private String changeType; // 变更类型

    private String changeTime; // 变更时间

    private String detail; // 变更详情


    private Float acreage; // 作业亩数

    private BigDecimal demandPrice; // 索要报酬

}
