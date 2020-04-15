package com.plansolve.farm.model.client.userOrder;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: 高一平
 * @Date: 2019/6/6
 * @Description:
 **/
@Data
public class OrderItemDTO {

    private String userOrderNo; // 订单编号

    private String crop;

    private String machineryType; // 所需农机类型

    private BigDecimal price; // 下单金额（单价）

    private String address;

    private String createTime;

    private String startTime;

    private Float acreage;

    private Integer state;

}
