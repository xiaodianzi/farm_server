package com.plansolve.farm.model.console.user;

import lombok.Data;

/**
 * @Author: 高一平
 * @Date: 2019/6/4
 * @Description:
 **/
@Data
public class AppMachineryDTO {

    private Long idMachinery; // 主键（数据库自增）

    private String machineryType; // 农机类型

    private Integer machineryAbility; // 作业能力（亩/时）

    private Integer count; // 农机类型

}
