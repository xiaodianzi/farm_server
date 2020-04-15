package com.plansolve.farm.model.database;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 农田
 **/

@Data
@Entity
public class Farmland implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idFarmland; // 主键（数据库自增）

    @Column(nullable = false, updatable = false)
    private Long idUser; // 农田所属用户

    @Column(nullable = false, updatable = false)
    private Long idAddress; // 农田地址

    @Column(nullable = false, length = 16)
    private String farmlandName; // 农田名字

    @Column(nullable = false)
    private Float farmlandAcreage; // 农田大小（亩）

    @Column(nullable = false, length = 32)
    private String farmlandState; // 农田状态

    private String pictures; // 农田详情图片

    private String detail; // 农田描述

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createDate; // 创建时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date updateDate; // 更新时间

}
