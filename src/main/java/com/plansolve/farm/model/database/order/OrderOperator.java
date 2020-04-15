package com.plansolve.farm.model.database.order;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/

@Data
@Entity
public class OrderOperator implements Serializable {

    /*@EmbeddedId
    @Column(updatable = false)
    private OrderEmbeddedId id; // 复合主键*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idOperator; // 主键

    @Column(nullable = false, updatable = false)
    private Long idUserOrder; // 订单

    @Column(nullable = false, updatable = false)
    private Long idUser; // 用户

    @Column(nullable = false)
    private String operatorState;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date endTime;

    @Column(updatable = false)
    private Integer idCooperation; // 合作社

    private Long idCompletionReport; // 完工报告

}
