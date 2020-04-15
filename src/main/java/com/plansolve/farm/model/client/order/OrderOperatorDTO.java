package com.plansolve.farm.model.client.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: 高一平
 * @Date: 2019/4/17
 * @Description:
 **/
@Data
public class OrderOperatorDTO {

    private Long idOperator; // 主键

    private String username; // 工作人员姓名

    private String mobile; // 工作人员联系方式

    private Float acre; // 作业亩数

    private Boolean isReported; // 是否提交完工报告

    private BigDecimal demandPrice; // 索要报酬

}
