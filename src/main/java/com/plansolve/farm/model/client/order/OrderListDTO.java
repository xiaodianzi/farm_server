package com.plansolve.farm.model.client.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.plansolve.farm.model.client.AddressDTO;
import com.plansolve.farm.util.Date2LongSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/20
 * @Description:
 **/
@Data
public class OrderListDTO {

    private String userOrderNo; // 订单编号

    private String userOrderState; // 订单状态

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime; // 下单时间

    private String createMan; // 下单人昵称

    private String target; // 下单给哪儿

    private Float arce; // 面积（亩数）

    private AddressDTO address; // 地块地址

    private String cropName; // 作物名称

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date startTime; // 开始时间

    private String machineryType; // 所需农机类型

    private Integer machineryNum; // 所需农机数量

    private Integer period; // 工作周期（工作几天）

    private BigDecimal price; // 下单金额（单价）

    private Boolean isCooperative; // 作业方式

    private String cannotGet; // 该单当前用户不可抢

    public Boolean getCooperative() {
        return isCooperative;
    }

    public void setCooperative(Boolean cooperative) {
        isCooperative = cooperative;
    }
}
