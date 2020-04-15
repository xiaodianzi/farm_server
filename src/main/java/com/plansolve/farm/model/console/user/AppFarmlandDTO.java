package com.plansolve.farm.model.console.user;

import lombok.Data;

/**
 * @Author: 高一平
 * @Date: 2019/6/3
 * @Description:
 **/
@Data
public class AppFarmlandDTO {

    private Long idFarmland; // 主键（数据库自增）

    private String farmlandName; // 农田名字

    private Float farmlandAcreage; // 农田大小（亩）

    private String farmlandAddress; // 农田地址

}
