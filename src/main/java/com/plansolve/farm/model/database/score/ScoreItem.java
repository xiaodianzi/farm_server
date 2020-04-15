package com.plansolve.farm.model.database.score;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/3/18
 * @Description:
 **/

@Entity
@Data
public class ScoreItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idScoreItem;

    @Column(nullable = false)
    private String title; // 积分商品主标题

    @Column(nullable = false)
    private String subTitle; // 积分商品副标题

    @Column(nullable = false, updatable = false)
    private String itemType; // 积分类型（现金、物品等）

    @Column(nullable = false)
    private Integer num; // 库存（需严谨的事物）

    @Column(nullable = false)
    private Integer consumeScore; // 消耗积分数

    @Column(nullable = false)
    private Boolean isValid; // 商品是否有效

    private String pic; // 商品图片

    private String detail; // 商品介绍

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date updateTime;

}
