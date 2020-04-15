package com.plansolve.farm.model.client.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.plansolve.farm.model.client.user.FarmlandDTO;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.util.Date2LongSerializer;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/20
 * @Description:
 **/
@Data
public class OrderDTO {

    private String userOrderNo; // 订单编号

    private String userOrderState; // 订单状态

    private UserDTO createBy; // 下单人

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date createTime; // 下单时间

    private FarmlandDTO farmlandDTO; // 下单地块

    private String operatorState; // 工作人员状态

    @NotBlank(message = "请指定下单TARGET")
    private String target; // 下单给哪儿

    private Float arce; // 面积（亩数）

    @NotBlank(message = "作物名称不能为空")
    private String cropName; // 作物名称

    @JsonSerialize(using = Date2LongSerializer.class)
    @NotNull(message = "开始时间不能为空")
    private Date startTime; // 开始时间

    @JsonSerialize(using = Date2LongSerializer.class)
    private Date updateTime; // 更新时间

    private Integer idMachineryType;

    @NotBlank(message = "农机类型不能为空")
    private String machineryType; // 所需农机类型

    @NotNull(message = "农机数量不能为空")
    private Integer machineryNum; // 所需农机数量

    @NotNull(message = "工作周期不能为空")
    private Integer period; // 工作周期（工作几天）

    @NotNull(message = "下单金额不能为空")
    private BigDecimal price; // 下单金额（单价）

    private BigDecimal demandAmount; // 接单人索要金额

    private BigDecimal amountPayable; // 应付金额

    private Boolean isOnlinePayment; // 是否是线上付款

    private String detail; // 备注

    private GuideDTO guideDTO; // 领路人及集合信息

    private UserDTO receiveBy; // 接单人

    private UserDTO reportedBy;

    private Boolean isCooperative; // 作业方式

    private BigDecimal income; //收入

    private String cannotGet; // 该单当前用户不可抢

    public Boolean getCooperative() { return isCooperative; }

    public void setCooperative(Boolean cooperative) { isCooperative = cooperative; }

}
