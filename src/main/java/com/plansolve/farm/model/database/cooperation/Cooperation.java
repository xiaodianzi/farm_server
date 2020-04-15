package com.plansolve.farm.model.database.cooperation;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 合作社
 **/

@Data
@Entity
public class Cooperation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer idCooperation; // 主键（数据库自增）

    @Column(nullable = false, length = 16)
    private String cooperationName; // 合作社名称

    @Column(nullable = false, length = 16)
    private String cooperationType; // 合作社类型

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date registTime; // 注册时间

    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime; // 更新时间

    @Column(nullable = false, updatable = false)
    private Long idAddress; // 合作社地址

    @Column(nullable = false, length = 32)
    private String cooperationState; // 合作社状态

    @Column(nullable = false, updatable = false)
    private Long idUser; // 社长（创建人）

    @Column(nullable = false)
    private Integer sno; // 序号

    private String description; // 描述

}
