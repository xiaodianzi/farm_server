package com.plansolve.farm.model.client.userOrder;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.plansolve.farm.util.Date2LongSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/4/30
 * @Description: 订单列表ITEM传输对象
 **/
@Data
public class OrderSimpleDTO {

    private String userOrderNo; // 订单编号
    private String machineryTypeName; // 农机类型名称
    private String createMan; // 下单人昵称
    private BigDecimal price; // 下单金额（单价）
    private Integer userOrderState; // 订单状态
    private String address; // 订单地块所在地址
    private String startTime; // 订单作业开始时间
    private Float acreage; // 面积（亩数）
    @JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime; // 下单时间
    private Integer crop; // 作物
    private String cropImg; // 作物图标

    private Boolean cooperative; // 是否是协同作业订单
    private Integer userIdentity; // 0为下单人，1为接单人，2为协同作业人员（既不是接单人，也不是小队长），3为小队长，-1为无关人员
    private Integer operatorState; // 0为被邀请，1为已接受，2为已完成,-1为已拒绝，-2为已取消
    private Boolean canSee; // 当前用户是否可以查看订单详情

}
