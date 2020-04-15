package com.plansolve.farm.model.database.promotion;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

/**
 * @Author: Andrew
 * @Date: 2019/5/27
 * @Description: 控制优惠活动开关
 */
@Entity
@Data
public class ActivitySwitch implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idActivitySwitch;

    @Column(nullable = false, updatable = false)
    private Long idPromotionActivity;

    @Column(nullable = false)
    private String activityType; // 活动名称

    @Column(nullable = false)
    private String activityName; // 活动名称

    @Column(nullable = false)
    private Boolean valid; // 是否开启

}
