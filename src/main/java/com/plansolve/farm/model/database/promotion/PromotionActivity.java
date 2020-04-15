package com.plansolve.farm.model.database.promotion;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Andrew
 * @Date: 2019/5/27
 * @Description: 优惠活动
 */
@Entity
@Data
public class PromotionActivity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idPromotionActivity;

    private String pictureUrl; // 活动图片

    @Column(nullable = false)
    private String activityName; // 活动名称

    @Column(nullable = false)
    private String description; // 活动描述（对规则进行简单描述，使客户端用户快速理解该规则）

    @Column(nullable = false)
    private String activityType; // 活动类型（同一类型下可以有多个不同的优惠活动）

    private Integer maxPlayers; // 人数限制

    private Float maxAcreage; // 土地面积限制

    private String contacts; // 联系方式

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date startTime; // 开始时间

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date endTime; // 结束时间

    private String addressDetail; //活动地点

    @Column(nullable = false)
    private Boolean isValid; // 该规则是否可用（APP页面只显示有效的活动）

}
