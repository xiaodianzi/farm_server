package com.plansolve.farm.model.database.agricultural;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: Andrew
 * @Date: 2019/3/29
 * @Description:
 */
@Entity
@Data
public class GrainMarket implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer idGrainMarket;//主键id

    @Column(nullable = false, updatable = false)
    private Long idUser;//发布人

    @Column(nullable = false)
    private String name;//姓名

    @Column(nullable = false)
    private String mobile;//电话

    @Column(nullable = false, updatable = false)
    private String marketType;//买卖类型

    @Column(nullable = false)
    private String grainType;//作物类型

    @Column(nullable = false)
    private float amount;//粮食数量

    private BigDecimal salePrice;//出售价格

    private BigDecimal minimumBuyPrice;//收购最低价

    private BigDecimal highestBuyPrice;//收购最高价

    @Column(nullable = false)
    private String addressDetail; // 地址

    private String validTime;//有效期

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createTime; // 创建时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date invalidTime; // 过期时间

}
