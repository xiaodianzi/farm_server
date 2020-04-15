package com.plansolve.farm.model.database.order;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/8/3
 * @Description:
 **/

@Data
@Entity
public class BidOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer idBidOrder; // 竞价订单主键

    @Column(nullable = false, updatable = false)
    private String bidOrderNo; // 竞价订单编号

    @Column(nullable = false)
    private String bidOrderState; // 竞价订单状态

    @Column(nullable = false, updatable = false)
    private Long createBy; // 下单人

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createTime; // 下单时间

    @Column(nullable = false, updatable = false)
    private String farmlandAddress; // 农田地址

    @Column(nullable = false, updatable = false)
    private Float arceage; // 农田面积

    @Column(nullable = false, updatable = false)
    private String cropName; // 农作物种类

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date startTime; // 作业开始时间

    @Column(nullable = false, updatable = false)
    private String machineryType; // 农机类型

    @Column(nullable = false, updatable = false)
    private Integer machineryNum; // 农机数量

    @Column(nullable = false, updatable = false)
    private Integer period; // 作业周期

    private String detail; // 详情

}
