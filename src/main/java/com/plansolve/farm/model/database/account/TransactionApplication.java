package com.plansolve.farm.model.database.account;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/2/19
 * @Description: 财务变更申请
 **/
@Entity
@Data
public class TransactionApplication implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idTransactionApplication;

    @Column(updatable = false, nullable = false)
    private String transactionApplicationNo;

    @Column(updatable = false, nullable = false)
    private Long idUser; // 申请人

    @Column(updatable = false, nullable = false)
    private String applicationType; // 申请类型--提现、付款

    @Column(nullable = false)
    private String applicationState; // 申请进度--创建、初审、复核

    @Column(updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date applicationCreateTime; // 申请创建时间

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date applicationUpdateTime; // 申请更新时间

    private Long idUserOrder; // 相关订单号

    private Long idBankCard; // 相关银行卡

    @Column(updatable = false)
    private BigDecimal money; // 涉及金额

}
