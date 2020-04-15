package com.plansolve.farm.model.database.order;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/8/1
 * @Description: 农机手提前预约订单
 **/

@Data
@Entity
public class AppointOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer idAppointOrder; // 主键（数据库自增）

    @Column(nullable = false, updatable = false)
    private String appointOrderNo; // 订单号

    @Column(nullable = false)
    private String appointOrderState; // 订单状态

    @Column(nullable = false, updatable = false)
    private Long createBy; // 下单人

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createTime; // 下单时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date operateCreateTime; // 作业开始时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date operateEndTime; // 作业结束时间

    @Column(nullable = false, updatable = false)
    private String machineryType; // 农机类型

    @Column(nullable = false)
    private Integer machineryNum; // 农机数量

    @Column(nullable = false)
    private Float machineryAbility; // 作业能力

    @Column(nullable = false)
    private Double latitude; // 位置（经度）

    @Column(nullable = false)
    private Double longitude; // 位置（纬度）

    @Column(nullable = false)
    private String address; // 地址详情

}
