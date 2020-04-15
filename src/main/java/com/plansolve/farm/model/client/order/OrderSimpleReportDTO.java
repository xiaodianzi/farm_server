package com.plansolve.farm.model.client.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/4/17
 * @Description:
 **/
@Data
public class OrderSimpleReportDTO {

    private BigDecimal demandPriceAmount;

    private String operatorNum;

    private String reportedOperatorNum;

    private List<OrderOperatorDTO> operators;

}
