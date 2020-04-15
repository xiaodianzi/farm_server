package com.plansolve.farm.model.database.log;

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
public class OrderChangeLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idOrderChangeLog; // 主键（数据库自增）

    @Column(nullable = false, updatable = false)
    private Long idUserOrder; // 订单

    @Column(nullable = false, updatable = false, length = 32)
    private String changeType; // 变更类型

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date changeTime; // 变更时间

    @Column(nullable = false, updatable = false)
    private Long changeBy; // 变更人

    @Column(updatable = false)
    private String detail; // 变更详情

}
