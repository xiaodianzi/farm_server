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
public class CompletionReport implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idCompletionReport; // 主键（数据库自增）

    @Column(nullable = false, updatable = false)
    private Long idUserOrder; // 订单

    @Column(nullable = false, updatable = false)
    private Long idUser; // 农机手

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date taskStartTime; // 作业开始时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date taskEndTime; // 作业结束时间

    @Column(nullable = false)
    private Float acre; // 作业亩数

    @Column(nullable = false)
    private Integer machineryNum; // 使用农机数

    private BigDecimal demandPrice;

    private String detail; // 备注

}
