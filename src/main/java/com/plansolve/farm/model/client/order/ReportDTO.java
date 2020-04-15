package com.plansolve.farm.model.client.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.plansolve.farm.model.client.user.UserDTO;
import com.plansolve.farm.util.Date2LongSerializer;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 完工报告传输对象
 **/

@Data
public class ReportDTO {

    @NotBlank(message = "订单编号不能为空")
    private String orderNo; // 订单编号

    @JsonSerialize(using = Date2LongSerializer.class)
    @NotNull(message = "作业开始时间不能为空")
    private Date taskStartTime; // 开始时间

    @JsonSerialize(using = Date2LongSerializer.class)
    @NotNull(message = "作业结束时间不能为空")
    private Date taskEndTime; // 结束时间

    @NotNull(message = "作业亩数不能为空")
    private Float acre; // 作业亩数

    @NotNull(message = "使用农机数不能为空")
    private Integer machineryNum; // 使用农机数

    private BigDecimal demandPrice;

    private String detail; // 备注

    private UserDTO user; // 相关用户

}
