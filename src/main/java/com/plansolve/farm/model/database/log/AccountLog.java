package com.plansolve.farm.model.database.log;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/2/22
 * @Description:
 **/
@Entity
@Data
public class AccountLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idAccountLog;

    @Column(updatable = false, nullable = false)
    private Long idUser; // 用户ID

    private String accountType; // 账户类型 农小满钱包、微信钱包、银行卡

    @Column(updatable = false, nullable = false)
    private Date changeTime; // 动作发生时间

    @Column(updatable = false, nullable = false)
    private String changeType; // 发生类型 付款、收款、充值、提现

    @Column(updatable = false, nullable = false)
    private BigDecimal changeNum; // 金钱变更数量

    @Column(updatable = false)
    private BigDecimal beforeAccountBalance; // 变更前农小满钱包余额

    @Column(updatable = false)
    private BigDecimal afterAccountBalance; // 变更前农小满钱包余额

    private Long idTransactionApplication; // 相关申请ID

    private String openId; // 微信支付账户ID（微信支付平台下发）

    private Long idWxAccount; // 用户微信账户ID（农小满平台生成）

    private Long idWxPayOrderNotify; // 微信支付回执

    private String bankCardNo; // 用户相关银行卡号

    private Long idBankCard; // 银行卡ID

    private String detail; // 备注


}
