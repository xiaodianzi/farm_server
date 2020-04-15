package com.plansolve.farm.model.database.score;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/3/18
 * @Description: 用户积分账户
 **/
@Entity
@Data
public class ScoreUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id; // 主键（数据库自增）

    @Column(nullable = false, updatable = false)
    private Long idUser; // 积分用户ID

    @Column(nullable = false)
    private Integer latestScore; // 用户最新积分

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createTime; // 创建时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date latestChangeTime; // 最近一次更新时间

}
