package com.plansolve.farm.model.database.log;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/6/1
 * @Description: 合作社变更日志
 **/

@Data
@Entity
public class CooperationChangeLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idCooperationChangeLog; // 主键（数据库自增）

    @Column(nullable = false, updatable = false)
    private Integer idCooperation; // 农机合作社ID

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date changeTime; // 变更时间

    @Column(nullable = false, updatable = false, length = 32)
    private String changeType; // 变更类型

    @Column(nullable = false, updatable = false)
    private Long changeBy; // 变更人

    @Column(updatable = false)
    private String detail; // 相关用户

}
