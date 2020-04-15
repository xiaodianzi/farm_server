package com.plansolve.farm.model.database.promotion;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Andrew
 * @Date: 2019/5/28
 * @Description: 优惠活动参与者
 */
@Data
@Entity
public class PromotionPlayer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idPromotionPlayer; // 主键

    @Column(nullable = false, updatable = false)
    private Long idPromotionActivity; // 优惠活动

    @Column(nullable = false, updatable = false)
    private Long idUser; // 参与者

    @Column(updatable = false)
    private String idFarmLands; // 活动关联的用户土地id

    @Column(updatable = false)
    private Float landAcreage; // 土地面积

    @Column(nullable = false)
    private String remark; // 活动备注

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date createTime; // 参与时间

    @Column(nullable = false)
    private boolean valid; //是否有效

}
