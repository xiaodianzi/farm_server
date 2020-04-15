package com.plansolve.farm.model.database.promotion;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2019/6/4
 * @Description: 奖品类
 */

@Data
@Entity
public class Prize implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idPrize;//奖品id

    @Column(nullable = false)
    private Long idPromotionActivity;//关联的优惠活动

    @Column(nullable = false)
    private String prizeName;//奖品名称

    @Column(nullable = false)
    private Integer prizeAmount;//奖品（剩余）数量

    @Column(nullable = false)
    private Double prizeWeight;//奖品权重

    private String remark; //备注

}
