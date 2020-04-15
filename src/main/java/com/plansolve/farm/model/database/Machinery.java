package com.plansolve.farm.model.database;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 农机
 **/

@Data
@Entity
public class Machinery implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idMachinery; // 主键（数据库自增）

    @Column(nullable = false, updatable = false)
    private Long idUser; // 农机所属用户

    private Integer idMachineryType; // 农机类型 // 新增 农机类型ID

    private String parentMachineryType; // 农机类型 // 新增 农机类型父类型

    @Column(nullable = false, updatable = false, length = 32)
    private String machineryType; // 农机类型

    @Column(nullable = false)
    private Integer machineryAbility; // 作业能力（亩/时）

    @Column(nullable = false, length = 32)
    private String machineryState; // 农机状态

    @Column(nullable = false)
    private Integer count; // 农机类型

    @Column(length = 16)
    private String draggingDevice; // 拖拽装置

    private Integer ratedPower; // 额定功率

    @Column(length = 16)
    private String licenseNum; // 牌照号码

    private String pictures; // 农机图片

    private String detail; // 农机描述

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createDate; // 创建时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date updateDate; // 更新时间
}
