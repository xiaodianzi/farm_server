package com.plansolve.farm.model.database.account;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/2/20
 * @Description: 财务变更申请详情
 **/
@Entity
@Data
public class ApplicationDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idApplicationDetail;

    @Column(updatable = false, nullable = false)
    private Long idTransactionApplication; // 申请ID

    @Column(updatable = false)
    private String transactionId; // 微信交易号

    @Column(updatable = false, nullable = false)
    private String transactionType; // 审核类型 初审、复核、财务打款

    @Column(updatable = false, nullable = false)
    private String applicationType; // 审核类型 初审、复核、财务打款

    @Column(updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date applicationTime; // 审核时间

    @Column(updatable = false, nullable = false)
    private BigDecimal applicationMoney; // 审核金额

    @Column(updatable = false, nullable = false)
    private Boolean applicationResult; // 审核结果

    @Column(updatable = false)
    private Integer idAdmin; // 审核人ID

    @Column(updatable = false)
    private String adminMobile; // 审核人电话

    @Column(updatable = false)
    private String applicationDetail; // 审核备注
}
