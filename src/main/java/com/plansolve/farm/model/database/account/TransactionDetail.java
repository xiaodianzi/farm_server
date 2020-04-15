package com.plansolve.farm.model.database.account;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2019/2/20
 * @Description: 金钱变更详情（包括：订单支付、订单收款、提现等）
 **/
@Entity
@Data
public class TransactionDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long idTransactionDetail;

    @Column(updatable = false, nullable = false)
    private Long idUser; // 金钱变更人

    @Column(updatable = false, nullable = false)
    private Long idTransactionApplication; // 变更申请ID

    @Column(updatable = false, nullable = false)
    private String transactionType; // 变更类型（订单付款、订单收款、用户提现（银行卡）、用户充值）

    @Column(updatable = false, nullable = false)
    private BigDecimal transactionMoney; // 交易金额（正数为收入、负数为支出）

    @Column(updatable = false, nullable = false)
    private Date transactionTime; // 交易时间

    @Column(updatable = false)
    private Long idUserIncome; // 收入账户

    @Column(updatable = false)
    private String incomeAccountType; // 收入账户类型（托管之家钱包、微信、银行卡）

    @Column(updatable = false)
    private Long bankCardIncomeId; // 相关银行卡ID（收入账户）

    @Column(updatable = false)
    private String bankCardIncomeNo; // 相关银行卡卡号（收入账户）

    @Column(updatable = false)
    private Long idUserExpense; // 支付账户

    @Column(updatable = false)
    private String expenseAccountType; // 支付账户类型（托管之家钱包、微信、银行卡）

    @Column(updatable = false)
    private Long bankCardExpenseId; // 相关银行卡ID（支出账户）

    @Column(updatable = false)
    private String bankCardExpenseNo; // 相关银行卡卡号（支出账户）

    @Column(updatable = false)
    private  Long idOrder; // 相关订单

    @Column(updatable = false)
    private String wxTransactionId; // 微信相关订单号

    @Column(updatable = false)
    private Long idWxPayOrderNotify; // 微信支付成功回执

    @Column(updatable = false)
    private String detail;

}
