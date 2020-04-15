package com.plansolve.farm.model.database.score;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/3/18
 * @Description: 用户积分日志（每一项积分变动都应有对应日志）
 **/

@Entity
@Data
public class ScoreLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idScoreLog;

    @Column(nullable = false, updatable = false)
    private Long idUser; // 变动积分用户

    @Column(nullable = false, updatable = false)
    private String changeType; // 变动类型（增加积分，兑换现金或物品，分好不同的类型）

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date changeTime; // 变动时间

    @Column(nullable = false, updatable = false)
    private Integer scoreOld; // 变动前积分数值

    @Column(nullable = false, updatable = false)
    private Integer changeScore; // 变动积分（增加积分为“+”，减少积分为“-”）

    @Column(nullable = false, updatable = false)
    private Integer scoreNew; // 变动后积分数值

    @Column(nullable = false, updatable = false)
    private String detail; // 详情


}
