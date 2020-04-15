package com.plansolve.farm.model.database.order;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/8/6
 * @Description: 竞价订单接单人
 **/

@Data
@Entity
public class BidOrderOperator implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer idBidOrderOperator; // 主键

    @Column(nullable = false, updatable = false)
    private Integer idBidOrder; // 竞价订单主键

    @Column(nullable = false, updatable = false)
    private Long idUser; // 竞价订单申请人

    @Column(nullable = false, updatable = false)
    private String username; // 申请人姓名

    @Column(nullable = false, updatable = false)
    private String userMobile; // 申请人手机号码

    @Column(nullable = false, updatable = false)
    private String address; // 申请人位置

    @Column(nullable = false, updatable = false)
    private Double latitude; // 申请人位置（经度）

    @Column(nullable = false, updatable = false)
    private Double longitude; // 申请人位置（纬度）

    @Column(nullable = false)
    private String operatorState; // 申请人状态

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date startTime; // 订单开始时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date endTime; // 订单结束时间

    @Column(nullable = false, updatable = false)
    private String machineryType; // 农机类型

    @Column(nullable = false)
    private Integer machineryNum; // 农机数量

    @Column(nullable = false)
    private Float machineryAbility; // 农机作业能力

    @Column(nullable = false)
    private Float acreage; // 可作业面积

    @Column(nullable = false)
    private BigDecimal price; // 竞价价钱

    private Long idInvited; // 邀请人

    private Integer idCooperation; // 合作社

    private Boolean isCooperative; // 是否协同作业

}
