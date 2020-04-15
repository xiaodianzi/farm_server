package com.plansolve.farm.model.database.order;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/

@Data
@Entity
public class UserOrder implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idUserOrder; // 主键（数据库自增）

    @Column(nullable = false, updatable = false, length = 12)
    private String userOrderNo; // 订单编号

    @Column(nullable = false)
    private String userOrderState; // 订单状态

    @Column(nullable = false, updatable = false)
    private Long createBy; // 下单人

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createTime; // 下单时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date updateTime; // 下单时间

    @Column(nullable = false, updatable = false)
    private Long idFarmland; // 下单地块

    @Column(nullable = false)
    private String target; // 下单给   1、平台    2、自身所在合作社   3、电话查找农机手（恰好是社长，则可认为下单给该合作社）

    @Column(nullable = false, updatable = false)
    private Float arce; // 面积（亩数）

    @Column(nullable = false, updatable = false)
    private String cropName; // 作物名称

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date startTime; // 开始时间

    @Column(nullable = false, updatable = false)
    private String machineryType; // 所需农机类型

    @Column(nullable = false, updatable = false)
    private Integer machineryNum; // 所需农机数量

    @Column(nullable = false, updatable = false)
    private Integer period; // 工作周期（工作几天）

    @Column(nullable = false)
    private BigDecimal price; // 下单金额（单价）

    private BigDecimal demandAmount; // 接单人索要金额

    private BigDecimal amountPayable; // 实付金额

    private Boolean isOnlinePayment; // 是否是线上付款

    private Date paymentTime; // 付款时间

    @Column(nullable = false, updatable = false)
    private String guideName; // 引路人名称

    @Column(nullable = false, updatable = false)
    private String guideMobile; // 引路人联系方式

    @Column(nullable = false)
    private String assemblyAddress; // 集合地址

    @Column(nullable = false)
    private Double latitude; // 集合地址（纬度）

    @Column(nullable = false)
    private Double longitude; // 集合地址（经度）

    private Long receiveBy; // 接单人

    private Boolean isCooperative; // 作业方式

    private Long reportedBy; // 完工提交人

    @Column(updatable = false)
    private String detail; // 备注

    public Boolean getCooperative() { return isCooperative; }

    public void setCooperative(Boolean cooperative) { isCooperative = cooperative; }

}
