package com.plansolve.farm.model.client.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: 高一平
 * @Date: 2018/6/11
 * @Description:
 **/
@Data
public class MachineryDTO {

    private String machineryNo; // 农机编号

    private Integer idMachineryType;

    @NotBlank(message = "农机类型不能为空")
    private String machineryType; // 农机类型

    @NotNull(message = "农机作业能力不能为空")
    private Integer machineryAbility; // 作业能力（亩/时）

    private String machineryState; // 农机状态

    @NotNull(message = "农机数量不能为空")
    private Integer count; // 农机类型

    private String draggingDevice; // 拖拽装置

    private Integer ratedPower; // 额定功率

    private String licenseNum; // 牌照号码

    private String pictures; // 农机图片

    private String detail; // 农机描述

}
